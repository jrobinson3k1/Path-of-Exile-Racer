package com.jasonrobinson.racer.ui.ladder;

import com.jasonrobinson.racer.enumeration.PoEClass;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.network.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class LadderManager {

    private static final int MAX_PER_PAGE = 200;

    private static final int MAX_ENTRIES = 15000;

    @Inject
    RestService mRestService;

    public Observable<List<Entry>> fetchLadder(String id, int offset, int limit) {
        return mRestService.ladder(id, offset, limit).map(Ladder::getEntries);
    }

    public Observable<List<Entry>> fetchLadderForClass(String id, int count, PoEClass poEClass) {
        return Observable.range(0, MAX_ENTRIES / MAX_PER_PAGE)
                .map(page -> page * MAX_PER_PAGE)
                .concatMap(offset -> fetchLadder(id, offset, MAX_PER_PAGE))
                .flatMap(entries ->
                        Observable.from(entries)
                        .filter(entry -> entry.getCharacter().getPoeClass().equals(poEClass))
                        .toList()
                )
                .scan((entries, entries2) -> {
                    entries.addAll(entries2);
                    return entries;
                })
                .takeUntil(entries -> entries.size() >= count)
                .flatMap(Observable::from)
                .limit(count)
                .toList();
    }
}
