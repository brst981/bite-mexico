package com.brst.android.bite.app.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;

public class UserFragment extends Fragment {

	UserDataHandler uDataHandler;
	User user;

	SharedPreferences mSharedPreferences;

	int pos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uDataHandler = UserDataHandler.getInstance();
		user = uDataHandler.getUser();
		mSharedPreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout_user,
				container, false);

		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.setting_user);

		EditText firstName = (EditText) rootView.findViewById(R.id.firstName);
		EditText lastName = (EditText) rootView.findViewById(R.id.lastName);
		EditText email = (EditText) rootView.findViewById(R.id.email);

		firstName.setText(mSharedPreferences.getString(BiteBc.FIRSTNAME, ""));
		lastName.setText(mSharedPreferences.getString(BiteBc.LASTNAME, ""));
		email.setText(mSharedPreferences.getString(BiteBc.USER_ID, ""));

		return rootView;
	}

}
