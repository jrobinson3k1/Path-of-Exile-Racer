package com.jasonrobinson.racer.ui.race;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public class FinishedRacesFragment extends RacesFragment {

    public static FinishedRacesFragment newInstance() {
        return new FinishedRacesFragment();
    }

    @NonNull
    @Override
    protected Observable.Transformer<List<Race>, List<Race>> racesTransformer() {
        return observable -> observable
                .flatMap(Observable::from)
                .filter(Race::isFinished)
                .toList();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.finished);
    }
}
