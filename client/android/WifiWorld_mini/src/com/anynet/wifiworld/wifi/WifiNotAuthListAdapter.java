package com.anynet.wifiworld.wifi;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anynet.wifiworld.R;

public class WifiNotAuthListAdapter extends BaseAdapter {
	private final static String TAG = WifiNotAuthListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiListItem> mWifiListItems;
	private LayoutInflater mLayoutInflater;
	private WifiAdmin mWifiAdmin;
	
	public WifiNotAuthListAdapter(Context context, List<WifiListItem> wifiListItems) {
		mContext = context;
		mWifiListItems = wifiListItems;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWifiAdmin = WifiAdmin.getInstance(mContext);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mLayoutInflater.inflate(R.layout.wifi_list_item, null);
		}
		
		TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
		TextView wifiAlias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
		TextView wifiOptions = (TextView)view.findViewById(R.id.tv_wifi_free_item_options);
		
		WifiListItem wifiListItem = mWifiListItems.get(position);
		wifiAlias.setVisibility(View.GONE);
		wifiName.setText(wifiListItem.getWifiName());
		wifiOptions.setText(wifiListItem.getOptions());
		
		setItemBg(position, view);
		
		return view;
	}

	private void setItemBg(int pos, View view) {
		if (pos == 0) {
			view.setBackgroundResource(R.drawable.wifi_lock_item0);
		} else {
			view.setBackgroundResource(R.drawable.wifi_lock_item1);
		}
	}
}
