package com.junkcleaner.memorycleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.junkcleaner.memorycleaner.utill.CacheUtil;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.junkcleaner.memorycleaner.circleprogress.ArcProgress;

public class MainActivity extends AppCompatActivity implements
		OnClickListener, AnimationListener {

	private TextView btnJunkClean, btnMemBoost, btnBatteryBooster,storage_details,ram_details;
	private TextView btnAppManger;
	private ImageButton btnBoost;
	private static final String TAG = "MainActivity";
	private long cacheSize = 0;
	private Animation animation;
	ArcProgress arcstorage,arcram;
	static String app_package_name;
	private String msgMemoryInfo;
	private TextView tvAvailibleMem;
	private AdView mAdView;
	DecimalFormat df ;
	InterstitialAd mInterstitialAd;
	public LinearLayout junkcleaner,membooster,appmanager,batterysaver;
	public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		new AppRate().rate(this);
		mInterstitialAd = new InterstitialAd(this);
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

		app_package_name = getApplicationContext().getPackageName();
		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		animation = AnimationUtils.loadAnimation(this, R.anim.rotet_animation);
		membooster = (LinearLayout) findViewById(R.id.linearmembooster);
		junkcleaner = (LinearLayout) findViewById(R.id.linearjunkclean);
		appmanager = (LinearLayout) findViewById(R.id.linearappsmanager);
		batterysaver = (LinearLayout) findViewById(R.id.linearbatterysaver);
		tvAvailibleMem = (TextView) findViewById(R.id.tvAvailibleMemory);
		btnJunkClean = (TextView) findViewById(R.id.btnJunkClean);
		btnMemBoost = (TextView) findViewById(R.id.btnMemBoost);
		storage_details = (TextView) findViewById(R.id.storage_details);
		ram_details = (TextView) findViewById(R.id.ram_details);
		btnAppManger = (TextView) findViewById(R.id.btnAppManager);
		btnBoost = (ImageButton) findViewById(R.id.btnBoost);
		btnBatteryBooster = (TextView) findViewById(R.id.btnBatteryBooster);
		arcstorage = (ArcProgress) findViewById(R.id.arc_storage);
		arcram = (ArcProgress) findViewById(R.id.arc_ram);
		btnBatteryBooster.setOnClickListener(this);
		btnJunkClean.setOnClickListener(this);
		btnMemBoost.setOnClickListener(this);
		btnAppManger.setOnClickListener(this);
		btnBoost.setOnClickListener(this);
		membooster.setOnClickListener(this);
		junkcleaner.setOnClickListener(this);
		appmanager.setOnClickListener(this);
		batterysaver.setOnClickListener(this);
		arcstorage.setProgress(getPercentageStorage());

		df = new DecimalFormat("#.#");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				btnBoost.startAnimation(animation);
				animation.setAnimationListener(MainActivity.this);
			}
		}, 1000);

		scanStorageInfo();
		setRam();
		checkPermission();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		scanAvailableMemory();
	}


	private void checkPermission() {


		Log.d("TAG", "checkPermission:yes ");
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED) {

			Log.d("TAG", "checkPermission: permission not granted");
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.READ_PHONE_STATE)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				Log.d("TAG", "checkPermission: permission granted ");
				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{Manifest.permission.READ_PHONE_STATE},
						MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

				// MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
		else {
			Log.d("TAG", "checkPermission: granted");
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}



	public void scanStorageInfo() {
		long inttotalsize =0;
		long intavailablesize = 0;
		long exttotalsize = 0;
		long extavailablesize = 0;
		double totalsize = 0;

		double totalavailsize = 0;
		Map<String, File> externalLocations = MemoryStorage
				.getAllStorageLocations();
		File sdCard = externalLocations.get(MemoryStorage.SD_CARD);
		File externalSdCard = externalLocations
				.get(MemoryStorage.EXTERNAL_SD_CARD);
		if (sdCard != null) {
			Log.i("TAG", "Internal SD path: " + sdCard.getPath());
			StatFs stat = new StatFs(sdCard.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			// return totalBlocks * blockSize;
			long memsize = totalBlocks * blockSize / 1048576;
			long availblocks = stat.getAvailableBlocks();
			long availablesize = availblocks * blockSize / 1048576;
			Log.i("TAG", "Internal Sd size: " + memsize + "");
			inttotalsize = memsize;
			Log.i("TAG", "Internal Sd size available: " + availablesize + "");
			intavailablesize = availablesize;
			// txtInternalMemory.setText(memsize + " MB");
		}
		if (externalSdCard != null) {
			Log.i("TAG", "External SD path: " + externalSdCard.getPath());
			StatFs stat = new StatFs(externalSdCard.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			long extavailblocks = stat.getAvailableBlocks();
			// return totalBlocks * blockSize;
			long memsize = totalBlocks * blockSize / 1048576;
			extavailablesize = extavailblocks*blockSize / 1048576;
			Log.i("TAG", "External Sd size: " + memsize + "");
			exttotalsize = memsize;

			// txtExternalMemory.setText(memsize + " MB");
		}

		totalsize = (double)(inttotalsize+exttotalsize)/1024;
		totalavailsize = (double)(intavailablesize + extavailablesize)/1024;
		Log.d("TAG", "scanStorageInfo: total size"+totalsize);
		Log.d("TAG", "scanStorageInfo: avail size"+totalavailsize);

		arcstorage.setBottomText("STORAGE");
		storage_details.setText(df.format((totalsize-totalavailsize))+"/"+df.format(totalsize)+" GB");

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case R.id.action_rate_us:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id="
							+ MainActivity.this.getPackageName())));
			break;

		}

		return super.onOptionsItemSelected(item);
	}


	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static String getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return formatSize(availableBlocks * blockSize);
	}

	public static String getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return formatSize(totalBlocks * blockSize);
	}

	public static String getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return formatSize(availableBlocks * blockSize);
		} else {
			return "error";
		}
	}

	public static String getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return formatSize(totalBlocks * blockSize);
		} else {
			return "error";
		}
	}

	public int getPercentageStorage()
	{
		double percentage = 0;
		long ttlext = 0;
		long totalmemory = 0;
		long availablememoryext = 0;
		long availablememoryint = 0;
		long totalavailablememory = 0;
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			ttlext = totalBlocks * blockSize;
		} else {
			ttlext = 0;
		}
		long ttlint = 0;

		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		ttlint = totalBlocks * blockSize;

		totalmemory = ttlext + ttlint;
		long availableBlocks = stat.getAvailableBlocks();
		availablememoryint = availableBlocks * blockSize;


		if (externalMemoryAvailable()) {
			File path1 = Environment.getExternalStorageDirectory();
			StatFs stat1 = new StatFs(path1.getPath());
			long availableBlocks1 = stat1.getAvailableBlocks();
			availablememoryext = availableBlocks1*blockSize;
			totalavailablememory = totalavailablememory + availablememoryext;


		} else {
			totalavailablememory = availablememoryint;
		}

		Log.d("TAG", "getPercentageStorage: tam:"+totalavailablememory);
		Log.d("TAG", "getPercentageStorage: tm:"+totalmemory);

		percentage = (double)((double)(totalmemory-totalavailablememory)/(double) totalmemory)*100;

		Log.d("TAG", "getPercentageStorage: per"+Math.round(percentage));

		return (int)percentage;



	}

	public static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException
					(l + " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

	public static String formatSize(long size) {
		String suffix = null;

		if (size >= 1024) {
			suffix = "KB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null) resultBuffer.append(suffix);
		return resultBuffer.toString();
	}


	public void setRam()
	{
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager)this. getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;

		long asdas = mi.totalMem / 1048576L;
		long used = asdas - availableMegs;

		Log.d("TAG", "setRam: used"+used   +"    total "+asdas );
		double percentAvail = (double)(mi.totalMem - mi.availMem) / (double) mi.totalMem;
		Log.d("TAG", "setRam: "+percentAvail*100);
		arcram.setProgress((int)Math.round(percentAvail*100));
	/*	double totalsize = mi.totalMem/(1024*1048576L);
		double totalavailsize = mi.availMem/(1024*1048576L);
	*/	//Log.d("TAG", "setRam: total size:"+totalsize +"totalavailsize:"+totalavailsize);
		ram_details.setText(df.format((double) ((double)used/1024))+"/"+df.format((double)((double)asdas/1024))+" GB");

	}

	private String scanAvailableMemory() {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long totalMemory;
		if (Build.VERSION.SDK_INT > 15) {
			totalMemory = mi.totalMem / 1048576L;
		} else {
			totalMemory = getTotalMemory() / 1048576L;
		}
		long availableMegs = mi.availMem / 1048576L;
		tvAvailibleMem.setText("Available RAM: "
				+ String.valueOf(availableMegs) + " MB");



		String msg = "All unneccssary apps and sevices are closed and freed the max possible RAM. \n\nTotal Memory: "
				+ totalMemory
				+ " MB"
				+ "\n\nUsed Memory: "
				+ (totalMemory - availableMegs)
				+ " MB"
				+ "\n\nAvailable Memory: " + availableMegs + " MB";
		return msg;
	}

	public void clearRam() {
		System.gc();
		List<ApplicationInfo> packages;
		// get a list of installed apps.
		packages = getPackageManager().getInstalledApplications(0);

		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		for (ApplicationInfo packageInfo : packages) {
			if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
				continue;
			if (packageInfo.packageName.equals(getPackageName()))
				continue;
			mActivityManager.killBackgroundProcesses(packageInfo.packageName);
		}
		// scanAvailableMemory();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {

		case R.id.btnJunkClean:
			startActivity(new Intent(MainActivity.this, ActivityClearJunk.class));
			break;
		case R.id.btnMemBoost:
			startActivity(new Intent(MainActivity.this,
					RunningApplicationDetail.class));
			break;
		case R.id.btnBoost:
			btnBoost.startAnimation(animation);
			clearRam();
			msgMemoryInfo = scanAvailableMemory();
			break;
		case R.id.btnAppManager:
			intent = new Intent(this, AppManger.class);
			startActivity(intent);
			break;
		case R.id.btnBatteryBooster:
			startActivity(new Intent(MainActivity.this, SettingActivity.class));
			break;

//new cases
			case R.id.linearmembooster:
				startActivity(new Intent(MainActivity.this,
						RunningApplicationDetail.class));
				break;
			case R.id.linearjunkclean:
				startActivity(new Intent(MainActivity.this, ActivityClearJunk.class));
				break;
			case R.id.linearappsmanager:
				intent = new Intent(this, AppManger.class);
				startActivity(intent);
				break;
			case R.id.linearbatterysaver:
				startActivity(new Intent(MainActivity.this, SettingActivity.class));
				break;
		}

	}

	public long getTotalMemory() {

		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// meminfo
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			// total Memory
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
			localBufferedReader.close();
			return initial_memory;
		} catch (IOException e) {
			return -1;
		}
	}

	private void clearcache() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				cacheSize = 0;
				ClearCache.clearWhatsAppCache();
				try {
					new CacheUtil(MainActivity.this).clearAllCache();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void checkFileSystem(String path) {
		File[] currentFileList = new File(path).listFiles();
		if (currentFileList != null && currentFileList.length > 0) {
			for (int i = 0; i < currentFileList.length; i++) {

				if (currentFileList[i].isDirectory()) {

					Log.i(TAG, currentFileList[i].getAbsolutePath() + "----->"
							+ currentFileList[i].getName() + "  is directory");
					checkFileSystem(currentFileList[i].getAbsolutePath());
				} else {
					Log.i(TAG, currentFileList[i].getAbsolutePath() + "----->"
							+ currentFileList[i].getName() + "  is a file");
					cacheSize += currentFileList[i].length() / (1024);

				}
			}
		}
	}

	public static void clearCallLog(Context context) {
		Cursor managedCursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null, null);
		int count = 0;
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		if (managedCursor != null) {
			while (managedCursor.moveToNext()) {
				String phNumber = managedCursor.getString(number);
				String queryString = "NUMBER=" + phNumber;
				context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,
						queryString, null);
				count++;
			}
		}
		Log.i(TAG, "cursor length:" + count);

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(msgMemoryInfo)) {
			Intent dialogActivity = new Intent(MainActivity.this,
					DailogAcitvity.class);
			dialogActivity.putExtra("msg", msgMemoryInfo);
			startActivity(dialogActivity);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}
}
