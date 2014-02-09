package com.jasonrobinson.racer.ui.race;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.RaceAdapter;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseExpandableListFragment;
import com.jasonrobinson.racer.util.AlarmUtils;

public class RacesFragment extends BaseExpandableListFragment {

	private static final String TAG = RacesFragment.class.getSimpleName();

	public static final String ARG_OPTION = "option";

	private RaceAdapter mAdapter;

	private RaceOptions mRaceOption;
	private RacesCallback mCallback;

	public static RacesFragment newInstance(RaceOptions option) {

		RacesFragment fragment = new RacesFragment();

		Bundle args = new Bundle();
		args.putSerializable(ARG_OPTION, option);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mRaceOption = (RaceOptions) getArguments().getSerializable(ARG_OPTION);
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mCallback = castActivity(RacesCallback.class);
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
		inflater.inflate(R.menu.races_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.menu_expand_all) {
			expandAllGroups();
		}
		else if (id == R.id.menu_collapse_all) {
			collapseAllGroups();
		}
		else {
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		ExpandableListContextMenuInfo adapterInfo = (ExpandableListContextMenuInfo) menuInfo;
		if (ExpandableListView.getPackedPositionType(adapterInfo.packedPosition) != ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			return;
		}

		int groupPosition = ExpandableListView.getPackedPositionGroup(adapterInfo.packedPosition);
		int childPosition = ExpandableListView.getPackedPositionChild(adapterInfo.packedPosition);

		getActivity().getMenuInflater().inflate(R.menu.races_context_menu, menu);

		Race race = mAdapter.getChild(groupPosition, childPosition);

		if (TextUtils.isEmpty(race.getUrl())) {
			menu.removeItem(R.id.menu_forum_post);
		}

		if (AlarmUtils.isAlarmAdded(getActivity(), race)) {
			menu.removeItem(R.id.menu_add_notification);
		}
		else {
			menu.removeItem(R.id.menu_remove_notification);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		ExpandableListContextMenuInfo menuInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
		if (ExpandableListView.getPackedPositionType(menuInfo.packedPosition) != ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			return false;
		}

		int groupPosition = ExpandableListView.getPackedPositionGroup(menuInfo.packedPosition);
		int childPosition = ExpandableListView.getPackedPositionChild(menuInfo.packedPosition);
		Race race = mAdapter.getChild(groupPosition, childPosition);

		int id = item.getItemId();
		if (id == R.id.menu_ladder) {
			mCallback.showLadder(race);
		}
		else if (id == R.id.menu_forum_post) {
			mCallback.showUrl(race.getUrl());
		}
		else if (id == R.id.menu_add_notification) {
			AlarmUtils.addAlarm(getActivity(), race);
		}
		else if (id == R.id.menu_remove_notification) {
			AlarmUtils.cancelAlarm(getActivity(), race);
		}
		else {
			return super.onContextItemSelected(item);
		}

		return true;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		Race race = mAdapter.getChild(groupPosition, childPosition);

		if (race.isRegistrationOpen() || race.isFinished()) {
			mCallback.showLadder(race);
		}
		else {
			String url = race.getUrl();
			if (!TextUtils.isEmpty(url)) {
				mCallback.showUrl(url);
			}
			else {
				Toast.makeText(getActivity(), R.string.no_forum_post, Toast.LENGTH_SHORT).show();
			}
		}

		return true;
	}

	private void expandAllGroups() {

		int groupCount = getExpandableListAdapter().getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			getExpandableListView().expandGroup(i);
		}
	}

	private void collapseAllGroups() {

		int groupCount = getExpandableListAdapter().getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			getExpandableListView().collapseGroup(i);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void setData(List<Race> races) {

		mAdapter = new RaceAdapter(getActivity(), races);
		setListAdapter(mAdapter);
		setListShown(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getExpandableListView().expandGroup(0, false);
		}
		else {
			getExpandableListView().expandGroup(0);
		}
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
