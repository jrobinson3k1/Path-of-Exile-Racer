package com.jasonrobinson.racer.ui.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BasePreferenceActivity;

public class SettingsActivity extends BasePreferenceActivity {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		setTitle(R.string.settings);

		// TODO: Add fragment implementation side-by-side
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		else {
			return super.onOptionsItemSelected(item);
		}

		return true;
	}
}
