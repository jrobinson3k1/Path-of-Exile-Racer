package com.jasonrobinson.racer.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.util.SettingsManager;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;

import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseFragmentImpl {

    private final BehaviorSubject<FragmentEvent> mLifecycleSubject = BehaviorSubject.create();

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
        mLifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    public void onAttach(Activity activity) {
        mLifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        ButterKnife.inject(mFragment, view);
    }

    public void onStart() {
        mLifecycleSubject.onNext(FragmentEvent.START);
    }

    public void onResume() {
        mLifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    public void onPause() {
        mLifecycleSubject.onNext(FragmentEvent.PAUSE);
    }

    public void onStop() {
        mLifecycleSubject.onNext(FragmentEvent.STOP);
    }

    public void onDestroyView() {
        mLifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    }

    public void onDetach() {
        mLifecycleSubject.onNext(FragmentEvent.DETACH);
    }

    public void onDestroy() {
        mLifecycleSubject.onNext(FragmentEvent.DESTROY);
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

    public final Observable<FragmentEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    public final <T> Observable.Transformer<T, T> bindUntilEvent(FragmentEvent event) {
        return RxLifecycle.bindUntilFragmentEvent(mLifecycleSubject, event);
    }

    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindFragment(mLifecycleSubject);
    }
}
