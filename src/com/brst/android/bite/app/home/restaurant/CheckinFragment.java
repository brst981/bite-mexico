package com.brst.android.bite.app.home.restaurant;

import java.util.HashMap;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

/**
 * @author ShalviSharma
 * 
 *         ${tags}
 */
public class CheckinFragment extends Fragment implements OnClickListener,
		TextWatcher {

	private static String TAG = "CheckinFragment";
	RestaurantDataHandler rHandler;
	Restaurant restaurant;
	UserDataHandler uHandler;
	User user;
	ImageLoader imageLoader;
	private boolean isResumed = false;
	ImageView mImageView;

	TextView name;
	Button btnConfrim;
	ProfilePictureView profilePictureView;
	String userImageurl;

	TextView validity, mTextViewdate, txtMembrid;
	SharedPreferences mSharedPreferences;
	String packageDescription;
	String daysLeft, date;
	HashMap<String, String> params;
	EditText edtTotalbill, edtDiscount;
	String totalbill, biteDisc;
	long bill = 0;
	long discount = 0;
	public String current = "0";
	boolean isFirstTimeType = true;

	int cursorPosition = 0;

	private static final int REAUTH_ACTIVITY_CODE = 100;

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	public static CheckinFragment create(String packageDescription,
			String daysLeft, String date) {
		CheckinFragment fragment = new CheckinFragment();
		Bundle args = new Bundle();
		args.putString("pkg", packageDescription);
		args.putString("days", daysLeft);
		args.putString("date", date);
		fragment.setArguments(args);
		return fragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback); // track the
		Log.e("checkm in ","check in");

		uiHelper.onCreate(savedInstanceState);
		uHandler = UserDataHandler.getInstance();
		user = uHandler.getUser();
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();

		if (getArguments() != null) {
			packageDescription = getArguments().getString("pkg");
			daysLeft = getArguments().getString("days");
			date = getArguments().getString("date");
			Log.e("days",daysLeft);
			Log.e("date",date);

		}
		mSharedPreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		imageLoader = AppController.getInstance().getImageLoader();
		// imageLoader = AppController.getInstance().getImageLoader();
		// userImageurl = "http://graph.facebook.com/" + user.getuFacebookId()
		// + "/picture?type=small";

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout_checkin_,
				null);

		name = (TextView) rootView.findViewById(R.id.name);
		txtMembrid = (TextView) rootView.findViewById(R.id.txt_member_id);
		btnConfrim = (Button) rootView.findViewById(R.id.btn_confirm);
		edtTotalbill = (EditText) rootView.findViewById(R.id.edt_total_bill);
		edtDiscount = (EditText) rootView.findViewById(R.id.edt_bite_discount);

		edtTotalbill.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String currentTemp = current;

				if (isFirstTimeType
						|| edtTotalbill.getText().toString().equals("")) {
					isFirstTimeType = false;
					cursorPosition = 1;
				}

				if (edtTotalbill.getText().toString().equals("$")) {
					edtTotalbill.setText("");
					current = "";
				} else {
					if (!edtTotalbill.getText().toString().equals("")) {

						String newString = "";
						String withDollar = edtTotalbill.getText().toString();
						String strings[] = withDollar.split("\\$");

						for (int i = 0; i < strings.length; i++) {
							newString += strings[i];

						}

						if (!current.equals("$" + newString)) {

							if (edtTotalbill.getSelectionStart() > 0
									&& edtTotalbill.getSelectionStart() < currentTemp
											.length()) {

							}

							current = "$" + newString;
							edtTotalbill.setText(current);

							if (edtTotalbill.getText().toString().length() > currentTemp
									.length()) {
								cursorPosition++;

							} else if (edtTotalbill.getText().toString()
									.length() < currentTemp.length()) {
								cursorPosition--;
							}

							if (cursorPosition < 0) {
								cursorPosition = 0;
							}
							if (cursorPosition > edtTotalbill.getText()
									.toString().length()) {
								cursorPosition = edtTotalbill.getText()
										.toString().length();
							}

							edtTotalbill.setSelection(cursorPosition);
						} else {
							if (!edtTotalbill.getText().toString()
									.contains("$")) {
								edtTotalbill.setText("$"
										+ edtTotalbill.getText().toString());
							}
						}

					}

				}

				// TODO Auto-generated method stub
				/*
				 * if (!s.toString().equals(current) &&
				 * !edtTotalbill.getText().equals("")) {
				 * 
				 * 
				 * 
				 * //edtTotalbill.removeTextChangedListener(this); // String
				 * cleanString = s.toString().replaceAll("[$,.]", ""); // double
				 * parsed = Double.parseDouble(cleanString); // String formatted
				 * = NumberFormat.getCurrencyInstance() // .format((parsed /
				 * 100)); // current = formatted; //
				 * edtTotalbill.setText(formatted); //
				 * edtTotalbill.setSelection(formatted.length()); // //
				 * edtTotalbill.addTextChangedListener(this);
				 * 
				 * } else { current = ""; }
				 */

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		edtDiscount.addTextChangedListener(this);

		btnConfrim.setOnClickListener(this);

		profilePictureView = (ProfilePictureView) rootView
				.findViewById(R.id.selection_profile_pic);
		mImageView = (ImageView) rootView.findViewById(R.id.custom_profile_pic);
		profilePictureView.setCropped(true);
		txtMembrid.setText(user.getCustomerid());

		if (mSharedPreferences != null
				&& mSharedPreferences.getString("WITHOUT_FACEBOOK", "")
						.matches("301")) {
			profilePictureView.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
			imageLoader.get(mSharedPreferences.getString("profileimg_url", ""),
					new ImageListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i(TAG,
									"Image Load Error: " + error.getMessage());
						}

						@Override
						public void onResponse(ImageContainer response,
								boolean arg1) {
							if (response.getBitmap() != null) {
								// load image into imageview
								mImageView.setImageBitmap(response.getBitmap());
							}
						}

					});

		}

		name.setText(mSharedPreferences.getString(BiteBc.FIRSTNAME, "") + " "
				+ mSharedPreferences.getString(BiteBc.LASTNAME, ""));
		validity = (TextView) rootView.findViewById(R.id.validity);
		// remainingTime = (TextView) rootView.findViewById(R.id.remainingTime);
		mTextViewdate = (TextView) rootView.findViewById(R.id.remainingdate);

		if (packageDescription != null) {
			Log.e("package description",packageDescription);
			validity.setText(packageDescription);
		}
		if (daysLeft != null && !daysLeft.equals("")) {
			// remainingTime.setText(String.valueOf(daysLeft + " days left"));
		}
		if (date != null && !date.equals("")) {
			
			//Quedan 27 días
			mTextViewdate.setText("Quedan "+daysLeft +" días");
		}

		else {
			mTextViewdate.setVisibility(View.GONE);
		}

		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
		}
		params = new HashMap<String, String>();
		params.put(DataKey.CUSTOMER_ID, user.getCustomerid());
		// makeRequestForCheckIn(Web.CHECKIN, params);

		return rootView;
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

						// msgResponse.setText(response.toString());
						// if (checkin.equals(Web.CHECKIN)) {
						// //parseResponse(response);
						// } else if (checkin.equals(Web.CHECK_IN)) {
						// //parseResponseForCheckIn(response);
						// }

						checkResponse(response);
						UI.hideProgressDialog();
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e(TAG, "Error: " + error.getMessage());

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

	protected void checkResponse(String response) {
		// TODO Auto-generated method stub
		try {
			Log.e("response",response);
			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {
				String msg = jsonOBject.getString("message");
				showAlertDialog(getActivity(), "", msg, true);
			} else {
				String msg = jsonOBject.getString("message");
				showAlertDialog_(getActivity(), "Error", msg, true);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	protected void parseResponseForCheckIn(String response) {

		try {
			JSONObject jsonOBject = new JSONObject(response);

			if (jsonOBject.getBoolean("success")) {
				String message = String.valueOf(mSharedPreferences.getString(
						BiteBc.FIRSTNAME, "")
						+ " "
						+ mSharedPreferences.getString(BiteBc.LASTNAME, "")
						+ " membership confirmed. Thank you.");
				if (jsonOBject.getString("status").equals("true")) {
					LogMsg.showAlertDialog(getActivity(), "Success", message,
							false);
					btnConfrim.setVisibility(View.GONE);
				} else {
					String errorMsg = "Membership not confirmed. Please try later";
					LogMsg.showAlertDialog(getActivity(), "Error", errorMsg,
							true);

				}
			} else {
				packageDescription = "No membership Plan found";
				daysLeft = "";
			}

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		UI.hideProgressDialog();

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

	protected void parseResponse(String response) {
		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);

			if (jsonOBject.getBoolean("success")) {
				Log.e("result",""+jsonOBject);
				packageDescription = jsonOBject.getString("package_name");
				daysLeft = String.valueOf(jsonOBject.getInt("daysleft"));
				date = String.valueOf(jsonOBject.getInt("end_date"));

				validity.setText(packageDescription);
				if (!daysLeft.equals("")) {

					// mTextViewdate.setText(daysLeft + " Days left");
				}
			} else {
				showAlertDialog(getActivity(), "No Membership",
						"your membership plan has been paused");
				packageDescription = "No membership Plan found";
				daysLeft = "";
				mTextViewdate.setVisibility(View.GONE);
			}

		} catch (JSONException e) {
			packageDescription = "Sorry no information";
			daysLeft = "";

			mTextViewdate.setVisibility(View.GONE);
		}

	}

	/**
	 * Facebook initialization
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */

	private void onSessionStateChange(final Session session,
			SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
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
								profilePictureView.setProfileId(user.getId());
								// Set the Textview's text to the user's name.
							}
						}
						if (response.getError() != null) {
							profilePictureView.setProfileId(null); // Handle
						}
					}
				});
		request.executeAsync();
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.btn_confirm:

			totalbill = edtTotalbill.getText().toString()
					.replaceAll("[$,]", "");
			biteDisc = edtDiscount.getText().toString().replaceAll("[$,]", "");

			//if (validate()) {
				params = new HashMap<String, String>();
				Log.e("customer id",user.getCustomerid());//8039,1,207
				Log.e("total amount",totalbill);
				Log.e("discount",biteDisc);
				Log.e("rest_id",restaurant.getId());

				
				params.put("customer_id", user.getCustomerid());
				params.put("total_amount", totalbill);
				params.put("discount", biteDisc);

				params.put("rest_id", restaurant.getId());
				makeRequestForCheckIn(Web.REPORTING, params);
			//}

			break;

		default:
			break;
		}

	}

	private boolean validate() {

		/*if (edtTotalbill.getText().toString().trim().equals("")) {
			showAlert_Dialog_(getActivity(), "Error",
					"Por favor primero Ingresa el importe pagado", true);
			return false;
		}*/
		/*if (edtDiscount.getText().toString().trim().equals("")) {
			showAlert_Dialog_(getActivity(), "Error",
					"Por favor, introduzca su descuento mordida", true);
			return false;
		}*/

		String test = edtTotalbill.getText().toString();
		test = test.replaceAll("[$,.]", "");
		if(!test.equals(""))
		bill = Long.valueOf(test);		
		String test2 = edtDiscount.getText().toString();
		test2 = test2.replaceAll("[$,.]", "");
		if(!test2.equals(""))
		discount = Long.valueOf(test2);
		
		/*if(!edtDiscount.getText().toString().trim().equals(""))
		{

		if (discount > bill) {
			show_AlertDialog_(getActivity(), "Error",
					"descuento picadura no debe mayor que a la factura total.",
					true);
			return false;
		}}*/

		return true;
	}

	public void showAlertDialog(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		if (error)
			alertDialogBuilder.setIcon(R.drawable.fail);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity

						
						edtDiscount.setText("");

						edtTotalbill.setText("");
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void showAlertDialog_(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		if (error)
			alertDialogBuilder.setIcon(R.drawable.fail);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity

						/* edtCode.setText(""); */

						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void showAlert_Dialog_(Context context, String title,
			String message, boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		if (error)
			alertDialogBuilder.setIcon(R.drawable.fail);

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

	public void show_AlertDialog_(Context context, String title,
			String message, boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		if (error)
			alertDialogBuilder.setIcon(R.drawable.fail);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity

						edtDiscount.setText("");

						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		String currentTemp = current;

		if (isFirstTimeType || edtTotalbill.getText().toString().equals("")) {
			isFirstTimeType = false;
			cursorPosition = 1;
		}

		if (edtDiscount.getText().toString().equals("$")) {
			edtDiscount.setText("");
			current = "";
		} else {
			if (!edtDiscount.getText().toString().equals("")) {

				String newString = "";
				String withDollar = edtDiscount.getText().toString();
				String strings[] = withDollar.split("\\$");

				for (int i = 0; i < strings.length; i++) {
					newString += strings[i];

				}

				if (!current.equals("$" + newString)) {

					if (edtDiscount.getSelectionStart() > 0
							&& edtDiscount.getSelectionStart() < currentTemp
									.length()) {

					}

					current = "$" + newString;
					edtDiscount.setText(current);

					if (edtDiscount.getText().toString().length() > currentTemp
							.length()) {
						cursorPosition++;

					} else if (edtDiscount.getText().toString().length() < currentTemp
							.length()) {
						cursorPosition--;
					}

					if (cursorPosition < 0) {
						cursorPosition = 0;
					}
					if (cursorPosition > edtDiscount.getText().toString()
							.length()) {
						cursorPosition = edtDiscount.getText().toString()
								.length();
					}

					edtDiscount.setSelection(cursorPosition);
				} else {
					if (!edtDiscount.getText().toString().contains("$")) {
						edtDiscount.setText("$"
								+ edtDiscount.getText().toString());
					}
				}

			}

		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

}
