package com.junkcleaner.memorycleaner;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class ClearCache {
	private static final String TAG = "ClearCache";
	private static ArrayList<File> files;

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

	public static final long clearAllJunkFile() {
		getAllJunkFilesNoExtension();
		return 0;
	}

	public static ArrayList<File> getAllJunkFilesNoExtension() {
		files = new ArrayList<File>();
		File file = Environment.getExternalStorageDirectory();
		scanSdCardForNoExtension(file);
		return files;
	}

	private static void scanSdCardForNoExtension(File file) {

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

					if (filenameArray.length == 1) {
						files.add(file);
						Log.i(TAG,
								"without Extension " + "Path:"
										+ file.getAbsolutePath());
					}
				} catch (IndexOutOfBoundsException e) {
					files.add(file);
					Log.i(TAG, file.getName());
					e.printStackTrace();
				}

			}
		}
	}
}
