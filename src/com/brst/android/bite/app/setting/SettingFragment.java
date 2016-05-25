package com.brst.android.bite.app.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.BaseContainerFragment;

public class SettingFragment extends Fragment {

	ListView list;
	DataAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.fragment_layout_setting, null);
		list = (ListView) rootView.findViewById(R.id.list_view);

		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.setting_header);
		adapter = new DataAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				userSettingPreference(position);

			}

		});

		return rootView;
	}

	protected void userSettingPreference(int position) {

		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new UserFragment();
			break;

		case 1:
			fragment = new SelectCityFragment();
			break;
		case 2:
			fragment = new ChangePlanFragment();
			break;
		case 3:
			fragment = new AboutUsFragment();
			break;
		case 4:
			fragment = new HowItWorkFragment();
			break;
		case 5:
			fragment = new UserGuideFragment();
			break;
		case 6:
			fragment = new TermAndConditionFragment();
			break;

		default:
			break;
		}

		((BaseContainerFragment) getParentFragment()).replaceFragment(fragment,
				true);

	}

	public class DataAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;

		public DataAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {

			return textData.length;
		}

		@Override
		public Object getItem(int position) {

			return textData[position];
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (inflater == null) {
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.layout_setting_list_row, null);
			}

			TextView txt = (TextView) convertView.findViewById(R.id.text_name);
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

			txt.setText(textData[position]);
			icon.setImageResource(imageResourc[position]);
			return convertView;
		}
	}

	String[] textData = { "Usuario", "Selecciona tu Ciudad", "Mi membresía actualmente",
			"Quienes Somos", "Como Funciona Bite Mexico", "Como Usar Mi Aplicación",
			"Terminos y Condiciones" };

	int[] imageResourc = { R.drawable.user, R.drawable.ic_city_setting,
			R.drawable.card, R.drawable.about, R.drawable.help,
			R.drawable.ic_how_to_use, R.drawable.terms };
}
