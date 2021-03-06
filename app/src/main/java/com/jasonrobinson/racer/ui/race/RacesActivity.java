package com.jasonrobinson.racer.ui.race;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.RaceGridPagerAdapter;
import com.jasonrobinson.racer.adapter.RaceListPagerAdapter;
import com.jasonrobinson.racer.async.RaceAsyncTask;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.RaceMode;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.ladder.LadderActivity;
import com.jasonrobinson.racer.ui.race.RaceListFragment.RacesCallback;
import com.jasonrobinson.racer.ui.web.WebActivity;
import com.jasonrobinson.racer.util.DepthPageTransformer;
import com.metova.slim.annotation.Layout;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.races_activity)
public class RacesActivity extends BaseActivity implements RacesCallback {

    private static final long FETCH_INTERVAL = 1000 * 60 * 60 * 24; // 24 hours

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager)
    ViewPager mPager;

    RaceListPagerAdapter mListAdapter;
    RaceGridPagerAdapter mGridAdapter;

    RacesTask mRacesTask;

    boolean mRefreshing;
    RaceMode mRaceMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toolbar toolbar = new Toolbar(this);
//        toolbar.setTitle(R.string.races);
//
//        setSupportActionBar(toolbar);

        List<RaceListPagerAdapter.RaceListParams> params = new ArrayList<RaceListPagerAdapter.RaceListParams>();
        params.add(new RaceListPagerAdapter.RaceListParams(RaceOptions.UNFINISHED, getString(R.string.upcoming)));
        params.add(new RaceListPagerAdapter.RaceListParams(RaceOptions.FINISHED, getString(R.string.finished)));

        mListAdapter = new RaceListPagerAdapter(getSupportFragmentManager(), params);

        mRaceMode = getSettingsManager().getRaceMode();
        if (mRaceMode == RaceMode.LIST) {
            showList();
        } else if (mRaceMode == RaceMode.CALENDAR) {
            showGrid();
        }

        mPager.setPageTransformer(true, new DepthPageTransformer());
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
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        // TODO: Implement calendar view
//        getMenuInflater().inflate(R.menu.races_menu, menu);

        if (!mRefreshing) {
            MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
            MenuItemCompat.setActionView(refreshItem, null);
        }

        if (mRaceMode == RaceMode.LIST) {
            menu.removeItem(R.id.menu_list);
        } else if (mRaceMode == RaceMode.CALENDAR) {
            menu.removeItem(R.id.menu_grid);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                fetchRaces();
                break;
            case R.id.menu_list:
                showList();
                break;
            case R.id.menu_grid:
                showGrid();
                break;
            default:
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

    private void showList() {
        mRaceMode = RaceMode.LIST;
        mPager.setAdapter(mListAdapter);
        mTabs.setVisibility(View.VISIBLE);

        setRaceMode(RaceMode.LIST);
    }

    private void showGrid() {
        mPager.setAdapter(mGridAdapter);
        mTabs.setVisibility(View.GONE);

        setRaceMode(RaceMode.CALENDAR);
    }

    private void setRaceMode(RaceMode mode) {
        mRaceMode = mode;
        getSettingsManager().setRaceMode(mRaceMode);
        supportInvalidateOptionsMenu();
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
                if (fragment instanceof RaceListFragment && fragment.isVisible()) {
                    ((RaceListFragment) fragment).refresh();
                }
            }
        }
    }
}
