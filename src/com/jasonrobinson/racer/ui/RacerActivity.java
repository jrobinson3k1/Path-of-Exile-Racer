package com.jasonrobinson.racer.ui;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.jasonrobinson.racer.R;

public class RacerActivity extends RoboFragmentActivity {

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

	public static class RaceTabAdapter extends FragmentPagerAdapter {

		public RaceTabAdapter(FragmentManager fm) {

			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			return UpcomingRacesFragment.newInstance();
		}

		@Override
		public int getCount() {

			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return "Races";
		}
	}
}
