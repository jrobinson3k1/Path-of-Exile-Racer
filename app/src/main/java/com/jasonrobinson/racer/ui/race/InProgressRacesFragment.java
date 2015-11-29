package com.jasonrobinson.racer.ui.race;

import android.support.annotation.NonNull;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;

import java.util.List;

import rx.Observable;

public class InProgressRacesFragment extends RacesFragment {

    public static InProgressRacesFragment newInstance() {
        return new InProgressRacesFragment();
    }

    @NonNull
    @Override
    protected Observable.Transformer<List<Race>, List<Race>> racesTransformer() {
        return observable -> observable
                .flatMap(Observable::from)
                .filter(Race::isInProgress)
                .toList();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.in_progress);
    }
}
