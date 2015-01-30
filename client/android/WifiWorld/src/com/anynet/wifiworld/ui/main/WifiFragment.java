package com.anynet.wifiworld.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListAdapter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WifiFragment extends Fragment {
	private final static String TAG = WifiFragment.class.getSimpleName();
	
	private View mView;
	private ListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<ScanResult> mWifiListScanned;
	private List<WifiConfiguration> mWifiConfigurationScanned;
	private Map<String, WifiInfoScanned> mWifiFree = new HashMap<String, WifiInfoScanned>();
	private Map<String, WifiInfoScanned> mWifiEncrypt = new HashMap<String, WifiInfoScanned>();
	 
	public void setWifiData(List<ScanResult> wifiList, List<WifiConfiguration> wificonfiguration){
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult scanResult = wifiList.get(i);
			for (int j = 0; j < wificonfiguration.size(); j++) {
				if (scanResult.BSSID.equals(wificonfiguration.get(j).BSSID)) {
					String pwd = wificonfiguration.get(j).preSharedKey;
					if (!pwd.isEmpty() && pwd.equals("*")) {
						String wifiName = wificonfiguration.get(j).SSID;
						WifiInfoScanned wifiInfoScanned = new WifiInfoScanned(wifiName, scanResult.level);
						mWifiFree.put(wifiName, wifiInfoScanned);
					}
				}
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WifiAdmin wifiAdmin = new WifiAdmin(getActivity());
		wifiAdmin.openWifi();
		wifiAdmin.startScan();
		mWifiListScanned = wifiAdmin.getWifiList();
		mWifiConfigurationScanned = wifiAdmin.getConfiguration();
		setWifiData(mWifiListScanned, mWifiConfigurationScanned);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		
		mView = inflater.inflate(R.layout.fragment_wifi, null);
		mWifiListView = (ListView) mView.findViewById(R.id.wifi_list_view);
		
//		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiList, mWifiListTag);
//		mWifiListView.setAdapter(mWifiListAdapter);
		
		return mView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
}
