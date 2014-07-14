package com.jasonrobinson.racer.util;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jasonrobinson.racer.R;

@ContextSingleton
public class SettingsManager {

	private static final int KEY_KEEP_SCREEN_ON = R.string.prefs_keepscreenon_key;
	private static final int KEY_AUTO_REFRESH = R.string.prefs_autorefresh_key;
	private static final int KEY_LAST_RACE_FETCH = R.string.prefs_lastracefetch_key;

	@Inject
	private Context mContext;

	@Inject
	private SettingsManager() {

	}

	public void setKeepScreenOn(boolean enable) {

		putBoolean(KEY_KEEP_SCREEN_ON, enable);
	}

	public boolean isKeepScreenOn() {

		return getBoolean(KEY_KEEP_SCREEN_ON, false);
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

	private String getKey(int resId) {

		return mContext.getString(resId);
	}

	private void putBoolean(int keyResId, boolean value) {

		getPrefs().edit().putBoolean(getKey(keyResId), value).commit();
	}

	private boolean getBoolean(int keyResId, boolean defValue) {

		return getPrefs().getBoolean(getKey(keyResId), defValue);
	}

	private void putLong(int keyResId, long value) {

		getPrefs().edit().putLong(getKey(keyResId), value).commit();
	}

	private long getLong(int keyResId, long defValue) {

		return getPrefs().getLong(getKey(keyResId), defValue);
	}

	private SharedPreferences getPrefs() {

		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
}
