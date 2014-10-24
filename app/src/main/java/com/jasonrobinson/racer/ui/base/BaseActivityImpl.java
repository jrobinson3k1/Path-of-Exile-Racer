package com.jasonrobinson.racer.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.analytics.AnalyticsManager;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.ui.settings.SettingsActivity;
import com.jasonrobinson.racer.util.CustomTypefaceSpan;
import com.jasonrobinson.racer.util.RawTypeface;
import com.jasonrobinson.racer.util.SettingsManager;
import com.metova.slim.Slim;

import butterknife.ButterKnife;

public class BaseActivityImpl {

    AnalyticsManager mAnalyticsManager;
    SettingsManager mSettingsManager;
    DatabaseManager mDatabaseManager;

    private Activity mActivity;

    private boolean mShowSettingsMenu;

    public BaseActivityImpl(Activity activity, boolean showSettingsMenu) {
        mActivity = activity;
        mShowSettingsMenu = showSettingsMenu;

        mAnalyticsManager = GraphHolder.getInstance().get(AnalyticsManager.class);
        mSettingsManager = GraphHolder.getInstance().get(SettingsManager.class);
        mDatabaseManager = GraphHolder.getInstance().get(DatabaseManager.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        View layout = Slim.createLayout(mActivity, mActivity);
        if (layout != null) {
            mActivity.setContentView(layout);
        }

        Slim.injectExtras(mActivity.getIntent().getExtras(), mActivity);

        mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        mActivity.setTitle(mActivity.getTitle());
    }

    public void onStart() {
        mAnalyticsManager.onStart(mActivity);
    }

    public void onStop() {
        mAnalyticsManager.onStop(mActivity);
    }

    public void onContentChanged() {
        ButterKnife.inject(mActivity);
    }

    public void finish() {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowSettingsMenu) {
            mActivity.getMenuInflater().inflate(R.menu.settings_menu, menu);
            return true;
        }

        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(mActivity);
            if (NavUtils.shouldUpRecreateTask(mActivity, upIntent)) {
                TaskStackBuilder.create(mActivity).addNextIntentWithParentStack(upIntent).startActivities();
            } else {
                NavUtils.navigateUpFromSameTask(mActivity);
            }
        } else if (id == R.id.menu_settings) {
            Intent intent = new Intent(mActivity, SettingsActivity.class);
            mActivity.startActivity(intent);
        } else {
            return false;
        }

        return true;
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

    public CharSequence formatTitleText(CharSequence title) {
        Typeface typeface = RawTypeface.obtain(mActivity, R.raw.fontin_regular);
        if (typeface != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new CustomTypefaceSpan(typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            title = s;
        }

        return title;
    }
}
