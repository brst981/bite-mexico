package com.brst.android.bite.app;

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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.membership.MembershipActivity;
import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;

public class LoginPage extends Activity implements OnClickListener {
	Button mButtonregister, mButtonlogin;
	ImageView mButtonback;
	EditText mEditTextemail, mEditText_password;
	SharedPreferences sharedpreferences;
	private UserDataHandler uDataHandler;
	private User user;
	String city, city_id;
	String customerId, username, password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		sharedpreferences = LoginPage.this.getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		username = sharedpreferences.getString(BiteBc.USER_ID_FINAL, "");
		password = sharedpreferences.getString(BiteBc.USER_PWD_FINAL, "");
		user = new User();
		uDataHandler = UserDataHandler.getInstance();
		mButtonback = (ImageView) findViewById(R.id.btn_back_loginpage);
		mButtonregister = (Button) findViewById(R.id.button_registerpage);
		mButtonlogin = (Button) findViewById(R.id.login);
		mEditTextemail = (EditText) findViewById(R.id.loginEmail);
		mEditText_password = (EditText) findViewById(R.id.loginpassword);
		mButtonback.setOnClickListener(this);
		mButtonlogin.setOnClickListener(this);
		mButtonregister.setOnClickListener(this);

		if (username != null) {
			mEditTextemail.setText(username);
		} else {
			mEditTextemail.setText("");
		}

