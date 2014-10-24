package com.jasonrobinson.racer.ui.settings;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseActivity;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, SettingsFragment.newInstance());
            ft.commit();
        }

        setTitle(R.string.settings);
    }

    @Override
    public boolean showSettingsMenu() {
        return false;
    }

    public static class SettingsFragment extends PreferenceFragment {

        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
