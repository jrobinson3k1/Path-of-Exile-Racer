package com.jasonrobinson.racer.ui.base;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;
import com.metova.slim.Slim;

public class BasePreferenceActivity extends PreferenceActivity {

    BaseActivityImpl mImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImpl = new BaseActivityImpl(this, false);
        mImpl.onCreate(savedInstanceState);

        Slim.injectExtras(getIntent().getExtras(), this);
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
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mImpl.setContentView(layoutResID);
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
