package com.jasonrobinson.racer.ui;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.RaceAdapter;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.ui.base.BaseListFragment;

public class RacesFragment extends BaseListFragment {

	private RacesTask mRacesTask;
	private boolean mRefreshing;

	private RacesCallback mCallback;

	public static RacesFragment newInstance() {

		return new RacesFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mCallback = castActivity(RacesCallback.class);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		fetchRaces();
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		if (mRacesTask != null) {
			mRacesTask.cancel(true);
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

		int id = item.getItemId();
		if (id == R.id.menu_refresh) {
			fetchRaces();
		}
		else {
			super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Race race = (Race) l.getItemAtPosition(position);

		boolean inProgress;
		try {
			inProgress = race.isInProgress();
		}
		catch (ParseException e) {
			e.printStackTrace();
			inProgress = false;
		}

		if (inProgress) {
			mCallback.showLadder(race.getId());
		}
		else {
			mCallback.showUrl(race.getUrl());
		}
	}

	private void fetchRaces() {

		if (mRacesTask != null) {
			mRacesTask.cancel(true);
		}

		mRacesTask = new RacesTask();
		mRacesTask.execute();
	}

	private void setRefreshing(boolean refreshing) {

		mRefreshing = refreshing;
		getActivity().supportInvalidateOptionsMenu();
	}

	public interface RacesCallback {

		public void showUrl(String url);

		public void showLadder(String id);
	}

	private class RacesTask extends AsyncTask<Void, Void, List<Race>> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			setRefreshing(true);
		}

		@Override
		protected List<Race> doInBackground(Void... params) {

			try {
				return new RaceClient().fetchRaces();
			}
			catch (SocketTimeoutException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Race> result) {

			super.onPostExecute(result);
			setRefreshing(false);
			if (result != null) {
				setListAdapter(new RaceAdapter(result));
			}
			else {
				Toast.makeText(getActivity(), R.string.error_unavailable, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
