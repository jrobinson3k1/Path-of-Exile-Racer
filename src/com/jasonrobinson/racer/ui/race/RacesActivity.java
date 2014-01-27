package com.jasonrobinson.racer.ui.race;

import java.text.ParseException;

import roboguice.inject.InjectFragment;
import android.content.Intent;
import android.os.Bundle;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.ladder.LadderActivity;
import com.jasonrobinson.racer.ui.race.RacesFragment.RacesCallback;
import com.jasonrobinson.racer.ui.web.WebActivity;

public class RacesActivity extends BaseActivity implements RacesCallback {

	@InjectFragment(tag = "races_fragment")
	RacesFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.racer_activity);
		setTitle(R.string.races);
	}

	@Override
	public void showUrl(String url) {

		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra(WebActivity.EXTRA_URL, url);
		startActivity(intent);
	}

	@Override
	public void showLadder(Race race) {

		Intent intent = new Intent(this, LadderActivity.class);
		intent.putExtra(LadderActivity.EXTRA_ID, race.getId());
		try {
			intent.putExtra(LadderActivity.EXTRA_START_AT, race.getStartAt().getTime());
			intent.putExtra(LadderActivity.EXTRA_END_AT, race.getEndAt().getTime());
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}

		startActivity(intent);
	}
}
