package com.jasonrobinson.racer.ui.race;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.RacesAdapter;
import com.jasonrobinson.racer.dagger.ComponentHolder;
import com.jasonrobinson.racer.manager.RacesManager;
import com.jasonrobinson.racer.event.RaceEvent;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.BaseFragment;
import com.jasonrobinson.racer.ui.TitleDelegate;
import com.jasonrobinson.racer.ui.view.DateDecoration;
import com.jasonrobinson.racer.ui.view.SimpleDividerDecoration;
import com.metova.slim.annotation.Callback;
import com.metova.slim.annotation.Layout;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

@Layout(R.layout.fragment_races)
public abstract class RacesFragment extends BaseFragment {

    @Inject
    RacesManager mRacesManager;

    @Callback
    TitleDelegate mTitleDelegate;

    @Bind(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty)
    TextView mEmptyTextView;

    private RacesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentHolder.getInstance().component().inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mTitleDelegate.setActionBarTitle(getTitle());

        mSwipeRefreshLayout.setOnRefreshListener(this::downloadRaces);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(getResources()));
        mRecyclerView.addItemDecoration(new DateDecoration(getContext()));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RacesAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        showRaces();
        subscribeForRaceChanges();
    }

    private void downloadRaces() {
        mRacesManager.download()
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(uiHook())
                .finallyDo(() -> mSwipeRefreshLayout.setRefreshing(false))
                .subscribe();
    }

    private void subscribeForRaceChanges() {
        mRacesManager.getEventObservable()
                .filter(event -> event == RaceEvent.TABLE_CHANGED)
                .subscribe(event -> showRaces());
    }

    private void showRaces() {
        mRacesManager.races()
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(uiHook())
                .compose(racesTransformer())
                .subscribe(races -> {
                    mAdapter.clearAll();
                    mAdapter.addAll(races);

                    mEmptyTextView.setVisibility(races.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                });
    }

    @NonNull
    protected Observable.Transformer<List<Race>, List<Race>> racesTransformer() {
        return observable -> observable;
    }

    public abstract CharSequence getTitle();
}
