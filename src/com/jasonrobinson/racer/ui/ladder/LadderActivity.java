package com.jasonrobinson.racer.ui.ladder;

import java.util.Date;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.PoeClass;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.util.RawTypeface;

public class LadderActivity extends BaseActivity {

	public static final String EXTRA_ID = "id";
	public static final String EXTRA_START_AT = "startAt";
	public static final String EXTRA_END_AT = "endAt";

	@InjectFragment(tag = "raceTime_fragment")
	RaceTimeFragment mRaceTimeFragment;
	@InjectFragment(tag = "ladder_fragment")
	LadderFragment mLadderFragment;

	private ClassSpinnerAdapter mNavAdapter;

	@InjectExtra(value = EXTRA_ID)
	private String mId;
	@InjectExtra(value = EXTRA_START_AT)
	private long mStartAt;
	@InjectExtra(value = EXTRA_END_AT)
	private long mEndAt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ladder_activity);

		mNavAdapter = new ClassSpinnerAdapter(PoeClass.values(), true);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mNavAdapter, new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int position, long id) {

				PoeClass poeClass = mNavAdapter.getItem(position);
				mLadderFragment.fetchLadder(mId, poeClass);

				getAnalyticsManager().trackEvent("Ladder", "Filter", poeClass == null ? "All" : poeClass.toString());

				return true;
			}
		});

		mRaceTimeFragment.setData(mId, new Date(mStartAt), new Date(mEndAt));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.ladder_menu, menu);

		boolean checked = getSettingsManager().isKeepScreenOn();
		menu.findItem(R.id.menu_keep_screen_on).setChecked(checked);
		keepScreenOn(checked);

		checked = getSettingsManager().isAutoRefreshEnabled();
		menu.findItem(R.id.menu_auto_refresh).setChecked(checked);
		setAutoRefresh(checked);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		else if (id == R.id.menu_keep_screen_on) {
			item.setChecked(!item.isChecked());
			keepScreenOn(item.isChecked());

			getAnalyticsManager().trackEvent("Ladder", item.isChecked() ? "Enable" : "Disable", "Keep Screen On");
		}
		else if (id == R.id.menu_auto_refresh) {
			item.setChecked(!item.isChecked());
			setAutoRefresh(item.isChecked());

			getAnalyticsManager().trackEvent("Ladder", item.isChecked() ? "Enable" : "Disable", "Auto Refresh");
		}
		else {
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	private void setAutoRefresh(final boolean enabled) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				mLadderFragment.setAutoRefresh(enabled);
				getSettingsManager().setAutoRefresh(enabled);
			}
		});
	}

	private void keepScreenOn(final boolean keepScreenOn) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				if (keepScreenOn) {
					getWindow().addFlags(flag);
				}
				else {
					getWindow().clearFlags(flag);
				}

				getSettingsManager().setKeepScreenOn(keepScreenOn);
			}
		});
	}

	private class ClassSpinnerAdapter extends BaseAdapter {

		PoeClass[] mPoeClasses;
		boolean mShowAll;

		public ClassSpinnerAdapter(PoeClass[] poeClasses, boolean showAll) {

			mPoeClasses = poeClasses;
			mShowAll = showAll;
		}

		@Override
		public int getCount() {

			return mPoeClasses.length + (mShowAll ? 1 : 0);
		}

		@Override
		public PoeClass getItem(int position) {

			if (mShowAll && position == 0) {
				return null;
			}

			return mPoeClasses[mShowAll ? position - 1 : position];
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {

			return getView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			return getView(position, convertView, parent, android.R.layout.simple_spinner_item);
		}

		private View getView(int position, View convertView, ViewGroup parent, int layoutResId) {

			View v = convertView;
			TextView textView;
			if (v == null) {
				v = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
				textView = (TextView) v.findViewById(android.R.id.text1);
				textView.setTextColor(Color.WHITE);
				textView.setTypeface(RawTypeface.obtain(parent.getContext(), R.raw.fontin_regular));
			}
			else {
				textView = (TextView) v.findViewById(android.R.id.text1);
			}

			PoeClass poeClass = getItem(position);

			textView.setText(poeClass == null ? parent.getContext().getString(R.string.all) : poeClass.toString());

			return v;
		}
	}
}
