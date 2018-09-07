package com.junkcleaner.memorycleaner.model;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String appName, Version, pkgName, strSize;
	private float size;
	private boolean iskill;
	private int processId;
	private String dir;
	private boolean isChecked;
	private int counter;
	private String usedDate;

	public String getUsedDate() {
		return usedDate;
	}

	public void setUsedDate(String usedDate) {
		this.usedDate = usedDate;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public AppInfo() {

	}

	public AppInfo(AppInfo info) {
		this.appName = info.appName;
		this.pkgName = info.pkgName;
		this.size = info.size;
		this.iskill = info.iskill;
		this.dir = info.dir;
		this.Version = info.Version;
		this.drawable = info.drawable;
	}

	public boolean getIskill() {
		return iskill;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public void setIskill(boolean iskill) {
		this.iskill = iskill;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public String getSizeStr() {
		return strSize;
	}

	public void setSizeStr(String size) {
		this.strSize = size;
	}

	private Drawable drawable;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public void setSourceDir(String dir) {
		this.dir = dir;
	}

	public String getSourceDir() {
		return dir;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
