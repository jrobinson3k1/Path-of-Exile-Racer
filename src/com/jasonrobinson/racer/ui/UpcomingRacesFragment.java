package com.jasonrobinson.racer.ui;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.jasonrobinson.racer.adapter.RaceAdapter;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.ui.base.BaseListFragment;

public class UpcomingRacesFragment extends BaseListFragment {

	private RacesCallback mCallback;

	public static UpcomingRacesFragment newInstance() {

		return new UpcomingRacesFragment();
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mCallback = castActivity(RacesCallback.class);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		new RaceTask().execute();
	}

	private class RaceTask extends AsyncTask<Void, Void, List<Race>> {

		@Override
		protected List<Race> doInBackground(Void... params) {

			return new RaceClient().build();
		}

		@Override
		protected void onPostExecute(List<Race> result) {

			super.onPostExecute(result);
			setListAdapter(new RaceAdapter(result));
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Race race = (Race) l.getItemAtPosition(position);

		mCallback.showUrl(race.getUrl());
	}

	public interface RacesCallback {

		public void showUrl(String url);
	}
}
