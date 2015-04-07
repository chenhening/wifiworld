package com.anynet.wifiworld.wifi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.bean.Msg;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiProfile;

import cn.bmob.v3.datatype.BmobGeoPoint;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.util.Log;

public class WifiListHelper {
	private final static String TAG = WifiListHelper.class.getSimpleName();
	
	public static WifiListHelper mInstance;
	
	private Context mContext;
	private Handler mHandler;
	private WifiAdmin mWifiAdmin;
	private List<WifiInfoScanned> mWifiFree;
	private List<WifiInfoScanned> mWifiEncrypt;
	private List<String> mWifiListUnique;
	public List<WifiProfile> mWifiProfiles;
	public WifiInfoScanned mWifiInfoCur; 
	
	private final String WIFI_LIST_FILE_NAME = "wifi_list_file.txt";

	public static WifiListHelper getInstance(Context context, Handler handler) {
		if (null == mInstance) {
			mInstance = new WifiListHelper(context, handler);
		}
		return mInstance;
	}
	
	private WifiListHelper(Context context, Handler handler) {
		mWifiAdmin = WifiAdmin.getInstance(context);
		mWifiFree = new ArrayList<WifiInfoScanned>();
		mWifiEncrypt = new ArrayList<WifiInfoScanned>();
		mWifiListUnique = new ArrayList<String>();
		mContext = context;
		mHandler = handler;
	}
	
