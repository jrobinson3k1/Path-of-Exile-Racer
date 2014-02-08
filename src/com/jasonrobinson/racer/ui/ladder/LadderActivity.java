package com.jasonrobinson.racer.ui.ladder;

import java.util.Timer;
import java.util.TimerTask;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.ClassSpinnerAdapter;
import com.jasonrobinson.racer.enumeration.PoeClass;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.ladder.RaceTimeFragment.RaceTimeCallback;

public class LadderActivity extends BaseActivity implements RaceTimeCallback {

	private static final long DISABLE_REFRESH_EXTENSION = 1000 * 60 * 5; // 5
																			// minutes

	public static final String EXTRA_ID = "id";

	@InjectFragment(tag = "raceTime_fragment")
	RaceTimeFragment mRaceTimeFragment;
	@InjectFragment(tag = "ladder_fragment")
	LadderFragment mLadderFragment;

	private Timer mDisableRefreshTimer;

	private ClassSpinnerAdapter mNavAdapter;

	@InjectExtra(value = EXTRA_ID)
	private String mId;
	private Race mRace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ladder_activity);

		mRace = getDatabaseManager().getRace(mId);
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

		boolean enabled = getSettingsManager().isAutoRefreshEnabled();
		setAutoRefreshEnabled(enabled, false);

		mRaceTimeFragment.setData(mRace);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		cancelDisableRefreshTimer();
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
		setAutoRefreshEnabled(checked, false);

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
			setAutoRefreshEnabled(item.isChecked(), true);

			getAnalyticsManager().trackEvent("Ladder", item.isChecked() ? "Enable" : "Disable", "Auto Refresh");
		}
		else {
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void onRaceFinished() {

		cancelDisableRefreshTimer();
		if (isDelayedRaceFinished()) {
			onDelayedRaceFinished();
		}
		else {
			mDisableRefreshTimer = new Timer();
			mDisableRefreshTimer.schedule(new DisableRefreshTimerTask(), getTimeUntilDelayedFinish());
		}
	}

	private void onDelayedRaceFinished() {

		setAutoRefreshEnabled(false, false);
		setRefreshEnabled(false);
	}

	private void cancelDisableRefreshTimer() {

		if (mDisableRefreshTimer != null) {
			mDisableRefreshTimer.cancel();
			mDisableRefreshTimer.purge();
		}
	}

	private long getTimeUntilDelayedFinish() {

		return (mRace.getEndAt().getTime() + DISABLE_REFRESH_EXTENSION) - System.currentTimeMillis();
	}

	private boolean isDelayedRaceFinished() {

		return getTimeUntilDelayedFinish() < 0;
	}

	private void setAutoRefreshEnabled(final boolean enabled, final boolean save) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (mLadderFragment != null) {
					if (isDelayedRaceFinished()) {
						mLadderFragment.setAutoRefreshEnabled(false);
					}
					else {
						mLadderFragment.setAutoRefreshEnabled(enabled);
					}
				}

				if (save) {
					getSettingsManager().setAutoRefresh(enabled);
				}
			}
		});
	}

	private void setRefreshEnabled(final boolean enabled) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (mLadderFragment != null) {
					mLadderFragment.setRefreshEnabled(enabled);
				}
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

	private class DisableRefreshTimerTask extends TimerTask {

		@Override
		public void run() {

			onDelayedRaceFinished();
		}
	}
}
