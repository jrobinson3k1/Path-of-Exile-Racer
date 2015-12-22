package com.jasonrobinson.racer.receiver;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.dagger.ComponentHolder;
import com.jasonrobinson.racer.manager.RacesManager;
import com.jasonrobinson.racer.model.Race;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateFormat;

import javax.inject.Inject;

public class RaceAlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_NAME = "name";

    @Inject
    RacesManager mRacesManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentHolder.getInstance().component().inject(this);
        handleAlarm(context, intent);
    }

    private void handleAlarm(Context context, Intent intent) {
        String name = intent.getStringExtra(EXTRA_NAME);
        mRacesManager.getRace(name).subscribe(race -> showAlarmNotification(context, race));
    }

    private void showAlarmNotification(Context context, Race race) {
        String date = DateFormat.getTimeFormat(context).format(race.getStartAt());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_run_white_24dp)
                .setColor(context.getResources().getColor(R.color.primary))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_message, race.getName(), date))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(context.getResources().getColor(R.color.primary), 1000, 10000)
                .setAutoCancel(true);

        // TODO: Add pending intent to display race ladder

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(race.getName(), 0, builder.build());

        race.setAlarm(null);
    }
}
