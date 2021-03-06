package com.jasonrobinson.racer.db;

import android.util.Log;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.Race.Rule;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseManager {

    private static final String TAG = DatabaseManager.class.getSimpleName();

    @Inject
    DatabaseHelper mHelper;

    public List<Race> getRaces(RaceOptions option) {
        switch (option) {
            case FINISHED:
                return getAllFinishedRaces();
            case UNFINISHED:
                return getAllUnfinishedRaces();
            case IN_PROGRESS:
                return getAllInProgressRaces();
            case ALL:
            default:
                return getAllRaces();
        }
    }

    private List<Race> getAllRaces() {
        try {
            return mHelper.getRaceDao().queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all races", e);
            return null;
        }
    }

    private List<Race> getAllFinishedRaces() {
        try {
            QueryBuilder<Race, String> queryBuilder = mHelper.getRaceDao().queryBuilder();
            queryBuilder.where().le("endAt", new Date(System.currentTimeMillis()));
            queryBuilder.orderBy("startAt", false);
            return queryBuilder.query();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all finished races", e);
            return null;
        }
    }

    private List<Race> getAllUnfinishedRaces() {
        try {
            QueryBuilder<Race, String> queryBuilder = mHelper.getRaceDao().queryBuilder();
            queryBuilder.where().gt("endAt", new Date(System.currentTimeMillis()));
            queryBuilder.orderBy("startAt", true);
            return queryBuilder.query();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all finished races", e);
            return null;
        }
    }

    private List<Race> getAllInProgressRaces() {
        Date now = new Date(System.currentTimeMillis());

        try {
            QueryBuilder<Race, String> queryBuilder = mHelper.getRaceDao().queryBuilder();
            queryBuilder.where().gt("endAt", now).and().lt("startAt", now);
            return queryBuilder.query();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all finished races", e);
            return null;
        }
    }

    public Race getRace(String id) {
        try {
            return mHelper.getRaceDao().queryForId(id);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve race with id " + id, e);
            return null;
        }
    }

    public boolean addOrUpdateRace(Race race) {
        try {
            CreateOrUpdateStatus status = mHelper.getRaceDao().createOrUpdate(race);

            for (Rule rule : race.getRules()) {
                rule.setRace(race);

                // Rules could have been added or removed, so remove persisted
                // rules and add current rules
                if (status.isUpdated()) {
                    DeleteBuilder<Rule, Long> deleteBuilder = mHelper.getRuleDao().deleteBuilder();
                    deleteBuilder.where().eq("ruleId", rule.getRuleId()).and().eq("race_id", race.getRaceId());
                    deleteBuilder.delete();
                }

                mHelper.getRuleDao().createOrUpdate(rule);
            }

            return true;
        } catch (SQLException e) {
            Log.e(TAG, "Failed to cache a race", e);
            return false;
        }
    }

    public int addOrUpdateRaceList(final List<Race> races) {
        try {
            return mHelper.getRaceDao().callBatchTasks(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {

                    int counter = 0;
                    for (Race race : races) {
                        counter += addOrUpdateRace(race) ? 1 : 0;
                    }

                    return counter;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to batch cache races", e);
        }

        return 0;
    }
}
