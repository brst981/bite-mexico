package com.brst.android.bite.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.home.Home;

public class ActivityFragmentContainer extends BaseContainerFragment {

	private boolean mIsViewInited;
	private static final String TAG = "ActivityFragmentContainer";

	public ActivityFragmentContainer() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "On CreateView");
		return inflater.inflate(R.layout.container_framelayout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "On ActivityCreated");
		if (!mIsViewInited) {
			mIsViewInited = true;
			initView();
		} else {
			getChildFragmentManager().popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}

	}

	private void initView() {
		Log.i(TAG, "On InitView");
		Home home = new Home();
		fragmentPrevious = home.getClass().getSimpleName();
		replaceFragment(home, false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		currentFragment().onActivityResult(requestCode, resultCode, data);

	}
}