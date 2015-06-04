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

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class WifiReenable {
	public static void schedule(final Context ctx) {
		ctx.startService(new Intent(ctx, BackgroundService.class));
	}
	
	private static void reenableAllAps(final Context ctx) {
		final WifiManager wifiMgr = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		if(configurations != null) {
			for(final WifiConfiguration config:configurations) {
				wifiMgr.enableNetwork(config.networkId, false);
			}
		}
	}
	
	public static class BackgroundService extends Service {

		private boolean mReenabled;
		
		private BroadcastReceiver mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
					final NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
					final NetworkInfo.DetailedState detailed = networkInfo.getDetailedState();
					if(detailed != NetworkInfo.DetailedState.DISCONNECTED
							&& detailed != NetworkInfo.DetailedState.DISCONNECTING
							&& detailed != NetworkInfo.DetailedState.SCANNING) {
						if(!mReenabled) {
							mReenabled = true;
							reenableAllAps(context);
							stopSelf();
						}
					}
				}
			}
		};
		
		private IntentFilter mIntentFilter;
		
		@Override
		public IBinder onBind(Intent intent) {
			return null; // We need not bind to it at all.
		}
		
		@Override
		public void onCreate() {
			super.onCreate();
			mReenabled = false;
			mIntentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			registerReceiver(mReceiver, mIntentFilter);
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			unregisterReceiver(mReceiver);
		}

	}
}
