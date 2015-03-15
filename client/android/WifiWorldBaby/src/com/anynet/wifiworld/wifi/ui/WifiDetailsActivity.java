package com.anynet.wifiworld.wifi.ui;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiInfoScanned;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiDetailsActivity extends Activity {

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_details_activity);
		
		Intent intent = getIntent();
		WifiInfoScanned wifiSelected = (WifiInfoScanned) intent.getSerializableExtra("WifiSelected");
		TextView detailsTitle = (TextView)findViewById(R.id.setting_main_title);
		detailsTitle.setText(wifiSelected.getWifiName());
		TextView strength = (TextView)findViewById(R.id.wifi_connect_strength_num);
		strength.setText(String.valueOf(wifiSelected.getWifiStrength()) + "%");
		
		ImageView backView = (ImageView)findViewById(R.id.iv_setting_header_left);
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
