package com.jasonrobinson.racer.ui.base;

import javax.inject.Inject;

import roboguice.RoboGuice;
import android.app.Activity;
import android.support.v4.app.Fragment;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;

public class BaseFragmentImpl {

	@Inject
	private AnalyticsManager mAnalyticsManager;
	@Inject
	private SettingsManager mSettingsManager;
	@Inject
	private DatabaseManager mDatabaseManager;

	private Fragment mFragment;

	public BaseFragmentImpl(Fragment fragment) {

		mFragment = fragment;
	}

	public Activity getActivity() {

		return mFragment.getActivity();
	}

	public void onAttach(Activity activity) {

		RoboGuice.injectMembers(activity, this);
	}

	public <T> T castActivity(Class<T> clz) {

		Activity activity = getActivity();
		if (activity == null) {
			throw new IllegalStateException("fragment is not attached to an activity");
		}

		try {
			return clz.cast(activity);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.getClass().getSimpleName() + " must implement " + clz.getSimpleName());
		}
	}

	public AnalyticsManager getAnalyticsManager() {

		return mAnalyticsManager;
	}

	public SettingsManager getSettingsManager() {

		return mSettingsManager;
	}

	public DatabaseManager getdDatabaseManager() {

		return mDatabaseManager;
	}
}
