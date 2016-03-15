package com.brst.android.bite.app.setting;

import com.brst.android.bitemaxico.app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutUsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout_about_us,
				container, false);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.setting_about_us);
		return rootView;
	}
}
