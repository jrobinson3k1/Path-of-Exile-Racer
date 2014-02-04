package com.jasonrobinson.racer.ui.race;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.inject.InjectFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.database.agent.RaceAgent;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.ladder.LadderActivity;
import com.jasonrobinson.racer.ui.race.RacesFragment.RacesCallback;
import com.jasonrobinson.racer.ui.web.WebActivity;

public class RacesActivity extends BaseActivity implements RacesCallback {

	private static final String TAG = RacesActivity.class.getSimpleName();

	@InjectFragment(tag = "races_fragment")
	private RacesFragment mFragment;

	@Inject
	private RaceAgent mRaceAgent;

	private RacesTask mRacesTask;
	private boolean mRefreshing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.racer_activity);
		setTitle(R.string.races);

		fetchRaces();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (mRacesTask != null) {
			mRacesTask.cancel(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.refresh_menu, menu);

		if (!mRefreshing) {
			MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
			MenuItemCompat.setActionView(refreshItem, null);
		}

		return true;
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
	public void showUrl(String url) {

		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra(WebActivity.EXTRA_URL, url);
		startActivity(intent);
	}

	@Override
	public void showLadder(Race race) {

		Intent intent = new Intent(this, LadderActivity.class);
		intent.putExtra(LadderActivity.EXTRA_ID, race.getRaceId());
		try {
			intent.putExtra(LadderActivity.EXTRA_START_AT, race.getStartAtDate().getTime());
			intent.putExtra(LadderActivity.EXTRA_END_AT, race.getEndAtDate().getTime());
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}

		startActivity(intent);
	}

	private void fetchRaces() {

		if (mRacesTask != null) {
			mRacesTask.cancel(true);
		}

		mRacesTask = new RacesTask(this, true);
		mRacesTask.execute();
	}

	private void setRefreshing(boolean refreshing) {

		mRefreshing = refreshing;
		supportInvalidateOptionsMenu();
	}

	private class RacesTask extends AsyncTask<Void, Void, List<Race>> {

		private Context mContext;
		private boolean mFromCache;

		public RacesTask(Context context, boolean fromCache) {

			mContext = context.getApplicationContext();
			mFromCache = fromCache;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			if (!mFromCache) {
				setRefreshing(true);
			}
		}

		@Override
		protected List<Race> doInBackground(Void... params) {

			if (mFromCache) {
				return fetchFromCache();
			}
			else {
				return fetchFromWeb();
			}
		}

		@Override
		protected void onPostExecute(List<Race> result) {

			super.onPostExecute(result);
			if (!mFromCache) {
				setRefreshing(false);
			}

			if (result != null) {
				mFragment.setData(result);

				if (!mFromCache) {
					new RaceCacheTask(result).execute();
				}
			}
		}

		private List<Race> fetchFromCache() {

			return mRaceAgent.list();
		}

		private List<Race> fetchFromWeb() {

			try {
				return new RaceClient().fetchRaces(mContext);
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

	private class RaceCacheTask extends AsyncTask<Void, Void, Void> {

		private List<Race> mRaces;

		public RaceCacheTask(List<Race> races) {

			mRaces = races;
		}

		@Override
		protected Void doInBackground(Void... params) {

			Log.d(TAG, "Cacheing races (" + mRaces.size() + " entries)");
			mRaceAgent.insertAll(mRaces);
			Log.d(TAG, "Finished cacheing");

			return null;
		}
	}
}
