package com.brst.android.bite.app.membership;

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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class MemberShipStepTwoFragment extends Fragment implements
		OnClickListener {

	public static String PUBLISHABLE_KEY = null;
	public static final String ARG_PLAN_ID = "id";
	private static final String ARG_COUPON = "coupon_s";

	private static String TAG = MemberShipStepTwoFragment.class.getName();

	EditText cardNumber, cvcNUmber;

	Spinner cardType, sExpYear, sEMonthYear;
	SpinnerAdapter adapterCType, adapterExpYear, adapterMonth,
			adapterStartYear;

	Button submit;

	UserDataHandler uDataHandler;

	User user;

	SharedPreferences sharedpreferences;

	String cardNumberValue, cardCVCNumber;
	Integer cardExpiryYear, cardExpiryMonth, card_name;
	String planID;
	String couponCode;
	
	public static MemberShipStepTwoFragment create(String id, String couponcode) {
		MemberShipStepTwoFragment fragment = new MemberShipStepTwoFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PLAN_ID, id);
		args.putString(ARG_COUPON, couponcode);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uDataHandler = UserDataHandler.getInstance();
		user = uDataHandler.getUser();
		planID = getArguments().getString(ARG_PLAN_ID);
		couponCode = getArguments().getString(ARG_COUPON);
		PUBLISHABLE_KEY = getPublishKeyFromStripe();
		sharedpreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout_step_two,
				container, false);

		cardNumber = (EditText) rootView.findViewById(R.id.card_number);
		cvcNUmber = (EditText) rootView.findViewById(R.id.cvc_number);

		cardType = (Spinner) rootView.findViewById(R.id.card_type);
		sExpYear = (Spinner) rootView.findViewById(R.id.expire_year);
		sEMonthYear = (Spinner) rootView.findViewById(R.id.expire_month);

		addTextChangeLisner();
		addSpinnerItemListner();

		submit = (Button) rootView.findViewById(R.id.btn_submit_);
		submit.setOnClickListener(this);

		String[] cardTypeItem = getResources().getStringArray(
				R.array.array_card_type);

		String[] expYearItem = getResources().getStringArray(
				R.array.arrary_year_e);
		String[] monthItem = getResources()
				.getStringArray(R.array.arrary_month);

		adapterCType = new SpinnerAdapter(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, cardTypeItem);
		adapterExpYear = new SpinnerAdapter(getActivity(),
				R.layout.layout_spinner_item_checkout, expYearItem);
		adapterMonth = new SpinnerAdapter(getActivity(),
				R.layout.layout_spinner_item_checkout, monthItem);

		cardType.setAdapter(adapterCType);
		sExpYear.setAdapter(adapterExpYear);
		sEMonthYear.setAdapter(adapterMonth);

		rootView.findViewById(R.id.btn_header_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().onBackPressed();

					}
				});

		return rootView;
	}

	private void addTextChangeLisner() {

		// cvcNUmber
		// .setOnEditorActionListener(new TextView.OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// if ((actionId == EditorInfo.IME_ACTION_DONE)) {
		// if (checkField()) {
		// hideKeyboard(getActivity(), cvcNUmber);
		// getStripeToken();
		//
		// } else {
		// hideKeyboard(getActivity(), cvcNUmber);
		// }
		// return true;
		// }
		// return false;
		// }
		// });

	}

	protected void getStripeToken() {
		UI.showProgressDialog(getActivity());
		Card card = new Card(cardNumber.getText().toString(), cardExpiryMonth,
				cardExpiryYear, cvcNUmber.getText().toString());

		boolean validation = card.validateCard();
		if (validation) {
			new Stripe().createToken(card, PUBLISHABLE_KEY,
					new TokenCallback() {
						public void onSuccess(Token token) {
							// LogMsg.LOG(getActivity(), token.toString());
							//Log.e("TOKEN", token.toString());
							sendTokenToServer(token);

						}

						public void onError(Exception error) {
							// LogMsg.LOG(getActivity(),
							// error.getLocalizedMessage());
							Log.e("TOKEN_Error", error.getLocalizedMessage());
							LogMsg.LOG(getActivity(),
									error.getLocalizedMessage());
							UI.hideProgressDialog();
						}
					});
		} else if (!card.validateNumber()) {
			UI.hideProgressDialog();
			LogMsg.LOG(getActivity(),
					"El número de la tarjeta que ha introducido no es válido");
		} else if (!card.validateExpiryDate()) {
			UI.hideProgressDialog();
			LogMsg.LOG(getActivity(),
					"La fecha de caducidad que ha introducido no es válido");
		} else if (!card.validateCVC()) {
			UI.hideProgressDialog();
			LogMsg.LOG(getActivity(),
					"El código CVV que ha introducido no es válido");
		} else {
			UI.hideProgressDialog();
			LogMsg.LOG(getActivity(),
					"Los datos de la tarjeta que ha introducido no son válidas");
		}
	}
	protected void sendTokenToServer(Token token) {

		if (token != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(DataKey.PLANID, planID);
			params.put(DataKey.COUPON_CODE, couponCode);
			params.put(DataKey.CUST_ID, user.getCustomerid());
			params.put(DataKey.TOKEN_ID, token.getId());
			
			
			Log.e("planID", planID);
			Log.e("couponCode", couponCode);
			Log.e("user.getCustomerid()", user.getCustomerid());
			Log.e("token.getId()", token.getId());

			makeRequestForCharge(WSConstant.Web.BUY, params);
		} else {
			LogMsg.LOG(getActivity(), "Error en conseguir datos de la tarjeta");
			UI.hideProgressDialog();
		}

	}

	// network call to purchase plan sending token to server
	private void makeRequestForCharge(String api,
			final Map<String, String> params) {

		String url = Web.HOST + api;
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e(TAG + "On Response", response);
						parseResult(response);
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

	protected void parseResult(String response) {
		UI.hideProgressDialog();
		if (response != null) {
			try {
				JSONObject jsonOBject = new JSONObject(response);
				if (jsonOBject.getBoolean("success")) {

					Editor editor = sharedpreferences.edit();
					editor.putString(BiteBc.USER_PLAN, planID);
					editor.commit();

					Intent intent = new Intent(getActivity(),
							HomeActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_left, R.anim.slide_out_right);
					getActivity().finish();

				} else if(!jsonOBject.getBoolean("success")) {
					String message = jsonOBject.getString("message");
					Log.e("message",""+message);
					
					
					show_Alert_Dialog(getActivity(), "",
							message);
				}

			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			LogMsg.LOG(getActivity(), "No se ha podido registrada");
		}

	}

	private String getPublishKeyFromStripe() {
		String key = BiteBc.STRIPE_KEY;

		if (key.equals(BiteBc.STRIPE_TEST)) {
			return getString(R.string.pk_test);
		} else if (key.equals(BiteBc.STRIPE_LIVE)) {
			return getString(R.string.pk_live);
		}
		return null;

	}

	// check ui field to be not null
	protected boolean checkField() {

		if (cardNumber.getText().toString().trim().equals("")) {
			LogMsg.LOG(getActivity(), "Por favor, introduzca el número de tarjeta");
			cardNumber.requestFocus();
			cardNumber.setFocusableInTouchMode(true);
			return false;
		}
		if (cvcNUmber.getText().toString().trim().equals("")) {
			LogMsg.LOG(getActivity(), "Por favor, introduzca el número CVV");
			cvcNUmber.requestFocus();
			cvcNUmber.setFocusableInTouchMode(true);
			return false;
		}
		// if(){
		// return false;
		// }

		return true;
	}

	// Class util funcitons to hide keyboard programically
	public static boolean hideKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

	}

	// handle all spinner listener here
	private void addSpinnerItemListner() {
		sExpYear.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				cardExpiryYear = Integer.valueOf(parent.getItemAtPosition(
						position).toString());
				

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		sEMonthYear.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				cardExpiryMonth = (position + 1);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		cardType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				card_name = (position + 1);
				Log.d("card_name:", "" + card_name);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

	}

	private class SpinnerAdapter extends ArrayAdapter<String> {
		Context context;
		String[] items = new String[] {};

		public SpinnerAdapter(final Context context,
				final int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(
						R.layout.layout_spinner_dropdown, parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.txt_d_id);
			tv.setText(items[position]);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(15);
			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(
						R.layout.layout_spinner_item_checkout, parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.txt_id);
			tv.setText(items[position]);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(15);
			return convertView;
		}
	}
	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.btn_submit_:
			if (checkField()) {
				hideKeyboard(getActivity(), cvcNUmber);
				getStripeToken();

			} else {
				hideKeyboard(getActivity(), cvcNUmber);
			}

			break;

		default:
			break;
		}

	}
	
	
	public void show_Alert_Dialog(Context context, String title, String message) {
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
	
	


}
