package com.jasonrobinson.racer.manager;

import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.receiver.RaceAlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RaceAlarmManager {

    private AlarmManager mAlarmManager;

    @Inject
    RaceAlarmManager(AlarmManager alarmManager) {
        mAlarmManager = alarmManager;
    }

    public void schedule(Context context, Calendar cal, Race race) {
        Bundle extras = new Bundle();
        extras.putString(RaceAlarmReceiver.EXTRA_NAME, race.getName());

        PendingIntent pi = createPendingIntent(context, race, extras, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        race.setAlarm(cal.getTime());
    }

    public void cancel(Context context, Race race) {
        mAlarmManager.cancel(createPendingIntent(context, race, null, 0));
        race.setAlarm(null);
    }

    private PendingIntent createPendingIntent(Context context, Race race, Bundle extras, int flags) {
        Intent intent = new Intent(context, RaceAlarmReceiver.class);
        if (extras != null) {
            intent.putExtras(extras);
        }

        return PendingIntent.getBroadcast(context, (int) race.getId(), intent, flags);
    }
}
