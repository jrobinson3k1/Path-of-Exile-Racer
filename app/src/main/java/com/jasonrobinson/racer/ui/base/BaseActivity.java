package com.jasonrobinson.racer.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;
import com.trello.rxlifecycle.ActivityEvent;

import rx.Observable;

public class BaseActivity extends ActionBarActivity {

    private final BaseActivityImpl mImpl = new BaseActivityImpl(this, showSettingsMenu());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImpl.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mImpl.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImpl.onResume();
    }

    @Override
    protected void onPause() {
        mImpl.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mImpl.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mImpl.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mImpl.onContentChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mImpl.onCreateOptionsMenu(menu) || super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mImpl.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        mImpl.finish();
    }

    public boolean showSettingsMenu() {
        return true;
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(mImpl.formatTitleText(title));
    }

    public SettingsManager getSettingsManager() {
        return mImpl.getSettingsManager();
    }

    public AnalyticsManager getAnalyticsManager() {
        return mImpl.getAnalyticsManager();
    }

    public DatabaseManager getDatabaseManager() {
        return mImpl.getdDatabaseManager();
    }

    public final Observable<ActivityEvent> lifecycle() {
        return mImpl.lifecycle();
    }

    public final <T> Observable.Transformer<T, T> bindUntilEvent(ActivityEvent event) {
        return mImpl.bindUntilEvent(event);
    }

    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return mImpl.bindToLifecycle();
    }
}