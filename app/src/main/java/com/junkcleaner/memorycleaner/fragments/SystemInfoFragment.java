package com.junkcleaner.memorycleaner.fragments;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.junkcleaner.memorycleaner.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SystemInfoFragment extends BaseFragment {
	private TextView txtBatterylevel;
	private ImageView pBar;
	private LinearLayout sytemInfoLayout;
	private AdView mAdView;

	public static SystemInfoFragment init(int val) {
		SystemInfoFragment truitonFrag = new SystemInfoFragment();
		// Supply val input as an argument.
		Bundle args = new Bundle();
		args.putInt("val", val);
		truitonFrag.setArguments(args);
		return truitonFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.system_info, container, false);
		sytemInfoLayout = (LinearLayout) v.findViewById(R.id.systemInfoLayout);
		sytemInfoLayout.setVisibility(View.INVISIBLE);
		pBar = (ImageView) v.findViewById(R.id.pBar);
		activity.loadProgress(pBar);
		mAdView = (AdView) v.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		((TextView) v.findViewById(R.id.txt_system_manufacturer))
				.setText(Build.MANUFACTURER);
		((TextView) v.findViewById(R.id.txt_system_model)).setText(Build.MODEL);
		((TextView) v.findViewById(R.id.txt_system_hardware))
				.setText(Build.HARDWARE);
		((TextView) v.findViewById(R.id.txt_system_cpu)).setText(Build.CPU_ABI);
		((TextView) v.findViewById(R.id.txt_system_board))
				.setText(Build.DISPLAY);
		((TextView) v.findViewById(R.id.txt_system_cpuusage))
				.setText((readCpuUsage() * 100) + " %");
		txtBatterylevel = (TextView) v.findViewById(R.id.txt_system_battery);
		activity.registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onActivityCreated(savedInstanceState);
	}

	private float readCpuUsage() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		activity.unregisterReceiver(mBatInfoReceiver);
		super.onDestroy();

	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			activity.stopProgress(pBar);
			txtBatterylevel.setText(String.valueOf(level) + "%");
			sytemInfoLayout.setVisibility(View.VISIBLE);

		}
	};

}
