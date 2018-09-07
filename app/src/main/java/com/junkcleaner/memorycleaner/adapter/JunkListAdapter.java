package com.junkcleaner.memorycleaner.adapter;

import java.text.DecimalFormat;

import com.junkcleaner.memorycleaner.ActivityClearJunk;
import com.junkcleaner.memorycleaner.R;
import com.junkcleaner.memorycleaner.model.JunkFile;
import com.junkcleaner.memorycleaner.pinnedheader.SectionedBaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JunkListAdapter extends SectionedBaseAdapter implements
		OnCheckedChangeListener {

	private JunkFile junkFiles;
	private ViewHolder holder;
	private ViewHolderSection sectionHolder;;
	private LayoutInflater inflater;
	public boolean isClearAllCacheFiles;
	public boolean isClearAllApk;
	private ActivityClearJunk activity;

	public JunkListAdapter(ActivityClearJunk activity, JunkFile junkFiles) {
		this.junkFiles = junkFiles;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.activity = activity;
		isClearAllCacheFiles = true;
		isClearAllApk = true;

		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getItem(int section, int position) {
		// TODO Auto-generated method stub
		switch (section) {
		case 0:
			return junkFiles.getListCacheFiles().get(position);

		case 1:
			return junkFiles.getListApkFiles().get(position);

		}
		return junkFiles.getListCacheFiles().get(position);

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
			if (junkFiles.getListCacheFiles().size() > 0) {
				return junkFiles.getListCacheFiles().size();
			}
			break;
		case 1:

			if (junkFiles.getListApkFiles().size() > 0) {
				return junkFiles.getListApkFiles().size();
			}
			break;

		}
		return 1;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.running_app_item, parent,
					false);
			holder = new ViewHolder();
			holder.cb = (CheckBox) convertView.findViewById(R.id.cb_kill);
			holder.cb.setVisibility(View.INVISIBLE);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.txt_running_appname);
			holder.tvSize = (TextView) convertView
					.findViewById(R.id.txt_running_app_size);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.iv_running_icon);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		System.out.println(" cache called" + section);
		switch (section) {
		case 0:

			if (junkFiles.getListCacheFiles().size() > 0) {
				holder.ivIcon.setImageDrawable(junkFiles.getListCacheFiles()
						.get(position).getDrawable());
				holder.tvName.setText(junkFiles.getListCacheFiles()
						.get(position).getAppName());
				holder.tvSize.setText(junkFiles.getListCacheFiles()
						.get(position).getAppJunkSize());
				holder.tvSize.setVisibility(View.VISIBLE);
				holder.ivIcon.setVisibility(View.VISIBLE);

			} else {
				holder.tvSize.setVisibility(View.GONE);
				holder.ivIcon.setVisibility(View.INVISIBLE);
				holder.tvName.setText("No Cache Files Found!");
			}
			break;
		case 1:
			if (junkFiles.getListApkFiles().size() > 0) {
				holder.ivIcon.setImageDrawable(junkFiles.getListApkFiles()
						.get(position).getDrawable());
				holder.tvName.setText(junkFiles.getListApkFiles().get(position)
						.getAppName());
				holder.tvSize.setText(junkFiles.getListApkFiles().get(position)
						.getAppJunkSize());
				holder.cb.setChecked(junkFiles.getListApkFiles().get(position)
						.isChecked());
				holder.tvSize.setVisibility(View.VISIBLE);
				holder.ivIcon.setVisibility(View.VISIBLE);
			} else {
				holder.tvSize.setVisibility(View.GONE);
				holder.ivIcon.setVisibility(View.INVISIBLE);
				holder.tvName.setText("No Obsolate Apks Found!");
			}

			break;

		}

		return convertView;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub if (convertView == null) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.section_header, parent,
					false);
			sectionHolder = new ViewHolderSection();
			sectionHolder.tv = (TextView) convertView
					.findViewById(R.id.tvSectionHeader);
			sectionHolder.tvTotalSize = (TextView) convertView
					.findViewById(R.id.tvTotalSize);
			sectionHolder.headerLayout = (RelativeLayout) convertView
					.findViewById(R.id.headerLayout);
			sectionHolder.cb = (CheckBox) convertView
					.findViewById(R.id.cb_clear_all);
			sectionHolder.cb.setOnCheckedChangeListener(this);
			convertView.setTag(sectionHolder);
		} else {
			sectionHolder = (ViewHolderSection) convertView.getTag();
		}
		sectionHolder.cb.setTag(section);
		float totalSize;
		DecimalFormat df = new DecimalFormat("0.0");
		DecimalFormat dfkb = new DecimalFormat("0");
		switch (section) {
		case 0:
			sectionHolder.headerLayout
					.setBackgroundResource(R.drawable.blue_tab_header_bg);
			sectionHolder.tv.setText("Cache Files");
			// isClearAllCacheFiles = true;
			sectionHolder.cb.setEnabled(true);
			if (JunkFile.getTotalCacheSize() == 0) {
				isClearAllCacheFiles = false;
				sectionHolder.cb.setEnabled(false);
			}
			sectionHolder.cb.setChecked(isClearAllCacheFiles);

			if (JunkFile.getTotalCacheSize() >= 1024) {
				totalSize = JunkFile.getTotalCacheSize() / 1024;
				sectionHolder.tvTotalSize.setText(df.format(totalSize) + " MB");
			} else {
				totalSize = JunkFile.getTotalCacheSize();
				sectionHolder.tvTotalSize.setText(dfkb.format(totalSize)
						+ " KB");
			}

			break;
		case 1:
			sectionHolder.headerLayout
					.setBackgroundResource(R.drawable.blue_tab_header_bg);
			sectionHolder.tv.setText("Obsolate APK");
			sectionHolder.cb.setEnabled(true);
			if (JunkFile.getTotalApkSize() == 0) {
				isClearAllApk = false;
				sectionHolder.cb.setEnabled(false);
			}
			sectionHolder.cb.setChecked(isClearAllApk);

			if (JunkFile.getTotalApkSize() >= 1024) {
				totalSize = JunkFile.getTotalApkSize() / 1024;
				sectionHolder.tvTotalSize.setText(df.format(totalSize) + " MB");
			} else {
				totalSize = JunkFile.getTotalApkSize();
				sectionHolder.tvTotalSize.setText(dfkb.format(totalSize)
						+ " KB");
			}
			break;

		}

		return convertView;
	}

	private class ViewHolderSection {
		TextView tv, tvTotalSize;
		CheckBox cb;
		RelativeLayout headerLayout;
	}

	private class ViewHolder {
		TextView tvName, tvSize;
		ImageView ivIcon;
		CheckBox cb;

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch ((Integer) buttonView.getTag()) {
		case 0:
			isClearAllCacheFiles = isChecked;
			break;
		case 1:
			isClearAllApk = isChecked;
			break;

		}
		activity.updateSize();
	}

}
