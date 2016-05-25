package com.brst.android.bite.app.locate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.home.RestaurantDetailMain;
import com.brst.android.bite.app.util.GPSTracker;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearMeFragment2 extends Fragment implements
		OnInfoWindowClickListener {

	private static final String MAP_FRAGMENT_TAG = "map";

	private GoogleMap mMap;
	private MapView mMapView;

	private Bundle mBundle;
	SupportMapFragment mSupportMapFragment;

	ImageView referesh;
	private static String TAG = "Map";

	ProgressDialog pDialog;

	LocationManager locationManager;

	boolean isGPSEnabled, isNetworkEnabled;
	boolean canGetLocation;

	long MIN_TIME_BW_UPDATES = 1000;
	float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0f;

	List<Restaurant> listRestaurants;

	List<LatLng> listLat = new ArrayList<LatLng>();

	private Marker marker;
	HashMap<String, Restaurant> markers;
	HashMap<String, String> markersUrl;
	RestaurantDataHandler rDataHandler;

	ImageLoader imageLoader;

	HashMap<String, Bitmap> mapBimap;
	View rootView;

	// GPSTracker class
	GPSTracker gps;

	LatLng latLng;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mBundle = savedInstanceState;
		gps = new GPSTracker(getActivity());
		latLng = getCurrentPosition();

		imageLoader = AppController.getInstance().getImageLoader();
		listRestaurants = new ArrayList<Restaurant>();
		mapBimap = new HashMap<>();
		listLat = new ArrayList<LatLng>();
		markers = new HashMap<String, Restaurant>();
		markersUrl = new HashMap<String, String>();
		rDataHandler = RestaurantDataHandler.getInstance();

		// makeRequestForRestaurantByLatLong(WSConstant.Web.NEAR_ME,
		// String.valueOf(gps.getLatitude()),
		// String.valueOf(gps.getLongitude()));

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(
					R.layout.fragement_layout_restaurant_near_me_2, null);
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
			return rootView;
		}

		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.near_header);
		referesh = ((ImageView) rootView.findViewById(R.id.refresh));

		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) rootView.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);

		// addtextSearchListner();

		addRefershListeneer();

		setUpMapIfNeeded();

		// makeRequestForRestaurantByLatLong(WSConstant.Web.NEAR_ME,
		// String.valueOf(49.2500), String.valueOf(123.1000));

		// makeRequestForRestaurantByLatLong(WSConstant.Web.NEAR_ME,
		// String.valueOf(gps.getLatitude()),
		// String.valueOf(gps.getLongitude()));

		return rootView;
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
			mMap = mMapView.getMap();
			if (mMap != null) {
				setUpMap();
			}

			if (listRestaurants.size() == 0) {
				// String message = String.valueOf("Latitude: "
				// + gps.getLatitude() + "\nLongitude:"
				// + gps.getLongitude());
				String message = String.valueOf("Latitude: " + latLng.latitude
						+ "\nLongitude:" + latLng.longitude);
				showAlertDialog(getActivity(), "Current Location", message);

			}

		}
	}

	// Facebook lifecycle ends

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
						// makeRequestForRestaurantByLatLong(
						// WSConstant.Web.NEAR_ME,
						// String.valueOf(gps.getLatitude()),
						// String.valueOf(gps.getLongitude()));
						makeRequestForRestaurantByLatLong(
								WSConstant.Web.NEAR_ME,
								String.valueOf(latLng.latitude),
								String.valueOf(latLng.longitude));
						dialog.cancel();
						// makeRequestForRestaurantByLatLong(
						// WSConstant.Web.NEAR_ME,
						// String.valueOf(49.2500),
						// String.valueOf(123.1000));

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private void setUpMap() {

		// latLng = new LatLng(49.2500, 123.1000); // vancouver location
		// latLng = getCurrentPosition();

		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		mMap.setMyLocationEnabled(true);

		// Updates the location and zoom of the MapView
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				13);
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mMap.animateCamera(cameraUpdate);
		mMap.setInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity()));
		mMap.setOnInfoWindowClickListener(this);

	}

	private LatLng getCurrentPosition() {

		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();

		// LatLng latLng = new LatLng(49.2500, -123.1000); // vancouver location
		LatLng latLng = new LatLng(latitude, longitude);

		return latLng;
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		setUpMapIfNeeded();

	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
		// mMap = null;
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	// private void addtextSearchListner() {
	//
	// textSearch
	// .setOnEditorActionListener(new TextView.OnEditorActionListener() {
	// @Override
	// public boolean onEditorAction(TextView v, int actionId,
	// KeyEvent event) {
	// if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	// if (!textSearch.getText().toString().trim()
	// .equals("")) {
	// makeRequestForRestaurant(Web.NEAR_BY,
	// textSearch.getText().toString().trim());
	// UI.hideKeyboard(getActivity(), textSearch);
	// } else {
	// LogMsg.LOG(getActivity(), "Empty fields");
	// }
	// return true;
	// }
	// return false;
	// }
	// });
	//
	// textSearch.addTextChangedListener(new TextWatcher() {
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before,
	// int count) {
	//
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	// textSearch.setCompoundDrawables(null, null, null, null);
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	//
	// }
	// });
	//
	// }

	private void addRefershListeneer() {

		referesh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = String.valueOf("Your Current lat: "
						+ gps.getLatitude() + " lng:" + gps.getLongitude());
				showAlertDialog(getActivity(), "Current Location", message);

			}
		});

	}

	// Service to fetch restaurant list on the basis of pin

	private void makeRequestForRestaurant(String product,
			final String searchString) {

		String url = Web.HOST + product;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.i(TAG + "On Response", response);
						parseResturant(response);
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						// VolleyLog.e(TAG, "Error: " + error.getMessage());
						Log.i(TAG, "Error: " + error.getMessage());
						Log.i(TAG, "Error: " + error.getCause());
						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put(DataKey.DATA_SEARCH, searchString);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	protected void parseResturant(String response) {

		UI.hideProgressDialog();

		listRestaurants = JsonParser.getRestaurantsDetail(response);

		if (listRestaurants != null && listRestaurants.size() != 0) {
			upDateMapElement();
		} else {
			// Toast.makeText(getActivity(), "No data to Update",
			// Toast.LENGTH_LONG).show();
			LogMsg.showAlertDialog(getActivity(), "Map",
					"No Restaurants Found", false);
		}

	}

	private void upDateMapElement() {
		LatLng rlatLng = null;

		mMap.clear();
		for (int i = 0; i < listRestaurants.size(); i++) {
			Restaurant r = listRestaurants.get(i);
			rlatLng = new LatLng(Double.parseDouble(r.getLatitude()),
					Double.parseDouble(r.getLongitude()));
			marker = mMap.addMarker(new MarkerOptions()
					.position(rlatLng)
					.title(r.getName())
					.snippet(r.getAddress())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_map)));
			markers.put(marker.getId(), r);
			markersUrl.put(marker.getId(), r.getImage());
		}

		mMap.moveCamera(CameraUpdateFactory.newLatLng(rlatLng));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

	}

	private void makeRequestForRestaurantByLatLong(String product,
			final String lat, final String lang) {

		String url = Web.HOST + product;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e(TAG + "On Response", response);
						parseResturant(response);
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// VolleyLog.e(TAG, "Error: " + error.getMessage());
						Log.i(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				
				Log.e("latitudeeghghe",lat);
				params.put(DataKey.LAT, lat);
				params.put(DataKey.LONG, lang);
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	private void LoadImagesBitmapArray(final String markerId,
			HashMap<String, Restaurant> markerMap) {
		String uri = markerMap.get(markerId).getImage();
		imageLoader.get(uri, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i(TAG, "Image Load Error: " + error.getMessage());
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					// load image into imageview
					mapBimap.put(markerId, response.getBitmap());
					// marker.showInfoWindow();
				}
			}
		});

	}

	public class InfoWindowAdapterMarker implements InfoWindowAdapter {

		private Context mContext;

		public InfoWindowAdapterMarker(Context context) {
			mContext = context;
		}

		@Override
		public View getInfoContents(final Marker marker) {

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.custom_info_window, null);

			final ImageView image = ((ImageView) view.findViewById(R.id.badge));
			final TextView titleUi = ((TextView) view.findViewById(R.id.title));
			final TextView snippetUi = ((TextView) view
					.findViewById(R.id.snippet));

			if (marker != null && marker.getId() != null) {

				titleUi.setText(marker.getTitle());
				snippetUi.setText(marker.getSnippet());
				// image.setImageBitmap(mapBimap.get(marker.getId()));

				imageLoader.get(markersUrl.get(marker.getId()),
						new ImageListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Log.e(TAG,
										"Image Load Error: "
												+ error.getMessage());
							}

							@Override
							public void onResponse(ImageContainer response,
									boolean arg1) {
								if (response.getBitmap() != null) {
									image.setImageBitmap(response.getBitmap());
									// load image into imageview
									// mapBimap.put(markerId,
									// response.getBitmap());
									// marker.showInfoWindow();
								}
							}
						});
			}
			return view;
		}
		@Override
		public View getInfoWindow(Marker arg0) {
			return null;
		}

	}

	// Adapters

	@Override
	public void onInfoWindowClick(Marker marker) {

		Restaurant res = markers.get(marker.getId());
		if (res != null) {
			rDataHandler.setRestaurant(res);
			// RestaurantDetailMain fragment = new RestaurantDetailMain();
			// ((BaseContainerFragment) getParentFragment()).replaceFragment(
			// fragment, true);
			RestaurantDetailMain fragment = new RestaurantDetailMain();
			// getActivity().getFragmentManager().beginTransaction()
			// .replace(R.id.container_framelayout, fragment);
			((BaseContainerFragment) getParentFragment()).replaceFragmentMap(
					fragment, true);
		} else {
			LogMsg.LOG(getActivity(), "No Info Available");
		}
		// SearchFragment fragment = new SearchFragment();
		// ((BaseContainerFragment)
		// getParentFragment()).replaceFragment(fragment,
		// true);

	}

}