		if (password != null) {
			mEditText_password.setText(password);
		} else {
			mEditText_password.setText("");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back_loginpage:
			Editor editor = sharedpreferences.edit();

			editor.putString("POP_UP", "CLOSED");

			editor.commit();
			Intent mIntent = new Intent(LoginPage.this, MainActivity_2.class);
			startActivity(mIntent);
			this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			break;
		case R.id.button_registerpage:
			Intent m_Intent = new Intent(LoginPage.this, LoginBiteBc.class);
			startActivity(m_Intent);
			this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			break;
		case R.id.login:
			if (validate()) {
				HashMap<String, String> params = new HashMap<>();
				params.put(DataKey.USER_ID, mEditTextemail.getText().toString());
				params.put(DataKey.USER_PWD, mEditText_password.getText()
						.toString());
				params.put("section", "email");

				makeRegisterRequest(WSConstant.Web.LOGIN, params);
			}
			break;

		default:
			break;
		}

	}

	private boolean validate() {
		// String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		if (mEditTextemail.getText().toString().trim().equals("")) {

			showAlertDialog(this, "Error", "Por favor ingresa tu correo", true);
			return false;
		}

		// if (!mEditTextemail.getText().toString().matches(emailPattern)) {
		// showAlertDialog(this, "Error", "Please enter valid email", true);
		// return false;
		// }

		if (mEditText_password.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa tu contraseña", true);
			return false;
		}

		return true;
	}

	// four call
	private void checkUserStatus() {
		HashMap<String, String> params;
		String mStringTest = sharedpreferences.getString(BiteBc.CUST_ID, "");
		params = new HashMap<String, String>();
		params.put(DataKey.CUSTOMER_ID,
				sharedpreferences.getString(BiteBc.CUST_ID, ""));
		makeRequestForCheckIn(Web.CHECKIN, params);

	}

	// five call
	private void makeRequestForCheckIn(final String checkin,
			final Map<String, String> params) {

		String url = Web.HOST + checkin;

		UI.showProgressDialog(this);
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						parseResponseChekin(response);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("", "Error: " + error.getMessage());
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

	// six call
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
					intent = new Intent(LoginPage.this,
							MembershipActivity.class);
					startActivity(intent);
					LoginPage.this.overridePendingTransition(
							R.anim.slide_in_left, R.anim.slide_out_right);
					LoginPage.this.finish();
				} else if (jsonOBject.getString("status").equals("paused")) {
					show_Alert_Dialog(LoginPage.this, "La membresía desactivado",
							"Su cuenta ha sido desactivada. Por favor, espere a que la aprobación del administrador.");

				} else {
					show_Alert_Dialog(LoginPage.this, "Error",
							"Su plan de membresía ha expirado");
				}
			} else {

				String planId = jsonOBject.getString("planid");
				if (jsonOBject.getString("city") != null) {
					city = jsonOBject.getString("city");
					city_id = jsonOBject.getString("cityid");
				}

				Editor editor = sharedpreferences.edit();

				editor.putString("profileimg_url",
						jsonOBject.getString("profileimg_url"));

				editor.putString(BiteBc.CITY, city);
				editor.putString(BiteBc.CITY_ID, city_id);
				editor.commit();

				editor.putString(BiteBc.USER_PLAN, planId);
				editor.commit();

				uDataHandler
						.setUserSelectedPlan(jsonOBject.getString("planid"));
				showMembershipActivity();

			}

		} catch (JSONException e) {
			Log.e("", e.getMessage());
		}

	}

	// seven call
	private void showMembershipActivity() {

		if (sharedpreferences != null
				&& sharedpreferences.contains(BiteBc.USER_PLAN)) {
			Intent intent = new Intent(LoginPage.this, HomeActivity.class);
			intent.putExtra("CITY", city);
			intent.putExtra("ID", customerId);

			startActivity(intent);
			LoginPage.this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			LoginPage.this.finish();

		} else {
			Intent intent1 = new Intent(LoginPage.this,
					MembershipActivity.class);
			startActivity(intent1);
			LoginPage.this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			LoginPage.this.finish();

		}

	}

	/**
	 * Making json object request to register user;
	 * */

	private void makeRegisterRequest(String login,
			final Map<String, String> rParams) {

		String url = Web.HOST + login;
		UI.showProgressDialog(this);
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("ME:" + "On Response", response);
						// msgResponse.setText(response.toString());
						UI.hideProgressDialog();
						login(response);

					}

				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// VolleyLog.e(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
						show_Alert_Dialog(LoginPage.this, "Error",
								"Error al conectar con el servidor. Por favor, intente más tarde");
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

	public void show_Alert_Dialog(Context context, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.fail);

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						// isInternetPresent = cd.isConnectingToInternet();
						// checkInternet(isInternetPresent, tag);

					}
				})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
								LoginPage.this.finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	protected void login(String response) {

		try {

			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {
				// user = new User();
				customerId = jsonOBject.getString("customerid");
				String session = jsonOBject.getString("sessionid");
				String firstName = jsonOBject.getString("firstname");
				String lastName = jsonOBject.getString("lastname");

				if (jsonOBject.getString("city") != null) {
					city = jsonOBject.getString("city");
					city_id = jsonOBject.getString("cityid");
				}

				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.CUST_ID, customerId);
				// editor.putString(DataKey.CUSTOMER_ID, customerId);

				editor.putString("SECTION_PHP", "email");
				editor.putString("WITHOUT_FACEBOOK", "301");
				editor.putString(BiteBc.FIRSTNAME, firstName);
				editor.putString(BiteBc.LASTNAME, lastName);
				editor.putString(BiteBc.USER_ID_FINAL, mEditTextemail.getText()
						.toString());
				editor.putString(BiteBc.USER_PWD_FINAL, mEditText_password.getText()
						.toString());
				editor.putString(BiteBc.CITY, city);
				editor.putString(BiteBc.CITY_ID, city_id);

				editor.commit();
				// user.setFirstName(firstName);
				// user.setLastName(lastName);
				user.setCustomerid(customerId);
				user.setUserSessionId(session);
				// user.setEmail(userId);
				// user.setuFacebookId(pwd);
				uDataHandler.setUser(user);
				checkUserStatus();

			} else {

				showAlertDialog(LoginPage.this, "Error",
						jsonOBject.getString("message"), true);
				// openMainActivity(TAG_1);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
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
						// isInternetPresent = cd.isConnectingToInternet();
						// checkInternet(isInternetPresent, tag);
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
