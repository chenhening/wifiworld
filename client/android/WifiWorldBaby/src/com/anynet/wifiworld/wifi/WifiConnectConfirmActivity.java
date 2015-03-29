package com.anynet.wifiworld.wifi;

import com.anynet.wifiworld.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WifiConnectConfirmActivity extends Activity {
	private final static String TAG = WifiConnectConfirmActivity.class.getSimpleName();
	
	private Context ctx;
	
	private View mView;
	private TextView mSecurityView;
	private TextView mSignalView;
	private Button mConnectBtn;
	private Button mCancelBtn;
	private TextView mTitle;
	
	private WifiInfoScanned mWifiInfoScanned;
	private WifiAdmin mWifiAdmin;
	private int mNumOpenNetworksKept;

	private OnClickListener mConnectClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean connResult = false;
			WifiConfiguration cfgSelected = mWifiAdmin.getWifiConfiguration(mWifiInfoScanned);
			if (cfgSelected != null) {
				connResult = mWifiAdmin.connectToConfiguredNetwork(ctx,	mWifiAdmin.getWifiConfiguration(mWifiInfoScanned), true);
			} else {
				connResult = mWifiAdmin.connectToNewNetwork(ctx, mWifiInfoScanned, mNumOpenNetworksKept);
			}
			if (connResult) {
				setResult(RESULT_OK);
			} else {
				setResult(RESULT_CANCELED);
			}
			finish();
		}
		
	};
	
	private OnClickListener mCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		ctx = this;
		
		mView = View.inflate(this, R.layout.wifi_floating, null);
		final DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mView.setMinimumWidth(Math.min(dm.widthPixels, dm.heightPixels) - 20);
		setContentView(mView);
		
		mTitle = (TextView)mView.findViewById(R.id.wifi_title);
		mSecurityView = (TextView)mView.findViewById(R.id.security_text);
		mSignalView = (TextView)mView.findViewById(R.id.signal_strength_text);
		mConnectBtn = (Button)mView.findViewById(R.id.btn_connect);
		mCancelBtn = (Button)mView.findViewById(R.id.btn_cancel);
		mConnectBtn.setOnClickListener(mConnectClickListener);
		mCancelBtn.setOnClickListener(mCancelClickListener);
		
		mWifiAdmin = WifiAdmin.getInstance(this);
		mNumOpenNetworksKept =  Settings.Secure.getInt(this.getContentResolver(),
	            Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
		processIntent(getIntent());
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
	
	private void processIntent(Intent intent) {
		mWifiInfoScanned = (WifiInfoScanned)intent.getSerializableExtra("WifiSelected");
		
		if(mWifiInfoScanned == null) {
			Toast.makeText(this, "No data in Intent!", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		mTitle.setText("连接到" + " " + mWifiInfoScanned.getWifiName());
		mSecurityView.setText(mWifiInfoScanned.getEncryptType());
		mSignalView.setText(String.valueOf(mWifiInfoScanned.getWifiStrength()));
	}
	
}
