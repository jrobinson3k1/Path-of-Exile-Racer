package com.jasonrobinson.racer.ui.ladder;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.adapter.LadderAdapter;
import com.jasonrobinson.racer.async.LadderAsyncTask;
import com.jasonrobinson.racer.async.LadderAsyncTask.LadderParams;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.PoeClass;
import com.jasonrobinson.racer.ui.base.BaseListFragment;
import com.jasonrobinson.racer.ui.ladder.WatchCharacterDialogFragment.WatchCharacterDialogListener;
import com.jasonrobinson.racer.util.LadderUtils;

public class LadderFragment extends BaseListFragment {

	private static final String ARG_ID = "id";

	private static final String TAG_WATCH_CHARACTER = "watchCharacter";

	private String mId;

	private String mWatchedCharacter;
	private PoeClass mWatchedCharacterClass;
	private PoeClass mWatchedClass;

	private Timer mTimer;
	private boolean mRefreshing;

	private LadderAdapter mAdapter;
	private LadderTask mTask;

	private boolean mTracked;

	public static final LadderFragment newInstance(String id) {

		LadderFragment fragment = new LadderFragment();

		Bundle args = new Bundle();
		args.putString(ARG_ID, id);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		setEmptyText(getString(R.string.no_rankings));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			String id = getArguments().getString(ARG_ID);
			if (id != null) {
				mId = id;
				fetchLadder();
			}
		}
	}

	@Override
	public void onResume() {

		super.onResume();
		if (mTask != null && mTask.getStatus() != Status.RUNNING && mId != null) {
			fetchLadder();
		}
	}

	@Override
	public void onPause() {

		super.onPause();
		if (mTimer != null) {
			mTimer.cancel();
		}
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		if (mTask != null) {
			mTask.cancel(true);
		}

		if (mTimer != null) {
			mTimer.cancel();
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
			fetchLadder();
		}
		else if (id == R.id.menu_watch_character) {
			showCharacterDialog();
		}
		else {
			super.onOptionsItemSelected(item);
		}

		return true;
	}

	private void showCharacterDialog() {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(TAG_WATCH_CHARACTER);
		if (prev != null) {
			ft.remove(prev);
		}

		WatchCharacterDialogFragment fragment = WatchCharacterDialogFragment.newInstance(mWatchedCharacter);
		fragment.setWatchCharacterDialogListener(new WatchCharacterDialogListener() {

			@Override
			public void onRemove() {

				getAnalyticsManager().trackEvent("Ladder", "Remove", "Character Watcher");
				mWatchedCharacter = null;
				mWatchedCharacterClass = null;
				fetchLadder();
			}

			@Override
			public void onCharacterSelected(String character) {

				getAnalyticsManager().trackEvent("Ladder", "Use", "Character Watcher");
				mWatchedCharacter = character;
				fetchLadder();
			}

			@Override
			public void onCancel() {

				// no-op
			}
		});

		fragment.show(ft, TAG_WATCH_CHARACTER);
	}

	private void fetchLadder() {

		fetchLadder(mId, mWatchedClass);
	}

	public void fetchLadder(String id, PoeClass poeClass) {

		mId = id;
		mWatchedClass = poeClass;

		if (mTask != null) {
			mTask.cancel(true);
		}

		LadderParams params = new LadderParams(id, 0, 20, mWatchedClass, mWatchedCharacter, mWatchedCharacterClass);

		mTask = new LadderTask();
		mTask.execute(params);

		if (!mTracked) {
			mTracked = true;
			getAnalyticsManager().trackEvent("Ladder", "View", mId);
		}
	}

	private void setRefreshing(boolean refreshing) {

		mRefreshing = refreshing;
		getActivity().supportInvalidateOptionsMenu();

		if (refreshing) {
			if (mTimer != null) {
				mTimer.cancel();
			}
		}
		else {
			mTimer = new Timer();
			mTimer.schedule(new RefreshTimerTask(), 30000);
		}
	}

	private class LadderTask extends LadderAsyncTask {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			setRefreshing(true);
		}

		@Override
		protected void onPostExecute(Ladder result) {

			super.onPostExecute(result);
			setRefreshing(false);

			if (result != null) {
				Entry[] entries = result.getEntries().toArray(new Entry[0]);
				if (mAdapter == null) {
					mAdapter = new LadderAdapter(entries, mWatchedClass != null);
					setListAdapter(mAdapter);
				}
				else {
					mAdapter.setEntries(entries, mWatchedClass != null);
				}

				if (!TextUtils.isEmpty(mWatchedCharacter)) {
					Entry entry = LadderUtils.findEntry(result.getEntries(), mWatchedCharacter);
					if (entry == null) {
						Toast.makeText(getActivity(), getString(R.string.character_not_found, mWatchedCharacter), Toast.LENGTH_LONG).show();
						mWatchedCharacter = null;
					}
					else {
						mWatchedCharacterClass = PoeClass.getClassForName(entry.getCharacter().getPoeClass());
					}
				}
			}
			else {
				Toast.makeText(getActivity(), R.string.error_unavailable, Toast.LENGTH_LONG).show();
			}
		}
	}

	private class RefreshTimerTask extends TimerTask {

		@Override
		public void run() {

			if (mId != null) {
				fetchLadder();
			}
		}
	};
}
