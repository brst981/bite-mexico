package com.brst.android.bite.app.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brst.android.bitemaxico.app.R;

public class PlaceHolderContainerFragment extends BaseContainerFragment {

	private boolean mIsViewInited;
	private static final String TAG = "PlaceHolderFragement";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "oncreateview");
		return inflater.inflate(R.layout.container_framelayout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "container on activity created");
		if (!mIsViewInited) {
			mIsViewInited = true;
			initView();
		}
	}

	private void initView() {
		Log.e(TAG, "tab 1 init view");
		replaceFragment(new PlaceholderFragment(), false);
	}

}