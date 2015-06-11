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

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

public class WifiListItem {
	private final static String TAG = WifiListItem.class.getSimpleName();
	
	private ScanResult mScanResult;
	private WifiConfiguration mWifiConfiguration;
	private WifiDBInfo mWifiDBInfo;
	
	public WifiListItem() {
		mScanResult = null;
		mWifiConfiguration = null;
		mWifiDBInfo = null;
	}
	
	public WifiListItem(ScanResult scanResult, WifiConfiguration wifiConfiguration, WifiDBInfo wifiDBInfo) {
		mScanResult = scanResult;
		mWifiConfiguration = wifiConfiguration;
		mWifiDBInfo = wifiDBInfo;
	}
	
	public void setScanResult(ScanResult scanResult) {
		mScanResult = scanResult;
	}
	
	public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
		mWifiConfiguration = wifiConfiguration;
	}
	public WifiConfiguration getWifiConfiguration() {
		return mWifiConfiguration;
	}
	
	public void setWifiDBInfo(WifiDBInfo wifiDBInfo) {
		mWifiDBInfo = wifiDBInfo;
	}
	
	public String getWifiName() {
		return mScanResult.SSID;
	}
	
	public String getWifiMac() {
		return mScanResult.BSSID;
	}
	
	public String getEncryptType() {
		return mScanResult.capabilities;
	}
	
	public void setNetworkId(int id) {
		
	}
	
	public String getAlias() {
		WifiProfile wifi = mWifiDBInfo.getWifiProfile();
		if (wifi != null) {
			return wifi.Alias;
		}
		return "未命名";
	}
}
