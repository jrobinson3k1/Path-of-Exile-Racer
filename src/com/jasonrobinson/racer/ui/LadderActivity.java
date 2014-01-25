package com.jasonrobinson.racer.ui;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.jasonrobinson.racer.R;

public class LadderActivity extends RoboActionBarActivity {

	public static final String EXTRA_ID = "id";
	public static final String EXTRA_TITLE = "title";

	@InjectFragment(R.id.ladder_fragment)
	LadderFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ladder_activity);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String id = getIntent().getStringExtra(EXTRA_ID);
		if (id == null) {
			throw new IllegalArgumentException("Id is missing");
		}

		setTitle(id);
		mFragment.fetchLadder(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.ladder_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.menu_keep_screen_on:
				item.setChecked(!item.isChecked());
				keepScreenOn(item.isChecked());
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		return true;
	}

	private void keepScreenOn(boolean keepScreenOn) {

		int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		if (keepScreenOn) {
			getWindow().addFlags(flag);
		}
		else {
			getWindow().clearFlags(flag);
		}
	}
}
