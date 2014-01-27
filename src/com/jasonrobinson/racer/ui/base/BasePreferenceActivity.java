package com.jasonrobinson.racer.ui.base;

import roboguice.activity.RoboPreferenceActivity;
import android.os.Bundle;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.util.SettingsManager;

public class BasePreferenceActivity extends RoboPreferenceActivity {

	BaseActivityImpl mImpl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mImpl = new BaseActivityImpl(this, false);
		mImpl.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {

		super.onStart();
		mImpl.onStart();
	}

	@Override
	protected void onStop() {

		super.onStop();
		mImpl.onStop();
	}

	public SettingsManager getSettingsManager() {

		return mImpl.getSettingsManager();
	}

	public AnalyticsManager getAnalyticsManager() {

		return mImpl.getAnalyticsManager();
	}

	@Override
	public void setTitle(int titleId) {

		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {

		super.setTitle(mImpl.formatTitleText(title));
	}
}
