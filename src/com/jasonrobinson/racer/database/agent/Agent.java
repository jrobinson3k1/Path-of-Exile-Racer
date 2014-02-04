package com.jasonrobinson.racer.database.agent;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public interface Agent<T> {

	public void createTable(SQLiteDatabase db);

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

	public void insert(T t);

	public void insertAll(List<T> t);

	public List<T> list();
}
