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
	Context mContext;

	public boolean is24HourClock() {

		return getPrefs().getBoolean(getKey(R.string.prefs_24hour_key), false);
	}

	private String getKey(int resId) {

		return mContext.getString(resId);
	}

	private SharedPreferences getPrefs() {

		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
}
