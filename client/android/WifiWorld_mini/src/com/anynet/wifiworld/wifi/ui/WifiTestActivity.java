package com.anynet.wifiworld.wifi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiSpeedTester;

public class WifiTestActivity extends BaseActivity {
	private final static String TAG = WifiTestActivity.class.getSimpleName();
	
	private Context mContext;
	private TextView mWifiName;
	private TextView mWifiDesc;
	
	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText("测试网络");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_wifi_test);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		setWifiConnected();
		
		WifiSpeedTester wifiSpeedTester = new WifiSpeedTester(this);
		Button testBtn = (Button) findViewById(R.id.start_button);
		testBtn.setOnClickListener(wifiSpeedTester);
	}
	
	private void setWifiConnected() {
		Intent intent = getIntent();
		String wifiName = intent.getStringExtra("WifiSpeedName");
		String wifiEncrypt = intent.getStringExtra("WifiSpeedEncrypt");
		
		mWifiName = (TextView) findViewById(R.id.tv_wifi_connected_name);
		mWifiDesc = (TextView) findViewById(R.id.tv_wifi_connected_desc);
		mWifiName.setText(wifiName);
		mWifiDesc.setText(wifiEncrypt);
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
