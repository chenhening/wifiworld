package com.anynet.wifiworld.wifi;

import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiSecuritiesV8.PskType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiNotAuthListAdapter extends BaseAdapter {
	private final static String TAG = WifiNotAuthListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiListItem> mWifiListItems;
	private LayoutInflater mLayoutInflater;
	
	public WifiNotAuthListAdapter(Context context, List<WifiListItem> wifiListItems) {
		mContext = context;
		mWifiListItems = wifiListItems;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mLayoutInflater.inflate(R.layout.wifi_list_item, null);
		}
		
		setItemBg(position, view);
		
		TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
		wifiName.setText(mWifiListItems.get(position).getWifiName());
		TextView wifialias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
		wifialias.setVisibility(View.GONE);
		TextView wifioptions = (TextView)view.findViewById(R.id.tv_wifi_free_item_options);
		wifioptions.setText("未认证，需要密码");
		
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
