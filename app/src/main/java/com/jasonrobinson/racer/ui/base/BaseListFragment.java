package com.jasonrobinson.racer.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;

import rx.Observable;
import rx.android.lifecycle.LifecycleEvent;

public class BaseListFragment extends ListFragment {

    BaseFragmentImpl mImpl = new BaseFragmentImpl(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImpl.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mImpl.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImpl.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mImpl.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mImpl.onResume();
    }

    @Override
    public void onPause() {
        mImpl.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mImpl.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mImpl.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mImpl.onDetatch();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mImpl.onDestroy();
        super.onDestroy();
    }

    public AnalyticsManager getAnalyticsManager() {
        return mImpl.getAnalyticsManager();
    }

    public SettingsManager getSettingsManager() {
        return mImpl.getSettingsManager();
    }

    public DatabaseManager getDatabaseManager() {
        return mImpl.getdDatabaseManager();
    }

    public Observable<LifecycleEvent> lifecycle() {
        return mImpl.lifecycle();
    }

    public <T> Observable<T> bindLifecycle(Observable<T> source) {
        return mImpl.bindLifecycle(source);
    }
}
