package com.anynet.wifiworld.ui.main;

import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiInfoScaned;
import com.anynet.wifiworld.wifi.WifiListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WifiFragment extends Fragment {
	private final static String TAG = "WifiFragment";
	
	private View mView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScaned> mWifiList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		mView = inflater.inflate(R.layout.fragment_wifi, null);
		
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), R.layout.wifi_item, mWifiList);
		
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
