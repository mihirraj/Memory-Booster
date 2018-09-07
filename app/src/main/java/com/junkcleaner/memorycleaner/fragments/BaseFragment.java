package com.junkcleaner.memorycleaner.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.junkcleaner.memorycleaner.AppManger;
import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.model.UsedAppInfo;

abstract public class BaseFragment extends Fragment implements OnClickListener {

	protected AppManger activity;
	private PackageManager packageManager;
	protected ProgressBar pBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (AppManger) getActivity();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

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
		packageManager = activity.getPackageManager();
		List<ApplicationInfo> applications = packageManager
				.getInstalledApplications(flags);
		for (int i = 0; i < applications.size(); i++) {

			ApplicationInfo info = applications.get(i);
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
				if (activity.isReservedPackage(info.packageName)
						|| info.packageName.equals(activity.getPackageName())) {
					continue;
				}

				AppInfo appInfo = new AppInfo();
				appInfo.setAppName(info.loadLabel(activity.getPackageManager())
						.toString());

				appInfo.setPkgName(info.packageName);

				appInfo.setSourceDir(info.publicSourceDir);
				if (!activity.dbHelper.database.isOpen()) {
					activity.dbHelper.openDataBase();
				}
				UsedAppInfo usedAppInfo = activity.dbHelper
						.gettUsedAppInfo(info.packageName);
				if (usedAppInfo != null) {
					appInfo.setCounter(usedAppInfo.getCounter());
					appInfo.setUsedDate(usedAppInfo.getTime());

				}
				File file = new File(info.sourceDir);
				double size = file.length();
				appInfo.setSizeStr(Math.floor((size / 1024) / 1024) + "");
				appInfo.setDrawable(info.loadIcon(activity.getPackageManager()));
				list.add(appInfo);
			}

		}
	}

	protected void showMsgDialog(String msg) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		((TextView) dialog.show().findViewById(android.R.id.message))
				.setGravity(Gravity.CENTER_HORIZONTAL);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
