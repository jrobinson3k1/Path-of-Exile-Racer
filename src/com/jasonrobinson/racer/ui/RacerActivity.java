package com.jasonrobinson.racer.ui;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectFragment;
import android.content.Intent;
import android.os.Bundle;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.RacesFragment.RacesCallback;

public class RacerActivity extends RoboActionBarActivity implements RacesCallback {

	@InjectFragment(R.id.races_fragment)
	RacesFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.racer_activity);
	}

	@Override
	public void showUrl(String url) {

		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra(WebActivity.EXTRA_URL, url);
		startActivity(intent);
	}

	@Override
	public void showLadder(String id) {

		Intent intent = new Intent(this, LadderActivity.class);
		intent.putExtra(LadderActivity.EXTRA_ID, id);
		startActivity(intent);
	}
}
