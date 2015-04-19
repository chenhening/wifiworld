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
import com.anynet.wifiworld.util.LoginHelper;

import cn.bmob.v3.datatype.BmobGeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
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
	private List<WifiInfoScanned> mWifiAuth; //认证wifi
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
		mWifiAuth = new ArrayList<WifiInfoScanned>();
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
		mWifiAuth = new ArrayList<WifiInfoScanned>();
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
		mWifiAuth.clear();
		mWifiFree.clear();
		mWifiEncrypt.clear();
		if (mWifiAdmin.openWifi()) {
			mWifiAdmin.closeWifi();
		}
	}

	public void organizeWifiList(final List<ScanResult> wifiList) {

		List<String> macAddresses = new ArrayList<String>();
		macAddresses.add("ec:26:ca:62:46:06");
		for (ScanResult scanResult : wifiList) {
			macAddresses.add(scanResult.BSSID);
		}
		
		WifiProfile wifiProfile = new WifiProfile();
		wifiProfile.BatchQueryByMacAddress(mContext, macAddresses, new MultiDataCallback<WifiProfile>() {
			
			@Override
			public void onSuccess(List<WifiProfile> objects) {
				Log.i(TAG, "Batch query by mac address success");
				mWifiProfiles = objects;
				refreshListUI(wifiList);
			}
			
			@Override
			public void onFailed(String msg) {
				Log.i(TAG, msg);
			}
		});
		
		refreshListUI(wifiList);
	}
	
	private void refreshListUI(List<ScanResult> wifiList) {
		mWifiAuth.clear();
		mWifiFree.clear();
		mWifiEncrypt.clear();
		WifiInfo wifiInfo = mWifiAdmin.getWifiConnection();
		for (ScanResult hotspot : wifiList) {
			//Check whether WiFi is stored local
			final WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(hotspot, null);
			verifyList(wifiCfg, hotspot, wifiInfo, mWifiProfiles);
		}
		mHandler.sendEmptyMessage(((MainActivity)mContext).UPDATE_WIFI_LIST);
	}
	
	private void verifyList(WifiConfiguration wifiCfg, ScanResult hotspot, WifiInfo wifiInfo, List<WifiProfile> objects) {
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
			if (objects != null) {
				int idx = isContained(WifiAdmin.convertToNonQuotedString(hotspot.BSSID), objects);
				boolean isAuthWifi = idx == -1 ? false : true;
				mWifiInfoCur.setAuthWifi(isAuthWifi);
			}
			return;
		}
		
		if (objects != null) {
			int idx = isContained(WifiAdmin.convertToNonQuotedString(hotspot.BSSID), objects);
			if (idx != -1) {
				Log.i(TAG, "Query wifi:" + hotspot.BSSID + ":" + hotspot.SSID + " has been shared");
				wifiName = hotspot.SSID;
				wifiMAC = hotspot.BSSID;
				wifiPwd = objects.get(idx).Password;
				wifiType = WifiAdmin.ConfigSec.getScanResultSecurity(hotspot);
				wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
				
				if (LoginHelper.getInstance(mContext).mKnockList.contains(wifiMAC)) {
					wifiRemark += "已经敲门成功可直接使用";
				} else {
					wifiRemark += "敲门成功就能使用";
				}
				wifiInfoScanned = new WifiInfoScanned(wifiName, wifiMAC, wifiPwd, 
						wifiType, wifiStrength, null, wifiRemark);
				WifiProfile wifi = objects.get(idx);
				if (wifi.Alias != null && wifi.Alias.length() > 0)
					wifiInfoScanned.setAlias(wifi.Alias);
				mWifiAuth.add(wifiInfoScanned);
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
				wifiRemark = "无密码";
			}
			//wifiRemark += "本地已保存";
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
	
	public Bitmap getWifiLogo(String wifiMac) {
		if (mWifiProfiles != null) {
			for (WifiProfile wifiProfile : mWifiProfiles) {
				if (wifiProfile.MacAddr.equals(wifiMac)) {
					return wifiProfile.getLogo();
				}
			}
		} else {
			Log.i(TAG, "Wifi Profile table return null");
		}
		return null;
	}
	
	public Bitmap getWifiLogo(WifiInfoScanned wifiInfoScanned) {
		if (mWifiProfiles != null) {
			for (WifiProfile wifiProfile : mWifiProfiles) {
				if (wifiProfile.MacAddr.equals(wifiInfoScanned.getWifiMAC())) {
					return wifiProfile.getLogo();
				}
			}
		} else {
			Log.i(TAG, "Wifi Profile table return null");
		}
		return null;
	}
	
	public String getWifiBnner(WifiInfoScanned wifiInfoScanned) {
		if (mWifiProfiles != null) {
			for (WifiProfile wifiProfile : mWifiProfiles) {
				if (wifiProfile.MacAddr.equals(wifiInfoScanned.getWifiMAC())) {
					return wifiProfile.Banner;
				}
			}
		} else {
			Log.i(TAG, "Wifi Profile table return null");
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

	public List<WifiInfoScanned> getWifiAuths() {
		return mWifiAuth;
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
