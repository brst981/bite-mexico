package com.brst.android.bite.app.login;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bite.app.LoginPage;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.membership.MembershipActivity;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * @author ShalviSharma
 * 
 *         ${tags}
 */
public class MainFragment extends Fragment {

	private static final String TAG = "MainFragment";
	SharedPreferences sharedpreferences;

	private UiLifecycleHelper uiHelper;
	private UserDataHandler uDataHandler;
	private User user;
	Button mButton_bitebc;
	String deviceid, POPUP;
	EditText input;
	String city;

	private boolean isResumed = false;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uDataHandler = UserDataHandler.getInstance();

		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		sharedpreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_APPEND);

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		deviceid = Secure.getString(getActivity().getContentResolver(),
				Secure.ANDROID_ID);
		
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.login_button);
		
		mButton_bitebc = (Button) view.findViewById(R.id.button_loginbitebc);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_likes", "email"));

		POPUP = sharedpreferences.getString("POP_UP", "");

		if (POPUP.matches("OPEN")) {

			notemailgetAlertDialog(getActivity(), "Email",
					"Please confirm your Facebook Email");

		}
		// authButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// onClickLogin();
		//
		// }
		// });
		mButton_bitebc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(getActivity(), LoginPage.class);
				startActivity(mIntent);
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				getActivity().finish();

			}
		});

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {

		if (isResumed) {
			if (state.isOpened()) {

				makeMeRequest(session);
			} else if (state.isClosed()) {

				Editor editor = sharedpreferences.edit();
				editor.putString("SECTION_PHP", null);
				// editor.putString("LASTNAME",
				// user.getuFacebookId());

				editor.commit();

			}
		}
	}

	// private Session.StatusCallback statusCallback = new
	// SessionStatusCallback();
	//
	// private void onClickLogin() {
	// Session session = Session.getActiveSession();
	// if (!session.isOpened() && !session.isClosed()) {
	// // session.openForRead(new Session.OpenRequest(this).setPermissions(
	// // Arrays.asList("public_profile"))
	// // .setCallback(statusCallback));
	// session.openForPublish(new Session.OpenRequest(this)
	// .setPermissions(Arrays.asList("publish_actions"))
	// .setCallback(statusCallback));
	// } else {
	// Session.openActiveSession(getActivity(), this, true, statusCallback);
	// }
	// }

	// private class SessionStatusCallback implements Session.StatusCallback {
	// @Override
	// public void call(Session session, SessionState state,
	// Exception exception) {
	// makeMeRequest(session);
	// }
	//
	// }

	/**
	 * Make a request for user Email id and information
	 * 
	 * @param session
	 */
	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		UI.showProgressDialog(getActivity());
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								// Set the id for the ProfilePictureView
								// view that in turn displays the profile
								// picture.
								UI.hideProgressDialog();

								JSONObject jObject = user.getInnerJSONObject();
								Log.e("response from faceboo",jObject+"");

								registerOrLoginUser(jObject,user.getName());

							}
						}
						if (response.getError() != null) {
							UI.hideProgressDialog();
							// Handle errors, will do so later.
						}
					}
				});
		request.executeAsync();
	}

	/**
	 * Register user to bite bc
	 * 
	 * @param jObject
	 */
	public void registerOrLoginUser(JSONObject jObject,String userName) {

		try {

			user = uDataHandler.getUser();

			if (user == null) {
				user = new User();
			}
			String firstname = jObject.optString("first_name");
			String lastname = jObject.optString("last_name");
			String email = null;
			if (jObject.has("email"))
				email = jObject.getString("email");
			String pwd = jObject.getString("id");
			
			if(firstname.equals(""))
			{
				
				String name[]=userName.split(" ");
				firstname=name[0];
				lastname=name[1];
			}

			user.setFirstName(firstname);
			user.setLastName(lastname);
			user.setEmail(email);
			user.setuFacebookId(pwd);
			uDataHandler.setUser(user);
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstname);
			params.put("lastname", lastname);
			params.put("email", email);
			params.put("password", pwd);
			params.put("device_id", deviceid);
			params.put("platform", "android");
			params.put("section", "facebook");
			Editor editor = sharedpreferences.edit();
			// editor.putString(BiteBc.USER_ID_FINAL, user.getEmail());
			//editor.putString(BiteBc.USER_PWD_FINAL, pwd);

			editor.putString("SECTION_PHP", "facebook");
			editor.putString(BiteBc.FIRSTNAME, firstname);
			editor.putString(BiteBc.LASTNAME, lastname);
			editor.putString(BiteBc.USER_PWD_FINAL, pwd);
									
			editor.commit();
			
			Log.e("final passwordd","passwordss"+sharedpreferences.getString(BiteBc.USER_PWD_FINAL, ""));

			uDataHandler.setUser(user);

			if (email != null) {
				Editor editor_ = sharedpreferences.edit();
				// editor.putString(BiteBc.USER_ID_FINAL, user.getEmail());
				editor_.putString(BiteBc.USER_ID_FINAL, email);

				editor_.commit();
				makeRegisterRequest(Web.REGISTER, params);
			} else {

				notemailgetAlertDialog(getActivity(), "Email",
						"Se ruega confirmación de Facebook Correo");
			}

		} catch (JSONException e) {
			/*
			 * showAlertDialog(getActivity(), "Email not exist",
			 * "Please check your facebook email id");
			 */
			e.printStackTrace();
		}
	}

	protected void login(String response) {
		// TODO Auto-generated method stub
		try {

			JSONObject jsonOBject = new JSONObject(response);

			if (jsonOBject.getBoolean("success")) {

				String customerId = jsonOBject.getString("customerid");
				String session = jsonOBject.getString("sessionid");
				user.setCustomerid(customerId);
				user.setUserSessionId(session);
				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.CUST_ID, customerId);
				editor.putString("CUSTOMER_ID", customerId);
				editor.putString("WITHOUT_FACEBOOK", null);
				editor.putString("SECTION_PHP", "facebook");

				editor.putString("POP_UP", "CLOSED");

				editor.commit();
				uDataHandler.setUser(user);
				checkUserStatus();
			} else {
				String msg = jsonOBject.getString("message");

				LogMsg.LOG(getActivity(), msg);
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

	protected void parseResponseChekin(String response) {
		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);

			if (!jsonOBject.getBoolean("success")) {

				if (jsonOBject.getString("status").equals("not purchased")) {
					Editor editor = sharedpreferences.edit();

					editor.putString("profileimg_url",
							jsonOBject.getString("profileimg_url"));

					editor.commit();
					Intent intent;
					intent = new Intent(getActivity(), MembershipActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_left, R.anim.slide_out_right);
					getActivity().finish();
				} else if (jsonOBject.getString("status").equals("paused")) {
					showAlertDialog(getActivity(), "sin Afiliación",
							"Su plan de membresía ha expirado");
				} else {
					showAlertDialog(getActivity(), "Error",
							"Su plan de membresía ha expirado");
				}
			} else {
				String planId = jsonOBject.getString("planid");
				if (jsonOBject.getString("city") != null) {
					city = jsonOBject.getString("city");
				}

				Editor editor = sharedpreferences.edit();
				editor.putString("profileimg_url",
						jsonOBject.getString("profileimg_url"));
				editor.putString(BiteBc.USER_PLAN, planId);
				editor.putString(BiteBc.CITY, city);
				editor.commit();

				uDataHandler
						.setUserSelectedPlan(jsonOBject.getString("planid"));
				showMembershipActivity();

			}

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

	}

	private void showMembershipActivity() {
		Log.e("membership acitivty","open");

		Intent intent;
		if (sharedpreferences != null
				&& sharedpreferences.contains(BiteBc.USER_PLAN)) {
			intent = new Intent(getActivity(), HomeActivity.class);
		} else {
			intent = new Intent(getActivity(), MembershipActivity.class);
		}
		// intent = new Intent(getActivity(), MembershipActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_left,
				R.anim.slide_out_right);
		getActivity().finish();

	}

	private void makeRequestForCheckIn(final String checkin,
			final Map<String, String> params) {

		String url = Web.HOST + checkin;

		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						parseResponseChekin(response);

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

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	/**
	 * Making json object request to register user;
	 * */

	private void makeRegisterRequest(String register,
			final Map<String, String> rParams) {

		String url = Web.HOST + register;

		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						UI.hideProgressDialog();
						login(response);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
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

	/**
	 * Facebook lifecycle method must handles
	 */

	@Override
	public void onResume() {
		super.onResume();
		// Session session = Session.getActiveSession();
		// if (session != null && (session.isOpened() || session.isClosed())) {
		// onSessionStateChange(session, session.getState(), null);
		// }
		uiHelper.onResume();
		isResumed = true;
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
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			//Editor editor = sharedpreferences.edit();

			//editor.putString("POP_UP", "OPEN");

			//editor.commit();
		} else {

		}

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

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void notemailgetAlertDialog(Context context, String title,
			String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		// Set an EditText view to get user input
		input = new EditText(context);
		alertDialogBuilder.setView(input);
		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						if (validate()) {
							dialog.cancel();
							String email = input.getText().toString();
							user = uDataHandler.getUser();

							if (user == null) {
								user = new User();
							}

							String firstname = sharedpreferences.getString(
									BiteBc.FIRSTNAME, "");
							String lastname = sharedpreferences.getString(
									BiteBc.LASTNAME, "");
							user.setEmail(email);
							// user.getEmail();
							String pwd = sharedpreferences.getString(
									BiteBc.USER_PWD_FINAL, "");
							Map<String, String> params = new HashMap<String, String>();
							params.put("firstname", firstname);
							params.put("lastname", lastname);
							params.put("email", email);
							params.put("password", pwd);
							params.put("device_id", deviceid);
							params.put("platform", "android");
							params.put("section", "facebook");
							user.setFirstName(sharedpreferences.getString(
									BiteBc.FIRSTNAME, ""));
							user.setLastName(sharedpreferences.getString(
									BiteBc.LASTNAME, ""));
							user.setEmail(email);
							user.setuFacebookId(pwd);
							uDataHandler.setUser(user);

							Editor editor = sharedpreferences.edit();
							editor.putString(BiteBc.USER_ID_FINAL, email);
							// editor.putString("LASTNAME",
							// user.getuFacebookId());

							editor.commit();

							makeRegisterRequest(Web.REGISTER, params);
						} else {
							dialog.cancel();

						}

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private boolean validate() {
		String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";

		if (input.getText().toString().trim().equals("")) {
			// showAlertDialog(this, "Error", "Please enter email", true);
			againshowAlertDialog(getActivity(), "Error",
					"Por favor, introduzca de correo electrónico válida");
			return false;
		}

		if (!input.getText().toString().matches(emailPattern)) {
			// showAlertDialog(this, "Error", "Please enter valid email", true);
			againshowAlertDialog(getActivity(), "Error",
					"Por favor, introduzca de correo electrónico válida");
			return false;
		}

		return true;
	}

	public void againshowAlertDialog(Context context, String title,
			String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.fail);
		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						notemailgetAlertDialog(getActivity(), "Email",
								"Se ruega confirmación de Facebook Correo");

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
