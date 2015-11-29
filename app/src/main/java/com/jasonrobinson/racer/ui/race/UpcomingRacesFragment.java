package com.jasonrobinson.racer.ui.race;

import android.support.annotation.NonNull;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;

import java.util.List;

import rx.Observable;

public class UpcomingRacesFragment extends RacesFragment {

    public static UpcomingRacesFragment newInstance() {
        return new UpcomingRacesFragment();
    }

    @NonNull
    @Override
    protected Observable.Transformer<List<Race>, List<Race>> racesTransformer() {
        return observable -> observable
                .flatMap(Observable::from)
                .filter(Race::isUpcoming)
                .toList();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.upcoming);
    }
}
