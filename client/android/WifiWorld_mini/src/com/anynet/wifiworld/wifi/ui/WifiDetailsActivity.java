package com.anynet.wifiworld.wifi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;

public class WifiDetailsActivity extends BaseActivity {
	private final static String TAG = WifiDetailsActivity.class.getSimpleName();
	
	private Context mContext;
	private WifiProfile mWifi;
	
	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText(mWifi.Ssid);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_wifi_details);
		super.onCreate(savedInstanceState);
		
		//获得序列化过来的数据
		Intent intent = getIntent();
		mWifi = (WifiProfile) intent.getSerializableExtra(WifiProfile.TAG);
		bingdingTitleUI();
	}

	protected Context getActivity() {
		return this;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onSop");
		super.onStop();
	}
}
