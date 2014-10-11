package com.jasonrobinson.racer.ui.race;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.async.RaceAsyncTask;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.ladder.LadderActivity;
import com.jasonrobinson.racer.ui.race.RacesActivity.RacesPagerAdapter.RacesAdapterParams;
import com.jasonrobinson.racer.ui.race.RacesFragment.RacesCallback;
import com.jasonrobinson.racer.ui.web.WebActivity;
import com.jasonrobinson.racer.util.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class RacesActivity extends BaseActivity implements RacesCallback {

    private static final long FETCH_INTERVAL = 1000 * 60 * 60 * 24; // 24 hours

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager)
    ViewPager mPager;

    RacesTask mRacesTask;
    boolean mRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.racer_activity);
        setTitle(R.string.races);

        List<RacesAdapterParams> params = new ArrayList<RacesAdapterParams>();
        params.add(new RacesAdapterParams(RaceOptions.UNFINISHED, getString(R.string.upcoming)));
        params.add(new RacesAdapterParams(RaceOptions.FINISHED, getString(R.string.finished)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPager.setPageTransformer(true, new DepthPageTransformer());
        }

        mPager.setAdapter(new RacesPagerAdapter(getSupportFragmentManager(), params));
        mTabs.setViewPager(mPager);

        long lastFetch = getSettingsManager().getLastRaceFetch();
        long now = System.currentTimeMillis();
        if (now - lastFetch >= FETCH_INTERVAL) {
            fetchRaces();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRacesTask != null) {
            mRacesTask.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);

        if (!mRefreshing) {
            MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
            MenuItemCompat.setActionView(refreshItem, null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            fetchRaces();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
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
        intent.putExtra(LadderActivity.EXTRA_ID, race.getRaceId());

        startActivity(intent);
    }

    private void fetchRaces() {
        if (mRacesTask != null) {
            mRacesTask.cancel(true);
        }

        mRacesTask = new RacesTask();
        mRacesTask.execute();
    }

    private void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;
        supportInvalidateOptionsMenu();
    }

    public static class RacesPagerAdapter extends FragmentPagerAdapter {

        private List<RacesAdapterParams> mParams;

        public RacesPagerAdapter(FragmentManager fm, List<RacesAdapterParams> params) {

            super(fm);
            mParams = params;
        }

        @Override
        public RacesFragment getItem(int position) {
            return RacesFragment.newInstance(mParams.get(position).option);
        }

        @Override
        public int getCount() {
            return mParams.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mParams.get(position).title;
        }

        public static class RacesAdapterParams {

            public RaceOptions option;
            public String title;

            public RacesAdapterParams(RaceOptions option, String title) {
                this.option = option;
                this.title = title;
            }
        }
    }

    private class RacesTask extends RaceAsyncTask {

        public RacesTask() {
            super(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Race> result) {
            super.onPostExecute(result);
            setRefreshing(false);
            getSettingsManager().updateLastRaceFetch();

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof RacesFragment && fragment.isVisible()) {
                    ((RacesFragment) fragment).refresh();
                }
            }
        }
    }
}
