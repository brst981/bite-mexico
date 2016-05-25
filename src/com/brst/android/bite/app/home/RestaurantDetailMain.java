package com.brst.android.bite.app.home;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.home.restaurant.CheckinFragment;
import com.brst.android.bite.app.home.restaurant.Detail;
import com.brst.android.bite.app.home.restaurant.Gallery;
import com.brst.android.bite.app.home.restaurant.Reviews;
import com.brst.android.bite.app.locate.MyMapFragemt;
import com.brst.android.bite.app.membership.MembershipActivity;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.UI;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class RestaurantDetailMain extends Fragment implements OnClickListener {

	private static final String TAG = "RestaurantDetail";
	Button btnDetail, btnGallery, btnReview, btnCheckin;
	ImageView map;

	ImageView imageRestaurant;
	SharedPreferences mSharedPreferences;
	TextView name, address;
	RestaurantDataHandler rHandler;
	Restaurant restaurant;
	UserDataHandler uHandler;
	View rootView;
	ImageLoader imageLoader;
	String product_id, entity_id;
	// facebook variable
	private Button shareButton;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	// These tags will be used to cancel the requests

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		imageLoader = AppController.getInstance().getImageLoader();
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();
		uHandler = UserDataHandler.getInstance();
		Log.e("detail","details");

		hideKeyboard();
		

		makeRequestForRestaurantData(Web.PRODUCT, restaurant.getId());

		mSharedPreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	private void hideKeyboard() {
		// TODO Auto-generated method stub
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_layout_restaurant_detail,
				null);

		if (savedInstanceState != null) {
			pendingPublishReauthorization = savedInstanceState.getBoolean(
					PENDING_PUBLISH_KEY, false);
		}

		shareButton = (Button) rootView.findViewById(R.id.shareButton);

		name = (TextView) rootView.findViewById(R.id.header_title);

		address = (TextView) rootView.findViewById(R.id.text_adr1);

		imageRestaurant = (ImageView) rootView.findViewById(R.id.image_hotel);

		btnDetail = (Button) rootView.findViewById(R.id.btn_detail);
		btnGallery = (Button) rootView.findViewById(R.id.btn_gallery);
		btnReview = (Button) rootView.findViewById(R.id.btn_review);
		btnCheckin = (Button) rootView.findViewById(R.id.btn_checkin);
		// back = (ImageView) rootView.findViewById(R.id.btn_header_back);
		map = (ImageView) rootView.findViewById(R.id.btn_map);
		// back.setOnClickListener(this);
		map.setOnClickListener(this);
		btnDetail.setOnClickListener(this);
		btnGallery.setOnClickListener(this);
		btnReview.setOnClickListener(this);
		btnCheckin.setOnClickListener(this);

		btnDetail.setSelected(true);

		updateUiValues();

		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				publishStory();
			}
		});

		return rootView;

	}

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d(TAG, String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d(TAG, "Success!");
			final String postId = data
					.getString("com.facebook.platform.extra.COMPLETION_GESTURE");
			if (postId.equalsIgnoreCase("post")) {
				// Toast.makeText(getActivity(),
				// "Restaurant successfully shared",
				// Toast.LENGTH_SHORT).show();
				Toast.makeText(getActivity(), "Successfully Posted",
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Restaurant successfully shared");
			} else if (postId.equalsIgnoreCase("cancel")) {
				Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT)
						.show();

			}
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			shareButton.setVisibility(View.VISIBLE);
		} else {
			shareButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void publishStory() {
		try {
			// if (mSharedPreferences != null
			// && mSharedPreferences.getString("WITHOUT_FACEBOOK", "")
			// .matches("301")) {

			// publishFeedDialog();
			// } else {
			Session session = Session.getActiveSession();

			if (session != null) {

				if (FacebookDialog.canPresentShareDialog(getActivity(),
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							getActivity()).setName(getMessage())
							.setLink("http://www.bitebc.ca")
							.setDescription(getDescription())
							.setCaption("Desarrollado por mordedura Bc")
							.setPicture(restaurant.getImage()).build();
					uiHelper.trackPendingDialogCall(shareDialog.present());

				} else if (mSharedPreferences != null
						&& mSharedPreferences.getString("WITHOUT_FACEBOOK", "")
								.matches("301")) {

					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							getActivity()).setName(getMessage())
							.setLink("http://www.bitebc.ca")
							.setDescription(getDescription())
							.setCaption("Desarrollado por mordedura Bc")
							.setPicture(restaurant.getImage()).build();
					uiHelper.trackPendingDialogCall(shareDialog.present());
				} else {
					publishFeedDialog();
				}

				// }

			}

		} catch (Exception ex) {

		}

	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", getMessage());
		params.putString("caption", "Desarrollado por mordedura Bc");
		params.putString("description", getDescription());
		params.putString("link", "http://www.bitebc.ca");
		params.putString("picture", restaurant.getImage());
		Log.e("image  ",restaurant.getImage());

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(),
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(getActivity(),
										"Publicado éxito",
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(
										getActivity().getApplicationContext(),
										"Cancelado", Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Cancelado", Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Error", Toast.LENGTH_SHORT).show();
						}
					}

				}).build();
		feedDialog.show();
	}

	private String getMessage() {
		String message;
		message = String.valueOf("Disfrutando "
				+ getOffer(restaurant.getOfferAvailable()) + " a "
				+ restaurant.getName() + " gracias a morder antes de Cristo.");

		return message;

	}

	private String getDescription() {
		String desc;
		desc = "Una membresia bite mexico te ofrece el 50% de descuento o el 2 x 1 en la cuenta total de tus alimentos en nuestros restaurantes participantes.";
		return desc;
	}

	private String getOffer(String key) {
		String offer;
		if (key.equals("31")) {
			offer = "2 for 1 meals";
		} else if (key.equals("32")) {
			offer = "50% off food";
		} else {
			offer = "No offer";
		}

		return offer;

	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	public void updateUiValues() {
		name.setText(restaurant.getName());
		address.setText(restaurant.getAddress());

		imageLoader.get(restaurant.getImage(), new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Log.e(TAG, "Image Load Error: " + error.getMessage());
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					// load image into imageview
					imageRestaurant.setImageBitmap(response.getBitmap());
				}
			}
		});
		getChildFragmentManager().beginTransaction()
				.replace(R.id.restarant_detail_frame_container, new Detail())
				.commit();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.btn_detail:
			if (!btnDetail.isSelected()) {
				btnDetail.setSelected(true);
				btnGallery.setSelected(false);
				btnReview.setSelected(false);

				getChildFragmentManager()
						.beginTransaction()
						.replace(R.id.restarant_detail_frame_container,
								new Detail()).commit();

			}

			break;
		case R.id.btn_gallery:
			if (!btnGallery.isSelected()) {
				btnDetail.setSelected(false);
				btnGallery.setSelected(true);
				btnReview.setSelected(false);

				getChildFragmentManager()
						.beginTransaction()
						.replace(R.id.restarant_detail_frame_container,
								new Gallery()).commit();

			}
			break;
		case R.id.btn_review:
			if (!btnReview.isSelected()) {
				btnDetail.setSelected(false);
				btnGallery.setSelected(false);
				btnReview.setSelected(true);

				getChildFragmentManager()
						.beginTransaction()
						.replace(R.id.restarant_detail_frame_container,
								new Reviews()).commit();
			}
			break;
		case R.id.btn_checkin:
			HashMap<String, String> params;
			params = new HashMap<String, String>();
			String userId = uHandler.getUser().getCustomerid();
			params.put(DataKey.CUSTOMER_ID, userId);
			makeRequestForCheckIn(Web.CHECKIN, params);

			break;
		case R.id.btn_map:
			Log.e("idd",restaurant.getId());

			MyMapFragemt map = MyMapFragemt.create(restaurant.getId());
			/*
			 * Bundle args = new Bundle(); args.putString("location",
			 * restaurant.getName());
			 * 
			 * map.setArguments(args);
			 */

			((BaseContainerFragment) getParentFragment()).replaceFragment(map,
					true);

			break;

		default:
			break;
		}

	}

	protected void parserResponseForCheckIn(String response) {

		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);
			
			Log.e("package details",response);

			if (jsonOBject.getBoolean("success")) {
				String packageDescription = jsonOBject
						.getString("package_name");
				String daysLeft = String.valueOf(jsonOBject.getInt("daysleft"));
				String date = jsonOBject.getString("end_date");

				CheckinFragment fragment = CheckinFragment.create(
						packageDescription, daysLeft, date);
				((BaseContainerFragment) getParentFragment()).replaceFragment(
						fragment, true);

			} else {

				if (jsonOBject.getString("status").equals("not purchased")) {
					showAlertDialogForMembership(getActivity(),
							"La membresía expiró",
							"Su membresía ha expirado. Por favor vuelva a comprar su plan de membresía.");

				} else if (jsonOBject.getString("status").equals("paused")) {
					showAlertDialog(getActivity(), "La membresía desactivado",
							"Su cuenta ha sido desactivada. Por favor, espere a que la aprobación del administrador.");
				} else {
					showAlertDialog(getActivity(), "Error",
							"Su membresía ha expirado. Por favor vuelva a comprar su plan de membresía.");
				}

			}

		} catch (JSONException e) {

		}

	}

	public void showAlertDialog(Context context, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						getActivity().finish();

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void showAlertDialogForMembership(Context context, String title,
			String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						Intent intent;
						intent = new Intent(getActivity(),
								MembershipActivity.class);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.slide_in_left, R.anim.slide_out_right);
						getActivity().finish();

					}
				})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
								getActivity().finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	/**
	 * service to fetch checking detail
	 * 
	 */

	private void makeRequestForCheckIn(final String checkin,
			final Map<String, String> params) {

		String url = Web.HOST + checkin;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						parserResponseForCheckIn(response);
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

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	private void makeRequestForRestaurantData(String product, final String id) {

		String url = Web.HOST + product;
		
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.i(TAG, "" + response);

						saveRestaurantData(response);
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put(DataKey.PRODUCT_KEY, id);
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	protected void saveRestaurantData(String response) {
		UI.hideProgressDialog();

		restaurant = JsonParser.getRestaurant(response, restaurant);
		rHandler.setRestaurant(restaurant);

		updateUiValues();

	}

}
