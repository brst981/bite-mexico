package com.brst.android.bite.app.home.restaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.brst.android.bitemaxico.app.R;

public class ImageHolderSlideFragment extends Fragment {

	public static String TAG = "ImageHolderSlideFragment";
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";
	public static final String ARG_URL = "url";

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;
	private String imageUrl;
	static ImageLoader imageLoader;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static ImageHolderSlideFragment create(int pageNumber, String url,
			ImageLoader imageLoader) {
		ImageHolderSlideFragment fragment = new ImageHolderSlideFragment();
		ImageHolderSlideFragment.imageLoader = imageLoader;
		Bundle args = new Bundle();

		args.putInt(ARG_PAGE, pageNumber);
		args.putString(ARG_URL, url);

		fragment.setArguments(args);
		return fragment;
	}

	public ImageHolderSlideFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		imageUrl = getArguments().getString(ARG_URL);

	}

	/*
	 * 
	 * id_name id_week rating_bar id_text_desc
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_image_viewer, container, false);

		final ImageView imageView = (ImageView) rootView
				.findViewById(R.id.image_viewer_id);

		imageLoader.get(imageUrl, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Image Load Error: " + error.getMessage());
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					// load image into imageview
					imageView.setImageBitmap(response.getBitmap());
				}
			}
		});

		// imageLoader.get(imageUrl, ImageLoader.getImageListener(imageView,
		// R.drawable.ico_loading, R.drawable.ico_error));

		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}
}
