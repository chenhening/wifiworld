package com.anynet.wifiworld.wifi.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;

public class WifiTestActivity extends BaseActivity {
	private final static String TAG = WifiTestActivity.class.getSimpleName();
	
	private Context mContext;
	
	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText("测试网络");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_wifi_test);
		super.onCreate(savedInstanceState);
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
