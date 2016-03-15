package com.brst.android.bite.app.locate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.home.RestaurantDetailMain;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.LocationUtils;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author ShalviSharma
 * 
 *         ${tags}
 */
public class NearMeFragment3 extends Fragment implements
		OnInfoWindowClickListener, LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private static String TAG = "Map";
	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;
	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = false;

	private GoogleMap mMap;
	private MapView mMapView;
	private Marker marker;

	ImageView referesh;
	ProgressDialog pDialog;

	List<Restaurant> listRestaurants;
	HashMap<String, Restaurant> markers;
	HashMap<String, String> markersUrl;
	RestaurantDataHandler rDataHandler;

	ImageLoader imageLoader;

	View rootView;

	LatLng latLng;

	AppLocationService appLocationService;

	private boolean updateLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		imageLoader = AppController.getInstance().getImageLoader();
		listRestaurants = new ArrayList<Restaurant>();
		markers = new HashMap<String, Restaurant>();
		markersUrl = new HashMap<String, String>();
		rDataHandler = RestaurantDataHandler.getInstance();
		appLocationService = new AppLocationService(getActivity());

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
		setUpMapAndUserCurrentLocation();
		locationInitilizer();
		geoloader();
		return rootView;
	}

	private void setUpMapAndUserCurrentLocation() {
		try {
			LatLng clatLng;
			mMap = mMapView.getMap();
			mMap.getUiSettings().setMyLocationButtonEnabled(false);
			mMap.setMyLocationEnabled(true);
			mMap.setInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity()));
			mMap.setOnInfoWindowClickListener(this);
			// Updates the location and zoom of the MapView
			Location loc = mMap.getMyLocation();
			if (loc != null) {
				clatLng = new LatLng(loc.getLatitude(), loc.getLongitude());

			} else {
				clatLng = new LatLng(49.28826d, -123.12799d);

			}

			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					clatLng, 13);
			mMap.moveCamera(CameraUpdateFactory.newLatLng(clatLng));
			mMap.animateCamera(cameraUpdate);

		} catch (Exception e) {
			// TODO: handle exception

		}

	}

	private void locationInitilizer() {

		if (servicesConnected()) {
			LocationManager locationManager = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					|| locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mLocationRequest = LocationRequest.create();
				mLocationRequest
						.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
				mLocationRequest.setInterval(1000);
				mLocationRequest.setFastestInterval(500);
				mLocationClient = new LocationClient(getActivity(), this, this);
				mLocationClient.connect();

			} else {
				showSettingsAlert();
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
				.setPositiveButton("DE ACUERDO", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						makeRequestForRestaurantByLatLong(
								WSConstant.Web.NEAR_ME,
								String.valueOf(latLng.latitude),
								String.valueOf(latLng.longitude));

						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						getActivity().startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	private void setUpMap() {

		// latLng = new LatLng(49.2500, 123.1000); // vancouver location
		latLng = getCurrentPosition();
		if (latLng != null) {
			try {
				mMap.getUiSettings().setMyLocationButtonEnabled(false);
				mMap.setMyLocationEnabled(true);
				// Updates the location and zoom of the MapView
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						latLng, 13);
				mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				mMap.animateCamera(cameraUpdate);
				mMap.setInfoWindowAdapter(new InfoWindowAdapterMarker(
						getActivity()));
				mMap.setOnInfoWindowClickListener(this);
				LogMsg.showAlertDialog(getActivity(), "GpsLoaction", "LatLang"
						+ latLng, false);
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {
			LogMsg.showAlertDialog(getActivity(), "GpsLoaction", "LatLang"
					+ latLng, false);
		}

	}

	private LatLng getCurrentPosition() {
		LatLng latLng = null;
		Location loc = getLocation();
		if (loc != null) {
			latLng = new LatLng(loc.getLatitude(), loc.getLongitude()); // vancouver
																		// location
		}

		return latLng;
	}

	public Location getLocation() {
		Location currentLocation = null;
		// If Google Play Services is available
		// Get the current location
		if (mLocationClient.isConnected()) {
			currentLocation = mLocationClient.getLastLocation();
		} else {
			LogMsg.showAlertDialog(getActivity(), "Error", "Enable to connect",
					true);
		}

		// Display the current location in the UI
		// mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));

		return currentLocation;
	}

	@Override
	public void onResume() {
		super.onResume();
		// If the app already has a setting for getting location updates, get it
		if (updateLocation) {
			if (listRestaurants.size() == 0) {
				mLocationClient.connect();
			}
		}
		mMapView.onResume();
		// setUpMapIfNeeded();

	}

	@Override
	public void onPause() {
		super.onPause();
		// Save the current setting for updates
		if (updateLocation)
			mLocationClient.disconnect();
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

	/*
	 * private void addtextSearchListner() {
	 * 
	 * textSearch .setOnEditorActionListener(new
	 * TextView.OnEditorActionListener() {
	 * 
	 * @Override public boolean onEditorAction(TextView v, int actionId,
	 * KeyEvent event) { if (actionId == EditorInfo.IME_ACTION_SEARCH) { if
	 * (!textSearch.getText().toString().trim() .equals("")) {
	 * makeRequestForRestaurant(Web.NEAR_BY,
	 * textSearch.getText().toString().trim()); UI.hideKeyboard(getActivity(),
	 * textSearch); } else { LogMsg.LOG(getActivity(), "Empty fields"); } return
	 * true; } return false; } });
	 * 
	 * textSearch.addTextChangedListener(new TextWatcher() {
	 * 
	 * @Override public void onTextChanged(CharSequence s, int start, int
	 * before, int count) {
	 * 
	 * }
	 * 
	 * @Override public void beforeTextChanged(CharSequence s, int start, int
	 * count, int after) { textSearch.setCompoundDrawables(null, null, null,
	 * null); }
	 * 
	 * @Override public void afterTextChanged(Editable s) {
	 * 
	 * } });
	 * 
	 * }
	 */

	private void addRefershListeneer() {

		referesh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLocationClient != null && mLocationClient.isConnected()) {
					getResturarantByCurrentLocation();
				} else {
					locationInitilizer();
				}

			}
		});

	}

	private void getResturarantByCurrentLocation() {

		if (latLng == null) {
			latLng = getCurrentPosition();
		}

		if (latLng == null) {
			latLng = new LatLng(49.28826, -123.12799

			);
		}
		if (latLng != null) {
			if (latLng.latitude == 0.0 && latLng.longitude == 0.0) {
				latLng = new LatLng(49.28826, -123.12799);

			}
		}

		if (latLng != null) {
			updateLocation = true;
			String message = String.valueOf("Latitud: " + latLng.latitude
					+ "\nLongitud:" + latLng.longitude);
			showAlertDialog(getActivity(), "Ubicación actual", message);
		} else {
			mLocationClient.connect();
		}
	};

	protected void parseResturant(String response) {

		UI.hideProgressDialog();

		listRestaurants = JsonParser.getRestaurantsDetail(response);

		if (listRestaurants != null && listRestaurants.size() != 0) {
			upDateMapElement();
		} else {
			// Toast.makeText(getActivity(), "No data to Update",
			// Toast.LENGTH_LONG).show();
			LogMsg.showAlertDialog(getActivity(), "Mapa",
					"No hay restaurantes que se encuentran", false);
		}

	}

	private void upDateMapElement() {
		LatLng rlatLng = null;

		mMap.clear();
		for (int i = 0; i < listRestaurants.size(); i++) {
			Restaurant r = listRestaurants.get(i);
			if (r.getLatitude() == null && r.getLongitude() == null) {
				LogMsg.showAlertDialog(getActivity(), "",
						"No hay restaurantes que se encuentran", false);

			}
			/*
			 * else if (r.getLongitude().equals("null")) {
			 * Toast.makeText(getActivity(),
			 * "No latitude and longitude found!!!!",
			 * Toast.LENGTH_SHORT).show(); }
			 */

			else {
				rlatLng = new LatLng(Double.parseDouble(r.getLatitude()),
						Double.parseDouble(r.getLongitude()));

				marker = mMap.addMarker(new MarkerOptions()
						.position(rlatLng)
						.title(r.getName())
						.snippet(r.getAddress())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_map1)));
				markers.put(marker.getId(), r);

				markersUrl.put(marker.getId(), r.getImage());
				mMap.moveCamera(CameraUpdateFactory.newLatLng(rlatLng));
				mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

			}

		}

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

	private void makeRequestForRestaurantByLatLong(String product,
			final String lat, final String lang) {

		String url = Web.HOST + product;
		//Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
		Log.e("urll",url);

		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("response of resturant",response);
						parseResturant(response);
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// VolleyLog.e(TAG, "Error: " + error.getMessage());

						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				
				Log.e("parmsss","parmsss"+lat +" "+lang);
				
				params.put(DataKey.LAT, lat);
				params.put(DataKey.LONG, lang);
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

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
								image.setImageBitmap(response.getBitmap());
								/*
								 * if (response.getBitmap() != null) {
								 * 
								 * // load image into imageview //
								 * mapBimap.put(markerId, //
								 * response.getBitmap()); //
								 * marker.showInfoWindow(); }
								 */
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

			RestaurantDetailMain fragment = new RestaurantDetailMain();

			((BaseContainerFragment) getParentFragment()).replaceFragmentMap(
					fragment, true);
		} else {
			LogMsg.LOG(getActivity(), "No Info Available");
		}

	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {
		getResturarantByCurrentLocation();
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		Log.v(TAG, getString(R.string.disconnected));
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */

		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(getActivity(),
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		if (location != null) {
			latLng = new LatLng(location.getLatitude(), location.getLongitude());
			updateLocation = true;
		}

	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				getActivity(),
				LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getChildFragmentManager(), LocationUtils.APPTAG);
		}
	}

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 * 
		 * @param dialog
		 *            An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG,
					getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					getActivity(), 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getChildFragmentManager(),
						LocationUtils.APPTAG);
			}
			return false;
		}
	}

	private void geoloader() {
		// TODO Auto-generated method stub
		Location location = appLocationService
				.getLocation(LocationManager.GPS_PROVIDER);

		// you can hard-code the lat & long if you have issues with
		// getting it
		// remove the below if-condition and use the following couple of
		// lines
		// double latitude = 37.422005;
		// double longitude = -122.084095

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			LocationAddress locationAddress = new LocationAddress();
			locationAddress.getAddressFromLocation(latitude, longitude,
					getActivity(), new GeocoderHandler());
		} else {
			// showSettingsAlert();
		}

	}

	private class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String locationAddress;
			switch (message.what) {
			case 1:
				Bundle bundle = message.getData();
				locationAddress = bundle.getString("address");
				break;
			default:
				locationAddress = null;
			}

		}
	}

}
