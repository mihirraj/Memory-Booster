package com.junkcleaner.memorycleaner.model;

import java.util.ArrayList;

public class JunkFile {

	private ArrayList<JunkInfo> listCacheFiles;
	private ArrayList<JunkInfo> listApkFiles;
	private ArrayList<JunkInfo> listResidualFiles;
	private static float totalCacheSize;
	private static float totalResidualSize;
	private static float totalApkSize;

	public static float getTotalCacheSize() {
		return totalCacheSize;
	}

	public static void setTotalCacheSize(float totalCacheSize) {
		JunkFile.totalCacheSize = totalCacheSize;
	}

	public static float getTotalResidualSize() {
		return totalResidualSize;
	}

	public static void setTotalResidualSize(float totalResidualSize) {
		JunkFile.totalResidualSize = totalResidualSize;
	}

	public static float getTotalApkSize() {
		return totalApkSize;
	}

	public static void setTotalApkSize(float totalApkSize) {
		JunkFile.totalApkSize = totalApkSize;
	}

	public JunkFile() {
		listCacheFiles = new ArrayList<JunkInfo>();
		listApkFiles = new ArrayList<JunkInfo>();
		listResidualFiles = new ArrayList<JunkInfo>();
		totalApkSize = 0;
		totalCacheSize = 0;
		totalResidualSize = 0;
	}

	public ArrayList<JunkInfo> getListCacheFiles() {
		return listCacheFiles;
	}

	public void setListCacheFiles(ArrayList<JunkInfo> listCacheFiles) {
		this.listCacheFiles = listCacheFiles;
	}

	public ArrayList<JunkInfo> getListApkFiles() {
		return listApkFiles;
	}

	public void setListApkFiles(ArrayList<JunkInfo> listApkFiles) {
		this.listApkFiles = listApkFiles;
	}

	public ArrayList<JunkInfo> getListResidualFiles() {
		return listResidualFiles;
	}

	public void setListResidualFiles(ArrayList<JunkInfo> listResisualFiles) {
		this.listResidualFiles = listResisualFiles;
	}

}
