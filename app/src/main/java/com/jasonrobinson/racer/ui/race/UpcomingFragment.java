package com.jasonrobinson.racer.ui.race;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.UpcomingAdapter;
import com.jasonrobinson.racer.dagger.ComponentHolder;
import com.jasonrobinson.racer.network.RestService;
import com.jasonrobinson.racer.ui.BaseFragment;
import com.metova.slim.annotation.Layout;
import com.trello.rxlifecycle.FragmentEvent;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.fragment_upcoming)
public class UpcomingFragment extends BaseFragment {

    @Inject
    RestService mRestService;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
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

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRestService.races()
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(uiHook())
                .subscribe(races -> mRecyclerView.setAdapter(new UpcomingAdapter(races)));
    }
}
