package com.jasonrobinson.racer.ui.race;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
		registerForContextMenu(getListView());
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.races_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		Race race = (Race) getListView().getItemAtPosition(menuInfo.position);

		int id = item.getItemId();
		if (id == R.id.menu_ladder) {
			mCallback.showLadder(race);
		}
		else if (id == R.id.menu_forum_post) {
			mCallback.showUrl(race.getUrl());
		}
		else {
			return super.onContextItemSelected(item);
		}

		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Race race = (Race) l.getItemAtPosition(position);

		boolean registrationOpen;
		boolean finished;
		try {
			registrationOpen = race.isRegistrationOpen();
			finished = race.isFinished();
		}
		catch (ParseException e) {
			e.printStackTrace();
			registrationOpen = false;
			finished = false;
		}

		if (registrationOpen || finished) {
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

		public void showLadder(Race race);
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
				setListAdapter(new RaceAdapter(getActivity(), result));
			}
			else {
				Toast.makeText(getActivity(), R.string.error_unavailable, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
