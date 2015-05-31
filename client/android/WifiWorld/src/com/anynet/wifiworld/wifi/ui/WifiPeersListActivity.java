package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiP2pAdmin;
import com.anynet.wifiworld.wifi.WifiPeerListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.net.wifi.p2p.WifiP2pDevice;

public class WifiPeersListActivity extends Activity {
	private final static String TAG = WifiPeersListActivity.class.getSimpleName();
	
	private WifiP2pAdmin mWifiP2pAdmin;
	
	private ListView mPeerListView;
	private WifiPeerListAdapter mPeerListAdapter;
	private List<WifiP2pDevice> mPeerList = new ArrayList<WifiP2pDevice>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.wifi_peerlist_activity);
		super.onCreate(savedInstanceState);
		
		mPeerListView = (ListView)findViewById(R.id.wifi_peerlist_view);
		mPeerListAdapter = new WifiPeerListAdapter(this, mPeerList);
		mPeerListView.setAdapter(mPeerListAdapter);
		mPeerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mWifiP2pAdmin = WifiP2pAdmin.getInstance(this);
		mWifiP2pAdmin.discoverPeers(mPeerListAdapter);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "Register p2p broatcast receiver");
		mWifiP2pAdmin.registerP2PBroadcastReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "UnRegister p2p broatcast receiver");
		mWifiP2pAdmin.unregisterP2PBroadcastReceiver();
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
