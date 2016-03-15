package com.brst.android.bite.app.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.brst.android.bitemaxico.app.R;

public class UI {

	static ProgressDialog pDialog;

	public static void showProgressDialog(Context context) {
		if (pDialog == null) {
			pDialog = new ProgressDialog(context);

			pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			pDialog.setMessage("Cargando...");
			pDialog.setCancelable(false);
			pDialog.show();
		}
	}

	public static void hideProgressDialog() {
		if (pDialog != null && pDialog.isShowing()) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	// Class util funcitons
	public static boolean hideKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

	}

}
