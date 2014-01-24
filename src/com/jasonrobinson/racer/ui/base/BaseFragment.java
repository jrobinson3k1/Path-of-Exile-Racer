package com.jasonrobinson.racer.ui.base;

import roboguice.fragment.RoboFragment;

public abstract class BaseFragment extends RoboFragment {

	BaseFragmentImpl mImpl = new BaseFragmentImpl(this);

	public <T> T castActivity(Class<T> clz) {

		return mImpl.castActivity(clz);
	}
}
