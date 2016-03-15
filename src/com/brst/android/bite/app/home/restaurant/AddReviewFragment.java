package com.brst.android.bite.app.home.restaurant;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;

public class AddReviewFragment extends Fragment implements OnClickListener {

	public static final String TAG = "AddReviewFragment";

	public final int[] Rate = { 1, 2, 3, 4, 5 };
	public final String[] key = { "overall", "food", "customer", "carduse",
			"atmosphere", };

	private String tag_json_obj = "jobj_req";

	HashMap<String, String> params = new HashMap<String, String>();
	Button btnCancelReview, btnSubmit;

	EditText name, reviewDesc;

	RatingBar ratingOverall, ratingFood, ratingCustService, ratingEase,
			ratingAtm;

	RestaurantDataHandler rHandler;
	UserDataHandler uHandler;
	Restaurant restaurant;
	User user;

	ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uHandler = UserDataHandler.getInstance();
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();
		user = uHandler.getUser();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_layout_add_review,
				null);
		btnCancelReview = (Button) rootView
				.findViewById(R.id.btn_cancel_review);
		btnCancelReview.setOnClickListener(this);
		btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
		btnSubmit.setOnClickListener(this);

		name = (EditText) rootView.findViewById(R.id.name_v);
		reviewDesc = (EditText) rootView.findViewById(R.id.review_v);

		addOnScorllChangetoReview();

		ratingOverall = (RatingBar) rootView.findViewById(R.id.overall_r_v);
		ratingFood = (RatingBar) rootView.findViewById(R.id.food_r_v);
		ratingCustService = (RatingBar) rootView
				.findViewById(R.id.cust_ser_r_v);
		ratingEase = (RatingBar) rootView.findViewById(R.id.ease_r_v);
		ratingAtm = (RatingBar) rootView.findViewById(R.id.atm_r_v);

		ratingOverall
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {

						setParam(ratingBar.getId(), rating);

					}
				});
		ratingFood
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						setParam(ratingBar.getId(), rating);
					}
				});
		ratingCustService
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						setParam(ratingBar.getId(), rating);
					}
				});
		ratingEase
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						setParam(ratingBar.getId(), rating);
					}
				});
		ratingAtm.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				setParam(ratingBar.getId(), rating);
			}
		});
		setDefaultRatingParam();

		return rootView;
	}

	private void addOnScorllChangetoReview() {
		reviewDesc.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				if (view.getId() == R.id.review_v) {
					view.getParent().requestDisallowInterceptTouchEvent(true);
					switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_UP:
						view.getParent().requestDisallowInterceptTouchEvent(
								false);
						break;
					}
				}
				return false;
			}
		});
	}

	// -- name_v review_v overall_r_v food_r_v cust_ser_r_v ease_r_v atm_r_v
	// btn_submit btn_cancel_review -->
	protected void setParam(int id, float rating) {

		switch (id) {
		case R.id.overall_r_v:
			params.put("ratings[" + key[0] + "]", getIntValue(rating));
			break;
		case R.id.food_r_v:
			params.put("ratings[" + key[1] + "]", getIntValue(rating));
			break;
		case R.id.cust_ser_r_v:
			params.put("ratings[" + key[2] + "]", getIntValue(rating));
			break;
		case R.id.ease_r_v:
			params.put("ratings[" + key[3] + "]", getIntValue(rating));
			break;
		case R.id.atm_r_v:
			params.put("ratings[" + key[4] + "]", getIntValue(rating));
			break;

		default:
			break;
		}

	}

	protected void setDefaultRatingParam() {

		params.put("ratings[" + key[0] + "]", getIntValue(5.0f));
		params.put("ratings[" + key[1] + "]", getIntValue(5.0f));
		params.put("ratings[" + key[2] + "]", getIntValue(5.0f));
		params.put("ratings[" + key[3] + "]", getIntValue(5.0f));
		params.put("ratings[" + key[4] + "]", getIntValue(5.0f));

	}

	private String getIntValue(float rating) {

		int value = 0;
		if (rating == 1.0) {
			value = 1;
		}
		if (rating == 2.0) {
			value = 2;
		}
		if (rating == 3.0) {
			value = 3;
		}
		if (rating == 4.0) {
			value = 4;
		}
		if (rating == 5.0) {
			value = 5;
		}

		return String.valueOf(value);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.btn_cancel_review:

			getFragmentManager()
					.beginTransaction()
					.replace(R.id.restarant_detail_frame_container,
							new Reviews()).commit();
			break;
		case R.id.btn_submit:

			if (validate()) {
				params.put("productid", restaurant.getId());
				params.put("customer_id", user.getCustomerid());
				params.put("detail", reviewDesc.getText().toString());
				params.put("nickname", name.getText().toString());
				makeRequestForRestaurantData(Web.REVIEW, params);
			}

			break;

		default:
			break;
		}

	}

	private boolean validate() {

		if (name != null && name.getText().toString().trim().equals("")) {
			LogMsg.showAlertDialog(getActivity(), "Error", "Por favor escribe...",
					true);
			return false;
		}
		// if (reviewDesc != null
		// && reviewDesc.getText().toString().trim().equals("")) {
		// LogMsg.showAlertDialog(getActivity(), "Error",
		// "Please enter review", true);
		// return false;
		// }

		return true;
	}

	private void makeRequestForRestaurantData(String review,
			final HashMap<String, String> params) {

		String url = Web.HOST + review;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.i(TAG + "On Response", response);
						// msgResponse.setText(response.toString());
						statusOfResponse(response);
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
		AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

	}

	protected void statusOfResponse(String response) {
		UI.hideProgressDialog();
		JSONObject jsonOBject;
		try {
			jsonOBject = new JSONObject(response);

			if (jsonOBject.getBoolean("success")) {
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.restarant_detail_frame_container,
								new Reviews()).commit();
				LogMsg.showAlertDialog(
						getActivity(),
						"Éxito",
						"Comentario publicado correctamente. Por favor, espere a que la aprobación del administrador",
						false);
			} else {
				LogMsg.showAlertDialog(getActivity(), "Error",
						"Error durante la revisión de contabilización. Por favor intente mas tarde", true);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
