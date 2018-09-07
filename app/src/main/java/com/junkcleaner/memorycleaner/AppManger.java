package com.junkcleaner.memorycleaner;

import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.junkcleaner.memorycleaner.adapter.AppMangerPagerAdapter;

import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

public class AppManger extends ActionBarBaseActivity {
	private AppMangerPagerAdapter pagerAdapter;
	private PagerTabStrip tabStrip;
	private ViewPager viewPager;

	InterstitialAd mInterstitialAd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.app_manger_activity);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
		tabStrip.setBackgroundResource(R.drawable.header_bg);
		pagerAdapter = new AppMangerPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		actionBar.setSubtitle("App Manager");
		viewPager
				.setCurrentItem(getIntent().getIntExtra("setPagerPoistion", 0));
		mInterstitialAd = new InterstitialAd(this);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// Actions to do after 10 seconds
				mInterstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_ads_id));
				AdRequest adRequestInter = new AdRequest.Builder().build();
				mInterstitialAd.setAdListener(new AdListener() {
					@Override
					public void onAdLoaded() {
						if (mInterstitialAd.isLoaded()) {
							Log.d("check", "onAdLoaded: ad loaded");
							mInterstitialAd.show();
						}
					}
				});
				mInterstitialAd.loadAd(adRequestInter);
			}
		}, 10000);



	}

}
