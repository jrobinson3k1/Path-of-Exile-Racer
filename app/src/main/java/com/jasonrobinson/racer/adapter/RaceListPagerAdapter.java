package com.jasonrobinson.racer.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.ui.race.RaceListFragment;

import java.util.List;

public class RaceListPagerAdapter extends FragmentPagerAdapter {

    private List<RaceListParams> mParams;

    public RaceListPagerAdapter(FragmentManager fm, List<RaceListParams> params) {
        super(fm);
        mParams = params;
    }

    @Override
    public RaceListFragment getItem(int position) {
        return RaceListFragment.newInstance(mParams.get(position).option);
    }

    @Override
    public int getCount() {
        return mParams.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mParams.get(position).title;
    }

    public static class RaceListParams {

        public RaceOptions option;
        public String title;

        public RaceListParams(RaceOptions option, String title) {
            this.option = option;
            this.title = title;
        }
    }
}
