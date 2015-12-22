package com.jasonrobinson.racer;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.jasonrobinson.racer.dagger.ComponentHolder;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import android.app.Application;

import io.fabric.sdk.android.Fabric;

public class RacerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ComponentHolder.getInstance().onApplicationCreate(this);
        FlowManager.init(this);

        if (BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this).setDryRun(true);
            GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
        }

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
    }
}
