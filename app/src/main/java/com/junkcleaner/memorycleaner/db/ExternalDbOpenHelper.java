package com.junkcleaner.memorycleaner.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.model.UsedAppInfo;
import com.junkcleaner.memorycleaner.utill.Const;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

	// Path to the device folder with databases
	public static String DB_PATH;

	// Database file name
	public static String DB_NAME;
	public SQLiteDatabase database;
	public final Context context;

	public SQLiteDatabase getDb() {
		return database;
	}

	public ExternalDbOpenHelper(Context context, String databaseName,
			int databaseVersion) {
		super(context, databaseName, null, databaseVersion);
		this.context = context;
		// Write a full path to the databases of your application
		String packageName = context.getPackageName();
		DB_PATH = String.format("//data//data//%s//databases//", packageName);
		DB_NAME = databaseName;
		openDataBase();
	}

	// This piece of code will create a database if it's not yet created
	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {

			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				//Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		}
	}

	// Performing a database existence check
	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			//Log.e(this.getClass().toString(), "Error while checking db");
		}
		// Android doesn?t like resource leaks, everything should
		// be closed
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	// Method for copying the database
	private void copyDataBase() throws IOException {
		// Open a stream for reading from our ready-made database
		// The stream source is located in the assets
		InputStream externalDbStream = context.getAssets().open(DB_NAME);

		// Path to the created empty database on your Android device
		String outFileName = DB_PATH + DB_NAME;

		// Now create a stream for writing the database byte by byte
		OutputStream localDbStream = new FileOutputStream(outFileName);

		// Copying the database
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		// Don?t forget to close the streams
		localDbStream.close();
		externalDbStream.close();
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		if (database == null) {
			createDataBase();
		}
		database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		return database;
	}

	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void insertAutoKillApps(final ArrayList<AppInfo> listAutoKillApps) {
		for (int i = 0; i < listAutoKillApps.size(); i++) {
			if (listAutoKillApps.get(i).isChecked()) {
				AppInfo info = listAutoKillApps.get(i);
				ContentValues values = new ContentValues();
				values.put(Const.TblAutoKillColumn.APP_NAME, info.getAppName());
				values.put(Const.TblAutoKillColumn.PACKAGE_NAME,
						info.getPkgName());
				values.put(Const.TblAutoKillColumn.PROCESS_ID,
						info.getProcessId());
				int id = (int) database.insert(Const.TABLE_AUTO_KILL, null,
						values);

			}
		}
	}

	public int insertAutoKillApp(AppInfo info) {

		ContentValues values = new ContentValues();
		values.put(Const.TblAutoKillColumn.APP_NAME, info.getAppName());
		values.put(Const.TblAutoKillColumn.PACKAGE_NAME, info.getPkgName());
		int id = (int) database.insert(Const.TABLE_AUTO_KILL, null, values);
		return id;

	}

	public int insertAllAutoKillApp(ArrayList<AppInfo> list) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			AppInfo info = list.get(i);
			ContentValues values = new ContentValues();
			values.put(Const.TblAutoKillColumn.APP_NAME, info.getAppName());
			values.put(Const.TblAutoKillColumn.PACKAGE_NAME, info.getPkgName());
			int id = (int) database.insert(Const.TABLE_AUTO_KILL, null, values);
			if (id != -1) {
				count += 1;
			}
		}
		System.out.println("Total app kill inserted " + count);
		return count;

	}

	public boolean deleteAutoKillApp(AppInfo info) {

		int row = database.delete(Const.TABLE_AUTO_KILL,
				Const.TblAutoKillColumn.PACKAGE_NAME + " = ?",
				new String[] { info.getPkgName() });

		if (row != 0) {
			return true;
		}

		return false;

	}

	public int deleteAllAutoKillApp(ArrayList<AppInfo> list) {

		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			AppInfo info = list.get(i);
			int row = database.delete(Const.TABLE_AUTO_KILL,
					Const.TblAutoKillColumn.PACKAGE_NAME + " = ?",
					new String[] { info.getPkgName() });
			if (row != 0) {
				count += 1;
			}
		}
		System.out.println("Total app kill deleted " + count);
		return count;
	}

	public ArrayList<AppInfo> getAutoKillApps() {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		String selectQuery = "SELECT  * FROM " + Const.TABLE_AUTO_KILL;
		try {

			Cursor cursor = database.rawQuery(selectQuery, null);
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {

						AppInfo info = new AppInfo();
						info.setAppName(cursor.getString(0));
						info.setPkgName(cursor.getString(1));
						appList.add(info);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}

			return appList;
		} catch (Exception e) {
			return appList;
		}
	}

	public int insertUsedAppInfo(UsedAppInfo uAppInfo, boolean isUpdate) {
		int id = 0;
		if (isUpdate) {
			ContentValues values = new ContentValues();
			values.put(Const.TblAppUsedColumn.OPEN_COUNTER,
					uAppInfo.getCounter());
			values.put(Const.TblAppUsedColumn.OPEN_TIME, uAppInfo.getTime());
			String[] args = new String[] { uAppInfo.getPackage_name() };
			id = (int) database.update(Const.TABLE_APP_USED_DETAIL, values,
					Const.TblAppUsedColumn.PACKAGE_NAME + "=?", args);
		} else {
			ContentValues values = new ContentValues();
			values.put(Const.TblAppUsedColumn.PACKAGE_NAME,
					uAppInfo.getPackage_name());
			values.put(Const.TblAppUsedColumn.OPEN_COUNTER,
					uAppInfo.getCounter());
			values.put(Const.TblAppUsedColumn.OPEN_TIME, uAppInfo.getTime());
			id = (int) database.insert(Const.TABLE_APP_USED_DETAIL, null,
					values);
		}
		return id;
	}

	public UsedAppInfo gettUsedAppInfo(String packageName) {
		UsedAppInfo uAppInfo = null;
		String selectQuery = "SELECT  * FROM " + Const.TABLE_APP_USED_DETAIL
				+ " WHERE " + Const.TblAppUsedColumn.PACKAGE_NAME + " = "
				+ "\"" + packageName + "\"";

		try {

			Cursor cursor = database.rawQuery(selectQuery, null);
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					uAppInfo = new UsedAppInfo();
					uAppInfo.setPackage_name(cursor.getString(0));
					uAppInfo.setCounter(cursor.getInt(1));
					uAppInfo.setTime(cursor.getString(2));
				}
				cursor.close();
			}

			return uAppInfo;
		} catch (Exception e) {
			//Log.d("Error in getting app info from db", e.getMessage());
			return null;
		}

	}
}