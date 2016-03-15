package com.brst.android.bite.app.setting;

import com.brst.android.bitemaxico.app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HowItWorkFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_layout_bite_bc_funtions, container, false);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.setting_how_it_work);
		return rootView;
	}
}