	public static WifiListHelper getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new WifiListHelper(context);
		}
		return mInstance;
	}
	
	private WifiListHelper(Context context) {
		mWifiAdmin = WifiAdmin.getInstance(context);
		mWifiFree = new ArrayList<WifiInfoScanned>();
		mWifiEncrypt = new ArrayList<WifiInfoScanned>();
		mWifiListUnique = new ArrayList<String>();
		mContext = context;
	}
	
	public boolean fillWifiList() {
		if (mWifiAdmin.isWifiEnabled()) {
			organizeWifiList(mWifiAdmin.scanWifi());
			return true;
		}
		return false;
	}

	public void clearListAndCloseWifi() {
		mWifiFree.clear();
		mWifiEncrypt.clear();
		if (mWifiAdmin.openWifi()) {
			mWifiAdmin.closeWifi();
		}
	}

	public void organizeWifiList(final List<ScanResult> wifiList) {
		//readFile();
		
		List<String> macAddresses = new ArrayList<String>();
		for (ScanResult scanResult : wifiList) {
			macAddresses.add(scanResult.BSSID);
		}
		
		final WifiInfo wifiInfo = mWifiAdmin.getWifiConnection();
		
		WifiProfile wifiProfile = new WifiProfile();
		wifiProfile.BatchQueryByMacAddress(mContext, macAddresses, new MultiDataCallback<WifiProfile>() {
			
			@Override
			public void onSuccess(List<WifiProfile> objects) {
				Log.i(TAG, "Batch query by mac address success");
				mWifiProfiles = objects;
				mWifiFree.clear();
				mWifiEncrypt.clear();
				for (ScanResult hotspot : wifiList) {
					//Check whether WiFi is stored local
					final WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(hotspot, null);
//					int idx = isContained(WifiAdmin.convertToNonQuotedString(hotspot.BSSID), objects);
//					if (idx != -1) {
//						Log.i(TAG, "Query wifi:" + hotspot.BSSID + ":" + hotspot.SSID + " has been shared");
//						final String wifiName = hotspot.SSID;
//						final String wifiMac = hotspot.BSSID;
//						final String wifiPwd = objects.get(idx).Password;
//						final String wifiType = WifiAdmin.ConfigSec.getScanResultSecurity(hotspot);
//						final Integer wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
//						final WifiInfoScanned wifiSharedTmp = new WifiInfoScanned(wifiName, wifiMac, wifiPwd, 
//								wifiType, wifiStrength, null, "已认证");
//						if (wifiCfg != null) {
//							wifiSharedTmp.setRemark(wifiSharedTmp.getRemark() + ", 本地已保存");
//						}
//						mWifiFree.add(wifiSharedTmp);
//					} else {
//						refreashList(wifiCfg, hotspot, wifiInfo);
//					}
					refreashList(wifiCfg, hotspot, wifiInfo, objects);
				}
				mHandler.sendEmptyMessage(((MainActivity)mContext).UPDATE_WIFI_LIST);
			}
			
			@Override
			public void onFailed(String msg) {
				Log.i(TAG, msg);
				mWifiFree.clear();
				mWifiEncrypt.clear();
				for (ScanResult hotspot : wifiList) {
					//Check whether WiFi is stored local
					final WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(hotspot, null);
					refreashList(wifiCfg, hotspot, wifiInfo, null);
				}
				mHandler.sendEmptyMessage(((MainActivity)mContext).UPDATE_WIFI_LIST);
			}
		});
			
//			String hotspotKey = hotspot.SSID + ":" + hotspot.BSSID + " " + hotspot.capabilities;
//			if (!mWifiListUnique.contains(hotspotKey)) {
//				mWifiListUnique.add(hotspotKey);
//				//upload WIFI info to WIFI Unregistered
//				Log.i(TAG, "upload to database: " + hotspotKey);
//				WifiHandleDB.getInstance(mContext).updateWifiUnregistered(wifiInfoScanned);
//			}
		//writeFile();
	}
	
	private void refreashList(WifiConfiguration wifiCfg, ScanResult hotspot, WifiInfo wifiInfo, List<WifiProfile> objects) {
		String wifiName;
		String wifiPwd;
		String wifiType;
		String wifiMAC;
		String wifiRemark = "";
		Integer wifiStrength;
		BmobGeoPoint wifiGeometry;
		WifiInfoScanned wifiInfoScanned;
		
		if (wifiInfo != null &&  wifiInfo.getSSID().equals(WifiAdmin.convertToQuotedString(hotspot.SSID))) {
			Log.i(TAG, hotspot.SSID + " is the current connected wifi");
			wifiName = hotspot.SSID;
			wifiType = WifiAdmin.ConfigSec.getScanResultSecurity(hotspot);
			wifiMAC = hotspot.BSSID;
			wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
			wifiGeometry = WifiAdmin.getWifiGeometry(mContext, hotspot.level);
			wifiPwd = null;
			wifiRemark = "当前连接WiFi";
			mWifiInfoCur = new WifiInfoScanned(wifiName, wifiMAC, wifiPwd, wifiType,
					wifiStrength, wifiGeometry, wifiRemark);
			return;
		}
		
		if (objects != null) {
			Log.i(TAG, "Query wifi:" + hotspot.BSSID + ":" + hotspot.SSID + " has been shared");
			int idx = isContained(WifiAdmin.convertToNonQuotedString(hotspot.BSSID), objects);
			if (idx != -1) {
				Log.i(TAG, "Query wifi:" + hotspot.BSSID + ":" + hotspot.SSID + " has been shared");
				wifiName = hotspot.SSID;
				wifiMAC = hotspot.BSSID;
				wifiPwd = objects.get(idx).Password;
				wifiType = WifiAdmin.ConfigSec.getScanResultSecurity(hotspot);
				wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
				wifiRemark = "已认证";
				if (wifiCfg != null) {
					wifiRemark += ", 本地已保存";
				}
				wifiInfoScanned = new WifiInfoScanned(wifiName, wifiMAC, wifiPwd, 
						wifiType, wifiStrength, null, wifiRemark);
				mWifiFree.add(wifiInfoScanned);
				return;
			}
		}
		
		if (wifiCfg != null) {
			wifiName = hotspot.SSID;
			wifiPwd = wifiCfg.preSharedKey;
			wifiType = WifiAdmin.ConfigSec.getWifiConfigurationSecurity(wifiCfg);
			wifiMAC = hotspot.BSSID;
			wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
			wifiGeometry = WifiAdmin.getWifiGeometry(mContext, hotspot.level);
			if (WifiAdmin.ConfigSec.isOpenNetwork(wifiType)) {
				wifiRemark = "无密码, ";
			}
			wifiRemark += "本地已保存";
			wifiInfoScanned = new WifiInfoScanned(wifiName, wifiMAC, wifiPwd, wifiType,
					wifiStrength, wifiGeometry, wifiRemark);
			mWifiFree.add(wifiInfoScanned);
		} else {
			//Check whether is a open WiFi
			wifiName = hotspot.SSID;
			wifiType = WifiAdmin.ConfigSec.getScanResultSecurity(hotspot);
			wifiMAC = hotspot.BSSID;
			wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
			wifiGeometry = WifiAdmin.getWifiGeometry(mContext, hotspot.level);
			if (WifiAdmin.ConfigSec.isOpenNetwork(wifiType)) {
				wifiRemark = "无密码";
				wifiInfoScanned = new WifiInfoScanned(wifiName,wifiMAC, null, wifiType, wifiStrength,
						wifiGeometry, wifiRemark);
				mWifiFree.add(wifiInfoScanned);
			} else {
				wifiInfoScanned = new WifiInfoScanned(wifiName,wifiMAC, null, wifiType, wifiStrength,
						wifiGeometry, null);
				mWifiEncrypt.add(wifiInfoScanned);
			}
		}
	}
	
	private int isContained(String macAddress, List<WifiProfile> mList) {
		for (int idx=0; idx<mList.size(); ++idx) {
			if (mList.get(idx).MacAddr.equals(macAddress)) {
				return idx;
			}
		}
		
		return -1;
	}
	
	//remove WIFI which is connected from free-WIFI-list or encrypt-WIFI-list
	//need compare with WIFI SSID and Encrypt type
	public WifiInfoScanned rmWifiConnected(String wifiName) {
		for (Iterator<WifiInfoScanned> it = mWifiFree.iterator(); it.hasNext();) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifiName())) {
				it.remove();
				return tmpInfo;
			}
		}

		for (Iterator<WifiInfoScanned> it = mWifiEncrypt.iterator(); it.hasNext();) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifiName())) {
				it.remove();
				return tmpInfo;
			}
		}

		return null;
	}
	
	private boolean readFile() {
		mWifiListUnique.clear();
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = mContext.openFileInput(WIFI_LIST_FILE_NAME);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line = "";
			while ((line=br.readLine()) != null) {
				mWifiListUnique.add(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			
		}
		return true;
	}
	
	private boolean writeFile() {
		if (mWifiListUnique.isEmpty()) {
			Log.i(TAG, "no wifi date store in wifilistunique");
			return false;
		}
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = mContext.openFileOutput(WIFI_LIST_FILE_NAME, mContext.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);
			for (String wifi_str : mWifiListUnique) {
				bw.write(wifi_str+"\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (osw != null) {
					osw.close();
				}
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		return true;
	}
	
	public WifiAdmin getWifiAdmin() {
		return mWifiAdmin;
	}

	public List<WifiInfoScanned> getWifiFrees() {
		return mWifiFree;
	}

	public List<WifiInfoScanned> getWifiEncrypts() {
		return mWifiEncrypt;
	}
	
	public boolean uploadWifiListSanned() {
		
		return true;
	}
}
