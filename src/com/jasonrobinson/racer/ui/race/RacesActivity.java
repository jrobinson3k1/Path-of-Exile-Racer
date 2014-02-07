package com.jasonrobinson.racer.ui.race;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit.RetrofitError;
import roboguice.inject.InjectFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonrobinson.racer.R;
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
		intent.putExtra(LadderActivity.EXTRA_START_AT, race.getStartAt().getTime());
		intent.putExtra(LadderActivity.EXTRA_END_AT, race.getEndAt().getTime());

		startActivity(intent);
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
		supportInvalidateOptionsMenu();
	}

	private class RacesTask extends AsyncTask<Void, Void, List<Race>> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			setRefreshing(true);
		}

		@Override
		protected List<Race> doInBackground(Void... params) {

			List<Race> races = fetchFromWeb();

			Log.d(TAG, "Cacheing races (" + races.size() + " entries)");
			int rows = getDatabaseManager().addOrUpdateRaceList(races);
			Log.d(TAG, "Finished cacheing (" + rows + " entries)");

			return fetchFromCache();
		}

		@Override
		protected void onPostExecute(List<Race> result) {

			super.onPostExecute(result);
			setRefreshing(false);

			if (result != null) {
				mFragment.setData(result);
			}
		}

		private List<Race> fetchFromCache() {

			return getDatabaseManager().getAllUnfinishedRaces();
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
}
