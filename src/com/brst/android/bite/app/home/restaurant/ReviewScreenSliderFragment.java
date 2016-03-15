package com.brst.android.bite.app.home.restaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.domain.Rating;

public class ReviewScreenSliderFragment extends Fragment {

	public static Rating rating;

	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";
	public static final String ARG_NAME = "name";
	public static final String ARG_DESC = "desc";
	public static final String ARG_DATE = "date";
	public static final String ARG_RATE = "rate";

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;
	private String name;
	private String desc;
	private String rate;
	private String dateOn;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static ReviewScreenSliderFragment create(int pageNumber,
			String name, String desc, String date, String rate) {
		ReviewScreenSliderFragment fragment = new ReviewScreenSliderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		args.putString(ARG_NAME, name);
		args.putString(ARG_DESC, desc);
		args.putString(ARG_DATE, date);
		args.putString(ARG_RATE, rate);

		fragment.setArguments(args);
		return fragment;
	}

	public ReviewScreenSliderFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		name = getArguments().getString(ARG_NAME);
		desc = getArguments().getString(ARG_DESC);
		dateOn = getArguments().getString(ARG_DATE);
		rate = getArguments().getString(ARG_RATE);
	}

	/*
	 * 
	 * id_name id_week rating_bar id_text_desc
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page, container, false);

		TextView nameBy = (TextView) rootView.findViewById(R.id.id_name);
		TextView date = (TextView) rootView.findViewById(R.id.id_week);
		RatingBar rateVal = (RatingBar) rootView.findViewById(R.id.rating_bar);
		TextView description = (TextView) rootView
				.findViewById(R.id.id_text_desc);

		nameBy.setText(name);
		description.setText(desc);
		date.setText(dateOn.split(" ")[0]);
		rateVal.setRating(Float.parseFloat(rate));
		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}
}
