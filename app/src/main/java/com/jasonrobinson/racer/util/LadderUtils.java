package com.jasonrobinson.racer.util;

import com.jasonrobinson.racer.enumeration.PoEClass;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.WatchType;

import java.util.List;

public class LadderUtils {

    private LadderUtils() {

    }

    public static Entry findEntry(List<Entry> entries, String name, WatchType type) {
        for (Entry entry : entries) {
            switch (type) {
                case ACCOUNT:
                    if (entry.getAccount().getName().equalsIgnoreCase(name)) {
                        return entry;
                    }
                    break;
                case CHARACTER:
                    if (entry.getCharacter().getName().equalsIgnoreCase(name)) {
                        return entry;
                    }
                    break;
            }
        }

        return null;
    }

    public static int getClassCount(List<Entry> entries, PoEClass poEClass) {
        int count = 0;
        for (Entry entry : entries) {
            if (poEClass.equals(entry.getCharacter().getPoeClass())) {
                count++;
            }
        }

        return count;
    }

    public static void filterEntriesByClass(List<Entry> entries, PoEClass poEClass) {
        if (poEClass == null) {
            return;
        }

        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry entry = entries.get(i);
            if (!entry.getCharacter().getPoeClass().equals(poEClass)) {
                entries.remove(i);
            }
        }
    }

    public static void addClassRankToEntry(List<Entry> entries, Entry entry, int startRank) {
        int rank = startRank + 1;
        String name = entry.getCharacter().getName();
        PoEClass poEClass = entry.getCharacter().getPoeClass();

        for (Entry nextEntry : entries) {
            String nextName = nextEntry.getCharacter().getName();
            if (nextName.equalsIgnoreCase(name)) {
                entry.setClassRank(rank);
                break;
            }

            PoEClass nextPoEClass = nextEntry.getCharacter().getPoeClass();
            if (nextPoEClass == poEClass) {
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
            switch (entry.getCharacter().getPoeClass()) {
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
