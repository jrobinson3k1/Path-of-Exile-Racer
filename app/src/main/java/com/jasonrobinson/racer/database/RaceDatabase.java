package com.jasonrobinson.racer.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = RaceDatabase.NAME, version = RaceDatabase.VERSION)
public class RaceDatabase {

    public static final String NAME = "racer";

    public static final int VERSION = 1;
}
