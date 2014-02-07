package com.jasonrobinson.racer.async;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RaceClient;

public class RaceAsyncTask extends AsyncTask<Void, Void, List<Race>> {

	private static final String TAG = RaceAsyncTask.class.getSimpleName();

	@Inject
	private DatabaseManager mDatabaseManager;

	public RaceAsyncTask(Context context) {

		RoboGuice.injectMembers(context, this);
	}

	@Override
	protected List<Race> doInBackground(Void... params) {

		List<Race> races = fetchFromWeb();

		Log.d(TAG, "Cacheing races (" + races.size() + " entries)");
		int rows = mDatabaseManager.addOrUpdateRaceList(races);
		Log.d(TAG, "Finished cacheing (" + rows + " entries)");

		return fetchFromCache();
	}

	private List<Race> fetchFromCache() {

		return mDatabaseManager.getRaces(RaceOptions.UNFINISHED);
	}

	private List<Race> fetchFromWeb() {

		try {
			return new RaceClient().fetchRaces();
		}
		catch (SocketTimeoutException e) {
			e.printStackTrace();
		}
		catch (RetrofitError e) {
			e.printStackTrace();
		}

		return null;
	}
}
