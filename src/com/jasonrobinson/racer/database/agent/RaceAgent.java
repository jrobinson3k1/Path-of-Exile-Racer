package com.jasonrobinson.racer.database.agent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jasonrobinson.racer.database.RacerDatabase;
import com.jasonrobinson.racer.database.columns.RaceColumns;
import com.jasonrobinson.racer.database.columns.RuleColumns;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.Race.Rule;

@ContextSingleton
public class RaceAgent implements Agent<Race> {

	private static final String TAG = RaceAgent.class.getSimpleName();

	// @formatter:off
	private static final String CREATE_RACE_TABLE = "create table " + RaceColumns.TABLE_NAME + "(" +
			RaceColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RaceColumns.RACE_ID + " TEXT, " +
			RaceColumns.DESCRIPTION + " TEXT, " +
			RaceColumns.URL + " TEXT, " +
			RaceColumns.REGISTER_AT + " TEXT, " +
			RaceColumns.START_AT + " TEXT, " +
			RaceColumns.END_AT + " TEXT, " +
			RaceColumns.EVENT + " INTEGER, " +
			RaceColumns.RULE_IDS + " TEXT)";
	
	private static final String CREATE_RULE_TABLE = "create table " + RuleColumns.TABLE_NAME + "(" +
			RuleColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RuleColumns.RULE_ID + " INTEGER, " +
			RuleColumns.NAME + " TEXT, " +
			RuleColumns.DESCRIPTION + " TEXT)";
	// @formatter:on

	@Inject
	private RacerDatabase mDatabase;

	@Override
	public void createTable(SQLiteDatabase db) {

		Log.d(TAG, "Creating table: " + CREATE_RACE_TABLE);
		db.execSQL(CREATE_RACE_TABLE);
		Log.d(TAG, "Creating table: " + CREATE_RULE_TABLE);
		db.execSQL(CREATE_RULE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// No upgrades yet
	}

	@Override
	public void insert(Race t) {

		ContentValues values = new ContentValues();
		values.put(RaceColumns.RACE_ID, t.getRaceId());
		values.put(RaceColumns.DESCRIPTION, t.getDescription());
		values.put(RaceColumns.URL, t.getUrl());
		values.put(RaceColumns.REGISTER_AT, t.getRegisterAt());
		values.put(RaceColumns.START_AT, t.getStartAt());
		values.put(RaceColumns.END_AT, t.getEndAt());
		values.put(RaceColumns.EVENT, t.isEvent());

		Rule[] rules = t.getRules();
		StringBuilder ids = new StringBuilder();
		for (int i = 0; i < rules.length; i++) {
			Rule rule = rules[i];
			ContentValues ruleValues = new ContentValues();
			ruleValues.put(RuleColumns.RULE_ID, rule.getRuleId());
			ruleValues.put(RuleColumns.NAME, rule.getName());
			ruleValues.put(RuleColumns.DESCRIPTION, rule.getDescription());

			mDatabase.insertOrUpdate(RuleColumns.TABLE_NAME, ruleValues, RuleColumns.RULE_ID);

			if (i > 0) {
				ids.append(",");
			}
			ids.append(rule.getRuleId());
		}
		values.put(RaceColumns.RULE_IDS, ids.toString());

		mDatabase.insertOrUpdate(RaceColumns.TABLE_NAME, values, RaceColumns.RACE_ID);
	}

	@Override
	public void insertAll(List<Race> t) {

		for (Race race : t) {
			insert(race);
		}
	}

	@Override
	public List<Race> list() {

		List<Race> races = new ArrayList<Race>();

		Cursor c = mDatabase.getReadableDatabase().query(RaceColumns.TABLE_NAME, null, null, null, null, null, null);
		int raceIdColumn = c.getColumnIndexOrThrow(RaceColumns.RACE_ID);
		int descriptionColumn = c.getColumnIndexOrThrow(RaceColumns.DESCRIPTION);
		int urlColumn = c.getColumnIndexOrThrow(RaceColumns.URL);
		int startAtColumn = c.getColumnIndexOrThrow(RaceColumns.START_AT);
		int registerAtColumn = c.getColumnIndexOrThrow(RaceColumns.REGISTER_AT);
		int endAtColumn = c.getColumnIndexOrThrow(RaceColumns.END_AT);
		int eventColumn = c.getColumnIndexOrThrow(RaceColumns.EVENT);
		int ruleIdsColumn = c.getColumnIndexOrThrow(RaceColumns.RULE_IDS);

		while (c.moveToNext()) {
			String raceId = c.getString(raceIdColumn);
			String description = c.getString(descriptionColumn);
			String url = c.getString(urlColumn);
			String startAt = c.getString(startAtColumn);
			String registerAt = c.getString(registerAtColumn);
			String endAt = c.getString(endAtColumn);
			boolean event = c.getInt(eventColumn) == 1 ? true : false;

			String ruleIds = c.getString(ruleIdsColumn);
			String[] ruleIdArray = ruleIds.split(",");

			Rule[] rules = new Rule[ruleIdArray.length];
			for (int i = 0; i < rules.length; i++) {

				String ruleIdString = ruleIdArray[i];
				Cursor ruleCursor = mDatabase.getReadableDatabase().query(RuleColumns.TABLE_NAME, null, RuleColumns.RULE_ID + " = ?", new String[] { ruleIdString }, null, null, null);

				if (ruleCursor.getCount() > 0) {
					int ruleIdColumn = ruleCursor.getColumnIndexOrThrow(RuleColumns.RULE_ID);
					int nameColumn = ruleCursor.getColumnIndexOrThrow(RuleColumns.NAME);
					int ruleDescriptionColumn = ruleCursor.getColumnIndexOrThrow(RuleColumns.DESCRIPTION);

					while (ruleCursor.moveToNext()) {
						long ruleId = ruleCursor.getLong(ruleIdColumn);
						String name = ruleCursor.getString(nameColumn);
						String ruleDescription = ruleCursor.getString(ruleDescriptionColumn);

						rules[i] = new Rule(ruleId, name, ruleDescription);
					}
				}
			}

			races.add(new Race(raceId, description, url, event, registerAt, startAt, endAt, rules));
		}

		return races;
	}
}
