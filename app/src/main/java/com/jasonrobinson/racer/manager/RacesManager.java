package com.jasonrobinson.racer.manager;

import com.jasonrobinson.racer.event.RaceEvent;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RestService;
import com.raizlabs.android.dbflow.runtime.DBTransactionInfo;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.BaseTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListener;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.UpdateModelListTransaction;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;

@Singleton
public class RacesManager {

    private static final long FETCH_PERIOD = 1000 * 60 * 60 * 24; // 24 hours

    private RestService mRestService;
    private SettingsManager mSettingsManager;

    private PublishSubject<RaceEvent> mEventSubject = PublishSubject.create();
    private Subscription mFetchDelaySubscription;

    @Inject
    RacesManager(RestService restService, SettingsManager settingsManager) {
        mRestService = restService;
        mSettingsManager = settingsManager;

        scheduleRacesDownload();
        mSettingsManager.getLastRaceFetchObservable()
                .subscribe(time -> scheduleRacesDownload());
    }

    private void updateTable(List<Race> races) {
        ProcessModelInfo<Race> processModelInfo = ProcessModelInfo.withModels(races)
                .info(DBTransactionInfo.create(BaseTransaction.PRIORITY_HIGH))
                .result(new TransactionListener<List<Race>>() {
                    @Override
                    public void onResultReceived(List<Race> result) {
                        mEventSubject.onNext(RaceEvent.TABLE_CHANGED);
                    }

                    @Override
                    public boolean onReady(BaseTransaction<List<Race>> transaction) {
                        return true;
                    }

                    @Override
                    public boolean hasResult(BaseTransaction<List<Race>> transaction, List<Race> result) {
                        return !result.isEmpty();
                    }
                });

        TransactionManager.getInstance().addTransaction(new UpdateModelListTransaction<>(processModelInfo));
    }

    public Observable<List<Race>> download() {
        return mRestService.races()
                .doOnNext(races -> {
                    updateTable(races);
                    mSettingsManager.updateLastRaceFetch();
                });
    }

    public Observable<List<Race>> races() {
        return Observable.create(new Observable.OnSubscribe<List<Race>>() {
            @Override
            public void call(Subscriber<? super List<Race>> subscriber) {
                try {
                    subscriber.onNext(new Select().from(Race.class).queryList());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private void scheduleRacesDownload() {
        if (mFetchDelaySubscription != null) {
            mFetchDelaySubscription.unsubscribe();
        }

        long now = System.currentTimeMillis();
        long fetchDelay = mSettingsManager.getLastRaceFetch() + FETCH_PERIOD - now;
        mFetchDelaySubscription = download()
                .delaySubscription(fetchDelay, TimeUnit.MILLISECONDS)
                .subscribe();
    }

    public Observable<RaceEvent> getEventObservable() {
        return mEventSubject.asObservable();
    }
}
