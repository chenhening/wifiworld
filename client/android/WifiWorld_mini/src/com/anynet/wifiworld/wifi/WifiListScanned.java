package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.data.DBCallback.MultiDataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.util.NetHelper;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class WifiListScanned{
	private final static String TAG = WifiListScanned.class.getSimpleName();
	
	private static WifiListScanned mWifiListScanned = null;
	private Context mContext;
	private Handler mHandler;
	private List<WifiListItem> mWifiFree;
	private List<WifiListItem> mWifiEncrypt;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private boolean isRefreshThreadFinish;
	
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
		mWifiCurrent = WifiCurrent.getInstance(mContext);
		isRefreshThreadFinish = true;
	}
	
	public List<WifiListItem> getFreeList() {
		return mWifiFree;
	}
	
	public List<WifiListItem> getEncryptList() {
		return mWifiEncrypt;
	}
 	
	public void refresh() {
		if (mWifiAdmin.isWifiEnabled() && isRefreshThreadFinish) {
			isRefreshThreadFinish = false;
			WifiBRService.setWifiScannable(true);
		} else {
			Toast.makeText(mContext, "WiFi状态不可用，请稍后刷新", Toast.LENGTH_LONG).show();
		}
	}
	
	private class RefreshListThread implements Runnable{

		@Override
		public void run() {
			final List<ScanResult> scanResults = mWifiAdmin.scanWifi();
			List<String> macAddresses = new ArrayList<String>();
			for (ScanResult scanResult : scanResults) {
				macAddresses.add(scanResult.BSSID);
			}
			
			//final List<WifiProfile> wifiProfiles;
			WifiProfile wifiProfile = new WifiProfile();
			wifiProfile.BatchQueryByMacAddress(mContext, macAddresses, false, new MultiDataCallback<WifiProfile>() {
				
				@Override
				public boolean onSuccess(List<WifiProfile> objects) {
					Log.d(TAG, "Batch query by mac address success");
					reGroupWifiList(scanResults, objects);
					isRefreshThreadFinish = true;
					return false;
				}
				
				@Override
				public boolean onFailed(String msg) {
					Log.e(TAG, msg);
					isRefreshThreadFinish = true;
					return false;
				}
			});
			reGroupWifiList(scanResults, null);
		}
	}
	
	public void refreshWifiList() {
		Thread thread = new Thread(new RefreshListThread());
		thread.start();
	}
	
	public void reGroupWifiList(List<ScanResult> scanResults, List<WifiProfile> wifiProfiles) {
		mWifiFree.clear();
		mWifiEncrypt.clear();
		
		for (ScanResult scanResult : scanResults) {
			WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(scanResult, null);
			WifiProfile wifiProfile = getWifiProfile(scanResult.BSSID, wifiProfiles);
			wifiDistribution(scanResult, wifiCfg, wifiProfile);
		}
		
		//send update wifi list message to wifi connect
		Message msg = new Message();
		msg.what = GlobalHandler.UPDATE_WIFI_LIST;
		mHandler.sendMessageAtFrontOfQueue(msg);
	}
	
	private WifiProfile getWifiProfile(String macAddress, List<WifiProfile> wifiProfiles) {
		if (wifiProfiles != null) {
			for (int idx=0; idx<wifiProfiles.size(); ++idx) {
				if (wifiProfiles.get(idx).MacAddr.equals(macAddress)) {
					return wifiProfiles.get(idx);
				}
			}
		}
		
		return null;
	}
	
	private void wifiDistribution(ScanResult scanResult, WifiConfiguration wifiCfg, WifiProfile wifiProfile) {
		WifiDBInfo wifiDBInfo = new WifiDBInfo(wifiProfile);
		WifiListItem wifiItem = new WifiListItem(scanResult, wifiCfg, wifiDBInfo);
		boolean isOpenWifi = WifiAdmin.ConfigSec.isOpenNetwork(WifiAdmin.ConfigSec.getScanResultSecurity(scanResult));
		if (NetHelper.isWifiNet(mContext)
				&& mWifiCurrent.getWifiName().equals(WifiAdmin.convertToNonQuotedString(scanResult.SSID))) {
			Log.d(TAG, scanResult.SSID + " is the current connected wifi");
			mWifiCurrent.setWifiListItem(wifiItem);
		} else if (isOpenWifi || wifiCfg != null || wifiProfile != null) {
			mWifiFree.add(wifiItem);
		} else {
			mWifiEncrypt.add(wifiItem);
		}
	}
}
