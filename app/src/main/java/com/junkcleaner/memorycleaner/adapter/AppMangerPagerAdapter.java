package com.junkcleaner.memorycleaner.adapter;

import com.junkcleaner.memorycleaner.fragments.SystemInfoFragment;
import com.junkcleaner.memorycleaner.fragments.UninstallAppFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AppMangerPagerAdapter extends FragmentPagerAdapter {

	private static final String TITLES[] = { "Uninstall", "System Info" };

	public AppMangerPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			return UninstallAppFragment.init(position);
		case 1:
			return SystemInfoFragment.init(position);
		default:
			return UninstallAppFragment.init(position);

		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

}
