package com.brst.android.bite.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bitemaxico.app.R;

public class UserGuideActivity extends FragmentActivity {

	TextView skipGuide;

	SharedPreferences sharedpreferences;
	Intent mIntent;
	String mString_message, mString_price;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_help);
		Log.e("user acitivyt","guide");

		sharedpreferences = getSharedPreferences(BiteBc.MyPREFERENCES,
				Context.MODE_PRIVATE);

		skipGuide = (TextView) findViewById(R.id.skipGuide);
		mIntent = getIntent();
		mString_message = mIntent.getStringExtra("Plan_message");
		mString_price = mIntent.getStringExtra("Plan_amount");
		if (mString_message == null) {
			mString_message = "";
		}

		showAlertDialog(UserGuideActivity.this, "Message",
				"Thank you for purchasing a " + mString_message
						+ " membership to Bite BC");

		skipGuide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.USER_PLAN, getIntent().getExtras()
						.getString(BiteBc.USER_PLAN));
				editor.putBoolean(BiteBc.USER_GUIDE, true);
				editor.commit();

				Intent intent = new Intent(UserGuideActivity.this,
						HomeActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();

			}
		});

	}

	public void showAlertDialog(Context context, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						// finish();

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public void onBackPressed() {
		LogMsg.showAlertDialog(UserGuideActivity.this, "Message",
				"Please click on Next button to complete registration process",
				false);
		// super.onBackPressed();
	}
}
