package com.jasonrobinson.racer.ui.race;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public class AlarmRacesFragment extends RacesFragment {

    public static AlarmRacesFragment newInstance() {
        return new AlarmRacesFragment();
    }

    @NonNull
    @Override
    protected Observable.Transformer<List<Race>, List<Race>> racesTransformer() {
        return observable -> observable
                .flatMap(Observable::from)
                .filter(race -> race.getAlarm() != null)
                .toList();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.alarms);
    }
}
