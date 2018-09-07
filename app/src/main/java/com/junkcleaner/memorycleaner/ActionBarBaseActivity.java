package com.junkcleaner.memorycleaner;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junkcleaner.memorycleaner.db.ExternalDbOpenHelper;
import com.junkcleaner.memorycleaner.services.AutoKillAppService;
import com.junkcleaner.memorycleaner.utill.Const;
import com.junkcleaner.memorycleaner.utill.PreferencesHandler;

abstract public class ActionBarBaseActivity extends ActionBarActivity {

	protected ActionBar actionBar;
	protected TextView title;
	protected final String MY_PACKAGE = "com.memoryboost";
	public DecimalFormat df;
	public ExternalDbOpenHelper dbHelper;
	private static int SCREEN_TIMEOUT = 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setScreenTimeOut();
		actionBar = getSupportActionBar();
		df = new DecimalFormat("0.0");
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.title_text, null);
		title = (TextView) v.findViewById(R.id.tv_title);
		title.setText(R.string.app_name);
		actionBar.setCustomView(v);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.header_bg));
		dbHelper = new ExternalDbOpenHelper(this, Const.DATABASE_NAME,
				Const.DATABASE_VERSION);
		startBackgroundService();
		dbHelper.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PreferencesHandler.newinstance(this);
		setAutoBrightness(PreferencesHandler.getInstance()
				.isPrefAutoBrightness());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			onBackPressed();
			break;

		}
		return true;
	}

	public void loadProgress(ImageView iv) {
		((LinearLayout) iv.getParent()).setVisibility(View.VISIBLE);
		((LinearLayout) iv.getParent())
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});
		iv.setVisibility(View.VISIBLE);
		final AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				anim.start();
			}
		});

	}

	public void stopProgress(ImageView iv) {
		((LinearLayout) iv.getParent()).setVisibility(View.GONE);
		AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
		anim.start();
		iv.setVisibility(View.GONE);
	}

	public boolean isReservedPackage(String pkg) {
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

	public void startBackgroundService() {
		Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.SECOND, 10);
		Intent intent = new Intent(this, AutoKillAppService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				3 * 1000, pintent);
	}

	private void setScreenTimeOut() {
		android.provider.Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, SCREEN_TIMEOUT);
	}

	void setAutoBrightness(boolean isChecked) {
		if (isChecked) {
			Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

			Toast.makeText(this, "automatic", Toast.LENGTH_SHORT).show();
		} else {

			int brightness = (int) getBrightnessLevel();
			android.provider.Settings.System.putInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS,
					brightness);
			refreshBrightness(brightness);

		}

	}

	private void refreshBrightness(float brightness) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();

		System.out.println();
		if (brightness < 0) {
			lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
		} else {
			if (brightness < 0.1) {
				brightness = 0.1f;
			}
			lp.screenBrightness = brightness;
		}
		getWindow().setAttributes(lp);
	}

	float getBrightnessLevel() {
		// convert brightness level to range 0..1

		float bright = 0;
		try {
			bright = Settings.System.getInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bright;
	}
}
