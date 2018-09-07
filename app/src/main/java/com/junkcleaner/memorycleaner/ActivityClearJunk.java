package com.junkcleaner.memorycleaner;

import java.io.File;

import com.google.android.gms.ads.AdView;
import com.junkcleaner.memorycleaner.adapter.JunkListAdapter;
import com.junkcleaner.memorycleaner.model.JunkFile;
import com.junkcleaner.memorycleaner.model.JunkInfo;
import com.junkcleaner.memorycleaner.pinnedheader.PinnedHeaderListView;
import com.junkcleaner.memorycleaner.pinnedheader.SectionedBaseAdapter;
import com.junkcleaner.memorycleaner.utill.CacheUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityClearJunk extends ActionBarBaseActivity implements
		OnClickListener {

	private TextView tvFilesCount, tvScanning;
	private static final String TAG = "ClearJunk";
	private PinnedHeaderListView listView;
	private JunkFile junkfile;
	private boolean isObtainedSize;
	private ImageView pBar;
	private JunkListAdapter adapter;
	private float totalCacheFileSize;
	private float totalApkFileSize;
	private LinearLayout btnClear;
	private TextView tvTotalSize;
	private static int t=0;
	private InterstitialAd fullScreenAd2;
	private AdRequest fullScreenRequest2;
	private AdView mAdView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.junk_activity);
		listView = (PinnedHeaderListView) findViewById(R.id.listViewJunk);
		btnClear = (LinearLayout) findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(this);
		pBar = (ImageView) findViewById(R.id.pBar);
		tvTotalSize = (TextView) findViewById(R.id.tvTotalSize);
		tvTotalSize.setText("0.0 KB");
		listView.setPinHeaders(false);
		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		junkfile = new JunkFile();
		adapter = new JunkListAdapter(this, junkfile);
		listView.setAdapter(adapter);
		
		

		listView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {

			@Override
			public void onSectionClick(SectionedBaseAdapter adapter, View view,
					int section, long id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemClick(SectionedBaseAdapter adapter, View view,
					int section, int position, long id) {
				// TODO Auto-generated method stub
				Intent i = null;
				switch (section) {
				case 0:
					if (junkfile.getListCacheFiles().size() > 0) {
						i = new Intent(
								android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						i.addCategory(Intent.CATEGORY_DEFAULT);
						i.setData(Uri.parse("package:"
								+ junkfile.getListCacheFiles().get(position)
										.getAppPackage()));
					}

					break;
				case 1:

					break;
				case 2:
					if (junkfile.getListApkFiles().size() > 0) {
						i = new Intent(ActivityClearJunk.this, AppManger.class);
						i.putExtra("setPagerPoistion", 2);
					}
					break;

				}
				if (i != null) {
					startActivity(i);
				}

			}
		});
	}

	@Override
	protected void onResume() {

		super.onResume();
		loadData();
	}

	public static long clearWhatsAppCache() {
		long size = 0;
		String watsapppath = Environment.getExternalStorageDirectory()
				+ "/WhatsApp/Profile Pictures";
		File[] watsappfile = new File(watsapppath).listFiles();
		if (watsappfile != null && watsappfile.length > 0) {
			for (int i = 0; i < watsappfile.length; i++) {
				Log.i(TAG,
						"Watsapp profile picture:  " + watsappfile[i].getName());
				size += (watsappfile[i].getTotalSpace()) / (1024 * 1024);
				watsappfile[i].delete();
			}
		} else {
			Log.i(TAG, "WhatsApp is not there or no data available");
		}
		return size;
	}

	public final long clearAllJunkFile() {
		getAllJunkFilesNoExtension();
		return 0;
	}

	public ArrayList<File> getAllJunkFilesNoExtension() {
		final File file = Environment.getExternalStorageDirectory();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scanSdCardForNoExtension(file);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// tvFilesCount.setText(files.size() + "");
						// btnClearJunk.setEnabled(true);
					}
				});
			}
		}).start();

		// return files;
		return null;
	}

	private void scanSdCardForNoExtension(File file) {

		if (file != null) {

			if (file.isDirectory()) {
				File[] tempFiles = file.listFiles();
				if (tempFiles != null) {
					for (int i = 0; i < tempFiles.length; i++) {
						scanSdCardForNoExtension(tempFiles[i]);
					}
				}
			} else {
				try {
					String filename = file.getName();
					String filenameArray[] = filename.split("\\.");
					final String filePath = file.getPath();
					if (filenameArray.length == 1) {
						// files.add(file);
						JunkInfo info = new JunkInfo();
						info.setAppName(file.getName());
						info.setFilePath(file.getAbsolutePath());
						info.setDrawable(getResources().getDrawable(
								R.drawable.ic_launcher));
						long fileLength = (file.length() / 1024);
						if (fileLength >= 1024) {
							fileLength /= 1024;
							info.setAppJunkSize(fileLength + " MB");
						} else {
							info.setAppJunkSize(fileLength + " KB");
						}

						junkfile.getListResidualFiles().add(info);

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								// tvScanning.setText("Scanning: " + filePath);
							}
						});
						// Log.i(TAG,
						// "without Extension " + "Path:"
						// + file.getAbsolutePath());
					}
				} catch (IndexOutOfBoundsException e) {
					// files.add(file);
					Log.i(TAG, file.getName());
					e.printStackTrace();
				}

			}
		}

	}

	private void getAllCacheSize() {

		totalCacheFileSize = 0;

		PackageManager packageManager = getPackageManager();
		List<PackageInfo> packs = packageManager
				.getInstalledPackages(PackageManager.GET_META_DATA);

		for (int i = 0; i < packs.size(); i++) {

			final JunkInfo info = new JunkInfo();

			PackageInfo p = packs.get(i);

			info.setAppPackage(p.packageName);
			info.setChecked(false);
			try {
				info.setAppName(packageManager.getApplicationLabel(
						packageManager.getApplicationInfo(p.packageName, 0))
						.toString());
				info.setDrawable(packageManager
						.getApplicationIcon(p.packageName));
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			isObtainedSize = false;

			Method getPackageSizeInfo;
			try {
				getPackageSizeInfo = packageManager.getClass().getMethod(
						"getPackageSizeInfo", String.class,
						IPackageStatsObserver.class);

				getPackageSizeInfo.invoke(packageManager, p.packageName,
						new IPackageStatsObserver.Stub() {

							@Override
							public void onGetStatsCompleted(
									PackageStats pStats, boolean succeeded)
									throws RemoteException {
								// TODO Auto-generated method stub

								long cacheSize = pStats.cacheSize / 1024;
								info.setAppJunkSize(String.valueOf(cacheSize)
										+ " KB");
								if (cacheSize >= 1024) {
									cacheSize /= 1024;
									info.setAppJunkSize(String
											.valueOf(cacheSize) + " MB");
								}
								if (cacheSize >= 15) {
									totalCacheFileSize += (pStats.cacheSize / 1024);
									junkfile.getListCacheFiles().add(info);
								}
								isObtainedSize = true;

							}
						}

				);
			} catch (Exception e) {
				e.printStackTrace();
			}
			while (!isObtainedSize) {
				// waiting...
			}

		}

		JunkFile.setTotalCacheSize(totalCacheFileSize);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			pBar.setVisibility(View.GONE);
			stopProgress(pBar);
			btnClear.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
			updateSize();

		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_clear:
			if (adapter.isClearAllCacheFiles) {
				try {
					new CacheUtil(this).clearAllCache();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (adapter.isClearAllApk) {
				for (int i = 0; i < junkfile.getListApkFiles().size(); i++) {
					File file = new File(junkfile.getListApkFiles().get(i)
							.getFilePath());
					file.delete();
				}

			}
			if (adapter.isClearAllCacheFiles || adapter.isClearAllApk) {
				
				int random = ((int)(Math.random()*9)) + 1;
				
				if(t==0 && random>=5 ){
					fullScreenAd2 = new InterstitialAd(this);
					fullScreenAd2.setAdUnitId(getResources().getString(
							R.string.Interstitial_ads_id));
					fullScreenRequest2 = new AdRequest.Builder().build();
					fullScreenAd2.loadAd(fullScreenRequest2);
					fullScreenAd2.setAdListener(new AdListener() {
						@Override
						public void onAdLoaded() {
							t=1;
							fullScreenAd2.show();
						}	
						
					});
				}
				
				loadData();
				
			}

			break;
		}

	}

	public void updateSize() {
		float totalSize = 0;
		if (adapter.isClearAllCacheFiles) {
			totalSize += totalCacheFileSize;
		}
		if (adapter.isClearAllApk) {
			totalSize += totalApkFileSize;
		}
		if (totalSize >= 1024) {
			totalSize /= 1024;
			tvTotalSize.setText(df.format(totalSize) + " MB");
		} else {
			tvTotalSize.setText(df.format(totalSize) + " KB");
		}

	}

	private void loadData() {

		listView.setVisibility(View.GONE);
		pBar.setVisibility(View.VISIBLE);
		btnClear.setVisibility(View.GONE);
		loadProgress(pBar);
		final File file = Environment.getExternalStorageDirectory();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (adapter.isClearAllCacheFiles) {

					junkfile.getListCacheFiles().clear();
					getAllCacheSize();

				}
				if (adapter.isClearAllApk) {

					junkfile.getListApkFiles().clear();
					totalApkFileSize = 0;
					scanForApkFile(file);
					JunkFile.setTotalApkSize(totalApkFileSize);
				}
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	private void scanForApkFile(final File file) {

		if (file != null) {

			if (file.isDirectory()) {
				File[] tempFiles = file.listFiles();
				if (tempFiles != null) {
					for (int i = 0; i < tempFiles.length; i++) {
						scanForApkFile(tempFiles[i]);
					}
				}
			} else {
				try {
					String filename = file.getName();
					String filenameArray[] = filename.split("\\.");
					String extension = filenameArray[filenameArray.length - 1]
							.trim();
					if (extension.equals("apk")) {
						JunkInfo info = new JunkInfo();
						info.setAppName(file.getName());
						info.setFilePath(file.getAbsolutePath());
						long fileLength = (file.length() / 1024);
						totalApkFileSize += fileLength;
						if (fileLength >= 1024) {
							fileLength /= 1024;
							info.setAppJunkSize(fileLength + " MB");
						} else {
							info.setAppJunkSize(fileLength + " KB");
						}
						PackageInfo pi = getPackageManager()
								.getPackageArchiveInfo(file.getAbsolutePath(),
										0);
						if (pi != null) {
							pi.applicationInfo.sourceDir = file
									.getAbsolutePath();
							pi.applicationInfo.publicSourceDir = file
									.getAbsolutePath();
							Drawable apkIcon = pi.applicationInfo
									.loadIcon(getPackageManager());
							info.setDrawable(apkIcon);

							junkfile.getListApkFiles().add(info);
						}

					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}

			}

		}

	}

}
