package com.anynet.wifiworld.wifi.ui;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiAdmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;;

public class WifiAdvanceActivity extends Activity {
	private final static String TAG = WifiAdvanceActivity.class.getSimpleName();

	private TextView mWifiName;
	private WifiAdmin mWifiAdmin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.wifi_advance_activity);
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		final String security = intent.getStringExtra("security");
		
		mWifiAdmin = WifiAdmin.getInstance(this);
		mWifiName = (TextView)findViewById(R.id.wifi_advance_name);
		mWifiName.setText(WifiAdmin.convertToNonQuotedString(mWifiAdmin.getWifiNameConnection()));
		
		final ProgressBar progressBar = (ProgressBar)findViewById(R.id.encrypt_process);
		final ImageView imageView = (ImageView)findViewById(R.id.encrypt_result);
		final TextView textView = (TextView)findViewById(R.id.wifi_encrypt_text);
		progressBar.setVisibility(View.VISIBLE);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				textView.setText(textView.getText() + "(" + security + ")");
			}
		}, 1000);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		finishWithAnimation();
	}
	
	private void finishWithAnimation() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_left_out);
	}
}
