package com.anynet.wifiworld.ui.main;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListAdapter;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
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
	private Handler mWifiUpdatorHanlder = new Handler();
	private Runnable mWifiUpdatorRunnable = null;
	
	public void setWifiData(List<ScanResult> wifiList, List<WifiConfiguration> wificonfiguration) {
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult scanResult = wifiList.get(i);
			WifiConfiguration wifiCfg = mWifiAdmin.isExsits(scanResult.SSID);
			if (wifiCfg != null) {
				String wifiName = scanResult.SSID;
				String wifiPwd = wifiCfg.preSharedKey;
				WifiAdmin.WifiCipherType wifiType = mWifiAdmin.getWifiType(wifiCfg.allowedKeyManagement);
				WifiInfoScanned wifiInfoScanned = new WifiInfoScanned(wifiName, wifiPwd, wifiType, scanResult.level+150);
				mWifiFree.add(wifiInfoScanned);
			} else {
				mWifiEncrypt.add(new WifiInfoScanned(scanResult.SSID, null, null, scanResult.level+150));
			}
		}
		
		Collections.sort(mWifiFree, new SortBySignalStrength());
		Collections.sort(mWifiEncrypt, new SortBySignalStrength());
	}
	
	private class SortBySignalStrength implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			WifiInfoScanned wifi0 = (WifiInfoScanned)arg0;
			WifiInfoScanned wifi1 = (WifiInfoScanned)arg1;
			if (wifi0.getWifi_level() > wifi1.getWifi_level()) {
				return 1;
			}
			
			return 0;
		}
		
	}
	
	private void scanWifi() {
		mWifiAdmin = WifiAdmin.getInstance(getActivity());
		mWifiAdmin.openWifi();
		mWifiAdmin.startScan();
		mWifiListScanned = mWifiAdmin.getWifiList();
		mWifiConfigurationScanned = mWifiAdmin.getConfiguration();
		setWifiData(mWifiListScanned, mWifiConfigurationScanned);
	}
	
	private boolean rmWifiConnected(String wifiName, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		for (Iterator<WifiInfoScanned> it = wifiFree.iterator(); it.hasNext(); ) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifi_name())) {
				it.remove();
				return true;
			}
		}
		
		for (Iterator<WifiInfoScanned> it = wifiEncrypt.iterator(); it.hasNext(); ) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifi_name())) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.scanWifi();
		//updateWifiList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		
		mView = inflater.inflate(R.layout.fragment_wifi, null);
		
		String wifiConnected = mWifiAdmin.getSSID();
		if (!wifiConnected.equals("")) {
			TextView wifi_connected = (TextView)mView.findViewById(R.id.wifi_name);
			wifi_connected.setText("已连接" + wifiConnected.substring(1, wifiConnected.length()-1));
			wifi_connected.setTextColor(Color.BLACK);
			this.rmWifiConnected(wifiConnected.substring(1, wifiConnected.length()-1), mWifiFree, mWifiEncrypt);
		}
		
		mWifiListView = (ListView) mView.findViewById(R.id.wifi_list_view);
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiFree, mWifiEncrypt);
		mWifiListView.setAdapter(mWifiListAdapter);
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < (mWifiFree.size() + 1)) {
					WifiInfoScanned wifiSelected = mWifiFree.get(position-1);
					mWifiAdmin.Connect(wifiSelected.getWifi_name(), wifiSelected.getWifi_pwd(), WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
					
					mWifiFree.remove(position-1);
					mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
					TextView wifi_connected = (TextView)mView.findViewById(R.id.wifi_name);
					String wifiConnected = mWifiAdmin.getSSID();
					while (wifiConnected == "") {
						wifiConnected = mWifiAdmin.getSSID();
					}
					wifi_connected.setText("已连接" + wifiConnected.substring(1, wifiConnected.length()-1));
					wifi_connected.setTextColor(Color.BLACK);
					wifi_connected.refreshDrawableState();
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
	
	private void updateWifiList() {
		if (mWifiUpdatorRunnable == null) {
			mWifiUpdatorRunnable = new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.currentThread();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					scanWifi();
					mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
					mWifiUpdatorHanlder.postDelayed(this, 100);
				}
			};
		}
		mWifiUpdatorHanlder.post(mWifiUpdatorRunnable);
	}
	
}
