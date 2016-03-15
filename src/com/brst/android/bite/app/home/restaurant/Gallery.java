package com.brst.android.bite.app.home.restaurant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;

public class Gallery extends Fragment {

	private final String TAG = "Gallery.class";

	private static int NUM_PAGES;

	GridView gridview;

	RestaurantDataHandler rHandler;
	Restaurant restaurant;
	ImageLoader imageLoader;

	List<String> mThumbIds = new ArrayList<String>();

	ViewPager viewPager;
	ScreenSlidePagerAdapter mPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = AppController.getInstance().getImageLoader();
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();

		if (restaurant.getGalleryImages() != null) {
			mThumbIds = restaurant.getGalleryImages();
		} else {
			mThumbIds = new ArrayList<String>();
		}

		NUM_PAGES = mThumbIds.size();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.fragment_layout_gallery, null);

		// gridview = (GridView) rootView.findViewById(R.id.gridview);
		// gridview.setAdapter(new ImageAdapter(getActivity()));

		if (NUM_PAGES == 0) {
			rootView.findViewById(R.id.noReview).setVisibility(View.VISIBLE);
		} else {
			rootView.findViewById(R.id.noReview).setVisibility(View.GONE);
			viewPager = (ViewPager) rootView.findViewById(R.id.myviewpager);

			mPagerAdapter = new ScreenSlidePagerAdapter(
					getChildFragmentManager());
			viewPager.setAdapter(mPagerAdapter);
		}
		return rootView;

	}

	/*
	 * Data view adapter
	 */

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			return ImageHolderSlideFragment.create(position,
					mThumbIds.get(position), imageLoader);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater inflater;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			final ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
			// imageView.setImageResource(mThumbIds.get(position));
			imageLoader.get(restaurant.getImage(), new ImageListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Log.i(TAG, "Image Load Error: " + error.getMessage());
				}

				@Override
				public void onResponse(ImageContainer response, boolean arg1) {
					if (response.getBitmap() != null) {
						// load image into imageview
						imageView.setImageBitmap(response.getBitmap());
					}
				}
			});
			return imageView;
		}

	}
}
