package com.jasonrobinson.racer.ui.base;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;
import com.metova.slim.SlimActivity;

public class BaseActivity extends SlimActivity {

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
    protected void onStop() {
        super.onStop();
        mImpl.onStop();
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
}