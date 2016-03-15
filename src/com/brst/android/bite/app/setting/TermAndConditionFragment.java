package com.brst.android.bite.app.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;

public class TermAndConditionFragment extends Fragment {
	private WebView mWebview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_layout_terms_and_condition, null);
		mWebview = (WebView)rootView.findViewById(R.id.webViews);
		mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
          @Override
          public void onPageStarted(WebView view, String url, Bitmap favicon)
          {
              UI.showProgressDialog(getActivity());             
          }
            @Override
            public void onPageFinished(WebView view, String url) {
                //pd.dismiss();
                UI.hideProgressDialog();
                    String webUrl = mWebview.getUrl();
                    }
             });
		mWebview.loadUrl("http://bitemexico.com/terms-and-conditions/");
		return rootView;
	}
}
