package com.jasonrobinson.racer.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.network.RaceClient;
import com.jasonrobinson.racer.ui.base.BaseListFragment;

public class LadderFragment extends BaseListFragment {

	private static final String ARG_ID = "id";

	private LadderTask mTask;

	public static final LadderFragment newInstance(String id) {

		LadderFragment fragment = new LadderFragment();

		Bundle args = new Bundle();
		args.putString(ARG_ID, id);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			String id = getArguments().getString(ARG_ID);
			if (id != null) {
				fetchLadder(id);
			}
		}
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		if (mTask != null) {
			mTask.cancel(true);
		}
	}

	public void fetchLadder(String id) {

		if (mTask != null) {
			mTask.cancel(true);
		}

		mTask = new LadderTask();
		mTask.execute(id);
	}

	private class LadderTask extends AsyncTask<String, Void, Ladder> {

		@Override
		protected Ladder doInBackground(String... params) {

			String id = params[0];
			return new RaceClient().fetchLadder(id, 0, 100);
		}

		@Override
		protected void onPostExecute(Ladder result) {

			super.onPostExecute(result);
		}
	}
}
