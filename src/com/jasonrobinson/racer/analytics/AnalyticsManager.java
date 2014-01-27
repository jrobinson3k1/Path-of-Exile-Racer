package com.jasonrobinson.racer.analytics;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

@ContextSingleton
public class AnalyticsManager {

	@Inject
	Context mContext;

	public void onStart(Activity activity) {

		EasyTracker.getInstance(mContext).activityStart(activity);
	}

	public void onStop(Activity activity) {

		EasyTracker.getInstance(mContext).activityStop(activity);
	}

	public void trackFragment(Fragment fragment) {

		EasyTracker.getInstance(mContext).send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, fragment.getClass().getName()).build());
	}

	public void trackEvent(String category, String action, String label) {

		EasyTracker.getInstance(mContext).send(MapBuilder.createEvent(category, action, label, 0L).build());
	}
}
