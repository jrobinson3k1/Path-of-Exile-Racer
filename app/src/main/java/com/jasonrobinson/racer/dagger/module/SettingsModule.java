package com.jasonrobinson.racer.dagger.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {

    Context mContext;

    public SettingsModule(Context context) {
        mContext = context;
    }

    @Provides
    Context providesContext() {
        return mContext;
    }

    @Provides
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
