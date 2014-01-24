package com.jasonrobinson.racer.ui;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.RacesFragment.RacesCallback;

public class RacerActivity extends RoboActionBarActivity implements RacesCallback {

	@InjectView(R.id.pager)
	private ViewPager mPager;
	@InjectView(R.id.tabs)
	private PagerSlidingTabStrip mTabs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.racer_activity);

		mPager.setAdapter(new RaceTabAdapter(getSupportFragmentManager()));
		mTabs.setViewPager(mPager);
	}

	@Override
	public void showUrl(String url) {

		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra(WebActivity.EXTRA_URL, url);
		startActivity(intent);
	}

	public class RaceTabAdapter extends FragmentPagerAdapter {

		public RaceTabAdapter(FragmentManager fm) {

			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			return RacesFragment.newInstance();
		}

		@Override
		public int getCount() {

			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return "Upcoming";
		}
	}
}
