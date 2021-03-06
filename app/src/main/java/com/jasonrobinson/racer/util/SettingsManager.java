package com.jasonrobinson.racer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.RaceMode;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsManager {

    private static final int KEY_KEEP_SCREEN_ON = R.string.key_keep_screen_on;
    private static final int KEY_AUTO_REFRESH = R.string.key_auto_refresh;
    private static final int KEY_LAST_RACE_FETCH = R.string.key_last_race_fetch;
    private static final int KEY_RACE_MODE = R.string.key_race_mode;

    private final Context mContext;
    private final SharedPreferences mPrefs;

    @Inject
    public SettingsManager(Context context, SharedPreferences prefs) {
        mContext = context;
        mPrefs = prefs;
    }

    public boolean isKeepScreenOn() {
        return getBoolean(KEY_KEEP_SCREEN_ON, false);
    }

    public void setKeepScreenOn(boolean enable) {
        putBoolean(KEY_KEEP_SCREEN_ON, enable);
    }

    public void setAutoRefresh(boolean enable) {
        putBoolean(KEY_AUTO_REFRESH, enable);
    }

    public boolean isAutoRefreshEnabled() {
        return getBoolean(KEY_AUTO_REFRESH, true);
    }

    public void updateLastRaceFetch() {
        putLong(KEY_LAST_RACE_FETCH, System.currentTimeMillis());
    }

    public long getLastRaceFetch() {
        return getLong(KEY_LAST_RACE_FETCH, 0L);
    }

    public void setRaceMode(RaceMode mode) {
        putInt(KEY_RACE_MODE, mode.ordinal());
    }

    public RaceMode getRaceMode() {
        return RaceMode.values()[getInt(KEY_RACE_MODE, RaceMode.LIST.ordinal())];
    }

    private void putBoolean(int key, boolean value) {
        mPrefs.edit().putBoolean(mContext.getString(key), value).apply();
    }

    private boolean getBoolean(int key, boolean defValue) {
        return mPrefs.getBoolean(mContext.getString(key), defValue);
    }

    private void putLong(int key, long value) {
        mPrefs.edit().putLong(mContext.getString(key), value).apply();
    }

    private long getLong(int key, long defValue) {
        return mPrefs.getLong(mContext.getString(key), defValue);
    }

    private void putInt(int key, int value) {
        mPrefs.edit().putInt(mContext.getString(key), value).apply();
    }

    private int getInt(int key, int defValue) {
        return mPrefs.getInt(mContext.getString(key), defValue);
    }
}
