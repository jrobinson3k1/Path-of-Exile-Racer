package com.jasonrobinson.racer.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;

public class BaseListFragment extends ListFragment {

    BaseFragmentImpl mImpl = new BaseFragmentImpl(this);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImpl.onViewCreated(view, savedInstanceState);
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
}
