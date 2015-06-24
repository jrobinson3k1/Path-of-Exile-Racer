package com.jasonrobinson.racer.ui.ladder;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.LadderAdapter;
import com.jasonrobinson.racer.enumeration.PoEClass;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.WatchType;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.ui.base.BaseListFragment;
import com.jasonrobinson.racer.ui.ladder.WatchCharacterDialogFragment.WatchCharacterDialogListener;
import com.jasonrobinson.racer.util.LadderUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class LadderFragment extends BaseListFragment {

    private static final String TAG = LadderFragment.class.getSimpleName();

    private static final String EXTRA_ID = "id";

    private static final String TAG_WATCH_CHARACTER = "watchCharacter";

    @Inject
    LadderManager mLadderManager;

    private Race mRace;

    private String mWatchedName;

    private PoEClass mWatchedCharacterClass;

    private PoEClass mWatchedClass;

    private WatchType mWatchedType;

    private Timer mAutoRefreshTimer;

    private boolean mRefreshing;

    private LadderAdapter mAdapter;

    private boolean mTracked;

    private boolean mRefreshEnabled = true;

    private boolean mAutoRefreshEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GraphHolder.getInstance().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.no_rankings));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            String id = getArguments().getString(EXTRA_ID);
            if (id != null) {
                mRace = getDatabaseManager().getRace(id);
                fetchLadder();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRace != null) {
            fetchLadder();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelAutoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelAutoRefresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh_menu, menu);

        MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
        refreshItem.setEnabled(mRefreshEnabled);
        if (!mRefreshing) {
            MenuItemCompat.setActionView(refreshItem, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            fetchLadder();

            getAnalyticsManager().trackEvent("Ladder", "Refresh", "Manual");
        } else if (id == R.id.menu_watch_character) {
            showCharacterDialog();
        } else {
            super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void showCharacterDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_WATCH_CHARACTER);
        if (prev != null) {
            ft.remove(prev);
        }

        WatchCharacterDialogFragment fragment = WatchCharacterDialogFragment.newInstance(mWatchedName, mWatchedType);
        fragment.setWatchCharacterDialogListener(new WatchCharacterDialogListener() {

            @Override
            public void onRemove() {
                getAnalyticsManager().trackEvent("Ladder", "Remove", "Character Watcher");
                mWatchedName = null;
                mWatchedCharacterClass = null;
                fetchLadder();
            }

            @Override
            public void onNameSelected(String name, WatchType type) {
                getAnalyticsManager().trackEvent("Ladder", "Use", "Character Watcher");
                getAnalyticsManager().trackEvent("Ladder", "Character Watcher", type.name());
                mWatchedName = name;
                mWatchedType = type;

                getListView().smoothScrollToPosition(0);

                fetchLadder();
            }

            @Override
            public void onCancel() {
                // no-op
            }
        });

        fragment.show(ft, TAG_WATCH_CHARACTER);
    }

    private void fetchLadder() {
        fetchLadder(mRace.getRaceId(), mWatchedClass);
    }

    public void fetchLadder(String id, PoEClass poEClass) {
        if (mRace == null || !mRace.getRaceId().equals(id)) {
            mRace = getDatabaseManager().getRace(id);
        }

        mWatchedClass = poEClass;

        Observable.just(mWatchedClass)
                .flatMap(watchedClass -> {
                    if (watchedClass == null) {
                        return mLadderManager.fetchLadder(mRace.getRaceId(), 0, 200);
                    } else {
                        return mLadderManager.fetchLadderForClass(mRace.getRaceId(), 50, watchedClass);
                    }
                })
                .doOnSubscribe(() -> setRefreshing(true))
                .doOnTerminate(() -> setRefreshing(false))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onLadderReceived,
                        throwable -> Toast.makeText(getActivity(), R.string.error_unavailable, Toast.LENGTH_LONG).show(),
                        () -> setListShown(true)
                );

        if (!mTracked) {
            mTracked = true;
            getAnalyticsManager().trackEvent("Ladder", "View", id);
        }
    }

    private void cancelAutoRefresh() {
        if (mAutoRefreshTimer != null) {
            mAutoRefreshTimer.cancel();
            mAutoRefreshTimer.purge();
        }
    }

    private void startAutoRefresh() {
        cancelAutoRefresh();
        mAutoRefreshTimer = new Timer();
        mAutoRefreshTimer.schedule(new RefreshTimerTask(), 30000);
    }

    private void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;
        getActivity().supportInvalidateOptionsMenu();

        if (refreshing) {
            cancelAutoRefresh();
        } else {
            if (mAutoRefreshEnabled) {
                startAutoRefresh();
            }
        }
    }

    public void setAutoRefreshEnabled(boolean enabled) {
        if (mAutoRefreshEnabled == enabled) {
            return;
        }

        mAutoRefreshEnabled = enabled;

        if (mAutoRefreshEnabled) {
            startAutoRefresh();
        } else {
            cancelAutoRefresh();
        }
    }

    public void setRefreshEnabled(boolean enabled) {
        if (mRefreshEnabled == enabled) {
            return;
        }

        mRefreshEnabled = enabled;
        getActivity().supportInvalidateOptionsMenu();
    }

    protected void onLadderReceived(List<Ladder.Entry> entries) {
        Entry watchedEntry = null;
        if (!TextUtils.isEmpty(mWatchedName)) {
            watchedEntry = LadderUtils.findEntry(entries, mWatchedName, mWatchedType);
            if (watchedEntry == null && mWatchedCharacterClass == null) {
                Toast.makeText(getActivity(), getString(R.string.character_not_found, mWatchedName), Toast.LENGTH_LONG).show();
                mWatchedName = null;
            } else {
                if (watchedEntry != null) {
                    mWatchedCharacterClass = watchedEntry.getCharacter().getPoeClass();
                }
            }
        }

        boolean borderFirstItem = !TextUtils.isEmpty(mWatchedName) && watchedEntry != null;
        if (mAdapter == null) {
            mAdapter = new LadderAdapter(entries, mWatchedClass != null, borderFirstItem);
            setListAdapter(mAdapter);
        } else {
            mAdapter.setBorderFirstItem(borderFirstItem);
            mAdapter.setEntries(entries, mWatchedClass != null);
        }

        setListShown(true);
    }

    private class RefreshTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mRace != null) {
                getActivity().runOnUiThread(() -> {
                    fetchLadder();
                    getAnalyticsManager().trackEvent("Ladder", "Refresh", "Automatic");
                });
            }
        }
    }
}
