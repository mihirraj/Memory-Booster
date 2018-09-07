package com.junkcleaner.memorycleaner.model;

import android.graphics.drawable.Drawable;

public class JunkInfo {
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	private String appName;
	private String appPackage;
	private String appJunkSize;
	private Drawable drawable;
	private boolean isChecked;

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private String filePath;

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	public String getAppJunkSize() {
		return appJunkSize;
	}

	public void setAppJunkSize(String appJunkSize) {
		this.appJunkSize = appJunkSize;
	}
}
