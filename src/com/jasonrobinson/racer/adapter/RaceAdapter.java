package com.jasonrobinson.racer.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class RaceAdapter extends BaseAdapter {

	private static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("h':'mm a", Locale.getDefault());

	private List<Race> mRaces;

	public RaceAdapter(List<Race> races) {

		mRaces = races;
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
		}

		holder.titleTextView.setText(race.getId());

		Date startDate;
		Date endDate;
		Date registerDate;
		try {
			startDate = race.getStartAt();
			endDate = race.getEndAt();
			registerDate = race.getRegisterAt();
		}
		catch (ParseException e) {
			e.printStackTrace();
			startDate = null;
			endDate = null;
			registerDate = null;
		}

		long millisNow = System.currentTimeMillis();
		long millisTotal = (startDate.getTime() - millisNow) + endDate.getTime();

		holder.timer = new RaceCountDownTimer(context, holder, registerDate, startDate, endDate, millisTotal);
		holder.timer.start();

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

		return DATE_FORMAT_TIME.format(date);
	}

	private class ViewHolder {

		public TextView titleTextView;
		public TextView startDateTextView;
		public TextView startTimeTextView;
		public TextView registerTextView;
		public TextView descriptionTextView;

		public RaceCountDownTimer timer;

		public ViewHolder(View v) {

			titleTextView = (TextView) v.findViewById(R.id.title);
			startDateTextView = (TextView) v.findViewById(R.id.startDate);
			startTimeTextView = (TextView) v.findViewById(R.id.startTime);
			registerTextView = (TextView) v.findViewById(R.id.register);
			descriptionTextView = (TextView) v.findViewById(R.id.description);
		}
	}

	private class RaceCountDownTimer extends CountDownTimer {

		private Context mContext;
		private ViewHolder mViewHolder;
		private Date mRegisterDate;
		private Date mStartDate;
		private Date mEndDate;

		public RaceCountDownTimer(Context context, ViewHolder viewHolder, Date registerDate, Date startDate, Date endDate, long millisInFuture) {

			super(millisInFuture, 1000);
			mContext = context;
			mViewHolder = viewHolder;
			mRegisterDate = registerDate;
			mStartDate = startDate;
			mEndDate = endDate;
		}

		@Override
		public void onFinish() {

			mViewHolder.startTimeTextView.setText(R.string.finished);
			mViewHolder.startDateTextView.setText(null);
		}

		@Override
		public void onTick(long millisUntilFinished) {

			updateTimeViews(mContext, mViewHolder, mRegisterDate, mStartDate, mEndDate);
		}
	}

	private void updateTimeViews(Context context, ViewHolder holder, Date registerDate, Date startDate, Date endDate) {

		CharSequence startAtTime;
		CharSequence startAtDate;

		long millisNow = System.currentTimeMillis();
		long millisUntil = startDate.getTime() - millisNow;
		long millisRemaining = endDate.getTime() - millisNow;

		if (millisUntil <= 3600000) { // <60 minutes until start
			long millis;
			if (millisUntil <= 0) { // race in progress
				long raceDuration = endDate.getTime() - startDate.getTime();
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
			startAtTime = formatTime(startDate);
			startAtDate = formatDate(context, startDate);
		}

		holder.startTimeTextView.setText(startAtTime);
		holder.startDateTextView.setText(startAtDate);

		if (new Date(millisNow).after(registerDate)) {
			holder.registerTextView.setTextColor(Color.GREEN);
			holder.registerTextView.setText(R.string.open);
		}
		else {
			holder.registerTextView.setTextColor(Color.RED);
			holder.registerTextView.setText(R.string.closed);
		}
	}
}
