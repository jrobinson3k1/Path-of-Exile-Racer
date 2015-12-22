package com.jasonrobinson.racer.ui;

import com.metova.slim.Slim;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseActivity extends AppCompatActivity {

    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View layout = Slim.createLayout(this, this);
        if (layout != null) {
            setContentView(layout);
        }

        Slim.injectExtras(getIntent().getExtras(), this);
        mLifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycleSubject.onNext(ActivityEvent.STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleSubject.onNext(ActivityEvent.DESTROY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLifecycleSubject.onNext(ActivityEvent.PAUSE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleSubject.onNext(ActivityEvent.START);
    }

    public final Observable<ActivityEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    public final <T> Observable.Transformer<T, T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilActivityEvent(mLifecycleSubject, event);
    }

    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindActivity(mLifecycleSubject);
    }
}
