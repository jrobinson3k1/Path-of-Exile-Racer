package com.jasonrobinson.racer.module;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.analytics.AnalyticsManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = AnalyticsManager.class,
        includes = ContextModule.class
)
public class AnalyticsModule {

    @Provides
    @Singleton
    Tracker providesTracker(Context context) {
        return GoogleAnalytics.getInstance(context).newTracker(R.xml.analytics);
    }
}
