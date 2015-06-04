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

import com.anynet.wifiworld.util.Version;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

public abstract class WifiSecurities {
	/**
     * @return The security of a given {@link WifiConfiguration}.
     */
	public abstract String getWifiConfigurationSecurity(WifiConfiguration wifiConfig);
	/**
     * @return The security of a given {@link ScanResult}.
     */
	public abstract String getScanResultSecurity(ScanResult scanResult);
	/**
     * Fill in the security fields of WifiConfiguration config.
     * @param config The object to fill.
     * @param security If is OPEN, password is ignored.
     * @param password Password of the network if security is not OPEN.
     */
	public abstract void setupSecurity(WifiConfiguration config, String security, final String password);
	public abstract String getDisplaySecirityString(final ScanResult scanResult);
	public abstract boolean isOpenNetwork(final String security);
	
	public static WifiSecurities newInstance() {
		if(Version.SDK < 8) {
			return new WifiSecuritiesOld();
		} else {
			return new WifiSecuritiesV8();
		}
	}
	
}
