package com.brst.android.bite.app.home.restaurant;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Rating;
import com.brst.android.bite.app.domain.Restaurant;

public class Reviews extends Fragment implements OnClickListener {

	ViewPager viewPager;
	ScreenSlidePagerAdapter mPagerAdapter;

	ImageView previous, next, edit;

	private static int NUM_PAGES = 5;

	RestaurantDataHandler rHandler;
	Restaurant restaurant;

	List<Rating> ratings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();

		if (restaurant.getRatings() != null) {
			ratings = restaurant.getRatings();
		} else {
			ratings = new ArrayList<Rating>();
		}

		NUM_PAGES = ratings.size();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.fragment_layout_reviews, null);

		previous = (ImageView) rootView.findViewById(R.id.id_btn_slide_left);
		next = (ImageView) rootView.findViewById(R.id.id_btn_slide_right);
		edit = (ImageView) rootView.findViewById(R.id.id_edit_btn);
		edit.setOnClickListener(this);

		if (NUM_PAGES == 0) {
			rootView.findViewById(R.id.noReview).setVisibility(View.VISIBLE);

		} else {
			rootView.findViewById(R.id.noReview).setVisibility(View.GONE);
			viewPager = (ViewPager) rootView.findViewById(R.id.myviewpager);
			mPagerAdapter = new ScreenSlidePagerAdapter(
					getChildFragmentManager());
			viewPager.setAdapter(mPagerAdapter);
			previous.setOnClickListener(this);
			next.setOnClickListener(this);
		}

		return rootView;
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			String name = ratings.get(position).getRatingBy();
			String desc = ratings.get(position).getQuotes();
			String date = ratings.get(position).getRatingDate();
			String rate = ratings.get(position).getRatingValue() + "";
			return ReviewScreenSliderFragment.create(position, name, desc,
					date, rate);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.id_btn_slide_left:
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
			break;
		case R.id.id_btn_slide_right:
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
			break;
		case R.id.id_edit_btn:
			
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.restarant_detail_frame_container,
							new AddReviewFragment()).commit();
			break;

		default:
			break;
		}
	}

}
