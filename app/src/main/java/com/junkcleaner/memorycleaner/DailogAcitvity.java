package com.junkcleaner.memorycleaner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DailogAcitvity extends Activity {
	Button btnOk, btnOpenDirectory;
	private TextView tvMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);

		btnOk = (Button) findViewById(R.id.btnOK);
		tvMsg = (TextView) findViewById(R.id.tvMessage);
		btnOpenDirectory = (Button) findViewById(R.id.btnOpenDir);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DailogAcitvity.this.finish();

			}
		});
		btnOpenDirectory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openFolder();
				DailogAcitvity.this.finish();
			}
		});
		if (!TextUtils.isEmpty(getIntent().getStringExtra("msg"))) {
			btnOpenDirectory.setVisibility(View.GONE);
			tvMsg.setText(getIntent().getStringExtra("msg"));
		}
	}

	public void openFolder() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
				+ "/apk_backup/");
		intent.setDataAndType(uri, "text/csv");
		startActivity(Intent.createChooser(intent, "Open folder"));
	}
}
