package com.anynet.wifiworld.ui.main;

import java.util.ArrayList;
import java.util.BitSet;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WifiFragment extends Fragment {
	private final static String TAG = WifiFragment.class.getSimpleName();
	
	private View mView;
	private ListView mWifiListView;
	WifiAdmin mWifiAdmin;
	private WifiListAdapter mWifiListAdapter;
	private List<ScanResult> mWifiListScanned;
	private List<WifiConfiguration> mWifiConfigurationScanned;
	private List<WifiInfoScanned> mWifiFree = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiEncrypt = new ArrayList<WifiInfoScanned>();
	 
	public void setWifiData(List<ScanResult> wifiList, List<WifiConfiguration> wificonfiguration) {
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult scanResult = wifiList.get(i);
			WifiConfiguration wifiCfg = mWifiAdmin.isExsits(scanResult.SSID);
			if (wifiCfg != null) {
				String wifiName = scanResult.SSID;
				String wifiPwd = wifiCfg.preSharedKey;
				WifiAdmin.WifiCipherType wifiType = getWifiType(wifiCfg.allowedKeyManagement);
				WifiInfoScanned wifiInfoScanned = new WifiInfoScanned(wifiName, wifiPwd, wifiType, scanResult.level);
				mWifiFree.add(wifiInfoScanned);
			} else {
				mWifiEncrypt.add(new WifiInfoScanned(scanResult.SSID, null, null, scanResult.level));
			}
		}
	}
	
	private WifiAdmin.WifiCipherType getWifiType(BitSet kmtBitSet) {
		if (kmtBitSet.equals(WifiConfiguration.KeyMgmt.NONE)) {
			return WifiAdmin.WifiCipherType.WIFICIPHER_NOPASS;
		} else if (kmtBitSet.equals(WifiConfiguration.KeyMgmt.WPA_PSK)) {
			return WifiAdmin.WifiCipherType.WIFICIPHER_WPA;
		} else {
			return WifiAdmin.WifiCipherType.WIFICIPHER_WEP;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiAdmin = WifiAdmin.getInstance(getActivity());
		mWifiAdmin.openWifi();
		mWifiAdmin.startScan();
		mWifiListScanned = mWifiAdmin.getWifiList();
		mWifiConfigurationScanned = mWifiAdmin.getConfiguration();
		setWifiData(mWifiListScanned, mWifiConfigurationScanned);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		
		mView = inflater.inflate(R.layout.fragment_wifi, null);
		mWifiListView = (ListView) mView.findViewById(R.id.wifi_list_view);
		
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiFree, mWifiEncrypt);
		mWifiListView.setAdapter(mWifiListAdapter);
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < (mWifiFree.size() + 1)) {
					WifiInfoScanned wifiSelected = mWifiFree.get(position-1);
					mWifiAdmin.Connect(wifiSelected.getWifi_name(), wifiSelected.getWifi_pwd(), WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
				}
				
			}
			
		});
		
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
