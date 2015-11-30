package com.jasonrobinson.racer.ui.race;

import android.support.annotation.NonNull;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;

import java.util.List;

import rx.Observable;

public class FavoriteRacesFragment extends RacesFragment {

    public static FavoriteRacesFragment newInstance() {
        return new FavoriteRacesFragment();
    }

    @NonNull
    @Override
    protected Observable.Transformer<List<Race>, List<Race>> racesTransformer() {
        return observable -> observable
                .flatMap(Observable::from)
                .filter(race -> race.getInteractions().isFavorite())
                .toList();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.favorites);
    }
}
