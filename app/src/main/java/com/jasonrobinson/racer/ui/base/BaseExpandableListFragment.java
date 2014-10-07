package com.jasonrobinson.racer.ui.base;

import android.app.Activity;

import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.util.SettingsManager;

public class BaseExpandableListFragment extends RoboExpandableListFragment {

    BaseFragmentImpl mImpl = new BaseFragmentImpl(this);

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mImpl.onAttach(activity);
    }

    public <T> T castActivity(Class<T> clz) {

        return mImpl.castActivity(clz);
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
