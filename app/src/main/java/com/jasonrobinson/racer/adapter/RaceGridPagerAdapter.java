package com.jasonrobinson.racer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jasonrobinson.racer.ui.race.RaceGridFragment;

import java.util.List;

public class RaceGridPagerAdapter extends FragmentPagerAdapter {

    private List<RaceGridParams> mParams;

    public RaceGridPagerAdapter(FragmentManager fm, List<RaceGridParams> params) {
        super(fm);
        mParams = params;
    }

    @Override
    public Fragment getItem(int position) {
        RaceGridParams params = mParams.get(position);
        return RaceGridFragment.newInstance(params.month, params.year);
    }

    @Override
    public int getCount() {
        return mParams.size();
    }

    public static class RaceGridParams {

        public int month;
        public int year;

        public RaceGridParams(int month, int year) {
            this.month = month;
            this.year = year;
        }
    }
}
