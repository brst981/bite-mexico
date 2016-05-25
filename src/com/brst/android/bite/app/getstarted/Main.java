package com.brst.android.bite.app.getstarted;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;

public class Main extends Activity {
	Button mButton;
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_getstarted);
		mButton = (Button) findViewById(R.id.button_getstarted);

		sharedpreferences = getSharedPreferences(BiteBc.MyPREFERENCES,
				Context.MODE_PRIVATE);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(Main.this, Slider.class);
				startActivity(mIntent);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();
			}
		});

		// mButtonbutton_fast.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Editor editor = sharedpreferences.edit();
		//
		// editor.putString("GET_STARTED", "CASE_END");
		//
		// editor.commit();
		// Intent mIntent = new Intent(Main.this, MainActivity_2.class);
		// startActivity(mIntent);
		// overridePendingTransition(R.anim.slide_in_left,
		// R.anim.slide_out_right);
		// finish();
		// }
		// });
	}

}
