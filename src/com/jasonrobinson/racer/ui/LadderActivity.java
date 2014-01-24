package com.jasonrobinson.racer.ui;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectFragment;
import android.os.Bundle;

import com.jasonrobinson.racer.R;

public class LadderActivity extends RoboActionBarActivity {

	public static final String EXTRA_ID = "id";

	@InjectFragment(R.id.ladder_fragment)
	LadderFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ladder_activity);

		String id = getIntent().getStringExtra(EXTRA_ID);
		if (id == null) {
			throw new IllegalArgumentException("Id is missing");
		}

		mFragment.fetchLadder(id);
	}
}
