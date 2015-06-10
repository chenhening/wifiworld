package com.anynet.wifiworld.wifi;

import java.util.List;

import com.anynet.wifiworld.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiFreeListAdapter extends BaseAdapter {
	private final static String TAG = WifiFreeListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiListItem> mWifiListItems;
	private LayoutInflater mLayoutInflater;
	
	public WifiFreeListAdapter(Context context, List<WifiListItem> wifiListItems) {
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
		
		setItemBg(position, getCount(), view);
		
		TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
		wifiName.setText(mWifiListItems.get(position).getWifiName());
		
		return view;
	}
	
	private void setItemBg(int pos, int itemSize, View view) {
		switch (itemSize) {
		case 1:
			
			break;
		case 2:
			
			break;
		default:
			if (pos == 0) {
				view.setBackgroundResource(R.drawable.wifi_free_item0);
			} else if (pos == getCount()-1){
				view.setBackgroundResource(R.drawable.wifi_free_item2);
			} else {
				view.setBackgroundResource(R.drawable.wifi_free_item1);
			}
			break;
		}
	}
}
