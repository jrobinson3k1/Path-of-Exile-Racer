package com.jasonrobinson.racer.ui.base;

import javax.inject.Inject;

import roboguice.RoboGuice;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.ui.settings.SettingsActivity;
import com.jasonrobinson.racer.util.CustomTypefaceSpan;
import com.jasonrobinson.racer.util.RawTypeface;
import com.jasonrobinson.racer.util.SettingsManager;

public class BaseActivityImpl {

	@Inject
	private AnalyticsManager mAnalyticsManager;
	@Inject
	private SettingsManager mSettingsManager;

	private Activity mActivity;

	private boolean mShowSettingsMenu;

	public BaseActivityImpl(Activity activity, boolean showSettingsMenu) {

		mActivity = activity;
		mShowSettingsMenu = showSettingsMenu;

		RoboGuice.injectMembers(activity, this);
	}

	public void onCreate(Bundle savedInstanceState) {

		mActivity.setTitle(mActivity.getTitle()); // ensuring formatting is
	}

	public void onStart() {

		mAnalyticsManager.onStart(mActivity);
	}

	public void onStop() {

		mAnalyticsManager.onStop(mActivity);
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		if (mShowSettingsMenu) {
			// TODO Uncomment when there's settings
			// mActivity.getMenuInflater().inflate(R.menu.settings_menu, menu);
			// return true;
		}

		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.menu_settings) {
			Intent intent = new Intent(mActivity, SettingsActivity.class);
			mActivity.startActivity(intent);
			return true;
		}

		return false;
	}

	public AnalyticsManager getAnalyticsManager() {

		return mAnalyticsManager;
	}

	public SettingsManager getSettingsManager() {

		return mSettingsManager;
	}

	public CharSequence formatTitleText(CharSequence title) {

		Typeface typeface = RawTypeface.obtain(mActivity, R.raw.fontin_regular);
		if (typeface != null) {
			SpannableString s = new SpannableString(title);
			s.setSpan(new CustomTypefaceSpan(typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			title = s;
		}

		return title;
	}
}
