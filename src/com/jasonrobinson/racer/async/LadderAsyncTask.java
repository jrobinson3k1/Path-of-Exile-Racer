package com.jasonrobinson.racer.async;

import java.net.SocketTimeoutException;
import java.util.Locale;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.jasonrobinson.racer.async.LadderAsyncTask.LadderParams;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.PoeClass;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.util.LadderUtils;

public abstract class LadderAsyncTask extends AsyncTask<LadderParams, Void, Ladder> {

	public static final int LIMIT_PER_REQUEST = 200;

	// Character names are unique across all leagues (I think?), eliminating the
	// possibility of a name conflict between races
	private static LruCache<String, Integer> sCharacterRankCache = new LruCache<String, Integer>(10);

	@Override
	protected Ladder doInBackground(LadderParams... params) {

		LadderParams ladderParams = params[0];

		String id = ladderParams.id;
		int start = ladderParams.start;
		int count = ladderParams.count;
		PoeClass poeClass = ladderParams.poeClass;
		String character = ladderParams.character;
		PoeClass characterPoeClass = ladderParams.characterPoeClass;

		RaceClient client = new RaceClient();
		try {
			Ladder ladder = null;

			if (poeClass == null) {
				ladder = fetchLadder(client, id, start, count);
			}
			else {
				ladder = fetchLadderForClass(client, id, start, count, poeClass);
			}

			if (isCancelled()) {
				return ladder;
			}

			// Character Watcher
			Entry characterEntry = null;
			boolean poeClassEqual = poeClass != null && characterPoeClass != null && poeClass == characterPoeClass;
			if ((poeClassEqual || characterPoeClass == null || poeClass == null) && !TextUtils.isEmpty(character)) {
				characterEntry = LadderUtils.findEntry(ladder.getEntries(), character);
				if (characterEntry == null) {
					characterEntry = fetchEntry(client, id, character, ladder.getTotal(), poeClassEqual || characterPoeClass == null ? poeClass : null);
				}
			}

			if (isCancelled()) {
				return ladder;
			}

			// Filter by class
			if (poeClass != null) {
				LadderUtils.filterEntriesByClass(ladder.getEntries(), poeClass);
				LadderUtils.addClassRanksToEntries(ladder.getEntries());

				// Prune extra entries
				int size = ladder.getEntries().size();
				for (int i = size - 1; i >= count; i--) {
					ladder.getEntries().remove(i);
				}
			}

			if (characterEntry != null && (poeClass == null || PoeClass.getClassForName(characterEntry.getCharacter().getPoeClass()) == poeClass)) {
				ladder.getEntries().add(0, characterEntry);
			}

			return ladder;
		}
		catch (SocketTimeoutException e) {
			return null;
		}
	}

	/**
	 * This fetches enough ladder ranks to ensure the class count is greater
	 * than or equal to <code>count</code>. The ladder returned still contains
	 * every class.
	 */
	private Ladder fetchLadderForClass(RaceClient client, String id, int start, int count, PoeClass poeClass) throws SocketTimeoutException {

		Ladder ladder = null;
		int classCount = 0;
		do {
			Ladder nextLadder = fetchLadder(client, id, start, LIMIT_PER_REQUEST);
			classCount += LadderUtils.getClassCount(nextLadder.getEntries(), poeClass);

			if (ladder == null) {
				ladder = nextLadder;
			}
			else {
				ladder.getEntries().addAll(nextLadder.getEntries());
			}

			start += LIMIT_PER_REQUEST;

			if (isCancelled()) {
				break;
			}
		}
		while (classCount < count && start < ladder.getTotal());

		return ladder;
	}

