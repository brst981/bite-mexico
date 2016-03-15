package com.brst.android.bite.app.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.BaseContainerFragment;

public class Filter extends Fragment implements OnClickListener,
		OnItemSelectedListener {

	Button btnDone, btnBack;

	CheckBox checkOffer1, checkOffer2;

	Spinner numPeople, cuisines, offerType, availabity;
	SpinnerAdapter numPAdapter, cuisineAdapter, offerTypeAdapter,
			availabityAdapter;

	String textPeopleValue, textCusineValue, textAvailOffer, textAvailbity;

	RadioGroup radioOffer;
	RadioGroup radioAvailablity;
	String[] arrayNumOfPeople;
	String[] arrayCuisine;
	String[] arrayOfferType;
	String[] arrayDays;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		textPeopleValue = "";
		textCusineValue = "";
		textAvailOffer = "";
		textAvailbity = "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		arrayNumOfPeople = getResources().getStringArray(R.array.no_of_people);
		arrayCuisine = getResources().getStringArray(R.array.cuisine);
		arrayOfferType = getResources().getStringArray(R.array.offer_type);
		arrayDays = getResources().getStringArray(R.array.days_array);

		View rootView = inflater.inflate(R.layout.fragment_layout_filter, null);

		numPeople = (Spinner) rootView.findViewById(R.id.spinner_no_people);
		numPeople.setOnItemSelectedListener(this);
		cuisines = (Spinner) rootView.findViewById(R.id.spinner_cuisine);
		cuisines.setOnItemSelectedListener(this);
		offerType = (Spinner) rootView.findViewById(R.id.spinner_offer_type);
		offerType.setOnItemSelectedListener(this);
		availabity = (Spinner) rootView.findViewById(R.id.spinner_availablity);
		availabity.setOnItemSelectedListener(this);
		btnBack = (Button) rootView.findViewById(R.id.btn_header_back);
		btnDone = (Button) rootView.findViewById(R.id.btn_done);
		btnBack.setOnClickListener(this);
		btnDone.setOnClickListener(this);

		numPAdapter = new SpinnerAdapter(getActivity(),
				R.layout.layout_spinner_item, arrayNumOfPeople);
		cuisineAdapter = new SpinnerAdapter(getActivity(),
				R.layout.layout_spinner_item, arrayCuisine);
		offerTypeAdapter = new SpinnerAdapter(getActivity(),
				R.layout.layout_spinner_item, arrayOfferType);
		availabityAdapter = new SpinnerAdapter(getActivity(),
				R.layout.layout_spinner_item, arrayDays);
		numPeople.setAdapter(numPAdapter);
		cuisines.setAdapter(cuisineAdapter);
		offerType.setAdapter(offerTypeAdapter);
		availabity.setAdapter(availabityAdapter);
		return rootView;

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
			tv.setTextSize(20);
			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.layout_spinner_item,
						parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.txt_id);
			tv.setText(items[position]);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20);
			return convertView;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long vid) {
		if (position != 0) {

			int id = parent.getId();
			switch (id) {
			case R.id.spinner_no_people:
				textPeopleValue = parent.getItemAtPosition(position).toString();

				break;
			case R.id.spinner_cuisine:
				textCusineValue = parent.getItemAtPosition(position).toString();

				break;
			case R.id.spinner_offer_type:
				textAvailOffer = String.valueOf(30 + position);
				if (textAvailOffer.equals("33")) {
					textAvailOffer = "100";
				}
				if (textAvailOffer.equals("34")) {
					textAvailOffer = "101";
				}
				if (textAvailOffer.equals("35")) {
					textAvailOffer = "102";
				}
				if (textAvailOffer.equals("36")) {
					textAvailOffer = "103";
				}
				if (textAvailOffer.equals("37")) {
					textAvailOffer = "104";
				}
				if (textAvailOffer.equals("38")) {
					textAvailOffer = "105";
				}

				break;
			case R.id.spinner_availablity:
				textAvailbity = arrayDays[position];

				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_done:
			/*
			 * Intent intent = new Intent();
			 * 
			 * 
			 * 
			 * intent.putExtra("people", textPeopleValue);
			 * intent.putExtra("cusine", textCusineValue);
			 * intent.putExtra("offer1", textAvailOffer);
			 * intent.putExtra("availbity", textAvailbity); //
			 * intent.putExtra("offer2", textOfferOption2);
			 * 
			 * getFragmentManager().popBackStack();
			 * getActivity().onBackPressed();
			 */
			Home_ fragment = new Home_();

			/*
			 * getTargetFragment().onActivityResult(getTargetRequestCode(),
			 * Activity.RESULT_OK, intent);
			 */
			Bundle savedInstanceState = new Bundle();
			savedInstanceState.putString("people", textPeopleValue);
			savedInstanceState.putString("cusine", textCusineValue);
			savedInstanceState.putString("offer1", textAvailOffer);
			savedInstanceState.putString("availbity", textAvailbity);

			fragment.setArguments(savedInstanceState);
			((BaseContainerFragment) getParentFragment()).replaceFragment(
					fragment, true);

			break;

		case R.id.btn_header_back:
			Home fragment2 = new Home();

			((BaseContainerFragment) getParentFragment()).replaceFragment(
					fragment2, true);
			break;

		default:
			break;
		}

	}
}
