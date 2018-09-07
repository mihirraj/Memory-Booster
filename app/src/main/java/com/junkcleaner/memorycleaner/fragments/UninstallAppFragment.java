package com.junkcleaner.memorycleaner.fragments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.junkcleaner.memorycleaner.R;
import com.junkcleaner.memorycleaner.adapter.AppDetailAdapter;
import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.model.AppSize;

public class UninstallAppFragment extends BaseFragment implements
		OnItemClickListener {

	private ListView listView;
	private ArrayList<AppInfo> list;
	private AppDetailAdapter adapter;
	private ImageView pBar;
	private RelativeLayout relUninstallLayout;
	private Button btnUnistall;
	private ArrayList<AppSize> listAppSize;
	private boolean isSizeOperationCompleted;
	private Thread getListThread;
	private AdView mAdView;

	public static UninstallAppFragment init(int val) {
		UninstallAppFragment truitonFrag = new UninstallAppFragment();
		// Supply val input as an argument.
		Bundle args = new Bundle();
		args.putInt("val", val);
		truitonFrag.setArguments(args);
		return truitonFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		registerUnistallReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater
				.inflate(R.layout.uninstall_app_list, container, false);
		listView = (ListView) v.findViewById(R.id.listViewUninstallApp);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(this);
		pBar = (ImageView) v.findViewById(R.id.pBar);
		mAdView = (AdView) v.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		relUninstallLayout = (RelativeLayout) v
				.findViewById(R.id.uninstallListLayout);
		btnUnistall = (Button) v.findViewById(R.id.btnUninstall);
		btnUnistall.setOnClickListener(this);

		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		list = new ArrayList<AppInfo>();
		activity.df = new DecimalFormat("0.0");
		listAppSize = new ArrayList<AppSize>();
		adapter = new AppDetailAdapter(activity, list);
		getList();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnUninstall:
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isChecked()) {
					Uri packageURI = Uri.parse("package:"
							+ list.get(i).getPkgName());
					Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
							packageURI);
					startActivity(uninstallIntent);
				}
			}

			break;

		}
	}

	private void getList() {
		list.clear();
		activity.loadProgress(pBar);
		relUninstallLayout.setVisibility(View.GONE);
		getListThread = new Thread(runnableList);
		getListThread.start();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		list.get(arg2).setChecked(!list.get(arg2).isChecked());
		adapter.notifyDataSetChanged();

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				getList();

			}

		}
	};

	private void registerUnistallReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(Intent.ACTION_DELETE);
		intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
		intentFilter.addDataScheme("package");
		activity.registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		activity.unregisterReceiver(receiver);

		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

		super.onPause();

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		if (getListThread != null) {
			getListThread.interrupt();
		}
		super.onDetach();
	}

	private void getAppSize(String packageName) {

		try {
			PackageManager pm = activity.getPackageManager();
			Method getPackageSizeInfo;
			getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo",
					String.class, IPackageStatsObserver.class);
			getPackageSizeInfo.invoke(pm, packageName,
					new IPackageStatsObserver.Stub() {

						@Override
						public void onGetStatsCompleted(PackageStats pStats,
								boolean succeeded) throws RemoteException {
							// TODO Auto-generated method stub
							long totalSizeKb;
							float totalSizeMb;
							AppSize appSize = new AppSize();
							totalSizeKb = (pStats.cacheSize + pStats.codeSize + pStats.dataSize) / 1024;
							if (totalSizeKb >= 1024) {
								totalSizeMb = totalSizeKb / 1024;
								appSize.totalSize = activity.df
										.format(totalSizeMb) + "MB";
							} else {

								appSize.totalSize = (totalSizeKb) + "KB";
							}
							appSize.pacakageName = pStats.packageName;
							listAppSize.add(appSize);
							if (list.size() == listAppSize.size()) {
								isSizeOperationCompleted = true;
							}
						}
					});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Runnable runnableList = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				getApplicationList(list);
				listAppSize.clear();
				isSizeOperationCompleted = false;
				for (int i = 0; i < list.size(); i++) {
					getAppSize(list.get(i).getPkgName());
					if (Thread.interrupted())
						return;
				}
				while (!isSizeOperationCompleted) {
					if (Thread.interrupted()) {
						System.out.println("intruptted");
						return;

					}
					// waiting
				}
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setSizeStr(listAppSize.get(i).totalSize);
					if (Thread.interrupted())
						return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					activity.stopProgress(pBar);
					relUninstallLayout.setVisibility(View.VISIBLE);
					listView.setAdapter(adapter);

				}
			});
		}
	};

}
