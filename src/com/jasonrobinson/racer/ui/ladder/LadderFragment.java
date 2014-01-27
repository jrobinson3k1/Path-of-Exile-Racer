package com.jasonrobinson.racer.ui.ladder;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
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
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.ui.base.BaseListFragment;
import com.jasonrobinson.racer.ui.ladder.WatchCharacterDialogFragment.WatchCharacterDialogListener;

public class LadderFragment extends BaseListFragment {

	private static final String ARG_ID = "id";

	private static final String TAG_WATCH_CHARACTER = "watchCharacter";

	private String mId;

	private String mWatchedCharacter;
	private int mWatchedCharacterRank;

	private Timer mTimer;
	private boolean mRefreshing;

	private LadderAdapter mAdapter;
	private LadderTask mTask;

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

		getAnalyticsManager().trackEvent("Ladder", "View", mId);
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
				fetchLadder(mId);
			}
		}
	}

	@Override
	public void onResume() {

		super.onResume();
		if (mTask != null && mTask.getStatus() != Status.RUNNING && mId != null) {
			fetchLadder(mId);
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
			fetchLadder(mId);
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
				mWatchedCharacterRank = 0;
				fetchLadder(mId);
			}

			@Override
			public void onCharacterSelected(String character) {

				getAnalyticsManager().trackEvent("Ladder", "Use", "Character Watcher");
				mWatchedCharacter = character;
				fetchLadder(mId);
			}

			@Override
			public void onCancel() {

				// no-op
			}
		});

		fragment.show(ft, TAG_WATCH_CHARACTER);
	}

	public void fetchLadder(String id) {

		mId = id;

		if (mTask != null) {
			mTask.cancel(true);
		}

		mTask = new LadderTask(mWatchedCharacter);
		mTask.execute(mId);
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

	private Entry findEntry(List<Entry> entries, String character) {

		for (Entry entry : entries) {
			if (entry.getCharacter().getName().equalsIgnoreCase(character)) {
				return entry;
			}
		}

		return null;
	}

	private class LadderTask extends AsyncTask<String, Void, Ladder> {

		private static final int LIMIT = 200;

		private String mCharacter;

		public LadderTask(String character) {

			mCharacter = character;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			setRefreshing(true);
		}

		@Override
		protected Ladder doInBackground(String... params) {

			String id = params[0];
			RaceClient client = new RaceClient();
			try {
				Ladder ladder = client.fetchLadder(id, 0, LIMIT);
				if (TextUtils.isEmpty(mCharacter)) {
					return ladder;
				}

				Entry entry = findEntry(ladder.getEntries(), mCharacter);
				if (entry != null) {
					ladder.getEntries().add(0, entry);
					return ladder;
				}

				if (mWatchedCharacterRank == 0) { // Linear search

					for (int offset = LIMIT; offset < ladder.getTotal(); offset += LIMIT) {
						Ladder nextLadder = client.fetchLadder(id, offset, LIMIT);
						entry = findEntry(nextLadder.getEntries(), mCharacter);
						if (entry != null) {
							ladder.getEntries().add(0, entry);
							break;
						}
					}
				}
				else { // Fan search
					int startOffset = mWatchedCharacterRank - LIMIT / 2;
					int totalQueries = (int) Math.ceil((double) ladder.getTotal() / LIMIT);
					for (int i = 0; i < totalQueries; i++) {
						int offset;
						if (i % 2 == 0) {
							offset = startOffset + i * LIMIT;
						}
						else {
							offset = (startOffset - 1) - i * LIMIT;
						}

						if (offset < 0 || offset > ladder.getTotal()) {
							continue;
						}

						Ladder nextLadder = client.fetchLadder(id, offset, LIMIT);
						entry = findEntry(nextLadder.getEntries(), mCharacter);
						if (entry != null) {
							ladder.getEntries().add(0, entry);
							break;
						}
					}
				}

				return ladder;
			}
			catch (SocketTimeoutException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Ladder result) {

			super.onPostExecute(result);
			setRefreshing(false);

			if (result != null) {
				Entry[] entries = result.getEntries().toArray(new Entry[0]);
				if (mAdapter == null) {
					mAdapter = new LadderAdapter(entries);
					setListAdapter(mAdapter);
				}
				else {
					mAdapter.setEntries(entries);
				}

				if (!TextUtils.isEmpty(mCharacter)) {
					Entry entry = findEntry(result.getEntries(), mCharacter);
					if (entry != null) {
						mWatchedCharacterRank = entry.getRank();
					}
					else {
						Toast.makeText(getActivity(), getString(R.string.character_not_found, mCharacter), Toast.LENGTH_LONG).show();
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
				fetchLadder(mId);
			}
		}
	};
}
