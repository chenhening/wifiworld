/*
 * Copyright 2015 Anynet Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Anynet Corporation or its suppliers
 * or licensors. Title to the Material remains with Anynet Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Anynet or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions.
 * No part of the Material may be used, copied, reproduced, modified, published
 * , uploaded, posted, transmitted, distributed, or disclosed in any way
 * without Anynet's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Anynet in writing.
 *
 * @brief ANLog is the custom log for wifiworld project.
 * @date 2015-06-04
 * @author Jason.Chen
 *
 */

package com.anynet.wifiworld.wifi;

import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.BitmapUtil;

public class WifiListItem {
	private final static String TAG = WifiListItem.class.getSimpleName();
	
	private ScanResult mScanResult;
	private WifiConfiguration mWifiConfiguration;
	
	private WifiProfile mWifiProfile;
	
	private String[] WifiStr = {
			"未识别类型",
			"已认证, 安全, 可免费上网",
			"未认证, 本地已保存",
			"未认证, 无密码",
			"未认证, 需要密码"
	};
	public enum WifiType {
		UNKOWN,
		AUTH_WIFI,
		LOCAL_WIFI,
		OPEN_WIFI,
		ENCRYPT_WIFI
	};
	private WifiType mWifiType;
	private String mWifiPwd;
	private String mMacAddr; //技术债啊
	private String mSSID; //技术债啊
	private String mEncryptType; //技术债啊
	
	public WifiListItem() {
		mWifiType = WifiType.UNKOWN;
		mWifiPwd = null;
		mScanResult = null;
		mWifiConfiguration = null;
		mWifiProfile = null;
	}
	
	public WifiListItem(ScanResult scanResult, WifiConfiguration wifiConfiguration) {
		mWifiType = WifiType.UNKOWN;
		mWifiPwd = null;
		mScanResult = scanResult;
		mWifiConfiguration = wifiConfiguration;
	}
	
	public void setScanResult(ScanResult scanResult) {
		mScanResult = scanResult;
	}
	
	public ScanResult getScanResult() {
		return mScanResult;
	}
	
	public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
		mWifiConfiguration = wifiConfiguration;
	}
	public WifiConfiguration getWifiConfiguration() {
		return mWifiConfiguration;
	}
	
	public void setWifiProfile(WifiProfile wifiProfile) {
		mWifiProfile = wifiProfile;
	}
	
	public WifiProfile getWifiProfile() {
		return mWifiProfile;
	}
	
	public String getWifiName() {
		if (mScanResult != null)
			return mScanResult.SSID;
		else if (mWifiProfile != null)
			return mWifiProfile.Ssid;
		else
			return mSSID;
	}
	
	public void setWifiName(String ssid) {
		mSSID = ssid;
	}
	
	public String getWifiMac() {
		if (mScanResult != null)
			return mScanResult.BSSID;
		else
			return mMacAddr;
	}
	
	public void setWifiMac(String macid) {
		mMacAddr = macid;
	}
	
	public String getWifiPwd() {
		if (mWifiProfile != null) {
			mWifiPwd = mWifiProfile.getPassword();
		}
		return mWifiPwd;
	}
	
	public void setWifiPwd(String pwd) {
		mWifiPwd = pwd;
	}
	
	public String getEncryptStr() {
		if (mScanResult != null) {
			return WifiAdmin.ConfigSec.getDisplaySecirityString(mScanResult);
		} else {
			return "UnKown";
		}
	}
	
	public String getEncryptType() {
		if (mScanResult != null)
			return WifiAdmin.ConfigSec.getScanResultSecurity(mScanResult);
		else 
			return mEncryptType;
	}
	
	public void setEncryptType(String type) {
		mEncryptType = type;
	}
	
	public String getAlias() {
		if (mWifiProfile != null) {
			return mWifiProfile.Alias;
		}
		return "";
	}
	
	public Bitmap getLogo() {
		if (mWifiProfile != null) {
			return mWifiProfile.getLogo();
		}
		return null;
	}

	public WifiType getWifiType() {
		return mWifiType;
	}
	
	public void setWifiType(WifiType wifiType) {
		mWifiType = wifiType;
	}

	public String getOptions() {
		return WifiStr[mWifiType.ordinal()];
	}
	
	public boolean isLocalWifi() {
		return (mWifiConfiguration != null ? true : false);
	}
	
	public boolean isAuthWifi() {
		return (mWifiProfile != null ? true : false);
	}
	
	public boolean isOpenWifi() {
		if (mScanResult != null)
			return WifiAdmin.ConfigSec.isOpenNetwork(WifiAdmin.ConfigSec.getScanResultSecurity(mScanResult));
		return false;
	}
	
	public boolean isEncryptWifi() {
		return !isOpenWifi();
	}
	
	public String getBanner() {
		if (mWifiProfile != null) {
			return mWifiProfile.Banner;
		}
		return "";
	}
	
	public int getWifiStrength() {
		if (mScanResult != null)
			return WifiAdmin.getWifiStrength(mScanResult.level);
		return 0;
	}
	
	public int getDefaultLogo() {
		//根据wifi信号强度显示不同的信号图
		int signalStrength = getWifiStrength();
		switch (mWifiType) {
		case ENCRYPT_WIFI:
			if (signalStrength >= 80) {
				return R.drawable.ic_wifi_locked_signal_3;
			} else if (signalStrength >= 60) {
				return R.drawable.ic_wifi_locked_signal_2;
			} else {
				return R.drawable.ic_wifi_locked_signal_1;
			}
		default:
			if (signalStrength >= 80) {
				return R.drawable.ic_wifi_connecting_3; //这里图片重用connecting的
			} else if (signalStrength >= 60) {
				return R.drawable.ic_wifi_connecting_2;
			} else {
				return R.drawable.ic_wifi_connecting_1;
			}
		}
	}
}
