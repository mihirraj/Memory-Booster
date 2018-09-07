package com.junkcleaner.memorycleaner.utill;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHandler {
	private static final String PREF_OFF_BLUETOOTH = "prefOffBlootooth";
	private static final String PREF_DEC_BORGHTNESS = "prefDecBrightness";
	private static final String PREF_TURN_OFF_WIFI = "prefTurnOffWifi";
	private static final String PREF_AUTO_KILL_APPS = "prefAutoKillApps";
	public static final String PREF_AUTO_BRIGHTNESS = "prefAutoBrightness";
	private static final String PREF_AUTO_TURN_OFF_SYNC = "prefAutoTurnOffSync";

	public static final String TAG = "Prefereces";
	private static PreferencesHandler instance;
	private static SharedPreferences sharedPrefs;
	private boolean prefOffBlootooth;
	private boolean prefDecBrightness;
	private boolean prefTurnOffWifi;
	private boolean prefAutoKillApps;
	private boolean prefAutoBrightness;
	private boolean prefAutoTurnOffSync;

	public boolean isPrefAutoKillApps() {
		return prefAutoKillApps;
	}

	public static PreferencesHandler getInstance() {
		return instance;
	}

	private void initalize(Context context) {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		initPref();
	}

	public static void newinstance(Context context) {
		instance = new PreferencesHandler();
		instance.initalize(context);
	}

	public boolean isPrefOffBlootooth() {
		return prefOffBlootooth;
	}

	public boolean isPrefDecBrightness() {
		return prefDecBrightness;
	}

	public boolean isPrefTurnOffWifi() {
		return prefTurnOffWifi;
	}

	public void onSharedPreferenceChanged(
			SharedPreferences paramSharedPreferences, String paramString) {

	}

	public boolean isPrefAutoBrightness() {
		return prefAutoBrightness;
	}

	public boolean isPrefAutoTurnOffSync() {
		return prefAutoTurnOffSync;
	}

	private void initPref() {
		this.prefOffBlootooth = sharedPrefs.getBoolean(PREF_OFF_BLUETOOTH,
				false);
		this.prefDecBrightness = sharedPrefs.getBoolean(PREF_DEC_BORGHTNESS,
				false);
		this.prefTurnOffWifi = sharedPrefs
				.getBoolean(PREF_TURN_OFF_WIFI, false);
		this.prefAutoKillApps = sharedPrefs.getBoolean(PREF_AUTO_KILL_APPS,
				false);
		this.prefAutoBrightness = sharedPrefs.getBoolean(PREF_AUTO_BRIGHTNESS,
				false);
		this.prefAutoTurnOffSync = sharedPrefs.getBoolean(
				PREF_AUTO_TURN_OFF_SYNC, false);

	}

}