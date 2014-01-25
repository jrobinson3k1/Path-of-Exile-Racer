package com.jasonrobinson.racer.ui.base;

import javax.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.util.CustomTypefaceSpan;
import com.jasonrobinson.racer.util.RawTypeface;

public class BaseActivity extends RoboActionBarActivity {

	@Inject
	private AnalyticsManager mAnalyticsManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTitle(getTitle()); // ensuring formatting is applied
	}

	@Override
	protected void onStart() {

		super.onStart();
		mAnalyticsManager.onStart(this);
	}

	@Override
	protected void onStop() {

		super.onStop();
		mAnalyticsManager.onStop(this);
	}

	public AnalyticsManager getAnalyticsManager() {

		return mAnalyticsManager;
	}

	@Override
	public void setTitle(int titleId) {

		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {

		super.setTitle(formatTitleText(title));
	}

	private CharSequence formatTitleText(CharSequence title) {

		Typeface typeface = RawTypeface.obtain(this, R.raw.fontin_regular);
		if (typeface != null) {
			SpannableString s = new SpannableString(title);
			s.setSpan(new CustomTypefaceSpan(typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			title = s;
		}

		return title;
	}
}
