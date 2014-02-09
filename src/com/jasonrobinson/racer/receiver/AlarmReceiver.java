package com.jasonrobinson.racer.receiver;

import java.text.DateFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.ladder.LadderActivity;
import com.jasonrobinson.racer.util.AlarmUtils;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String EXTRA_RACE = "race";

	@Override
	public void onReceive(Context context, Intent intent) {

		Race race = intent.getParcelableExtra(EXTRA_RACE);
		AlarmUtils.cleanUpFinishedAlarm(context, race);

		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(race.getRaceId());
		builder.setContentInfo(context.getString(R.string.starts_at, timeFormat.format(race.getStartAt())));
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		builder.setLights(context.getResources().getColor(R.color.main_color), 1000, 10000);
		builder.setAutoCancel(true);

		Intent ladderIntent = new Intent(context, LadderActivity.class);
		ladderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ladderIntent.putExtra(LadderActivity.EXTRA_ID, race.getRaceId());

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(LadderActivity.class);
		stackBuilder.addNextIntent(ladderIntent);

		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(race.getRaceId(), 0, builder.build());
	}
}