	private Ladder fetchLadder(RaceClient client, String id, int start, int count) throws SocketTimeoutException {

		Ladder ladder = null;
		int end = start + count;
		for (int offset = start; offset < end; offset += LIMIT_PER_REQUEST) {
			Ladder nextLadder = client.fetchLadder(id, offset, LIMIT_PER_REQUEST);
			if (ladder == null) {
				ladder = nextLadder;
				continue;
			}

			ladder.getEntries().addAll(nextLadder.getEntries());

			if (isCancelled()) {
				break;
			}
		}

		// Prune extra entries
		int size = ladder.getEntries().size();
		for (int i = size - 1; i >= count; i--) {
			ladder.getEntries().remove(i);
		}

		return ladder;
	}

	/**
	 * ONLY supply the PoeClass if you want the class rank.
	 */
	private Entry fetchEntry(RaceClient client, String id, String character, int totalRanks, PoeClass poeClass) throws SocketTimeoutException {

		Integer characterRank = sCharacterRankCache.get(character.toLowerCase(Locale.US));
		if (characterRank == null) {
			characterRank = 0;
		}

		Entry entry;
		if (characterRank == 0 || poeClass != null) {
			entry = fetchEntryLinear(client, id, character, totalRanks, poeClass);
		}
		else {
			entry = fetchEntryBinary(client, id, character, characterRank, totalRanks);
		}

		if (entry != null) {
			sCharacterRankCache.put(entry.getCharacter().getName().toLowerCase(Locale.US), entry.getRank());
		}

		return entry;
	}

	private Entry fetchEntryLinear(RaceClient client, String id, String character, int totalRanks, PoeClass poeClass) throws SocketTimeoutException {

		Entry entry = null;
		boolean findClassRank = poeClass != null;
		int classRankCount = 0;

		for (int offset = 0; offset < totalRanks; offset += LIMIT_PER_REQUEST) {
			Ladder nextLadder = client.fetchLadder(id, offset, LIMIT_PER_REQUEST);
			entry = LadderUtils.findEntry(nextLadder.getEntries(), character);
			if (entry != null) {
				if (findClassRank) {
					LadderUtils.addClassRankToEntry(nextLadder.getEntries(), entry, classRankCount);
				}

				return entry;
			}

			if (findClassRank) {
				classRankCount += LadderUtils.getClassCount(nextLadder.getEntries(), poeClass);
			}

			if (isCancelled()) {
				break;
			}
		}

		return entry;
	}

	private Entry fetchEntryBinary(RaceClient client, String id, String character, int rank, int totalRanks) throws SocketTimeoutException {

		Entry entry = null;

		int startOffset = rank - LIMIT_PER_REQUEST / 2;
		int totalQueries = (int) Math.ceil((double) totalRanks / LIMIT_PER_REQUEST);
		for (int i = 0; i < totalQueries; i++) {
			int offset;
			if (i % 2 == 0) {
				offset = startOffset + i * LIMIT_PER_REQUEST;
			}
			else {
				offset = (startOffset - 1) - i * LIMIT_PER_REQUEST;
			}

			if (offset < 0 || offset > totalRanks) {
				continue;
			}

			Ladder nextLadder = client.fetchLadder(id, offset, LIMIT_PER_REQUEST);
			entry = LadderUtils.findEntry(nextLadder.getEntries(), character);
			if (entry != null) {
				return entry;
			}

			if (isCancelled()) {
				break;
			}
		}

		return entry;
	}

	public static class LadderParams {

		public static final int ALL = -1;

		private String id;
		private int start;
		private int count;
		private String character;
		private PoeClass characterPoeClass;
		private PoeClass poeClass;

		public LadderParams(String id, int start, int count) {

			this(id, start, count, null, null, null);
		}

		public LadderParams(String id, int start, int count, PoeClass poeClass) {

			this(id, start, count, poeClass, null, null);
		}

		public LadderParams(String id, int start, int count, String character, PoeClass characterPoeClass) {

			this(id, start, count, null, character, null);
		}

		public LadderParams(String id, int start, int count, PoeClass poeClass, String character, PoeClass characterPoeClass) {

			this.id = id;
			this.start = start;
			this.count = count;
			this.poeClass = poeClass;
			this.character = character;
			this.characterPoeClass = characterPoeClass;
		}
	}
}
