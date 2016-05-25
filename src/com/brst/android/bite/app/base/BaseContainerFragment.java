package com.brst.android.bite.app.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.brst.android.bitemaxico.app.R;

public class BaseContainerFragment extends Fragment {

	Fragment fr;
	String fragmentPrevious;
	String fragmentCurrent;

	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		// fr = fragment;
		// fragmentPrevious = fragmentCurrent;
		// fragmentCurrent = fr.getClass().getSimpleName();
		fr = fragment;
		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.container_framelayout, fragment);
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
		getChildFragmentManager().executePendingTransactions();

	}

	public void replaceFragmentMap(Fragment fragment, boolean addToBackStack) {
		// fr = fragment;
		// fragmentPrevious = fragmentCurrent;
		// fragmentCurrent = fr.getClass().getSimpleName();
		fr = fragment;
		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.container_framelayout, fragment);
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
		// getChildFragmentManager().executePendingTransactions();

	}

	public void add(Fragment fragment, boolean addToBackStack) {
		// fr = fragment;
		// fragmentPrevious = fragmentCurrent;
		// fragmentCurrent = fr.getClass().getSimpleName();
		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();
		transaction.add(R.id.container_framelayout, fragment);
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
		getChildFragmentManager().executePendingTransactions();
	}

	public boolean popFragment() {

		Log.i("Base", "pop fragment: "
				+ getChildFragmentManager().getBackStackEntryCount());
		boolean isPop = false;
		if (getChildFragmentManager().getBackStackEntryCount() > 0) {
			isPop = true;
			getChildFragmentManager().popBackStack();

		}

		return isPop;
	}

	public Fragment currentFragment() {
		return fr;
	}

}