package com.jasonrobinson.racer.ui.race;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
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
		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
		Race race = (Race) getListView().getItemAtPosition(adapterInfo.position);

		// Check if the device has an app that can handle calendar events
		boolean removeAddCalendar = false;
		try {
			Intent intent = buildCalendarIntent(race);
			PackageManager pm = getActivity().getPackageManager();
			List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
			if (list == null || list.isEmpty()) {
				removeAddCalendar = true;
			}
		}
		catch (ParseException e) {
			removeAddCalendar = true;
		}

		if (removeAddCalendar) {
			menu.removeItem(R.id.menu_add_to_calendar);
		}

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
		else if (id == R.id.menu_add_to_calendar) {
			try {
				Intent intent = buildCalendarIntent(race);
				startActivityForResult(intent, 0);
			}
			catch (ParseException e) {
				Toast.makeText(getActivity(), R.string.error_parse, Toast.LENGTH_LONG).show();
			}
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Intent buildCalendarIntent(Race race) throws ParseException {

		long startTime = race.getStartAt().getTime();
		long endTime = race.getEndAt().getTime();

		Intent intent;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			intent = new Intent(Intent.ACTION_INSERT);
			intent.setData(Events.CONTENT_URI);
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
			intent.putExtra(Events.TITLE, race.getId());
			intent.putExtra(Events.DESCRIPTION, race.getDescription());
			intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		}
		else {
			intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", startTime);
			intent.putExtra("endTime", endTime);
			intent.putExtra("rrule", "FREQ=YEARLY");
			intent.putExtra("title", race.getId());
		}

		return intent;
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
