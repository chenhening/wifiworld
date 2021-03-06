package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiWhite;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.wifi.WifiListItem.WifiType;

public class WifiListScanned{
	private final static String TAG = WifiListScanned.class.getSimpleName();
	
	private static WifiListScanned mWifiListScanned = null;
	private Context mContext;
	private Handler mHandler;
	private List<WifiListItem> mWifiAuth;
	private List<WifiListItem> mWifiNotAuth;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private boolean isRefreshThreadFinish;
	private List<WifiProfile> mWifiProfiles;
	
	private List<String> mTestWifis = new ArrayList<String>();
	
	public static WifiListScanned getInstance(Context context, Handler handler) {
		if (mWifiListScanned == null) {
			mWifiListScanned = new WifiListScanned(context, handler);
		}
		return mWifiListScanned;
	}
	
	private WifiListScanned(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mWifiAuth = new ArrayList<WifiListItem>();
		mWifiNotAuth = new ArrayList<WifiListItem>();
		mWifiAdmin = WifiAdmin.getInstance(mContext);
		mWifiCurrent = WifiCurrent.getInstance(mContext);
		isRefreshThreadFinish = true;
	}
	
	public List<WifiListItem> getAuthList() {
		return mWifiAuth;
	}
	
	public List<WifiListItem> getNotAuthList() {
		return mWifiNotAuth;
	}
 	
	public boolean refresh() {
		if (isRefreshThreadFinish) { //同一时间只允许一次搜索
			isRefreshThreadFinish = false;
			//WifiBRService.setWifiScannable(true);
			startWifiScanThread();
			return true;
		}
		
		return false;
	}
	
	public void startWifiScanThread() {
		Thread thread = new Thread(new RefreshListThread());
		thread.start();
	}
	
	private class RefreshListThread implements Runnable{

		@Override
		public void run() {
			final List<ScanResult> scanResults = mWifiAdmin.scanWifi();
			List<String> macAddresses = new ArrayList<String>();
			for (ScanResult scanResult : scanResults) {
				macAddresses.add(scanResult.BSSID);
			}
			//TODO(binfei): 生成test数据
			mTestWifis.clear();
			Random rand = new Random();
			mTestWifis.add("test" + rand.nextInt(3));
			for (String data : mTestWifis) {
				macAddresses.add(data);
			}
			
			//final List<WifiProfile> wifiProfiles;
			WifiProfile wifiProfile = new WifiProfile();
			wifiProfile.BatchQueryByMacAddress(mContext, macAddresses, true, new MultiDataCallback<WifiProfile>() {
				
				@Override
				public boolean onSuccess(List<WifiProfile> objects) {
					mWifiProfiles = objects;
					reGroupWifiList(scanResults, objects);
					isRefreshThreadFinish = true;
					
					//send update wifi list message to wifi connect
					Message msgs = new Message();
					msgs.what = GlobalHandler.UPDATE_WIFI_LIST;
					mHandler.sendMessageAtFrontOfQueue(msgs);
					return false;
				}
				
				@Override
				public boolean onFailed(String msg) {
					isRefreshThreadFinish = true;
					reGroupWifiList(scanResults, mWifiProfiles);
					
					//send update wifi list message to wifi connect
					Message msgf = new Message();
					msgf.what = GlobalHandler.UPDATE_WIFI_LIST;
					mHandler.sendMessageAtFrontOfQueue(msgf);
					return false;
				}
			});
		}
	}
	
	public void reGroupWifiList(List<ScanResult> scanResults, List<WifiProfile> wifiProfiles) {
		mWifiAuth.clear();
		mWifiNotAuth.clear();
		
		for (ScanResult scanResult : scanResults) {
			WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(scanResult, null);
			WifiProfile wifiProfile = getWifiProfile(scanResult.BSSID, wifiProfiles);
			wifiDistribution(scanResult, wifiCfg, wifiProfile);
		}
		
		//process test data
		for (String data : mTestWifis) {
			WifiProfile wifiProfile = getWifiProfile(data, wifiProfiles);
			if (wifiProfile == null) {
				continue;
			}
			wifiDistribution(null, null, wifiProfile);
		}
		
		sortWifiList();
	}
	
	private void sortWifiList() {
		int save = 0;
		List<WifiListItem> wifiNotAuth = new ArrayList<WifiListItem>();
		for (WifiListItem item : mWifiNotAuth) {
			if (item.isLocalWifi()) {
				++save;
				wifiNotAuth.add(0, item);
			} else if (item.isOpenWifi()) {
				wifiNotAuth.add(save, item);
			} else {
				wifiNotAuth.add(item);
			}
		}
		mWifiNotAuth = wifiNotAuth;
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
		WifiListItem wifiItem = new WifiListItem(scanResult, wifiCfg);
		wifiItem.setWifiProfile(wifiProfile);
		if (wifiProfile != null) { //下载其他相关数据
			pullWifiWhites(wifiProfile.Sponser, wifiItem);
		}
		
		if (scanResult != null && mWifiCurrent.getWifiName().equals(WifiAdmin.convertToNonQuotedString(scanResult.SSID))) {
			Log.d(TAG, "current connected wifi: " + scanResult.SSID);
			mWifiCurrent.setWifiListItem(wifiItem);
			return;
		}
		
		if (wifiItem.isAuthWifi()) {
			if (wifiItem.getWifiProfile().isShared()) {
				wifiItem.setWifiType(WifiType.AUTH_WIFI);
			}
			else
				wifiItem.setWifiType(WifiType.AUTH_CLOSE_WIFI);
			mWifiAuth.add(wifiItem);
		} else {
			if (wifiItem.isLocalWifi()) {
				wifiItem.setWifiType(WifiType.LOCAL_WIFI);
			} else if (wifiItem.isOpenWifi()) {
				wifiItem.setWifiType(WifiType.OPEN_WIFI);
			} else {
				wifiItem.setWifiType(WifiType.ENCRYPT_WIFI);
			}
			mWifiNotAuth.add(wifiItem);
		}
	}
	
	private void pullWifiWhites(final String sponser, final WifiListItem wifi) {
		WifiWhite wifiWhite = new WifiWhite();
		wifiWhite.QueryWhitersByUser(mContext, sponser, new MultiDataCallback<WifiWhite>() {

			@Override
			public boolean onSuccess(List<WifiWhite> objects) {
				wifi.setWifiWhites(objects);
				return true;
			}

			@Override
			public boolean onFailed(String msg) {
				return false;
			}
		});
	}
}
