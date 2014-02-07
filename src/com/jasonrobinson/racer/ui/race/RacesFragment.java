package com.jasonrobinson.racer.ui.race;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.RaceAdapter;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.base.BaseListFragment;

public class RacesFragment extends BaseListFragment {

	private static final String TAG = RacesFragment.class.getSimpleName();

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
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		setEmptyText(getString(R.string.races_unavailable));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.races_context_menu, menu);

		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
		Race race = (Race) getListView().getItemAtPosition(adapterInfo.position);

		if (TextUtils.isEmpty(race.getUrl())) {
			menu.removeItem(R.id.menu_forum_post);
		}

		// Check if the device can handle calendar intents
		Intent intent = buildCalendarIntent(race);
		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
		if (list == null || list.isEmpty() || race.isFinished() || race.isInProgress()) {
			menu.removeItem(R.id.menu_add_to_calendar);
		}
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
			Intent intent = buildCalendarIntent(race);
			startActivityForResult(intent, 0);
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

		if (race.isRegistrationOpen() || race.isFinished()) {
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

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private Intent buildCalendarIntent(Race race) {

		long startTime = race.getStartAt().getTime();
		long endTime = race.getEndAt().getTime();

		Intent intent;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			intent = new Intent(Intent.ACTION_INSERT);
			intent.setData(Events.CONTENT_URI);
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
			intent.putExtra(Events.TITLE, race.getRaceId());
			intent.putExtra(Events.DESCRIPTION, race.getDescription());
			intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				intent.putExtra(CalendarContract.Events.CUSTOM_APP_PACKAGE, getActivity().getPackageName());
			}
		}
		else {
			intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", startTime);
			intent.putExtra("endTime", endTime);
			intent.putExtra("rrule", "FREQ=YEARLY");
			intent.putExtra("title", race.getRaceId());
		}

		return intent;
	}

	public void setData(List<Race> races) {

		setListAdapter(new RaceAdapter(getActivity(), races));
		setListShown(true);
	}

	public interface RacesCallback {

		public void showUrl(String url);

		public void showLadder(Race race);
	}
}
