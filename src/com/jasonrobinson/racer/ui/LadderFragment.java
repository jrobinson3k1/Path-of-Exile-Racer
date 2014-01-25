package com.jasonrobinson.racer.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.LadderAdapter;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.ui.base.BaseListFragment;

public class LadderFragment extends BaseListFragment {

	private static final String ARG_ID = "id";

	private String mId;

	private Timer mTimer;
	private boolean mRefreshing;

	private LadderAdapter mAdapter;
	private LadderTask mTask;

	public static final LadderFragment newInstance(String id) {

		LadderFragment fragment = new LadderFragment();

		Bundle args = new Bundle();
		args.putString(ARG_ID, id);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			String id = getArguments().getString(ARG_ID);
			if (id != null) {
				mId = id;
				fetchLadder(mId);
			}
		}
	}

	@Override
	public void onResume() {

		super.onResume();
		if (mTask != null && mTask.getStatus() != Status.RUNNING && mId != null) {
			fetchLadder(mId);
		}
	}

	@Override
	public void onPause() {

		super.onPause();
		if (mTimer != null) {
			mTimer.cancel();
		}
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		if (mTask != null) {
			mTask.cancel(true);
		}

		if (mTimer != null) {
			mTimer.cancel();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.refresh_menu, menu);

		if (!mRefreshing) {
			MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
			MenuItemCompat.setActionView(refreshItem, null);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_refresh:
				fetchLadder(mId);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		return true;
	}

	public void fetchLadder(String id) {

		mId = id;

		if (mTask != null) {
			mTask.cancel(true);
		}

		mTask = new LadderTask();
		mTask.execute(mId);
	}

	private void setRefreshing(boolean refreshing) {

		mRefreshing = refreshing;
		getActivity().supportInvalidateOptionsMenu();

		if (refreshing) {
			if (mTimer != null) {
				mTimer.cancel();
			}
		}
		else {
			mTimer = new Timer();
			mTimer.schedule(new RefreshTimerTask(), 30000);
		}
	}

	private class LadderTask extends AsyncTask<String, Void, Ladder> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			setRefreshing(true);
		}

		@Override
		protected Ladder doInBackground(String... params) {

			String id = params[0];
			return new RaceClient().fetchLadder(id, 0, 100);
		}

		@Override
		protected void onPostExecute(Ladder result) {

			super.onPostExecute(result);
			setRefreshing(false);

			Entry[] entries = result.getEntries();
			if (mAdapter == null) {
				mAdapter = new LadderAdapter(entries);
				setListAdapter(mAdapter);
			}
			else {
				mAdapter.setEntries(entries);
			}
		}
	}

	private class RefreshTimerTask extends TimerTask {

		@Override
		public void run() {

			if (mId != null) {
				fetchLadder(mId);
			}
		}
	};
}
