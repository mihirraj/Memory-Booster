package com.junkcleaner.memorycleaner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RateDialogAcitvity extends Activity implements OnClickListener {
	Button btnNoThanks, btnRate, btnShare, btnLater;
	private TextView tvMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_dialog_layout);

		btnLater = (Button) findViewById(R.id.btnLater);
		btnNoThanks = (Button) findViewById(R.id.btnNoThanks);
		btnRate = (Button) findViewById(R.id.btnRate);
		btnShare = (Button) findViewById(R.id.btnShare);

		btnLater.setOnClickListener(this);
		btnNoThanks.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnRate.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btnLater:
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putInt("COUNT", 1).commit();

			break;
		case R.id.btnRate:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + getPackageName())));
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putBoolean("NEVERSHOW", true).commit();
			break;
		case R.id.btnShare:
			Intent pC = new Intent(Intent.ACTION_SEND);
			pC.setType("text/plain");
			pC.putExtra(
					Intent.EXTRA_TEXT,
					getBaseContext().getResources()
							.getString(R.string.share_text));
			startActivity(Intent.createChooser(pC,
					"SHARE RAM Booster & Cleaner"));
			break;
		case R.id.btnNoThanks:
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putBoolean("CLOSE", true).commit();
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putInt("COUNT", 0).commit();

			break;

		}
		this.finish();

	}
}
