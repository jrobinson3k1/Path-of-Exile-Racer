package com.jasonrobinson.racer.ui.race;

import com.jasonrobinson.racer.db.DatabaseEvent;
import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RestService;
import com.jasonrobinson.racer.util.SettingsManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class RaceManager {

    @Inject
    RestService mRestService;

    @Inject
    SettingsManager mSettingsManager;

    @Inject
    DatabaseManager mDatabaseManager;

    public Observable<List<Race>> fetchRaces() {
        return mRestService.races().doOnNext(races -> {
            mDatabaseManager.addOrUpdateRaceList(races);
            mSettingsManager.updateLastRaceFetch();
        });
    }

    public Observable<List<Race>> getRaces(RaceOptions options) {
        return mDatabaseManager.getRaces(options);
    }

    public Observable<List<Race>> getRaceUpdates(RaceOptions options) {
        return mDatabaseManager.getEventObservable()
                .filter(event -> event == DatabaseEvent.RACES_TABLE_CHANGED)
                .flatMap(event -> mDatabaseManager.getRaces(options));
    }
}
