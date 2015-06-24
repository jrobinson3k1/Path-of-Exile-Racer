package com.jasonrobinson.racer.model;

import com.google.gson.annotations.SerializedName;

import com.jasonrobinson.racer.enumeration.PoEClass;

import java.util.List;

public class Ladder {

    private int total;

    private List<Entry> entries;

    private Ladder() {
    }

    public int getTotal() {
        return total;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {

        private boolean online;

        private int rank;

        private boolean dead;

        private Character character;

        private Account account;

        private int classRank;

        public boolean isOnline() {
            return online;
        }

        public int getRank() {
            return rank;
        }

        public boolean isDead() {
            return dead;
        }

        public Character getCharacter() {
            return character;
        }

        public Account getAccount() {
            return account;
        }

        public void setClassRank(int classRank) {
            this.classRank = classRank;
        }

        public static class Character {

            private String name;

            private int level;

            @SerializedName("class")
            private PoEClass poeClass;

            private long experience;

            public String getName() {
                return name;
            }

            public int getLevel() {
                return level;
            }

            public PoEClass getPoeClass() {
                return poeClass;
            }

            public long getExperience() {
                return experience;
            }
        }

        public static class Account {

            private String name;

            private Challenges challenges;

            private Twitch twitch;

            public String getName() {
                return name;
            }

            public Challenges getChallenges() {
                return challenges;
            }

            public Twitch getTwitch() {
                return twitch;
            }

            public static class Challenges {

                private int total;

                public int getTotal() {
                    return total;
                }
            }

            public static class Twitch {

                private String name;

                public String getName() {
                    return name;
                }
            }
        }
    }
}
