package com.jasonrobinson.racer.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.util.SettingsManager;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BaseFragmentImpl {

    AnalyticsManager mAnalyticsManager;
    SettingsManager mSettingsManager;
    DatabaseManager mDatabaseManager;

    private Fragment mFragment;

    public BaseFragmentImpl(Fragment fragment) {
        mFragment = fragment;

        mAnalyticsManager = GraphHolder.getInstance().get(AnalyticsManager.class);
        mSettingsManager = GraphHolder.getInstance().get(SettingsManager.class);
        mDatabaseManager = GraphHolder.getInstance().get(DatabaseManager.class);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(mFragment, view);
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
}
