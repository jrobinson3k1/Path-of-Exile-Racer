package com.jasonrobinson.racer.db;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import roboguice.RoboGuice;
import roboguice.inject.ContextSingleton;
import android.content.Context;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.Race.Rule;

@ContextSingleton
public class DatabaseManager {

	@Inject
	private DatabaseHelper mHelper;

	@Inject
	private DatabaseManager(Context context) {

		RoboGuice.injectMembers(context, this);
	}

	public List<Race> getAllRaces() {

		List<Race> races = null;
		try {
			races = mHelper.getRaceDao().queryForAll();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return races;
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
		}
		catch (SQLException e) {
			e.printStackTrace();
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
