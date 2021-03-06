package com.jasonrobinson.racer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.jasonrobinson.racer.module.AnalyticsModule;
import com.jasonrobinson.racer.module.ContextModule;
import com.jasonrobinson.racer.module.DatabaseModule;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.module.SettingsModule;

public class RacerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GraphHolder.getInstance().addModules(getModules());
        if (BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this).setDryRun(true);
            GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            Crashlytics.getInstance().setDebugMode(true);
        }

        Crashlytics.start(this);
    }

    private Object[] getModules() {
        return new Object[]{
                new ContextModule(this),
                new AnalyticsModule(),
                new DatabaseModule(),
                new SettingsModule()
        };
    }
}
