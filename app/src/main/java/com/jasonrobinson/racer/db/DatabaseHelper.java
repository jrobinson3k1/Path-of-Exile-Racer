package com.jasonrobinson.racer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.Race.Rule;

import java.sql.SQLException;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "racer.db";
    private static final int VERSION = 1;

    private Dao<Race, String> mRaceDao;
    private Dao<Rule, Long> mRuleDao;

    @Inject
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        database.beginTransaction();
        try {
            TableUtils.createTable(connectionSource, Race.class);
            TableUtils.createTable(connectionSource, Rule.class);
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to create database", e);
            throw new RuntimeException(e);
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // no upgrades yet
    }

    public Dao<Race, String> getRaceDao() {
        if (mRaceDao == null) {
            try {
                mRaceDao = getDao(Race.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return mRaceDao;
    }

    public Dao<Rule, Long> getRuleDao() {
        if (mRuleDao == null) {
            try {
                mRuleDao = getDao(Rule.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return mRuleDao;
    }
}
