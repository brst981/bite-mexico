package com.brst.android.bite.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.getstarted.Main;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.membership.MembershipActivity;
import com.brst.android.bite.app.util.ConnectionDetector;
import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class SplashActivity extends Activity {

	private UiLifecycleHelper uiHelper;
	private static final String TAG = "SplashActivity";
	private static final int TAG_1 = 300;
	private static final int TAG_2 = 301;
	private static final int TAG_3 = 302;

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;

	private int SPLASH_TIME_OUT = 700;

	SharedPreferences sharedpreferences;
	private UserDataHandler uDataHandler;
	private User user;
	String userId;
	String pwd, id;
	String mStringSection;
	boolean isStatus = false;
	int daysleft = 0;
	String versionName;
	String city, customerId, city_prefs;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
		Log.e("splash","splash");


		try {
			versionName = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String deviceid = Secure.getString(
				SplashActivity.this.getContentResolver(), Secure.ANDROID_ID);

		uDataHandler = UserDataHandler.getInstance();
		id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		showHashKey(this);
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		sharedpreferences = getSharedPreferences(BiteBc.MyPREFERENCES,
				Context.MODE_APPEND);
		mStringSection = sharedpreferences.getString("SECTION_PHP", "");

		if (sharedpreferences != null
				&& sharedpreferences.getString("NEW_VERSION_FETCH", "")
						.matches("NEW_VERSION_FETCH")
				&& versionName.matches("1")) {

		} else {
			Editor editor = sharedpreferences.edit();
			editor.putString("NEW_VERSION_FETCH", "NEW_VERSION_FETCH");
			editor.putString("WITHOUT_FACEBOOK", "NO CASE");
			editor.putString("GET_STARTED", "CASE_STARTED");

			//editor.putString(BiteBc.USER_ID_FINAL, null);
			//editor.putString(BiteBc.USER_PWD_FINAL, null);

			editor.commit();
			Log.e("nullsss","nulllsss");

		}

		if (sharedpreferences != null
				&& sharedpreferences.contains(BiteBc.USER_ID_FINAL)
				&& sharedpreferences.contains(BiteBc.USER_PWD_FINAL)
				&& !sharedpreferences.getString("WITHOUT_FACEBOOK", "")
						.matches("301")
				&& !sharedpreferences.getString(BiteBc.USER_ID_FINAL, "").matches("")
				&& mStringSection.matches("facebook")) {

			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			mStringSection = sharedpreferences.getString("SECTION_PHP", "");

		} else if (sharedpreferences != null
				&& sharedpreferences.getString("WITHOUT_FACEBOOK", "").matches(
						"301")) {

			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			mStringSection = sharedpreferences.getString("SECTION_PHP", "");
			openMainActivity(TAG_2);
		} else if (mStringSection.matches("facebook")) {
			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
		}

		else if (mStringSection.matches("email")) {

			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			mStringSection = sharedpreferences.getString("SECTION_PHP", "");
			openMainActivity(TAG_2);
		}

		else if (sharedpreferences.getString("GET_STARTED", "").matches(
				"CASE_STARTED")) {
			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			openMainActivity(TAG_3);
		}

		else {

			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			mStringSection = sharedpreferences.getString("SECTION_PHP", "");
			openMainActivity(TAG_1);
		}

		// new Handler().postDelayed(new Runnable() {
		// /*
		// * Showing splash screen with a timer. This will be useful when you
		// * want to show case your app logo / company
		// */
		// @Override
		// public void run() {
		// checkInternet(isInternetPresent);
		// // This method will be executed once the timer is over
		// // Start your app main activity
		//
		// // Intent intent_home = new Intent(SplashActivity.this,
		// // MainActivity_2.class);
		// //
		// // startActivity(intent_home);
		// // overridePendingTransition(R.anim.slide_in_left,
		// // R.anim.slide_out_right);
		// // finish();
		// }
		// }, SPLASH_TIME_OUT);

	}

	private void openMainActivity(final int tag) {

		new Handler().postDelayed(new Runnable() {
			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */
			@Override
			public void run() {

				checkInternet(isInternetPresent, tag);

			}
		}, SPLASH_TIME_OUT);

	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {

		if (!isStatus) {
			isStatus = true;
			if (session != null && session.isOpened()) {
				// loginWithoutFb(TAG_2);
				// openMainActivity(TAG_1);
				if (mStringSection.matches("facebook")) {

					loginFromPreferences();
				}
			} else {
				Log.i("Facebook", "onSESSION CHANGED");
				// openMainActivity(TAG_1);
			}

		}
	}

	private void loginWithoutFb(int tag2) {
		checkInternet(isInternetPresent, tag2);
	}

	private void loginFromPreferences() {
		//sharedpreferences.getString(BiteBc.USER_ID_FINAL, "")
		userId = sharedpreferences.getString(BiteBc.USER_ID_FINAL, "");
		pwd = sharedpreferences.getString(BiteBc.USER_PWD_FINAL, "");
		
		Log.e("city status","data"+sharedpreferences.getString("cityData", ""));

		//userId="jatinder2.syall@gmail.com";
		//pwd="1100570700032173";
		
		Log.e("user idd","idd"+userId);
		Log.e("pawdd idd","idd"+pwd);

		HashMap<String, String> params = new HashMap<>();
		params.put(DataKey.USER_ID, userId);
		params.put(DataKey.USER_PWD, pwd);
		params.put("section", mStringSection);
		Log.e("section",mStringSection);
		makeRegisterRequest(WSConstant.Web.LOGIN, params);

	}

	private void checkInternet(Boolean isInternetPresent2, int tag) {
		// check for Internet status
		if (isInternetPresent2) {
			Intent intent_home = null;
			// Internet Connection is Present
			// make HTTP requests
			switch (tag) {
			case TAG_1:

				intent_home = new Intent(SplashActivity.this,
						MainActivity_2.class);
				startActivity(intent_home);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();

				break;
			case TAG_2:
				
				loginFromPreferences();

				break;
			case TAG_3:

				intent_home = new Intent(SplashActivity.this, Main.class);
				startActivity(intent_home);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();

				break;

			default:
				break;
			}

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet
			showAlertDialog(SplashActivity.this, "No Internet Connection",
					"You don't have internet connection.", false, tag);
		}

	}

	/**
	 * Function to display simple Alert Dialog
	 * 
	 * @param context
	 *            - application context
	 * @param title
	 *            - alert dialog title
	 * @param message
	 *            - alert message
	 * @param status
	 *            - success/failure (used to set icon)
	 * */
	public void showAlertDialog(Context context, String title, String message,
			Boolean status, final int tag) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.fail);

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						isInternetPresent = cd.isConnectingToInternet();

						checkInternet(isInternetPresent, tag);

					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
								SplashActivity.this.finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	/**
	 * Making json object request to register user;
	 * */

	private void makeRegisterRequest(String login,
			final Map<String, String> rParams) {
		UI.showProgressDialog(this);
		String url = Web.HOST + login;
		Log.e("urll",url);

		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e(TAG + "On Me Response", response);
						// msgResponse.setText(response.toString());
						UI.hideProgressDialog();
						login(response);

					}

				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// VolleyLog.e(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
						showAlertDialog(SplashActivity.this, "Error",
								"Error while connecting to the server. Please try later");
					}
				}) {

			/**
			 * Passing some request headers
			 * */
			// @Override
			// public Map<String, String> getHeaders() throws AuthFailureError {
			// HashMap<String, String> headers = new HashMap<String, String>();
			// headers.put("Content-Type", "application/json");
			// return headers;
			// }

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = rParams;
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				WSConstant.DataKey.TAG_JSON_OBJECT);

		// Cancelling request
		// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);

	}

	private void makeRequestForCheckIn(final String checkin,
			final Map<String, String> params) {

		String url = Web.HOST + checkin;

		UI.showProgressDialog(this);
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.i(TAG + "On Response", response);
						// msgResponse.setText(response.toString());
						// response
						parseResponseChekin(response);

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

	protected void parseResponseChekin(String response) {
		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);

			if (!jsonOBject.getBoolean("success")) {
				daysleft = jsonOBject.getInt("daysleft");

				if (jsonOBject.getString("status").equals("not purchased")) {

					showAlertDialogForMembership(SplashActivity.this,
							"La membresía expiró",
							"Su membresía ha expirado. Por favor, adquiera un nuevo plan de membresía.");

				} else if (jsonOBject.getString("status").equals("paused")) {
					showAlertDialog(SplashActivity.this, "La membresía desactivado",
							"Su cuenta ha sido desactivada. Por favor, espere a que la aprobación del administrador.");
				}

				else {
					showAlertDialog(SplashActivity.this, "Error",
							"Su membresía ha expirado. Por favor vuelva a comprar su plan de membresía.");
				}
			} else {
				String planId = jsonOBject.getString("planid");

				if (jsonOBject.getString("city") != null) {
					city = jsonOBject.getString("city");

					Editor editor = sharedpreferences.edit();
					editor.putString(BiteBc.USER_PLAN, planId);
					editor.putString(BiteBc.CITY, city);
					editor.commit();
				} else {

					Editor editor = sharedpreferences.edit();
					editor.putString(BiteBc.USER_PLAN, planId);
					editor.putString(BiteBc.CITY, city);
					editor.commit();
				}

				daysleft = jsonOBject.getInt("daysleft");

				uDataHandler
						.setUserSelectedPlan(jsonOBject.getString("planid"));
				showMembershipActivity();

			}

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

	}

	protected void login(String response) {
		try {
			Log.e("response from login","tt"+response);

			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {
				user = new User();
				customerId = jsonOBject.getString("customerid");
				String session = jsonOBject.getString("sessionid");
				String firstName = jsonOBject.getString("firstname");
				String lastName = jsonOBject.getString("lastname");
				// String email = jsonOBject.getString("email");
				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.CUST_ID, customerId);
				// editor.putString(BiteBc.LASTNAME,
				// mEditTextlastname.getText().toString());
				editor.commit();
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setCustomerid(customerId);
				user.setUserSessionId(session);
				user.setEmail(userId);
				user.setuFacebookId(pwd);
				uDataHandler.setUser(user);
				checkUserStatus();

			} else {
				Log.e("", "FACEBOOK EMAIL ELSE ELSE");
				openMainActivity(TAG_1);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkUserStatus() {
		HashMap<String, String> params;
		params = new HashMap<String, String>();
		params.put(DataKey.CUSTOMER_ID, user.getCustomerid());
		makeRequestForCheckIn(Web.CHECKIN, params);

	}

	private void showMembershipActivity() {

		Intent intent;
		if (sharedpreferences != null
				&& sharedpreferences.contains(BiteBc.USER_PLAN)
				&& daysleft != 0) {
			intent = new Intent(this, HomeActivity.class);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();

		} else {
			intent = new Intent(this, MembershipActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
		}
		// intent = new Intent(this, MembershipActivity.class);

	}

	/**
	 * Facebook lifecycle method must handles
	 */

	@Override
	public void onResume() {
		super.onResume();

		uiHelper.onResume();
		if (!isStatus) {
			Session session = Session.getActiveSession();
			if (session != null && (session.isOpened() || session.isClosed())) {
				onSessionStateChange(session, session.getState(), null);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	// Facebook lifecycle ends

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
						finish();

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
						intent = new Intent(SplashActivity.this,
								MembershipActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_left,
								R.anim.slide_out_right);
						finish();

					}
				})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
								finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public static void showHashKey(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"com.brst.android.bite.app", PackageManager.GET_SIGNATURES); // Your
																					// package
																					// name
																					// here
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}
}
