package com.jasonrobinson.racer.async;

import android.os.AsyncTask;
import android.util.Log;

import com.jasonrobinson.racer.db.DatabaseManager;
import com.jasonrobinson.racer.enumeration.RaceOptions;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.module.GraphHolder;
import com.jasonrobinson.racer.network.RaceClient;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit.RetrofitError;

public class RaceAsyncTask extends AsyncTask<Void, Void, List<Race>> {

    private static final String TAG = RaceAsyncTask.class.getSimpleName();

    DatabaseManager mDatabaseManager;

    boolean mReturnData;

    public RaceAsyncTask(boolean returnData) {
        mReturnData = returnData;
        mDatabaseManager = GraphHolder.getInstance().get(DatabaseManager.class);
    }

    @Override
    protected List<Race> doInBackground(Void... params) {

        Log.d(TAG, "Downloading races");
        List<Race> races = fetchFromWeb();
        if (races == null) {
            Log.d(TAG, "Failed to download races");
            return null;
        }

        Log.d(TAG, "Finished downloading (" + races.size() + " entries)");

        Log.d(TAG, "Cacheing races (" + races.size() + " entries)");
        int rows = mDatabaseManager.addOrUpdateRaceList(races);
        Log.d(TAG, "Finished cacheing (" + rows + " entries)");

        if (mReturnData) {
            return fetchFromCache();
        } else {
            return null;
        }
    }

    private List<Race> fetchFromCache() {

        return mDatabaseManager.getRaces(RaceOptions.ALL);
    }

    private List<Race> fetchFromWeb() {

        try {
            return new RaceClient().fetchRaces();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (RetrofitError e) {
            e.printStackTrace();
        }

        return null;
    }
}
