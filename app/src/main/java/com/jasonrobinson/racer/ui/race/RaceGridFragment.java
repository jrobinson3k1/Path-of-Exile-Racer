package com.jasonrobinson.racer.ui.race;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasonrobinson.racer.ui.base.BaseFragment;
import com.metova.slim.annotation.Extra;

public class RaceGridFragment extends BaseFragment {

    private static final String EXTRA_MONTH = "month";
    private static final String EXTRA_YEAR = "year";

    @Extra(EXTRA_MONTH)
    int mMonth;

    @Extra(EXTRA_YEAR)
    int mYear;

    public static RaceGridFragment newInstance(int month, int year) {
        RaceGridFragment fragment = new RaceGridFragment();

        Bundle args = new Bundle();
        args.putInt(EXTRA_MONTH, month);
        args.putInt(EXTRA_YEAR, year);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
