package com.jasonrobinson.racer.async;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.jasonrobinson.racer.async.LadderAsyncTask.LadderParams;
import com.jasonrobinson.racer.async.LadderAsyncTask.LadderResult;
import com.jasonrobinson.racer.enumeration.PoeClass;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.WatchType;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.util.LadderUtils;

import java.net.SocketTimeoutException;
import java.util.Locale;

import retrofit.RetrofitError;

public abstract class LadderAsyncTask extends AsyncTask<LadderParams, Void, LadderResult> {

    public static final int LIMIT_PER_REQUEST = 200;

    // Character names are unique across all leagues (I think?), eliminating the
    // possibility of a name conflict between races
    private static LruCache<String, Integer> sCharacterRankCache = new LruCache<>(10);

    @Override
    protected LadderResult doInBackground(LadderParams... params) {
        LadderResult result = new LadderResult();
        LadderParams ladderParams = params[0];

        String id = ladderParams.id;
        int start = ladderParams.start;
        int count = ladderParams.count;
        PoeClass poeClass = ladderParams.poeClass;
        String character = ladderParams.name;
        WatchType type = ladderParams.type;
        PoeClass characterPoeClass = ladderParams.characterPoeClass;

        RaceClient client = new RaceClient();
        try {
            Ladder ladder;

            if (poeClass == null) {
                ladder = fetchLadder(client, id, start, count);
            } else {
                ladder = fetchLadderForClass(client, id, start, count, poeClass);
            }

            if (isCancelled()) {
                result.ladder = ladder;
                return result;
            }

            // Character Watcher
            Entry characterEntry = null;
            boolean poeClassEqual = poeClass != null && characterPoeClass != null && poeClass == characterPoeClass;
            if ((poeClassEqual || characterPoeClass == null || poeClass == null) && !TextUtils.isEmpty(character)) {
                characterEntry = LadderUtils.findEntry(ladder.getEntries(), character, type);
                if (characterEntry == null) {
                    characterEntry = fetchEntry(client, id, character, type, ladder.getTotal(), poeClassEqual || characterPoeClass == null ? poeClass : null);
                }
            }

            if (isCancelled()) {
                result.ladder = ladder;
                return result;
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

            if (characterEntry != null) {
                ladder.getEntries().add(0, characterEntry);
            }

            result.ladder = ladder;
            return result;
        } catch (SocketTimeoutException e) {
            result.socketException = e;
            return result;
        } catch (RetrofitError e) {
            result.retrofitError = e;
            return result;
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
            } else {
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
        if (ladder != null) {
            int size = ladder.getEntries().size();
            for (int i = size - 1; i >= count; i--) {
                ladder.getEntries().remove(i);
            }
        }

        return ladder;
    }

    /**
     * ONLY supply the PoeClass if you want the class rank.
     */
    private Entry fetchEntry(RaceClient client, String id, String name, WatchType type, int totalRanks, PoeClass poeClass) throws SocketTimeoutException {
        Integer characterRank = sCharacterRankCache.get(name.toLowerCase(Locale.US));
        if (characterRank == null) {
            characterRank = 0;
        }

        Entry entry;
        if (characterRank == 0 || poeClass != null) {
            entry = fetchEntryLinear(client, id, name, type, totalRanks, poeClass);
        } else {
            entry = fetchEntryBinary(client, id, name, type, characterRank, totalRanks);
        }

        if (entry != null) {
            sCharacterRankCache.put(name, entry.getRank());
        }

        return entry;
    }

    private Entry fetchEntryLinear(RaceClient client, String id, String name, WatchType type, int totalRanks, PoeClass poeClass) throws SocketTimeoutException {
        boolean findClassRank = poeClass != null;
        int classRankCount = 0;

        for (int offset = 0; offset < totalRanks; offset += LIMIT_PER_REQUEST) {
            Ladder nextLadder = client.fetchLadder(id, offset, LIMIT_PER_REQUEST);
            Entry entry = LadderUtils.findEntry(nextLadder.getEntries(), name, type);
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

        return null;
    }

    private Entry fetchEntryBinary(RaceClient client, String id, String name, WatchType type, int rank, int totalRanks) throws SocketTimeoutException {
        int startOffset = rank - LIMIT_PER_REQUEST / 2;
        int totalQueries = (int) Math.ceil((double) totalRanks / LIMIT_PER_REQUEST);
        for (int i = 0; i < totalQueries; i++) {
            int offset;
            if (i % 2 == 0) {
                offset = startOffset + i * LIMIT_PER_REQUEST;
            } else {
                offset = (startOffset - 1) - i * LIMIT_PER_REQUEST;
            }

            if (offset < 0 || offset > totalRanks) {
                continue;
            }

            Ladder nextLadder = client.fetchLadder(id, offset, LIMIT_PER_REQUEST);
            Entry entry = LadderUtils.findEntry(nextLadder.getEntries(), name, type);
            if (entry != null) {
                return entry;
            }

            if (isCancelled()) {
                break;
            }
        }

        return null;
    }

    public static class LadderParams {

        private String id;
        private int start;
        private int count;
        private PoeClass poeClass;
        private String name;
        private WatchType type;
        private PoeClass characterPoeClass;

        public LadderParams(String id, int start, int count, PoeClass poeClass, String name, WatchType type, PoeClass characterPoeClass) {
            this.id = id;
            this.start = start;
            this.count = count;
            this.poeClass = poeClass;
            this.name = name;
            this.type = type;
            this.characterPoeClass = characterPoeClass;
        }
    }

    public static class LadderResult {
        public SocketTimeoutException socketException;
        public RetrofitError retrofitError;
        public Ladder ladder;

        private LadderResult() {
        }
    }
}
