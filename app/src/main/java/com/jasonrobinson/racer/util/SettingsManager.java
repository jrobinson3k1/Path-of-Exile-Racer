package com.jasonrobinson.racer.util;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsManager {

    private static final String KEY_KEEP_SCREEN_ON = "keepscreenon";
    private static final String KEY_AUTO_REFRESH = "autorefresh";
    private static final String KEY_LAST_RACE_FETCH = "lastracefetch";

    private final SharedPreferences mPrefs;

    @Inject
    public SettingsManager(SharedPreferences prefs) {
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

    private void putBoolean(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    private boolean getBoolean(String key, boolean defValue) {
        return mPrefs.getBoolean(key, defValue);
    }

    private void putLong(String key, long value) {
        mPrefs.edit().putLong(key, value).apply();
    }

    private long getLong(String key, long defValue) {
        return mPrefs.getLong(key, defValue);
    }
}
