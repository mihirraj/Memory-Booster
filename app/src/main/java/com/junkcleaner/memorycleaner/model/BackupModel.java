package com.junkcleaner.memorycleaner.model;

import android.graphics.drawable.Drawable;

public class BackupModel implements Comparable<BackupModel> {
	private String name, type, packageName, headername, header;

	Drawable drawable;
	public static String HEADER = "header";
	public static String DATA = "data";
	private boolean isInstalled;
	private boolean isChecked;
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	public String getHeadername() {
		return headername;
	}

	public void setHeadername(String headername) {
		this.headername = headername;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable aPKicon) {
		this.drawable = aPKicon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public int compareTo(BackupModel another) {
		return this.isInstalled == another.isInstalled ? 0 : -1;
	}
}