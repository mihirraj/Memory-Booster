package com.junkcleaner.memorycleaner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;

public class AppRate {
	private int times = 0;
	private int maxTimes = 7;
	private Context context;

	// RATE
	public void rate(Context context) {
		this.context = context;
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				"NEVERSHOW", false)) {
			return;
		}
		// Update new times;
		times = PreferenceManager.getDefaultSharedPreferences(context).getInt(
				"COUNT", 0);
		times++;
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt("COUNT", times).commit();
		if (times >= maxTimes) {
			if (PreferenceManager.getDefaultSharedPreferences(context)
					.getBoolean("CLOSE", false)) {
				if (times % 3 == 0) {
					showActivity();
				}
			} else {
				showActivity();
			}
		}
	}

	private void showActivity() {
		if (context != null)
			context.startActivity(new Intent(context, RateDialogAcitvity.class));
	}

	public static void showDialogRate(final Context mContext, String title,
			String mess) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(mess);
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Rate",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mContext.startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://details?id="+MainActivity.app_package_name)));
						PreferenceManager.getDefaultSharedPreferences(mContext)
								.edit().putBoolean("NEVERSHOW", true).commit();
						dialog.dismiss();
					}
				});
		builder.setNeutralButton("Later",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						PreferenceManager.getDefaultSharedPreferences(mContext)
								.edit().putInt("COUNT", 1).commit();
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("No, thanks",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						PreferenceManager.getDefaultSharedPreferences(mContext)
								.edit().putBoolean("CLOSE", true).commit();
						PreferenceManager.getDefaultSharedPreferences(mContext)
								.edit().putInt("COUNT", 0).commit();
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}

