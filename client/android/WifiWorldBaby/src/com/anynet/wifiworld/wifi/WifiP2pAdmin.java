package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WifiP2pAdmin {
	private final static String TAG = WifiP2pAdmin.class.getSimpleName();
	
	private Context mContext;
	private static WifiP2pAdmin mWifiP2pAdmin = null;
	
	private WifiP2pManager mWifiP2pManager;
	private WifiP2pManager.Channel mChannel;
	private WifiP2pManager.PeerListListener mPeerListListener;
	
	private P2pBroadCastReceiver mP2pBroadCastReceiver;
	private List<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();
	
	public static WifiP2pAdmin getInstance(Context context) {
		if (mWifiP2pAdmin == null) {
			mWifiP2pAdmin = new WifiP2pAdmin(context);
		}
		return mWifiP2pAdmin;
	}
	
	private WifiP2pAdmin(Context context) {
		mContext = context;
		mWifiP2pManager = (WifiP2pManager)mContext.getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
	}
	
	public void discoverPeers(final BaseAdapter baseAdapter) {
		mPeerListListener = new WifiP2pManager.PeerListListener() {
			
			@Override
			public void onPeersAvailable(WifiP2pDeviceList peerList) {
				mPeers.clear();
				mPeers.addAll(peerList.getDeviceList());
				WifiPeerListAdapter wifiPeerListAdapter = (WifiPeerListAdapter)baseAdapter;
				wifiPeerListAdapter.refreshPeerList(mPeers);
			}
		};
		
		mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				Log.i(TAG, "p2p channel: " + mChannel);
			}
			
			@Override
			public void onFailure(int arg0) {
				Toast.makeText(mContext, "Failed to discover peers", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void registerP2PBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		mP2pBroadCastReceiver = new P2pBroadCastReceiver();
		mContext.registerReceiver(mP2pBroadCastReceiver, intentFilter);
	}
	
	public void unregisterP2PBroadcastReceiver() {
		mContext.unregisterReceiver(mP2pBroadCastReceiver);
	}
	
	private class P2pBroadCastReceiver extends BroadcastReceiver {
		
		public P2pBroadCastReceiver() {
			
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				Log.i(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
				if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
					
				} else {
					
				}
			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				Log.i(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
				if (mWifiP2pManager != null) {
					mWifiP2pManager.requestPeers(mChannel, mPeerListListener);
				}
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				Log.i(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
				
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				Log.i(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
				
			}
		}
		
	}
}