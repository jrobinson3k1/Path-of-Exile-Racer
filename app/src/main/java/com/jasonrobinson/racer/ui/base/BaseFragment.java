package com.jasonrobinson.racer.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;
import com.metova.slim.SlimFragment;
import com.trello.rxlifecycle.FragmentEvent;

import rx.Observable;

public abstract class BaseFragment extends SlimFragment {

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
        mImpl.onDetach();
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

    public final Observable<FragmentEvent> lifecycle() {
        return mImpl.lifecycle();
    }

    public final <T> Observable.Transformer<T, T> bindUntilEvent(FragmentEvent event) {
        return mImpl.bindUntilEvent(event);
    }

    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return mImpl.bindToLifecycle();
    }
}
