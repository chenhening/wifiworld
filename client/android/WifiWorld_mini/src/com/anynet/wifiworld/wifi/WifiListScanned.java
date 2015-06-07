package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class WifiListScanned{
	private final static String TAG = WifiListScanned.class.getSimpleName();
	
	private static WifiListScanned mWifiListScanned = null;
	private Context mContext;
	private Handler mHandler;
	private List<WifiListItem> mWifiFree;
	private List<WifiListItem> mWifiEncrypt;
	private WifiAdmin mWifiAdmin;
	
	public static WifiListScanned getInstance(Context context, Handler handler) {
		if (mWifiListScanned == null) {
			mWifiListScanned = new WifiListScanned(context, handler);
		}
		return mWifiListScanned;
	}
	
	private WifiListScanned(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mWifiFree = new ArrayList<WifiListItem>();
		mWifiEncrypt = new ArrayList<WifiListItem>();
		mWifiAdmin = WifiAdmin.getInstance(mContext);
	}
	
	public void refresh() {
		if (mWifiAdmin.isWifiEnabled()) {
			Thread thread = new Thread(new RefreshListThread());
			thread.start();
		} else {
			Toast.makeText(mContext, "WiFi状态不可用，请稍后刷新", Toast.LENGTH_LONG).show();
		}
	}
	
	private class RefreshListThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
