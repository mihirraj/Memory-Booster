package com.junkcleaner.memorycleaner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class ActivityRamCleared extends ActionBarBaseActivity {
	private TextView tvRamCleared;
	private static boolean ad_shown = false;
	private InterstitialAd fullScreenAd;
	private AdRequest fullScreenRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ram_cleared_layout);
		tvRamCleared = (TextView) findViewById(R.id.tvRamCleared);
		tvRamCleared.setText(df.format(getIntent().getFloatExtra(
				RunningApplicationDetail.CLEARED_RAM, 0.0f))
				+ " MB");
		findViewById(R.id.btnDone).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ActivityRamCleared.this.finish();
					}
				});
		
		if(!ad_shown){
			fullScreenAd = new InterstitialAd(this);
			fullScreenAd.setAdUnitId(getResources().getString(
					R.string.Interstitial_ads_id));
			fullScreenRequest = new AdRequest.Builder().build();
			fullScreenAd.loadAd(fullScreenRequest);
			fullScreenAd.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					ad_shown = true;
					fullScreenAd.show();
				}
				
			});
		}
	}
}
