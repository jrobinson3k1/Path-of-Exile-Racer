package com.jasonrobinson.racer.ui.ladder;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseFragment;
import com.jasonrobinson.racer.util.RacerTimeUtils;
import com.jasonrobinson.racer.util.RawTypeface;

public class RaceTimeFragment extends BaseFragment {

	private DateFormat mDateFormat;
	private DateFormat mTimeFormat;

	private RemainingCountdownTimer mRemainingTimer;

	private RaceTimeCallback mRaceTimeCallback;

	@InjectView(R.id.raceName)
	private TextView mNameTextView;
	@InjectView(R.id.startTime)
	private TextView mStartTimeTextView;
	@InjectView(R.id.endTime)
	private TextView mEndTimeTextView;
	@InjectView(R.id.countdownTime)
	private TextView mCountdownTimeTextView;
	@InjectView(R.id.countdownText)
	private TextView mCountdownTextView;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mRaceTimeCallback = castActivity(RaceTimeCallback.class);
		mDateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
		mTimeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.racetime_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		mNameTextView.setTypeface(RawTypeface.obtain(getActivity(), R.raw.fontin_bold));
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		if (mRemainingTimer != null) {
			mRemainingTimer.cancel();
		}
	}

	public void setData(Race race) {

		String name = race.getRaceId();
		Date startAt = race.getStartAt();
		Date endAt = race.getEndAt();

		mNameTextView.setText(name);

		boolean includeDate = !(isToday(startAt) && isToday(endAt));
		mStartTimeTextView.setText(getFormattedTime(startAt, includeDate));
		mEndTimeTextView.setText(getFormattedTime(endAt, includeDate));

		if (mRemainingTimer != null) {
			mRemainingTimer.cancel();
		}

		long remaining = endAt.getTime() - System.currentTimeMillis();
		mRemainingTimer = new RemainingCountdownTimer(startAt.getTime(), remaining);
		mRemainingTimer.start();
	}

	private boolean isToday(Date date) {

		Calendar now = Calendar.getInstance(Locale.getDefault());

		Calendar then = Calendar.getInstance(Locale.getDefault());
		then.setTime(date);

		return now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR);
	}

	private CharSequence getFormattedTime(Date date, boolean includeDate) {

		StringBuilder sb = new StringBuilder();
		if (includeDate) {
			sb.append(mDateFormat.format(date));
			sb.append("\n");
		}

		sb.append(mTimeFormat.format(date));

		return sb;
	}

	public interface RaceTimeCallback {

		public void onRaceFinished();
	}

	private class RemainingCountdownTimer extends CountDownTimer {

		private long mStartTime;

		public RemainingCountdownTimer(long startTime, long millisInFuture) {

			super(millisInFuture, 1000);
			mStartTime = startTime;
		}

		@Override
		public void onFinish() {

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					mCountdownTimeTextView.setText(R.string.finished);
					mRaceTimeCallback.onRaceFinished();
				}
			});
		}

		@Override
		public void onTick(long millisUntilFinished) {

			long timeToStart = mStartTime - System.currentTimeMillis();
			long seconds;
			if (timeToStart > 0) {
				seconds = timeToStart / 1000;
				mCountdownTextView.setText(R.string.starting_in);
			}
			else {
				seconds = millisUntilFinished / 1000;
				mCountdownTextView.setText(R.string.remaining);
			}

			mCountdownTimeTextView.setText(RacerTimeUtils.formatElapsedTime(seconds));
		}
	}
}
