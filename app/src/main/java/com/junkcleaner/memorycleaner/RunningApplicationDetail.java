package com.junkcleaner.memorycleaner;

import java.io.File;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.junkcleaner.memorycleaner.adapter.RunningAppAdapter;
import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.pinnedheader.PinnedHeaderListView;
import com.junkcleaner.memorycleaner.pinnedheader.SectionedBaseAdapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RunningApplicationDetail extends ActionBarBaseActivity implements
		OnItemClickListener, OnClickListener {
	private static final String TAG = "RunningApplicationDetail";
	private PinnedHeaderListView listView;
	private ArrayList<AppInfo> list;
	private ArrayList<AppInfo> listAllApps;
	private RunningAppAdapter adapter;
	private ActivityManager am;
	private float totalSize = 0;
	private TextView tvTotalSize, tvTotalApps;
	public static final String CLEARED_RAM = "clearedRam";
	private LinearLayout mainListLayout;
	public ImageView pBar;
	private AdView mAdView;
	InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.running_app);
		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);
		mInterstitialAd = new InterstitialAd(this);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// Actions to do after 10 seconds
				mInterstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_ads_id));
				AdRequest adRequestInter = new AdRequest.Builder().build();
				mInterstitialAd.setAdListener(new AdListener() {
					@Override
					public void onAdLoaded() {
						if (mInterstitialAd.isLoaded()) {
							Log.d("check", "onAdLoaded: ad loaded");
							mInterstitialAd.show();
						}
					}
				});
				mInterstitialAd.loadAd(adRequestInter);
			}
		}, 10000);

		df = new DecimalFormat("0.0");
		listView = (PinnedHeaderListView) findViewById(R.id.lv_running);
		pBar = (ImageView) findViewById(R.id.pBar);
		mainListLayout = (LinearLayout) findViewById(R.id.memBoostLayout);
		tvTotalApps = (TextView) findViewById(R.id.tv_total_apps);
		list = new ArrayList<AppInfo>();
		listAllApps = new ArrayList<AppInfo>();
		totalSize = 0;
		getRunningDetail();
		am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		adapter = new RunningAppAdapter(this, list, listAllApps);
		findViewById(R.id.btn_boost).setOnClickListener(this);
		tvTotalSize = (TextView) findViewById(R.id.tvTotalSize);
		listView.setOnItemClickListener(this);
		listView.setPinHeaders(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {

			@Override
			public void onSectionClick(SectionedBaseAdapter adapter, View view,
					int section, long id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemClick(SectionedBaseAdapter adapter, View view,
					int section, int position, long id) {
			}
		});

	}

	private void getRunningDetail() {
		mainListLayout.setVisibility(View.GONE);
		list.clear();
		listAllApps.clear();
		loadProgress(pBar);
		new Thread(new Runnable() {

			@Override
			public void run() {
				PackageManager pm = getPackageManager();
				ActivityManager am1 = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> processes = am1
						.getRunningAppProcesses();
				if (processes != null && processes.size() > 0) {
					for (int k = 0; k < processes.size(); k++) {
						Drawable ico = null;
						try {
							RunningAppProcessInfo runInfo = processes.get(k);
							if (isReservedPackage(runInfo.processName)
									|| runInfo.processName
											.equals(getPackageName())) {
								continue;
							}
							AppInfo info = new AppInfo();
							info.setProcessId(runInfo.pid);
							info.setChecked(true);
							String packageName = runInfo.processName.split(":")[0];
							info.setAppName(pm.getApplicationLabel(
									pm.getApplicationInfo(packageName, 0))
									.toString());
							info.setPkgName(packageName);
							int[] pid = new int[1];
							pid[0] = info.getProcessId();
							android.os.Debug.MemoryInfo[] memoryInfoArray = am1
									.getProcessMemoryInfo(pid);
							for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {

								float usageMemory = (pidMemoryInfo
										.getTotalPrivateDirty() / 1024);
								BigDecimal roundfinalPrice = new BigDecimal(
										usageMemory).setScale(1,
										BigDecimal.ROUND_HALF_UP);
								info.setSize(roundfinalPrice.floatValue());
								totalSize += usageMemory;

							}
							
							ico = pm.getApplicationIcon(runInfo.processName);
							info.setDrawable(ico);

							list.add(info);

						} catch (NameNotFoundException e) {
							Log.e(TAG, "No Running Process");
						}
					}
					try {
						getApplicationList(listAllApps);
						getAutoCloseApp();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(TAG, "LIST SIZE:" + list.size());

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							listView.setAdapter(adapter);
							tvTotalSize.setText(df.format(totalSize) + "MB");
							mainListLayout.setVisibility(View.VISIBLE);
							stopProgress(pBar);
						}
					});
				}
			}
		}).start();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_boost:
			boolean isOneChecked = false;
			;
			for (int i = 0; i < list.size(); i++) {
				AppInfo appInfo = list.get(i);
				Log.i(TAG, "onclick:" + appInfo.getIskill());
				if (appInfo.isChecked()) {
					isOneChecked = true;
					Process.sendSignal(appInfo.getProcessId(),
							android.os.Process.SIGNAL_KILL);
					am.killBackgroundProcesses(appInfo.getPkgName());
				}
			}
			if (isOneChecked) {
				list.clear();
				System.gc();
				Intent clearedRam = new Intent(RunningApplicationDetail.this,
						ActivityRamCleared.class);
				clearedRam.putExtra(CLEARED_RAM, totalSize);
				startActivity(clearedRam);
				finish();
			}
			break;

		default:
			break;
		}

	}

	public void updateSize() {
		totalSize = 0;
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).isChecked()) {
				continue;
			}
			totalSize += list.get(i).getSize();
		}
		tvTotalSize.setText(df.format(totalSize) + "MB");
	}

	public void getAutoCloseApp() {
		if (!dbHelper.database.isOpen())
			dbHelper.openDataBase();
		ArrayList<AppInfo> autoCloseAppList = dbHelper.getAutoKillApps();
		if (autoCloseAppList != null) {
			for (int i = 0; i < autoCloseAppList.size(); i++) {
				final AppInfo infoAuto = autoCloseAppList.get(i);
				for (int j = 0; j < listAllApps.size(); j++) {
					if (infoAuto.getPkgName().equals(
							listAllApps.get(j).getPkgName())) {
						listAllApps.get(j).setChecked(true);
					}
				}
			}
		}
	}

	protected void getApplicationList(final ArrayList<AppInfo> list)
			throws Exception {
		if (list == null) {
			throw new Exception("list can not be null", new Throwable(
					"please initilze arraylist"));
		}

		int flags = PackageManager.GET_META_DATA
				| PackageManager.GET_SHARED_LIBRARY_FILES
				| PackageManager.GET_UNINSTALLED_PACKAGES;
		PackageManager packageManager = getPackageManager();
		List<ApplicationInfo> applications = packageManager
				.getInstalledApplications(flags);
		for (int i = 0; i < applications.size(); i++) {

			ApplicationInfo info = applications.get(i);
			if (isReservedPackage(info.packageName)
					|| info.packageName.equals(getPackageName())) {
				continue;
			}

			AppInfo appInfo = new AppInfo();
			appInfo.setAppName(info.loadLabel(getPackageManager()).toString());

			appInfo.setPkgName(info.packageName);
			appInfo.setSourceDir(info.publicSourceDir);
			File file = new File(info.sourceDir);
			double size = file.length();
			BigDecimal roundfinalPrice = new BigDecimal(
					Math.floor((size / 1024) / 1024)).setScale(1,
					BigDecimal.ROUND_HALF_UP);
			appInfo.setSize(roundfinalPrice.floatValue());
			appInfo.setDrawable(info.loadIcon(getPackageManager()));
			list.add(appInfo);
		}
	}
}
