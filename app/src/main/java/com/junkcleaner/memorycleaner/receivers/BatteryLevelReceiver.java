package com.junkcleaner.memorycleaner.receivers;

import java.util.ArrayList;
import java.util.List;

import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.utill.PreferencesHandler;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.Toast;
import android.net.wifi.WifiManager;

public class BatteryLevelReceiver extends BroadcastReceiver {
	private PreferencesHandler preferencesHandler;
	private Context context;
	private Intent intent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.intent = intent;
		this.context = context;
		PreferencesHandler.newinstance(context);
		preferencesHandler = PreferencesHandler.getInstance();

		Toast.makeText(context, "Battery Low", Toast.LENGTH_SHORT).show();
		turnOffWifi();
		turnOffBlueTooth();
		decreaseBrightness();
		turnOffSync();
		killAllApps();

	}

	private void turnOffWifi() {
		if (preferencesHandler.isPrefTurnOffWifi()) {

			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}

		}
	}

	private void turnOffBlueTooth() {
		if (preferencesHandler.isPrefOffBlootooth()) {

			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			if (mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.disable();
			}

		}
	}

	private void decreaseBrightness() {
		if (preferencesHandler.isPrefDecBrightness()) {

			ContentResolver cResolver = context.getContentResolver();
			int brightness = 10;
			System.putInt(cResolver, System.SCREEN_BRIGHTNESS_MODE,
					System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			System.putInt(cResolver, System.SCREEN_BRIGHTNESS, brightness);

		}
	}

	private void killAllApps() {
		if (preferencesHandler.isPrefAutoKillApps()) {

			getRunningDetail();

		}
	}

	private void getRunningDetail() {
		final ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		final ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				PackageManager pm = context.getPackageManager();
				ActivityManager am1 = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> processes = am1
						.getRunningAppProcesses();
				if (processes != null && processes.size() > 0) {
					for (int k = 0; k < processes.size(); k++) {

						Drawable ico = null;
						try {
							RunningAppProcessInfo runInfo = processes.get(k);
							if (isReservedPackage(runInfo.processName)
									|| runInfo.processName.equals(context
											.getPackageName())) {
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
							list.add(info);

						} catch (NameNotFoundException e) {
							Log.e("", "No Running Process");
						}
					}

				}

				for (int i = 0; i < list.size(); i++) {
					AppInfo appInfo = list.get(i);

					Process.sendSignal(appInfo.getProcessId(),
							android.os.Process.SIGNAL_KILL);
					am.killBackgroundProcesses(appInfo.getPkgName());
				}

			}
		}).start();

	}

	private boolean isReservedPackage(String pkg) {
		java.util.ArrayList<String> reservedPackages = new java.util.ArrayList<String>();
		reservedPackages.add("com.android.launcher2");
		reservedPackages.add("com.android.launcher");
		reservedPackages.add("com.android.systemui");
		reservedPackages.add("com.android.inputmethod.latin");
		reservedPackages.add("com.android.phone");
		reservedPackages.add("com.android.wallpaper");
		reservedPackages.add("com.google.process.gapps");
		reservedPackages.add("android.process.acore");
		reservedPackages.add("android.process.media");
		reservedPackages.add("system");
		for (int i = 0; i < reservedPackages.size(); i++) {
			if (pkg.equals(reservedPackages.get(i)))
				return true;
		}
		return false;
	}

	private void turnOffSync() {
		if (preferencesHandler.isPrefAutoTurnOffSync()) {
			ContentResolver.setMasterSyncAutomatically(false);
		}
	}
}
