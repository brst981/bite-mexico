package com.brst.android.bite.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.membership.MembershipSelectFragment;
import com.brst.android.bite.app.search.SearchFragment;
import com.brst.android.bite.app.setting.SettingFragment;

public class SettingFragmentContainer extends BaseContainerFragment {

	private boolean mIsViewInited;
	private static final String TAG = "ActivityFragmentContainer";

	public SettingFragmentContainer() {
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
		SettingFragment settingFragment = new SettingFragment();
		fragmentPrevious = settingFragment.getClass().getSimpleName();
		replaceFragment(settingFragment, false);
	}

}
