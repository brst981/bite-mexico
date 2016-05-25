package com.brst.android.bite.app.getstarted;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brst.android.bite.app.MainActivity_2;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.viewpagerindicator.CirclePageIndicator;

public class Slider extends Activity {
	private ViewPager _mViewPager;
	private MainPagerAdapter _adapter;
	CirclePageIndicator mIndicator;
	ArrayList<Integer> mArrayList;
	ArrayList<String> maArrayList;
	LayoutInflater infl;
	ImageView imageView;
	TextView mTextView;
	TextView mButton_back;
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.slider_image);
		mArrayList = new ArrayList<>();
		maArrayList = new ArrayList<>();
		_adapter = new MainPagerAdapter();
		_mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

		sharedpreferences = getSharedPreferences(BiteBc.MyPREFERENCES,
				Context.MODE_PRIVATE);

		maArrayList.add("Como Funciona");
		maArrayList.add("Ofertas");
		maArrayList.add("Registrarse");

		maArrayList.add("Membresía");

		mArrayList.add(R.drawable.first_show_);
		mArrayList.add(R.drawable.second_show_);
		mArrayList.add(R.drawable.third_show_);
		mArrayList.add(R.drawable.four_show_);

		infl = (LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < mArrayList.size(); i++) {

			LinearLayout v1 = (LinearLayout) infl.inflate(R.layout.customimage,
					null);
			imageView = (ImageView) v1.findViewById(R.id._custom);
			mTextView = (TextView) v1.findViewById(R.id.title);
			mButton_back = (TextView) v1.findViewById(R.id.btn_back_getstarted);
			imageView.setImageResource(mArrayList.get(i));
			mTextView.setText(maArrayList.get(i));
			mButton_back.setVisibility(View.GONE);
			if (i == 3) {
				mButton_back.setVisibility(View.VISIBLE);
				mButton_back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Editor editor = sharedpreferences.edit();

						editor.putString("GET_STARTED", "CASE_END");

						editor.commit();
						Intent intent_home = new Intent(Slider.this,
								MainActivity_2.class);
						startActivity(intent_home);
						overridePendingTransition(R.anim.slide_in_left,
								R.anim.slide_out_right);
						finish();
					}
				});
			} else {
				mButton_back.setVisibility(View.GONE);
			}

			addView(v1);

		}
		_mViewPager.setAdapter(_adapter);
		mIndicator.setViewPager(_mViewPager);
	}

	// Here's what the app should do to add a view to the ViewPager.
	public void addView(View newPage) {
		int pageIndex = _adapter.addView(newPage);
		// You might want to make "newPage" the currently displayed page:
		// Log.e("outside", ""+pageIndex);
		// if(pageIndex==2){
		// Log.e("inside", ""+pageIndex);
		// }
		_mViewPager.setCurrentItem(pageIndex, true);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Editor editor = sharedpreferences.edit();

		editor.putString("GET_STARTED", "CASE_END");

		editor.commit();
		Intent intent_home = new Intent(Slider.this, MainActivity_2.class);
		startActivity(intent_home);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();
	}

}
