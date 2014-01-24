package com.jasonrobinson.racer.ui.base;

import roboguice.fragment.RoboListFragment;

public class BaseListFragment extends RoboListFragment {

	BaseFragmentImpl mImpl = new BaseFragmentImpl(this);

	public <T> T castActivity(Class<T> clz) {

		return mImpl.castActivity(clz);
	}
}
