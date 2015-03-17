package com.anynet.wifiworld.api;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiInfoScanned;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

public class WifiListHelper {

	public static WifiListHelper mInstance;
	private WifiAdmin mWifiAdmin;
	private List<WifiInfoScanned> mWifiFree;
	private List<WifiInfoScanned> mWifiEncrypt;

	public WifiListHelper(Context context) {
		// TODO Auto-generated constructor stub
		mWifiAdmin = WifiAdmin.getInstance(context);
	}

	public WifiAdmin getWifiAdmin() {
		return mWifiAdmin;
	}

	public static WifiListHelper getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new WifiListHelper(context);
		}
		return mInstance;
	}

	public void openAndInitList() {
		mWifiFree = new ArrayList<WifiInfoScanned>();
		mWifiEncrypt = new ArrayList<WifiInfoScanned>();
		mWifiAdmin.openWifi();
		organizeWifiList(mWifiAdmin.scanWifi());
	}

	public boolean refreshWifiList() {
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

		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult hotspot = wifiList.get(i);
			WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(
					hotspot, null);
			if (wifiCfg != null) {
				String wifiName = hotspot.SSID;
				String wifiPwd = wifiCfg.preSharedKey;
				String wifiType = WifiAdmin.ConfigSec
						.getWifiConfigurationSecurity(wifiCfg);
				String wifiMAC = hotspot.BSSID;
				WifiInfoScanned wifiInfoScanned = new WifiInfoScanned(wifiName,wifiMAC,
						wifiPwd, wifiType,
						WifiAdmin.getWifiStrength(hotspot.level), "本地已保存");
				mWifiFree.add(wifiInfoScanned);
			} else {
				String wifiName = hotspot.SSID;
				String wifiType = WifiAdmin.ConfigSec
						.getScanResultSecurity(hotspot);
				String wifiMAC = hotspot.BSSID;
				Integer wifiStrength = WifiAdmin.getWifiStrength(hotspot.level);
				if (WifiAdmin.ConfigSec.isOpenNetwork(wifiType)) {
					mWifiFree.add(new WifiInfoScanned(wifiName,wifiMAC, null, wifiType,
							wifiStrength, "无密码"));
				} else {
					mWifiEncrypt.add(new WifiInfoScanned(wifiName,wifiMAC, null,
							wifiType, WifiAdmin.getWifiStrength(hotspot.level),
							null));
				}
			}
		}
	}

	public List<WifiInfoScanned> getWifiFrees() {
		return mWifiFree;
	}

	public List<WifiInfoScanned> getWifiEncrypts() {
		return mWifiEncrypt;
	}
}
