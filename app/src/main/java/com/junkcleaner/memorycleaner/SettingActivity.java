package com.junkcleaner.memorycleaner;

import android.content.SharedPreferences;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.junkcleaner.memorycleaner.utill.PreferencesHandler;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	ActionBar actionBar;
	private AdView adView;
	InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.pref_setting_layout);
		getListView().setBackgroundResource(R.drawable.aquaf);
		getListView().setCacheColorHint(Color.parseColor("#00000000"));
//		initAddView();
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		settings.registerOnSharedPreferenceChangeListener(this);
		PreferencesHandler.newinstance(this);
		setAutoBrightness(PreferencesHandler.getInstance()
				.isPrefAutoBrightness());

		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
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
							Log.d("TAG", "onAdLoaded: ad loaded");
							mInterstitialAd.show();
						}
					}
				});
				mInterstitialAd.loadAd(adRequestInter);
			}
		}, 10000);

	}

	void setAutoBrightness(boolean isChecked) {
		if (isChecked) {
			Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
			refreshBrightness(-1);
		} else {
			int brightness = (int) getBrightnessLevel();
			android.provider.Settings.System.putInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS,
					brightness);
			refreshBrightness(brightness);
		}

		// After brightness change we need to "refresh" current app brightness

	}

	private void refreshBrightness(float brightness) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
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
		try {
			float value = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			return value;
		} catch (SettingNotFoundException e) {
			return 0;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equalsIgnoreCase("prefAutoBrightness")) {
			setAutoBrightness(sharedPreferences.getBoolean(
					PreferencesHandler.PREF_AUTO_BRIGHTNESS, false));
		}

	}
}
