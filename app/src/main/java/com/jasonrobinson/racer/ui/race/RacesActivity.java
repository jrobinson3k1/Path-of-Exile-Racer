package com.jasonrobinson.racer.ui.race;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.BaseActivity;
import com.metova.slim.annotation.Layout;

import butterknife.ButterKnife;

@Layout(R.layout.activity_races)
public class RacesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_races);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content, UpcomingFragment.newInstance());
            ft.commit();
        }
    }
}
