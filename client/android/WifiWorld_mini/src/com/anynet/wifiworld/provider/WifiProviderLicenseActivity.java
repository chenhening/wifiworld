package com.anynet.wifiworld.provider;

import android.content.Intent;
import android.os.Bundle;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;

public class WifiProviderLicenseActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setContentView(R.layout.activity_provider_license);
		super.onCreate(savedInstanceState);
		
		mTitlebar.tvTitle.setText("网络宝服务协议");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
