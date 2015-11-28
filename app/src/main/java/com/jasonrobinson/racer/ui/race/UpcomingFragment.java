package com.jasonrobinson.racer.ui.race;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.UpcomingAdapter;
import com.jasonrobinson.racer.dagger.ComponentHolder;
import com.jasonrobinson.racer.network.RestService;
import com.jasonrobinson.racer.ui.BaseFragment;
import com.jasonrobinson.racer.ui.view.SimpleDividerDecoration;
import com.metova.slim.annotation.Layout;
import com.trello.rxlifecycle.FragmentEvent;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.fragment_upcoming)
public class UpcomingFragment extends BaseFragment {

    @Inject
    RestService mRestService;

    @Bind(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty)
    TextView mEmptyTextView;

    private UpcomingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static UpcomingFragment newInstance() {
        return new UpcomingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentHolder.getInstance().component().inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this::downloadRaces);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(getResources()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new UpcomingAdapter();
        mRecyclerView.setAdapter(mAdapter);

        downloadRaces();
    }

    private void downloadRaces() {
        mRestService.races()
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(uiHook())
                .doOnTerminate(() -> mSwipeRefreshLayout.setRefreshing(false))
                .subscribe(races -> {
                    mAdapter.clearAll();
                    mAdapter.addAll(races);

                    mEmptyTextView.setVisibility(races.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                });
    }
}
