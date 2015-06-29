package com.anynet.wifiworld.wifi.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;

public class WifiDetailsActivity extends Activity {
	private final static String TAG = WifiDetailsActivity.class.getSimpleName();
	
	private Context mContext;
	
	private void bingdingTitleUI() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_details);
		bingdingTitleUI();
		super.onCreate(savedInstanceState);
		mContext = this;
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
