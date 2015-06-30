package com.anynet.wifiworld.wifi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.BitmapUtil;

public class WifiDetailsActivity extends BaseActivity {
	private final static String TAG = WifiDetailsActivity.class.getSimpleName();
	
	private Context mContext;
	private WifiProfile mWifi;
	
	//UI
	private ImageView mLogo;
	
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
		
		//init UI
		mLogo = (ImageView)findViewById(R.id.iv_detail_wifi_logo);
		mLogo.setImageBitmap(BitmapUtil.Bytes2Bimap(mWifi.Logo));
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
	
	//-----------------------------------------------------------------------------------------------------------------
    //custom functions
	
}
