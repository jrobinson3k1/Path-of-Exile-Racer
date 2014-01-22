package com.jasonrobinson.racer.ui;

import java.util.List;

import roboguice.fragment.RoboListFragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.jasonrobinson.racer.adapter.RaceAdapter;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RaceClient;

public class UpcomingRacesFragment extends RoboListFragment {

	public static UpcomingRacesFragment newInstance() {

		return new UpcomingRacesFragment();
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
}
