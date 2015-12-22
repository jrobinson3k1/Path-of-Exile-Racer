package com.jasonrobinson.racer.manager;

import com.jasonrobinson.racer.R;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class SettingsManager {

    private static final int KEY_KEEP_SCREEN_ON = R.string.key_keep_screen_on;
    private static final int KEY_AUTO_REFRESH = R.string.key_auto_refresh;
    private static final int KEY_LAST_RACE_FETCH = R.string.key_last_race_fetch;

    private final Context mContext;
    private final SharedPreferences mPrefs;

    private PublishSubject<Long> mLastRaceFetchSubject = PublishSubject.create();

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
        long now = System.currentTimeMillis();
        putLong(KEY_LAST_RACE_FETCH, now);
        mLastRaceFetchSubject.onNext(now);
    }

    public long getLastRaceFetch() {
        return getLong(KEY_LAST_RACE_FETCH, 0L);
    }

    public Observable<Long> getLastRaceFetchObservable() {
        return mLastRaceFetchSubject.asObservable();
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
