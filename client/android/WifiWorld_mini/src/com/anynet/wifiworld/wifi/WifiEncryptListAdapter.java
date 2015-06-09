package com.anynet.wifiworld.wifi;

import java.util.List;

import com.anynet.wifiworld.wifi.WifiSecuritiesV8.PskType;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class WifiEncryptListAdapter extends BaseAdapter {
	private final static String TAG = WifiEncryptListAdapter.class.getSimpleName();
	
	private List<WifiListItem> mWifiListItems;
	
	public WifiEncryptListAdapter(List<WifiListItem> wifiListItems) {
		mWifiListItems = wifiListItems;
	}
	
	public void refreshWifiList(List<WifiListItem> wifiListItems) {
		mWifiListItems = wifiListItems;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mWifiListItems.size();
	}

	@Override
	public Object getItem(int pos) {
		return mWifiListItems.get(pos);
	}

	@Override
	public long getItemId(int idx) {
		return idx;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
