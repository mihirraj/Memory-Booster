package com.junkcleaner.memorycleaner.utill;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;

public class CacheUtil {
	private CachePackageDataObserver mClearCacheObserver;
	private static final long CACHE_APP = Long.MAX_VALUE;
	private Context context;

	public CacheUtil(Context context) {
		this.context = context;
	}

	public void clearAllCache() throws Throwable {
		PackageManager localPackageManager = context.getApplicationContext()
				.getPackageManager();
		cleanAllCache(localPackageManager, Long.valueOf(CACHE_APP));
		cleanAllCache(localPackageManager, 4611686018427387903L);
		cleanAllCache(localPackageManager, 1722499072L);
		cleanAllCache(localPackageManager, 76926976L);
		cleanAllCache(localPackageManager, 2147483647L);
		cleanAllCache(localPackageManager, -20L);
		cleanAllCache(localPackageManager, -200L);
		cleanAllCache(localPackageManager, 51200L);

	}

	private void cleanAllCache(PackageManager packManger, long cacheClearValue)
			throws Throwable {

		if (mClearCacheObserver == null) {
			mClearCacheObserver = new CachePackageDataObserver();
		}
		@SuppressWarnings("rawtypes")
		final Class[] classes = { Long.TYPE, IPackageDataObserver.class };
		Method localMethod = PackageManager.class.getDeclaredMethod(
				"freeStorageAndNotify", classes);
		System.out.println("paccccccc " + packManger);
		localMethod.invoke(packManger, cacheClearValue, mClearCacheObserver);
		try {
			Thread.sleep(2L);
			return;
		} catch (Throwable localThrowable) {
		}

	}

	private void cleanAllAppCache(PackageManager packManger) throws Throwable {

		if (mClearCacheObserver == null) {
			mClearCacheObserver = new CachePackageDataObserver();
		}
		@SuppressWarnings("rawtypes")
		final Class[] classes = { String.class, IPackageDataObserver.class };
		Method localMethod = PackageManager.class.getDeclaredMethod(
				"deleteApplicationCacheFiles", classes);
		System.out.println("paccccccc App Cache delete " + packManger);
		localMethod.invoke(packManger, "com.memoryboost", mClearCacheObserver);
		try {
			Thread.sleep(2L);
			return;
		} catch (Throwable localThrowable) {
		}

	}

	private class CachePackageDataObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(String packageName, boolean succeeded) {
			System.out.println("Package name =" + packageName + "stauts ="
					+ succeeded);
		}
	}

}
