package com.jasonrobinson.racer.ui.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class BaseFragmentImpl {

	private Fragment mFragment;

	public BaseFragmentImpl(Fragment fragment) {

		mFragment = fragment;
	}

	public Activity getActivity() {

		return mFragment.getActivity();
	}

	public <T> T castActivity(Class<T> clz) {

		Activity activity = getActivity();
		if (activity == null) {
			throw new IllegalStateException("fragment is not attached to an activity");
		}

		try {
			return clz.cast(activity);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.getClass().getSimpleName() + " must implement " + clz.getSimpleName());
		}
	}
}
