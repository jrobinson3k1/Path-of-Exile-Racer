package com.jasonrobinson.racer.ui.ladder;

import java.util.Date;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseActivity;

public class LadderActivity extends BaseActivity {

	public static final String EXTRA_ID = "id";
	public static final String EXTRA_START_AT = "startAt";
	public static final String EXTRA_END_AT = "endAt";

	@InjectFragment(tag = "raceTime_fragment")
	RaceTimeFragment mRaceTimeFragment;
	@InjectFragment(tag = "ladder_fragment")
	LadderFragment mLadderFragment;

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

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle(R.string.ladder);

		mRaceTimeFragment.setData(mId, new Date(mStartAt), new Date(mEndAt));
		mLadderFragment.fetchLadder(mId, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.ladder_menu, menu);

		boolean checked = getSettingsManager().isKeepScreenOn();
		menu.findItem(R.id.menu_keep_screen_on).setChecked(checked);
		keepScreenOn(checked);

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
		}
		else {
			return super.onOptionsItemSelected(item);
		}

		return true;
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
}
