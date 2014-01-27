package com.jasonrobinson.racer.util;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jasonrobinson.racer.R;

@ContextSingleton
public class SettingsManager {

	@Inject
	private Context mContext;

	public void setKeepScreenOn(boolean enable) {

		putBoolean(R.string.prefs_keepscreenon_key, enable);
	}

	public boolean isKeepScreenOn() {

		return getBoolean(R.string.prefs_keepscreenon_key, false);
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

	private SharedPreferences getPrefs() {

		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
}
