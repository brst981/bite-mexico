package com.brst.android.bite.app.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;

public class PlaceholderFragment extends Fragment {

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_membership,
				container, false);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.header_coming_soon);
		return rootView;
	}
}