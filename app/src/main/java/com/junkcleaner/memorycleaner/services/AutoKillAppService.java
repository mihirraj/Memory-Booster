package com.junkcleaner.memorycleaner.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.junkcleaner.memorycleaner.db.ExternalDbOpenHelper;
import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.model.UsedAppInfo;
import com.junkcleaner.memorycleaner.utill.Const;

public class AutoKillAppService extends IntentService {
	public AutoKillAppService() {
		super("MEM BOOST");
		// TODO Auto-generated constructor stub
	}

	ExternalDbOpenHelper dbOpenHelper;

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		dbOpenHelper = new ExternalDbOpenHelper(this, Const.DATABASE_NAME,
				Const.DATABASE_VERSION);

		if (!dbOpenHelper.database.isOpen())
			dbOpenHelper.openDataBase();

		ArrayList<AppInfo> list = dbOpenHelper.getAutoKillApps();
		ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		for (int i = 0; i < list.size(); i++) {
			AppInfo appInfo = list.get(i);
			am.killBackgroundProcesses(appInfo.getPkgName());
			if (Thread.interrupted())
				return;

		}

		String myPackageName = AutoKillAppService.this.getPackageName();
		ActivityManager activityManager = (ActivityManager) AutoKillAppService.this
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = activityManager.getRunningTasks(1).get(0).topActivity
				.getPackageName();
		if (myPackageName.equals(packageName)) {
			return;
		}

		int count = 0;
		boolean isUpdate = false;
		UsedAppInfo uAppInfo = dbOpenHelper.gettUsedAppInfo(packageName);
		if (uAppInfo != null) {
			count = uAppInfo.getCounter();
			if (count >= 10000) {
				count = 1000;
			}
			count += 1;
			isUpdate = true;
		} else {
			isUpdate = false;
			uAppInfo = new UsedAppInfo();
			count += 1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

		uAppInfo.setPackage_name(packageName);
		uAppInfo.setCounter(count);
		uAppInfo.setTime(sdf.format(Calendar.getInstance().getTime()));
		int c = dbOpenHelper.insertUsedAppInfo(uAppInfo, isUpdate);
		dbOpenHelper.close();
	}

}
