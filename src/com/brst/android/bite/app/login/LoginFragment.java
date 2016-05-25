package com.brst.android.bite.app.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brst.android.bitemaxico.app.R;

public class LoginFragment extends Fragment {
	private static final String TAG = "LoginFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		// LoginButton authButton = (LoginButton) rootView
		// .findViewById(R.id.login_button);
		// authButton.setFragment(this);
		// authButton.setReadPermissions(Arrays.asList("email", "user_status"));
		return rootView;
	}

}
