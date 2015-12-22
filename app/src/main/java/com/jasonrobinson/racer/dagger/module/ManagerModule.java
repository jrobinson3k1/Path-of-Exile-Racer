package com.jasonrobinson.racer.dagger.module;

import android.app.AlarmManager;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class ManagerModule {

    @Provides
    public AlarmManager providesAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
}
