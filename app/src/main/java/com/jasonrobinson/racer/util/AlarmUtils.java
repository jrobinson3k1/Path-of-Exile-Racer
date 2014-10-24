package com.jasonrobinson.racer.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.receiver.AlarmReceiver;

import java.sql.Date;

public class AlarmUtils {

    private AlarmUtils() {

    }

    public static void addAlarm(Context context, Race race, long delay) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = getPendingIntentForRace(context, race, PendingIntent.FLAG_UPDATE_CURRENT);
        long time = race.getStartAt().getTime() - delay;
        am.set(AlarmManager.RTC_WAKEUP, time, operation);

        Date date = new Date(time);
        Toast.makeText(context, context.getString(R.string.notification_scheduled, DateFormat.getTimeFormat(context).format(date), DateFormat.getDateFormat(context).format(date)), Toast.LENGTH_LONG).show();
    }

    public static void cancelAlarm(Context context, Race race) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = getPendingIntentForRace(context, race, PendingIntent.FLAG_NO_CREATE);
        if (operation != null) {
            am.cancel(operation);
            operation.cancel();
        }
    }

    public static void cleanUpFinishedAlarm(Context context, Race race) {

        PendingIntent operation = getPendingIntentForRace(context, race, PendingIntent.FLAG_NO_CREATE);
        if (operation != null) {
            operation.cancel();
        }
    }

    public static boolean isAlarmAdded(Context context, Race race) {

        return getPendingIntentForRace(context, race, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private static PendingIntent getPendingIntentForRace(Context context, Race race, int flags) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setData(Uri.parse(race.getRaceId()));
        intent.putExtra(AlarmReceiver.EXTRA_RACE, race);
        return PendingIntent.getBroadcast(context, race.getRaceId().hashCode(), intent, flags);
    }
}
