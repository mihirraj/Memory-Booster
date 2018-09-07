package com.junkcleaner.memorycleaner.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.junkcleaner.memorycleaner.R;
import com.junkcleaner.memorycleaner.RunningApplicationDetail;
import com.junkcleaner.memorycleaner.model.AppInfo;
import com.junkcleaner.memorycleaner.pinnedheader.SectionedBaseAdapter;

public class RunningAppAdapter extends SectionedBaseAdapter implements
		OnCheckedChangeListener {
	private LayoutInflater inflater;
	private ArrayList<AppInfo> list;
	private ArrayList<AppInfo> listAllApps;
	private RunningApplicationDetail activity;
	private boolean isAllCheckedClicked;
	private boolean isAllCheckedStateChangedFromAdapter = false;
	private boolean isCheckedStateChangeFromAdapter = false;

	public RunningAppAdapter(Activity activity, ArrayList<AppInfo> list,
			ArrayList<AppInfo> listAllApps) {
		this.activity = (RunningApplicationDetail) activity;
		this.inflater = activity.getLayoutInflater();
		this.list = list;
		this.listAllApps = listAllApps;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		TagModel tgm = (TagModel) buttonView.getTag();
		switch (tgm.section) {
		case 0:
			switch (buttonView.getId()) {
			case R.id.cb_kill:
				list.get(tgm.position).setChecked(isChecked);
				activity.updateSize();
				break;
			}

			break;
		case 1:
			switch (buttonView.getId()) {
			case R.id.cb_select_all:
				isAllCheckedClicked = true;
				if (!isAllCheckedStateChangedFromAdapter) {
					if (!activity.dbHelper.database.isOpen())
						activity.dbHelper.openDataBase();
					if (isChecked) {
						for (int i = 0; i < listAllApps.size(); i++) {
							listAllApps.get(i).setChecked(true);

						}
						activity.loadProgress(activity.pBar);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								activity.dbHelper
										.insertAllAutoKillApp(listAllApps);
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										activity.dbHelper.close();
										activity.stopProgress(activity.pBar);
										notifyDataSetChanged();
									}
								});
							}
						}).start();

					} else {
						for (int i = 0; i < listAllApps.size(); i++) {
							listAllApps.get(i).setChecked(false);
						}
						activity.loadProgress(activity.pBar);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								activity.dbHelper
										.deleteAllAutoKillApp(listAllApps);
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										activity.dbHelper.close();
										activity.stopProgress(activity.pBar);
										notifyDataSetChanged();
									}
								});
							}
						}).start();

					}

				}
				isAllCheckedClicked = false;
				break;
			case R.id.cb_kill:
				if (!isAllCheckedClicked && !isCheckedStateChangeFromAdapter) {
					listAllApps.get(tgm.position).setChecked(isChecked);
					if (!activity.dbHelper.database.isOpen())
						activity.dbHelper.openDataBase();
					if (listAllApps.get(tgm.position).isChecked()) {
						activity.dbHelper.insertAutoKillApp(listAllApps
								.get(tgm.position));
					} else {
						activity.dbHelper.deleteAutoKillApp(listAllApps
								.get(tgm.position));
					}
					activity.dbHelper.close();
					notifyDataSetChanged();

				}
				break;

			}
			break;

		}

	}

	@Override
	public Object getItem(int section, int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getCountForSection(int section) {
		// TODO Auto-generated method stub
		switch (section) {
		case 0:
			return list.size();
		case 1:
			return listAllApps.size();
		}
		return 0;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		TagModel tagModel = new TagModel();
		RunningHolder holder;
		if (convertView == null) {
			holder = new RunningHolder();
			convertView = inflater.inflate(R.layout.running_app_item, parent,
					false);
			holder.appname = (TextView) convertView
					.findViewById(R.id.txt_running_appname);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.iv_running_icon);
			holder.cbKill = (CheckBox) convertView.findViewById(R.id.cb_kill);

			holder.cbKill.setOnCheckedChangeListener(this);

			holder.tvAppSize = (TextView) convertView
					.findViewById(R.id.txt_running_app_size);
			convertView.setTag(holder);

		} else {
			holder = (RunningHolder) convertView.getTag();
		}
		tagModel.position = position;
		tagModel.section = section;
		float size = 0;
		switch (section) {
		case 0:
			if (list.size() > position) {
				final AppInfo info = list.get(position);
				holder.appname.setText(info.getAppName());
				holder.icon.setImageDrawable(info.getDrawable());
				size = info.getSize();
				if (size < 1.0f) {
					holder.tvAppSize.setText(size + "KB");
				} else {
					holder.tvAppSize.setText(size + "MB");
				}

				holder.tvAppSize.setText(info.getSize() + "MB");
				holder.cbKill.setVisibility(View.VISIBLE);
				holder.cbKill.setTag(tagModel);
				holder.cbKill.setChecked(list.get(position).isChecked());
			}
			break;
		case 1:
			if (listAllApps.size() > position) {
				final AppInfo infoAuto = listAllApps.get(position);
				holder.appname.setText(infoAuto.getAppName());
				holder.icon.setImageDrawable(infoAuto.getDrawable());
				size = infoAuto.getSize();
				if (size < 1.0f) {
					size *= 1024;
					holder.tvAppSize.setText(size + "KB");
				} else {
					holder.tvAppSize.setText(size + "MB");
				}
				holder.cbKill.setTag(tagModel);
				isCheckedStateChangeFromAdapter = true;
				holder.cbKill.setChecked(listAllApps.get(position).isChecked());
				isCheckedStateChangeFromAdapter = false;
			}
			break;

		}

		return convertView;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {

		// TODO Auto-generated method stub
		SectionHolder sectionHolder;
		TagModel tagModel = new TagModel();
		if (convertView == null) {
			sectionHolder = new SectionHolder();
			convertView = inflater.inflate(R.layout.running_app_section_header,
					parent, false);
			sectionHolder.tvTotalApps = (TextView) convertView
					.findViewById(R.id.tv_total_apps);
			sectionHolder.tvHeaderName = (TextView) convertView
					.findViewById(R.id.textView1);
			sectionHolder.cbSelectAll = (CheckBox) convertView
					.findViewById(R.id.cb_select_all);
			sectionHolder.cbSelectAll.setOnCheckedChangeListener(this);
			convertView.setTag(sectionHolder);
		} else {
			sectionHolder = (SectionHolder) convertView.getTag();
		}
		tagModel.section = section;
		switch (section) {
		case 0:
			sectionHolder.tvTotalApps.setText(list.size() + "");
			sectionHolder.tvHeaderName.setText("Runnings Apps");
			sectionHolder.cbSelectAll.setVisibility(View.GONE);
			sectionHolder.cbSelectAll.setTag(tagModel);
			break;
		case 1:
			sectionHolder.tvTotalApps.setText(listAllApps.size() + "");
			sectionHolder.tvHeaderName.setText("Auto Close Apps");
			sectionHolder.cbSelectAll.setVisibility(View.VISIBLE);
			sectionHolder.cbSelectAll.setTag(tagModel);
			isAllCheckedStateChangedFromAdapter = true;
			if (isAllChecked())
				sectionHolder.cbSelectAll.setChecked(true);
			else
				sectionHolder.cbSelectAll.setChecked(false);

			isAllCheckedStateChangedFromAdapter = false;

			break;

		}
		return convertView;
	}

	class RunningHolder {
		TextView appname, tvAppSize;
		ImageView icon;
		CheckBox cbKill;
	}

	class SectionHolder {
		TextView tvTotalApps, tvHeaderName;
		CheckBox cbSelectAll;

	}

	class TagModel {
		public int position;
		public int section;
	}

	private boolean isAllChecked() {
		boolean isAllChecked = true;
		for (int i = 0; i < listAllApps.size(); i++) {
			if (!listAllApps.get(i).isChecked()) {
				isAllChecked = false;
				break;
			}
		}
		return isAllChecked;
	}
}
