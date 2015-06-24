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

import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.BitmapUtil;

import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

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
	
	public WifiListItem() {
		mWifiType = WifiType.UNKOWN;
		mWifiPwd = null;
		mScanResult = null;
		mWifiConfiguration = null;
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
	
	public String getWifiName() {
		return mScanResult.SSID;
	}
	
	public String getWifiMac() {
		return mScanResult.BSSID;
	}
	
	public String getWifiPwd() {
		if (mWifiProfile != null) {
			mWifiPwd = mWifiProfile.Password;
		}
		return mWifiPwd;
	}
	
	public void setWifiPwd(String pwd) {
		mWifiPwd = pwd;
	}
	
	public String getEncryptType() {
		return WifiAdmin.ConfigSec.getScanResultSecurity(mScanResult);
	}
	
	public String getAlias() {
		if (mWifiProfile != null) {
			return mWifiProfile.Alias;
		}
		return "";
	}
	
	public Bitmap getLogo() {
		if (mWifiProfile != null) {
			return BitmapUtil.Bytes2Bimap(mWifiProfile.Logo);
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
		return WifiAdmin.ConfigSec.isOpenNetwork(WifiAdmin.ConfigSec.getScanResultSecurity(mScanResult));
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
		return WifiAdmin.getWifiStrength(mScanResult.level);
	}
}
