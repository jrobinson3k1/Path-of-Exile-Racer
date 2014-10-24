package com.jasonrobinson.racer.ui.race;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.RaceAdapter;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseExpandableListFragment;
import com.jasonrobinson.racer.ui.race.NotificationPickerDialogFragment.OnTimeSelectedListener;
import com.jasonrobinson.racer.util.AlarmUtils;
import com.metova.slim.annotation.Callback;
import com.metova.slim.annotation.Extra;

import java.util.List;

public class RaceListFragment extends BaseExpandableListFragment implements RaceAdapter.OnRaceActionClickListener {

    public static final String EXTRA_OPTION = "option";

    RaceAdapter mAdapter;

    @Extra(EXTRA_OPTION)
    RaceOptions mRaceOption;

    @Callback
    RacesCallback mCallback;

    public static RaceListFragment newInstance(RaceOptions option) {
        RaceListFragment fragment = new RaceListFragment();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_OPTION, option);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.races_unavailable));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getExpandableListView());
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.race_list_menu, menu);

        if (mAdapter == null) {
            menu.removeItem(R.id.menu_expand_all);
            menu.removeItem(R.id.menu_collapse_all);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_expand_all) {
            expandAllGroups();
        } else if (id == R.id.menu_collapse_all) {
            collapseAllGroups();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Race race = mAdapter.getChild(groupPosition, childPosition);
        mCallback.showLadder(race);

        return true;
    }

    @Override
    public void onForumPostClicked(Race race) {
        mCallback.showUrl(race.getUrl());
    }

    @Override
    public void onAddNotificationClicked(Race race) {
        showNotificationDialog(race);
    }

    @Override
    public void onRemoveNotificationClicked(Race race) {
        AlarmUtils.cancelAlarm(getActivity(), race);
        mAdapter.notifyDataSetChanged();
    }

    private void showNotificationDialog(final Race race) {
        NotificationPickerDialogFragment fragment = NotificationPickerDialogFragment.newInstance();
        fragment.setOnTimeSelectedListener(new OnTimeSelectedListener() {

            @Override
            public void onTimeSelected(long millis) {
                AlarmUtils.addAlarm(getActivity(), race, millis);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
                // no-op
            }
        });
        fragment.show(getFragmentManager(), null);
    }

    private void expandAllGroups() {
        if (mAdapter != null) {
            int groupCount = mAdapter.getGroupCount();
            for (int i = 0; i < groupCount; i++) {
                getExpandableListView().expandGroup(i);
            }
        }
    }

    private void collapseAllGroups() {
        if (mAdapter != null) {
            int groupCount = mAdapter.getGroupCount();
            for (int i = 0; i < groupCount; i++) {
                getExpandableListView().collapseGroup(i);
            }
        }
    }

    private void setData(List<Race> races) {
        if (races.isEmpty()) {
            mAdapter = null;
        } else {
            mAdapter = new RaceAdapter(getActivity(), races, this);
        }

        setListAdapter(mAdapter);
        setListShown(true);

        if (mAdapter != null) {
            getExpandableListView().expandGroup(0, false);
        }

        getActivity().supportInvalidateOptionsMenu();
    }

    public void refresh() {
        List<Race> races = getDatabaseManager().getRaces(mRaceOption);
        setData(races);
    }

    public interface RacesCallback {

        public void showUrl(String url);

        public void showLadder(Race race);
    }
}
