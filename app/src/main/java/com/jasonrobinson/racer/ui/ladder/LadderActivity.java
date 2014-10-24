package com.jasonrobinson.racer.ui.ladder;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.WindowManager;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.ClassSpinnerAdapter;
import com.jasonrobinson.racer.enumeration.PoeClass;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.ladder.RaceTimeFragment.RaceTimeCallback;
import com.metova.slim.annotation.Extra;
import com.metova.slim.annotation.Layout;

import java.util.Timer;
import java.util.TimerTask;

@Layout(R.layout.ladder_activity)
public class LadderActivity extends BaseActivity implements RaceTimeCallback {

    private static final long DISABLE_REFRESH_EXTENSION = 1000 * 60 * 5; // 5 minutes

    public static final String EXTRA_ID = "com.jasonrobinson.racer.id";

    RaceTimeFragment mRaceTimeFragment;
    LadderFragment mLadderFragment;

    private Timer mDisableRefreshTimer;

    private ClassSpinnerAdapter mNavAdapter;

    @Extra(EXTRA_ID)
    private String mId;

    private Race mRace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRaceTimeFragment = (RaceTimeFragment) getSupportFragmentManager().findFragmentById(R.id.raceTime_fragment);
        mLadderFragment = (LadderFragment) getSupportFragmentManager().findFragmentById(R.id.ladder_fragment);

        mRace = getDatabaseManager().getRace(mId);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        mNavAdapter = new ClassSpinnerAdapter(actionBar.getThemedContext(), PoeClass.values(), true);

        actionBar.setListNavigationCallbacks(mNavAdapter, new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int position, long id) {
                PoeClass poeClass = mNavAdapter.getItem(position);
                mLadderFragment.fetchLadder(mId, poeClass);

                getAnalyticsManager().trackEvent("Ladder", "Filter", poeClass == null ? "All" : poeClass.toString());

                return true;
            }
        });

        mRaceTimeFragment.setData(mRace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAutoRefreshEnabled(getSettingsManager().isAutoRefreshEnabled());
        keepScreenOn(getSettingsManager().isKeepScreenOn());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDisableRefreshTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ladder_menu, menu);

        return true;
    }

    @Override
    public void onRaceFinished() {
        cancelDisableRefreshTimer();
        if (isDelayedRaceFinished()) {
            onDelayedRaceFinished();
        } else {
            mDisableRefreshTimer = new Timer();
            mDisableRefreshTimer.schedule(new DisableRefreshTimerTask(), getTimeUntilDelayedFinish());
        }
    }

    private void onDelayedRaceFinished() {
        setAutoRefreshEnabled(false);
        setRefreshEnabled(false);
    }

    private void cancelDisableRefreshTimer() {
        if (mDisableRefreshTimer != null) {
            mDisableRefreshTimer.cancel();
            mDisableRefreshTimer.purge();
        }
    }

    private long getTimeUntilDelayedFinish() {
        return (mRace.getEndAt().getTime() + DISABLE_REFRESH_EXTENSION) - System.currentTimeMillis();
    }

    private boolean isDelayedRaceFinished() {
        return getTimeUntilDelayedFinish() < 0;
    }

    private void setAutoRefreshEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mLadderFragment != null) {
                    if (isDelayedRaceFinished()) {
                        mLadderFragment.setAutoRefreshEnabled(false);
                    } else {
                        mLadderFragment.setAutoRefreshEnabled(enabled);
                    }
                }
            }
        });
    }

    private void setRefreshEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mLadderFragment != null) {
                    mLadderFragment.setRefreshEnabled(enabled);
                }
            }
        });
    }

    private void keepScreenOn(final boolean keepScreenOn) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                if (keepScreenOn) {
                    getWindow().addFlags(flag);
                } else {
                    getWindow().clearFlags(flag);
                }
            }
        });
    }

    private class DisableRefreshTimerTask extends TimerTask {

        @Override
        public void run() {
            onDelayedRaceFinished();
        }
    }
}
