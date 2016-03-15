package com.brst.android.bite.app;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.membership.MembershipActivity;
import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class MainActivity extends FragmentActivity {

	private UserDataHandler uDataHandler;

	SharedPreferences sharedpreferences;

	private User user;

	private static final String TAG = "MainActivity";

	private boolean isResumed = false;

	private static final int LOGIN = 0;
	// private static final int SELECTION = 1;
	private static final int FRAGMENT_COUNT = 1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	private UiLifecycleHelper uiHelper;

	private ProgressDialog pDialog;
	// These tags will be used to cancel the requests
	private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

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
		setContentView(R.layout.activity_main);

		uDataHandler = UserDataHandler.getInstance();

		uiHelper = new UiLifecycleHelper(this, callback); // track the session
		uiHelper.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();
		fragments[LOGIN] = fm.findFragmentById(R.id.loginFragment);
		// fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		transaction.commit();

	}

	/**
	 * this method handle to hide or show fragment that was hide on start
	 * 
	 * @param fragmentIndex
	 * @param addToBackStack
	 */
	private void showFragment(int fragmentIndex, boolean addToBackStack) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				ft.show(fragments[i]);
			} else {
				ft.hide(fragments[i]);
			}

		}
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();

	}

	/**
	 * The method shows the relevant fragment based on the person's
	 * authenticated state.
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// Only make changes if the activity is visible
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			// Clear the back stack
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			if (state.isOpened()) {
				// If the session state is open:
				// Show the authenticated fragment
				// showFragment(SELECTION, false);
				makeMeRequest(session);
			} else if (state.isClosed()) {
				// If the session state is closed:
				// Show the login fragment
				showFragment(LOGIN, false);
			}
			// if (!session.isOpened()) {
			// // if the session is already open,
			// // try to show the selection fragment
			// // showFragment(SELECTION, false);
			// // showMembershipActivity();
			// showFragment(LOGIN, false);
			// }
		}
	}

	/**
	 * this method handle the case where fragments are newly instantiated and
	 * the authenticated versus nonauthenticated UI needs to be properly set.
	 */
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Session session = Session.getActiveSession();

		if (session != null && !session.isOpened()) {
			// if the session is already open,
			// try to show the selection fragment
			// showFragment(SELECTION, false);
			// makeMeRequest(session);
			// String[] permission = { "public_profile", "email" };
			// session.openForRead(new Session.OpenRequest(this).setPermissions(
			// Arrays.asList(permission)).setCallback(callback));
			showFragment(LOGIN, false);
		}

	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		UI.showProgressDialog(this);
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

								JSONObject jObject = user.getInnerJSONObject();

								registerOrLoginUser(jObject);

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

	protected void registerOrLoginUser(JSONObject jObject) {
		try {
			user = uDataHandler.getUser();

			if (user == null) {
				user = new User();
			}
			String firstname = jObject.getString("first_name");
			String lastname = jObject.getString("last_name");
			String email = jObject.getString("email");
			String pwd = jObject.getString("id");

			user.setFirstName(firstname);
			user.setLastName(lastname);
			user.setEmail(email);
			user.setuFacebookId(pwd);
			Map<String, String> params = new HashMap<String, String>();
			params.put("fristname", firstname);
			params.put("lastname", lastname);
			params.put("email", email);
			params.put("password", pwd);
			makeRegisterRequest(Web.REGISTER, params);

		} catch (JSONException e) {
			e.printStackTrace();
		}
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
						Log.e(TAG + "On Response", response);
						// msgResponse.setText(response.toString());
						UI.hideProgressDialog();
						login(response);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e(TAG, "Error: " + error.getMessage());
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
		AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

		// Cancelling request
		// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);

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

				uDataHandler.setUser(user);
				showMembershipActivity();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// private void showProgressDialog() {
	// if (pDialog == null) {
	// pDialog = new ProgressDialog(this, R.style.ProgressDialog);
	// pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
	// pDialog.setCancelable(false);
	// pDialog.show();
	// }
	// }
	//
	// private void hideProgressDialog() {
	// if (pDialog != null && pDialog.isShowing()) {
	// pDialog.dismiss();
	// pDialog = null;
	// }
	// }

	private void showMembershipActivity() {

		sharedpreferences = getSharedPreferences(BiteBc.MyPREFERENCES,
				Context.MODE_PRIVATE);

		Intent intent;
		if (sharedpreferences != null
				&& sharedpreferences.contains(BiteBc.USER_PLAN)) {
			intent = new Intent(MainActivity.this, HomeActivity.class);
		} else {
			intent = new Intent(MainActivity.this, MembershipActivity.class);
		}

		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();

	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
		UI.hideProgressDialog();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
