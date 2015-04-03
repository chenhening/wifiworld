package com.anynet.wifiworld.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiHandleDB;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.umeng.message.proguard.br;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WifiListHelper {
	private final static String TAG = WifiListHelper.class.getSimpleName();
	
	public static WifiListHelper mInstance;
	
	private Context mContext;
	private WifiAdmin mWifiAdmin;
	private List<WifiInfoScanned> mWifiFree;
	private List<WifiInfoScanned> mWifiEncrypt;
	private List<String> mWifiListUnique;
	
	private final String WIFI_LIST_FILE_NAME = "wifi_list_file.txt";

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
		mWifiFree.clear();
		mWifiEncrypt.clear();

		//readFile();
		
		String wifiName;
		String wifiPwd;
		String wifiType;
		String wifiMAC;
		Integer wifiStrength;
		BmobGeoPoint wifiGeometry;
		WifiInfoScanned wifiInfoScanned;
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult hotspot = wifiList.get(i);
			
			WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(hotspot, null);
			if (wifiCfg != null) {
				wifiName = hotspot.SSID;
				wifiPwd = wifiCfg.preSharedKey;
				wifiType = WifiAdmin.ConfigSec.getWifiConfigurationSecurity(wifiCfg);
				wifiMAC = hotspot.BSSID;
				wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
				wifiGeometry = WifiAdmin.getWifiGeometry(mContext, hotspot.level);
				wifiInfoScanned = new WifiInfoScanned(wifiName, wifiMAC, wifiPwd, wifiType,
						wifiStrength, wifiGeometry, "本地已保存");
				mWifiFree.add(wifiInfoScanned);
			} else {
				wifiName = hotspot.SSID;
				wifiType = WifiAdmin.ConfigSec.getScanResultSecurity(hotspot);
				wifiMAC = hotspot.BSSID;
				wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
				wifiGeometry = WifiAdmin.getWifiGeometry(mContext, hotspot.level);
				if (WifiAdmin.ConfigSec.isOpenNetwork(wifiType)) {
					wifiInfoScanned = new WifiInfoScanned(wifiName,wifiMAC, null, wifiType, wifiStrength,
							wifiGeometry, "无密码");
					mWifiFree.add(wifiInfoScanned);
				} else {
					wifiInfoScanned = new WifiInfoScanned(wifiName,wifiMAC, null, wifiType, wifiStrength,
							wifiGeometry, null);
					mWifiEncrypt.add(wifiInfoScanned);
				}
			}
			
//			String hotspotKey = hotspot.SSID + ":" + hotspot.BSSID + " " + hotspot.capabilities;
//			if (!mWifiListUnique.contains(hotspotKey)) {
//				mWifiListUnique.add(hotspotKey);
//				//upload WIFI info to WIFI Unregistered
//				Log.i(TAG, "upload to database: " + hotspotKey);
//				WifiHandleDB.getInstance(mContext).updateWifiUnregistered(wifiInfoScanned);
//			}
		}
		//writeFile();
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
