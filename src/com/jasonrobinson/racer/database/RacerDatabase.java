package com.jasonrobinson.racer.database;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jasonrobinson.racer.database.agent.Agent;
import com.jasonrobinson.racer.database.agent.RaceAgent;

@ContextSingleton
public class RacerDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "racer.db";
	private static final int VERSION = 1;

	private Agent<?>[] mAgents;

	@Inject
	private RacerDatabase(Context context, RaceAgent raceAgent) {

		super(context, DATABASE_NAME, null, VERSION);
		mAgents = new Agent<?>[] { raceAgent };
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();
		for (Agent<?> agent : mAgents) {
			agent.createTable(db);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.beginTransaction();
		for (Agent<?> agent : mAgents) {
			agent.onUpgrade(db, oldVersion, newVersion);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void insertOrUpdate(String table, ContentValues values, String updateColumn) {

		Object obj = values.get(updateColumn);
		String whereClause = updateColumn + " = ?";
		String[] whereArgs = new String[] { String.valueOf(obj) };

		int rows = getWritableDatabase().update(table, values, whereClause, whereArgs);
		if (rows == 0) {
			getWritableDatabase().insert(table, null, values);
		}
	}
}
