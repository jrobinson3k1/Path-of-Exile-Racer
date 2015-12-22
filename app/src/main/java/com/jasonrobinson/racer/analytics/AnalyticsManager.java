package com.jasonrobinson.racer.analytics;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnalyticsManager {

    private final Context mContext;

    private final Tracker mTracker;

    @Inject
    AnalyticsManager(Context context, Tracker tracker) {
        mContext = context;
        mTracker = tracker;
    }

    public void onStart(Activity activity) {
        GoogleAnalytics.getInstance(mContext).reportActivityStart(activity);
    }

    public void onStop(Activity activity) {
        GoogleAnalytics.getInstance(mContext).reportActivityStop(activity);
    }

    public void trackEvent(String category, String action, String label) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
