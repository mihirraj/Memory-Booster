package com.junkcleaner.memorycleaner.utill;

public class Const {
	public static final String DATABASE_NAME = "mem_boost.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_AUTO_KILL = "tblautokill";
	public static final String TABLE_APP_USED_DETAIL = "tblappuseddetail";

	public static class TblAutoKillColumn {
		public static final String APP_NAME = "app_name";
		public static final String PACKAGE_NAME = "package_name";
		public static final String PROCESS_ID = "process_id";
		public static final String DIR = "dir";
		public static final String VERSION = "version";
	}

	public static class TblAppUsedColumn {
		public static final String PACKAGE_NAME = "package_name";
		public static final String OPEN_COUNTER = "open_counter";
		public static final String OPEN_TIME = "open_time";

	}

}
