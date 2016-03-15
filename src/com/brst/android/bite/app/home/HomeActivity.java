package com.brst.android.bite.app.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.ActivityFragmentContainer;
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.base.NearMeFragmentContainer;
import com.brst.android.bite.app.base.SearchFragmentContainer;
import com.brst.android.bite.app.base.SettingFragmentContainer;

public class HomeActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;
	private static final String TAB_ACTIVITY = "Restaurantes";
	private static final String TAB_NEAR_ME = "Cerca De Mi";
	private static final String TAB_SEARCH = "Buscar";
	private static final String TAB_SETTING = "Ajustes";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent);

		mTabHost.addTab(
				setIndicator(mTabHost.newTabSpec(TAB_ACTIVITY),
						R.drawable.ic_home_, R.drawable.tab_item_selector),
				ActivityFragmentContainer.class, null);
		mTabHost.addTab(
				setIndicator(mTabHost.newTabSpec(TAB_NEAR_ME),
						R.drawable.ic_near_me, R.drawable.tab_item_selector),
				NearMeFragmentContainer.class, null);
		mTabHost.addTab(
				setIndicator(mTabHost.newTabSpec(TAB_SEARCH),
						R.drawable.ic_search, R.drawable.tab_item_selector),
				SearchFragmentContainer.class, null);

		mTabHost.addTab(
				setIndicator(mTabHost.newTabSpec(TAB_SETTING),
						R.drawable.ic_settings, R.drawable.tab_item_selector),
				SettingFragmentContainer.class, null);
	}

	public TabSpec setIndicator(TabSpec spec, int resid, int tabId) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
		v.setBackgroundResource(tabId);
		ImageView imageButton = (ImageView) v.findViewById(R.id.image_tabs);
		imageButton.setImageResource(resid);
		TextView text = (TextView) v.findViewById(R.id.text_tabs);
		// text.setTextColor(R.drawable.text_color_picker);
		text.setText(spec.getTag());

		return spec.setIndicator(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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

	@Override
	public void onBackPressed() {
		boolean isPopFragment = false;
		String currentTabTag = mTabHost.getCurrentTabTag();

		if (currentTabTag.equals(TAB_ACTIVITY)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(TAB_ACTIVITY)).popFragment();
		} else if (currentTabTag.equals(TAB_NEAR_ME)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(TAB_NEAR_ME)).popFragment();
		} else if (currentTabTag.equals(TAB_SEARCH)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(TAB_SEARCH)).popFragment();
		} else if (currentTabTag.equals(TAB_SETTING)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(TAB_SETTING)).popFragment();
		}

		if (!isPopFragment) {

			if (mTabHost.getCurrentTab() == 0) {
				finish();
			} else {
				mTabHost.setCurrentTab(0);
			}
		}
	}

	public void onBackButtonClick(View v) {
		this.onBackPressed();
		hideKeyboard();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String currentTabTag = mTabHost.getCurrentTabTag();
		((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(
				currentTabTag)).onActivityResult(requestCode, resultCode, data);

	}

	private void hideKeyboard() {
		// TODO Auto-generated method stub
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
