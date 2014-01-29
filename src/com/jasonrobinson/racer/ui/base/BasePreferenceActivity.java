package com.jasonrobinson.racer.ui.base;

import roboguice.activity.RoboPreferenceActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (mImpl.onCreateOptionsMenu(menu)) {
			return true;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mImpl.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {

		super.finish();
		mImpl.finish();
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
