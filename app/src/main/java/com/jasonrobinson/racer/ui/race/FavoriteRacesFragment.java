package com.jasonrobinson.racer.ui.race;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;

import android.support.annotation.NonNull;

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
                .filter(Race::isFavorite)
                .toList();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.favorites);
    }
}
