package com.jasonrobinson.racer.ui.base;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.util.SettingsManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import rx.Observable;
import rx.android.lifecycle.LifecycleEvent;
import rx.android.lifecycle.LifecycleObservable;
import rx.subjects.BehaviorSubject;

public class BaseFragmentImpl {

    private final BehaviorSubject<LifecycleEvent> mLifecycleSubject = BehaviorSubject.create();

    private AnalyticsManager mAnalyticsManager;

    private SettingsManager mSettingsManager;

    private DatabaseManager mDatabaseManager;

    private Fragment mFragment;

    public BaseFragmentImpl(Fragment fragment) {
        mFragment = fragment;

        mAnalyticsManager = GraphHolder.getInstance().get(AnalyticsManager.class);
        mSettingsManager = GraphHolder.getInstance().get(SettingsManager.class);
        mDatabaseManager = GraphHolder.getInstance().get(DatabaseManager.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        mLifecycleSubject.onNext(LifecycleEvent.CREATE);
    }

    public void onAttach(Activity activity) {
        mLifecycleSubject.onNext(LifecycleEvent.ATTACH);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLifecycleSubject.onNext(LifecycleEvent.CREATE_VIEW);
        ButterKnife.inject(mFragment, view);
    }

    public void onStart() {
        mLifecycleSubject.onNext(LifecycleEvent.START);
    }

    public void onResume() {
        mLifecycleSubject.onNext(LifecycleEvent.RESUME);
    }

    public void onPause() {
        mLifecycleSubject.onNext(LifecycleEvent.PAUSE);
    }

    public void onStop() {
        mLifecycleSubject.onNext(LifecycleEvent.STOP);
    }

    public void onDestroyView() {
        mLifecycleSubject.onNext(LifecycleEvent.DESTROY_VIEW);
    }

    public void onDetatch() {
        mLifecycleSubject.onNext(LifecycleEvent.DETACH);
    }

    public void onDestroy() {
        mLifecycleSubject.onNext(LifecycleEvent.DESTROY);
    }

    public AnalyticsManager getAnalyticsManager() {
        return mAnalyticsManager;
    }

    public SettingsManager getSettingsManager() {
        return mSettingsManager;
    }

    public DatabaseManager getdDatabaseManager() {
        return mDatabaseManager;
    }

    public Observable<LifecycleEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    public <T> Observable<T> bindLifecycle(Observable<T> source) {
        return LifecycleObservable.bindUntilLifecycleEvent(lifecycle(), source, LifecycleEvent.DESTROY_VIEW);
    }
}
