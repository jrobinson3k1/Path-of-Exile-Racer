package com.jasonrobinson.racer.analytics;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jasonrobinson.racer.R;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class AnalyticsManager {

    @Inject
    Context mContext;

    Tracker mTracker;

    public void onStart(Activity activity) {
        GoogleAnalytics.getInstance(mContext).reportActivityStart(activity);
    }

    public void onStop(Activity activity) {
        GoogleAnalytics.getInstance(mContext).reportActivityStop(activity);
    }

    public void trackFragment(Fragment fragment) {
        getTracker().setScreenName(fragment.getClass().getName());
        getTracker().send(new HitBuilders.AppViewBuilder().build());
    }

    public void trackEvent(String category, String action, String label) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    private Tracker getTracker() {
        if (mTracker == null) {
            mTracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.analytics);
        }

        return mTracker;
    }
}
