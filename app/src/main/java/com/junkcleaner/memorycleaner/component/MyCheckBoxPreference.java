package com.junkcleaner.memorycleaner.component;

import android.content.Context;
import android.graphics.Color;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class MyCheckBoxPreference extends CheckBoxPreference {
	public MyCheckBoxPreference(Context context) {
		super(context);
	}

	public MyCheckBoxPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyCheckBoxPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		TextView tvSummary = (TextView) view.findViewById(android.R.id.summary);
		tvSummary.setTextColor(Color.rgb(219, 195, 90));
	}
}