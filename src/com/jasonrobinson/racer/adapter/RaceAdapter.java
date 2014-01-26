package com.jasonrobinson.racer.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import roboguice.RoboGuice;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.Race.Rule;
import com.jasonrobinson.racer.util.SettingsManager;

public class RaceAdapter extends BaseAdapter {

	private static final SimpleDateFormat DATE_FORMAT_TIME_12 = new SimpleDateFormat("h':'mm a", Locale.getDefault());
	private static final SimpleDateFormat DATE_FORMAT_TIME_24 = new SimpleDateFormat("k':'mm", Locale.getDefault());

	@Inject
	SettingsManager mSettings;

	private List<Race> mRaces;

	public RaceAdapter(Context context, List<Race> races) {

		mRaces = races;

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public int getCount() {

		return mRaces.size();
	}

	@Override
	public Race getItem(int position) {

		return mRaces.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Context context = parent.getContext();
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.race_item, parent, false);
			view.setTag(new ViewHolder(view));
		}

		Race race = getItem(position);
		ViewHolder holder = (ViewHolder) view.getTag();

		if (holder.timer != null) {
			holder.timer.cancel();
			holder.timer = null;
		}

		holder.titleTextView.setText(race.getId());

		Date startDate = null;
		Date endDate = null;
		try {
			startDate = race.getStartAt();
			endDate = race.getEndAt();
		}
		catch (ParseException e) {
			e.printStackTrace();
			onError(holder);
		}

		if (startDate != null && endDate != null) {
			long millisNow = System.currentTimeMillis();
			long millisTotal = (startDate.getTime() - millisNow) + (endDate.getTime() - startDate.getTime());

			if (millisTotal > 0) {
				holder.timer = new RaceCountDownTimer(context, holder, race, millisTotal);
				holder.timer.start();
			}
			else {
				onRaceFinished(holder);
			}
		}

		holder.descriptionTextView.setText(formatRules(race.getRules()));

		return view;
	}

	private CharSequence formatRules(Rule[] rules) {

		if (rules == null || rules.length == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rules.length; i++) {
			Rule rule = rules[i];

			if (i != 0) {
				sb.append(", ");
			}

			sb.append(rule.getName());
		}

		return sb;
	}

	private void updateTimeViews(Context context, ViewHolder holder, Race race) throws ParseException {

		CharSequence startAtTime;
		CharSequence startAtDate;

		long millisNow = System.currentTimeMillis();
		long millisUntil = race.getStartAt().getTime() - millisNow;
		long millisRemaining = race.getEndAt().getTime() - millisNow;

		if (millisUntil <= 3600000) { // <60 minutes until start
			long millis;
			if (millisUntil <= 0) { // race in progress
				long raceDuration = race.getEndAt().getTime() - race.getStartAt().getTime();
				millis = raceDuration - millisRemaining;
				startAtDate = context.getString(R.string.in_progress);
			}
			else {
				millis = millisUntil;
				startAtDate = context.getString(R.string.starting_in);
			}

			startAtTime = DateUtils.formatElapsedTime(millis / 1000);
		}
		else { // >60 minutes until start
			startAtTime = formatTime(race.getStartAt());
			startAtDate = formatDate(context, race.getStartAt());
		}

		holder.timeTextView.setText(startAtTime);
		holder.dateTextView.setText(startAtDate);

		if (race.isRegistrationOpen()) {
			holder.registerTextView.setTextColor(Color.GREEN);
			holder.registerTextView.setText(R.string.open);
		}
		else {
			holder.registerTextView.setTextColor(Color.RED);
			holder.registerTextView.setText(R.string.closed);
		}
	}

	private CharSequence formatDate(Context context, Date date) {

		Calendar now = Calendar.getInstance(Locale.getDefault());

		Calendar time = Calendar.getInstance(Locale.getDefault());
		time.setTime(date);

		int nowDoY = now.get(Calendar.DAY_OF_YEAR);
		int timeDoY = time.get(Calendar.DAY_OF_YEAR);
		if (nowDoY == timeDoY) {
			return context.getString(R.string.today);
		}
		else if (timeDoY - nowDoY == 1 || (timeDoY == 1 && (nowDoY == 365 || nowDoY == 366))) {
			return context.getString(R.string.tomorrow);
		}

		return DateFormat.getDateFormat(context).format(date);
	}

	private CharSequence formatTime(Date date) {

		if (mSettings.is24HourClock()) {
			return DATE_FORMAT_TIME_24.format(date);
		}

		return DATE_FORMAT_TIME_12.format(date);
	}

	private void onRaceFinished(ViewHolder holder) {

		holder.timeTextView.setText(R.string.finished);
		holder.dateTextView.setText(null);
		holder.registerTextView.setText(null);
	}

	private void onError(ViewHolder viewHolder) {

		viewHolder.timeTextView.setText(R.string.unknown);
		viewHolder.dateTextView.setText(null);
		viewHolder.registerTextView.setText(null);
	}

	private class RaceCountDownTimer extends CountDownTimer {

		private Context mContext;
		private ViewHolder mViewHolder;
		private Race mRace;

		public RaceCountDownTimer(Context context, ViewHolder viewHolder, Race race, long millisInFuture) {

			super(millisInFuture, 1000);
			mContext = context;
			mViewHolder = viewHolder;
			mRace = race;
		}

		@Override
		public void onFinish() {

			onRaceFinished(mViewHolder);
		}

		@Override
		public void onTick(long millisUntilFinished) {

			try {
				updateTimeViews(mContext, mViewHolder, mRace);
			}
			catch (ParseException e) {
				e.printStackTrace();
				onError(mViewHolder);
			}
		}
	}

	private class ViewHolder {

		public TextView titleTextView;
		public TextView dateTextView;
		public TextView timeTextView;
		public TextView registerTextView;
		public TextView descriptionTextView;

		public RaceCountDownTimer timer;

		public ViewHolder(View v) {

			titleTextView = (TextView) v.findViewById(R.id.title);
			dateTextView = (TextView) v.findViewById(R.id.startDate);
			timeTextView = (TextView) v.findViewById(R.id.startTime);
			registerTextView = (TextView) v.findViewById(R.id.register);
			descriptionTextView = (TextView) v.findViewById(R.id.description);
		}
	}
}
