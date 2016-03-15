package com.brst.android.bite.app.locate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.util.GPSTracker;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.UI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapFragemt2 extends Fragment {

	private static String TAG = "MemberShipStepOneFragment";

	public static final String ARG_REST_ID = "id";
	public static final String NAME = "name";

	RestaurantDataHandler rHandler;
	Restaurant restaurant;

	String id;

	private GoogleMap mMap;
	private MapView mMapView;
	SupportMapFragment mSupportMapFragment;
	TextView textSearch;

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

	// GPSTracker class
	GPSTracker gps;

	private CameraPosition cp;

	public static MyMapFragemt2 create(String id) {
		MyMapFragemt2 fragment = new MyMapFragemt2();
		Bundle args = new Bundle();
		args.putString(ARG_REST_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = AppController.getInstance().getImageLoader();
		gps = new GPSTracker(getActivity());
		id = getArguments().getString(ARG_REST_ID);
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();
		markers = new HashMap<String, Restaurant>();
		markersUrl = new HashMap<String, String>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragement_layout_restaurant_map, null);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.near_header);
		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) rootView.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);

		return rootView;
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
			mMap = mMapView.getMap();
			if (mMap != null) {
				LatLng latLng = getCurrentPosition();

				mMap.getUiSettings().setMyLocationButtonEnabled(false);
				// mMap.setMyLocationEnabled(true);

				// Updates the location and zoom of the MapView
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						latLng, 12);
				mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				mMap.animateCamera(cameraUpdate);
				makeRequestForRestaurant(Web.NEAR_BY, restaurant.getZipcode());
			}
		}
	}

	private LatLng getCurrentPosition() {

		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();
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
		mMapView.onPause();
		super.onPause();
		mMap = null;
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
						Log.i(TAG, "Error: " + error);
						Log.i(TAG, "Error: " + error.getCause());

						// JSONObject jsonObject = new JSONObject( responseBody
						// );
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
		// TODO Auto-generated method stub
		UI.hideProgressDialog();

		listRestaurants = JsonParser.getRestaurantsDetail(response);
		Restaurant r = null;

		if (listRestaurants != null && listRestaurants.size() != 0) {
			for (int i = 0; i < listRestaurants.size(); i++) {

				if (listRestaurants.get(i).getId().equals(id)) {
					r = listRestaurants.get(i);
					break;
				}

			}
			if (r != null) {
				upDateMapElement(r);
			} else {
				Toast.makeText(getActivity(), "Sorry No location information",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getActivity(), "Sorry No location information",
					Toast.LENGTH_LONG).show();
		}

	}

	private void upDateMapElement(Restaurant r) {
		LatLng latLng = null;

		latLng = new LatLng(Double.parseDouble(r.getLatitude()),
				Double.parseDouble(r.getLongitude()));
		marker = mMap.addMarker(new MarkerOptions().position(latLng)
				.title(r.getName()).snippet(r.getAddress())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map)));
		// markers.put(marker.getId(), r);
		markersUrl.put(marker.getId(), r.getImage());

		mMap.setInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity()));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

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
								Log.i(TAG,
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

}
