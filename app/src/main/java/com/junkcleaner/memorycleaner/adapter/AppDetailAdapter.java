package com.junkcleaner.memorycleaner.adapter;

import java.text.ParseException;

import com.junkcleaner.memorycleaner.R;
import com.junkcleaner.memorycleaner.model.AppInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDetailAdapter extends BaseAdapter implements
		OnCheckedChangeListener {
	private LayoutInflater inflater;
	private ArrayList<AppInfo> list;
	private String COMMONNLY_USED = "Commonly used";
	private String OCCASIONALLY_USED = "Occasionally used";
	private String RARELY_USED = "Rarely used";

	public AppDetailAdapter(Activity activity, ArrayList<AppInfo> list) {
		this.inflater = activity.getLayoutInflater();
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.running_app_item, null);
			holder.appname = (TextView) convertView
					.findViewById(R.id.txt_running_appname);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.iv_running_icon);
			holder.tvUsage = (TextView) convertView.findViewById(R.id.tv_usage);
			holder.cbKill = (CheckBox) convertView.findViewById(R.id.cb_kill);
			holder.cbKill.setOnCheckedChangeListener(this);

			holder.size = (TextView) convertView
					.findViewById(R.id.txt_running_app_size);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tvUsage.setVisibility(View.VISIBLE);
		AppInfo info = list.get(position);
		if (info.getCounter() == 0 || info.getUsedDate() == null) {
			holder.tvUsage.setText(RARELY_USED);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");

			String time = info.getUsedDate();
			try {
				Date appDate = sdf.parse(time);
				Date todayDate = sdf.parse(sdf.format(Calendar.getInstance()
						.getTime()));

				int diffInDays = (int) ((appDate.getTime() - todayDate
						.getTime()) / (1000 * 60 * 60 * 24));

				if (diffInDays <= 2) {
					holder.tvUsage.setText(COMMONNLY_USED);
				} else if (diffInDays >= 2 && diffInDays <= 5) {
					holder.tvUsage.setText(OCCASIONALLY_USED);
				} else {
					holder.tvUsage.setText(RARELY_USED);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		holder.appname.setText(info.getAppName());
		holder.cbKill.setTag(position);

		holder.cbKill.setChecked(list.get(position).isChecked());
		holder.size.setText(list.get(position).getSizeStr());
		holder.icon.setImageDrawable(info.getDrawable());
		return convertView;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		list.get((Integer) buttonView.getTag()).setChecked(isChecked);
	}
}

class Holder {
	TextView appname, version, pkgname, size, tvUsage;
	ImageView icon;
	CheckBox cbKill;
}
