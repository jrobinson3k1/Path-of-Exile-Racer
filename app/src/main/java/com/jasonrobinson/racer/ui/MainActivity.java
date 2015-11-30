package com.jasonrobinson.racer.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.dagger.ComponentHolder;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.ui.race.RacesFragmentFactory;
import com.jasonrobinson.racer.ui.settings.SettingsActivity;
import com.metova.slim.annotation.Layout;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.activity_main)
public class MainActivity extends BaseActivity implements TitleDelegate {

    @Bind(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation)
    NavigationView mNavigationView;

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentHolder.getInstance().component().inject(this);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content, RacesFragmentFactory.newFragment(RaceOptions.UPCOMING));
            ft.commit();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.upcoming:
                    replaceContent(RacesFragmentFactory.newFragment(RaceOptions.UPCOMING));
                    break;
                case R.id.in_progress:
                    replaceContent(RacesFragmentFactory.newFragment(RaceOptions.IN_PROGRESS));
                    break;
                case R.id.finished:
                    replaceContent(RacesFragmentFactory.newFragment(RaceOptions.FINISHED));
                    break;
                case R.id.favorites:
                    // TODO: Implement
                    break;
                case R.id.alarms:
                    // TODO: Implement
                    break;
                case R.id.settings:
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    break;
            }

            mDrawerLayout.closeDrawers();
            return true;
        });
    }

    private void replaceContent(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment.getClass().equals(currentFragment.getClass())) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setActionBarTitle(CharSequence title) {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(title);
    }
}
