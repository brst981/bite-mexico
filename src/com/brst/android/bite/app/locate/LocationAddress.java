package com.brst.android.bite.app.locate;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocationAddress {
	private static final String TAG = "LocationAddress";
	static StringBuilder sb;
	static List<Address> addressList;
	static Address address;
	static String test;

	public static void getAddressFromLocation(final double latitude,
			final double longitude, final Context context, final Handler handler) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				String result = null;
				try {
					addressList = geocoder.getFromLocation(latitude, longitude,
							1);
					if (addressList != null && addressList.size() > 0) {
						address = addressList.get(0);
						sb = new StringBuilder();
						for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
							sb.append(address.getAddressLine(i)).append("\n");
						}
						sb.append(address.getLocality()).append("\n");
						test = sb.append(address.getPostalCode()).append("\n")
								.toString();
						sb.append(address.getCountryName());
						result = sb.toString();
					}
				} catch (IOException e) {
					Log.e(TAG, "Unable connect to Geocoder", e);
				} finally {
					Message message = Message.obtain();

					message.setTarget(handler);
					if (result != null) {
						message.what = 1;
						Bundle bundle = new Bundle();
						result = test;

						bundle.putString("address", result);
						message.setData(bundle);
					} else {
						message.what = 1;
						Bundle bundle = new Bundle();
						result = "Latitude: " + latitude + " Longitude: "
								+ longitude
								+ "\n Unable to get address for this lat-long.";
						bundle.putString("address", result);
						message.setData(bundle);
					}
					message.sendToTarget();
				}
			}
		};
		thread.start();
	}
}
