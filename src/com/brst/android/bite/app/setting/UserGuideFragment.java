package com.brst.android.bite.app.setting;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.viewpagerindicator.CirclePageIndicator;

public class UserGuideFragment extends Fragment {
	private ViewPager _mViewPager;
	private MainPagerAdapter _adapter;
	CirclePageIndicator mIndicator;
	ArrayList<Integer> mArrayList;
	ArrayList<String> maArrayList;
	LayoutInflater infl;
	ImageView imageView;
	TextView mTextView;
	TextView mButton_back;
	Button btn_back;
	SharedPreferences sharedpreferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.slider_image, container,
				false);
		mArrayList = new ArrayList<>();
		maArrayList = new ArrayList<>();
		_adapter = new MainPagerAdapter();
		_mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
		mIndicator = (CirclePageIndicator) rootView
				.findViewById(R.id.indicator);

		sharedpreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);

		maArrayList.add("Como Funciona");
		maArrayList.add("Ofertas");
		maArrayList.add("Registrarse");

		maArrayList.add("Membresía");
		// maArrayList.add("karan");

		mArrayList.add(R.drawable.first_show_);
		mArrayList.add(R.drawable.second_show_);
		mArrayList.add(R.drawable.third_show_);
		mArrayList.add(R.drawable.four_show_);

		// mArrayList.add(R.drawable.ic_launcher);
		// Create an initial view to display; must be a subclass of FrameLayout.
		infl = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < mArrayList.size(); i++) {

			LinearLayout v1 = (LinearLayout) infl.inflate(
					R.layout.customimage_, null);
			imageView = (ImageView) v1.findViewById(R.id._custom);
			mTextView = (TextView) v1.findViewById(R.id.header_title);
			// mButton_back = (TextView)
			// v1.findViewById(R.id.btn_back_getstarted);
			imageView.setImageResource(mArrayList.get(i));
			mTextView.setText(maArrayList.get(i));
			// mButton_back.setVisibility(View.GONE);

			addView(v1);

		}
		_mViewPager.setAdapter(_adapter);
		mIndicator.setViewPager(_mViewPager);

		return rootView;
	}

	public void addView(View newPage) {
		int pageIndex = _adapter.addView(newPage);
		// You might want to make "newPage" the currently displayed page:
		// Log.e("outside", ""+pageIndex);
		// if(pageIndex==2){
		// Log.e("inside", ""+pageIndex);
		// }
		_mViewPager.setCurrentItem(pageIndex, true);
	}

}
