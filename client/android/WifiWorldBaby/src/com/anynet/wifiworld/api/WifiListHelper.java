package com.anynet.wifiworld.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiInfoScanned;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
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
		if (mWifiAdmin.openWifi()) {
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
			//WifiHandleDB.getInstance(mContext).updateOneRow(wifiInfoScanned);
			
//			String hotspotKey = hotspot.SSID + " " + hotspot.capabilities;
//			if (!mWifiListUnique.contains(hotspotKey)) {
//				mWifiListUnique.add(hotspotKey);
//				//upload WIFI info to WIFI Unregistered
//				updateHotspot(wifiInfoScanned);
//			}
		}
	}
	
	//remove WIFI which is connected from free-WIFI-list or encrypt-WIFI-list
	//need compare with WIFI SSID and Encrypt type
	public boolean rmWifiConnected(String wifiName) {
		for (Iterator<WifiInfoScanned> it = mWifiFree.iterator(); it.hasNext();) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifiName())) {
				it.remove();
				return true;
			}
		}

		for (Iterator<WifiInfoScanned> it = mWifiEncrypt.iterator(); it.hasNext();) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifiName())) {
				it.remove();
				return true;
			}
		}

		return false;
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
