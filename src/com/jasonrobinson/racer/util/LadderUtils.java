package com.jasonrobinson.racer.util;

import java.util.List;

import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.PoeClass;

public class LadderUtils {

	private LadderUtils() {

	}

	public static Entry findEntry(List<Entry> entries, String character) {

		for (Entry entry : entries) {
			if (entry.getCharacter().getName().equalsIgnoreCase(character)) {
				return entry;
			}
		}

		return null;
	}

	public static int getClassCount(List<Entry> entries, PoeClass poeClass) {

		int count = 0;
		for (Entry entry : entries) {
			if (poeClass == PoeClass.getClassForName(entry.getCharacter().getPoeClass())) {
				count++;
			}
		}

		return count;
	}

	public static void filterEntriesByClass(List<Entry> entries, PoeClass poeClass) {

		if (poeClass == null) {
			return;
		}

		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry entry = entries.get(i);
			if (PoeClass.getClassForName(entry.getCharacter().getPoeClass()) != poeClass) {
				entries.remove(i);
			}
		}
	}

	public static void addClassRankToEntry(List<Entry> entries, Entry entry, int startRank) {

		int rank = startRank + 1;
		String name = entry.getCharacter().getName();
		PoeClass poeClass = PoeClass.getClassForName(entry.getCharacter().getPoeClass());

		for (Entry nextEntry : entries) {
			String nextName = nextEntry.getCharacter().getName();
			if (nextName.equalsIgnoreCase(name)) {
				entry.setClassRank(rank);
				break;
			}

			PoeClass nextPoeClass = PoeClass.getClassForName(nextEntry.getCharacter().getPoeClass());
			if (nextPoeClass == poeClass) {
				rank++;
			}
		}
	}

	public static void addClassRanksToEntries(List<Entry> entries) {

		int marauderRank = 1;
		int duelistRank = 1;
		int witchRank = 1;
		int templarRank = 1;
		int shadowRank = 1;
		int rangerRank = 1;
		int scionRank = 1;

		for (Entry entry : entries) {
			PoeClass poeClass = PoeClass.getClassForName(entry.getCharacter().getPoeClass());
			switch (poeClass) {
				case DUELIST:
					entry.setClassRank(duelistRank);
					duelistRank++;
					break;
				case MARAUDER:
					entry.setClassRank(marauderRank);
					marauderRank++;
					break;
				case RANGER:
					entry.setClassRank(rangerRank);
					rangerRank++;
					break;
				case SCION:
					entry.setClassRank(scionRank);
					scionRank++;
					break;
				case SHADOW:
					entry.setClassRank(shadowRank);
					shadowRank++;
					break;
				case TEMPLAR:
					entry.setClassRank(templarRank);
					templarRank++;
					break;
				case WITCH:
					entry.setClassRank(witchRank);
					witchRank++;
					break;
			}
		}
	}
}
