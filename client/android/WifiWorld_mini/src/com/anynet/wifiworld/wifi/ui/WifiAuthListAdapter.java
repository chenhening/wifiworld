package com.anynet.wifiworld.wifi.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.wifi.WifiListItem;

public class WifiAuthListAdapter extends BaseAdapter {
	private final static String TAG = WifiAuthListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiListItem> mWifiListItems;
	private LayoutInflater mLayoutInflater;
	
	public WifiAuthListAdapter(Context context, List<WifiListItem> wifiListItems) {
		mContext = context;
		mWifiListItems = wifiListItems;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void refreshWifiList(List<WifiListItem> wifiListItems) {
		mWifiListItems = wifiListItems;
		int size = 2-mWifiListItems.size();
		for (int i=0; i<size; ++i) {
			mWifiListItems.add(new WifiListItem());
		}
		
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
			view = mLayoutInflater.inflate(R.layout.item_wifi_list, null);
		}
		
		TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
		TextView wifiAlias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
		TextView wifiOptions = (TextView)view.findViewById(R.id.tv_wifi_free_item_options);
		ImageView logo = (ImageView)view.findViewById(R.id.iv_wifi_item_logo);
		
		setItemBg(position, getCount(), view.findViewById(R.id.ll_wifi_listitem));
		final WifiListItem wifiListItem = mWifiListItems.get(position);
		if (wifiListItem.isAuthWifi()) {
			wifiName.setText(wifiListItem.getAlias());
			wifiAlias.setVisibility(View.VISIBLE);
			wifiAlias.setText("[" + wifiListItem.getWifiName() + "]");
			wifiOptions.setText(wifiListItem.getOptions());
			//logo
			Bitmap bitmap = wifiListItem.getLogo();
			if (bitmap != null)
				logo.setImageBitmap(bitmap);
			//detail
			view.findViewById(R.id.iv_wifi_item_more).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(mContext, WifiDetailsActivity.class);
					Bundle wifiData = new Bundle();
					wifiData.putSerializable(WifiProfile.TAG, wifiListItem.getWifiProfile());
					i.putExtras(wifiData);
					mContext.startActivity(i);
				}
				
			});
		} else {
			logo.setImageResource(R.drawable.ic_wifi_connecting_3);
			wifiAlias.setVisibility(View.INVISIBLE);
			if (position == 0) {
				wifiName.setText("如何认证网络");
				wifiOptions.setText("[点击了解]");
			} else {
				wifiName.setText("什么是认证网络");
				wifiOptions.setText("[点击了解]");
			}
		}

		return view;
	}
	
	private void setItemBg(int pos, int itemSize, View view) {
		switch (itemSize) {
		case 1:
			
			break;
		case 2:
			if (pos == 0) {
				view.setBackgroundResource(R.drawable.wifi_list_auth_item0);
			} else {
				view.setBackgroundResource(R.drawable.wifi_list_auth_item2);
			}
			break;
		default:
			if (pos == 0) {
				view.setBackgroundResource(R.drawable.wifi_list_auth_item0);
			} else if (pos == getCount()-1){
				view.setBackgroundResource(R.drawable.wifi_list_auth_item2);
			} else {
				view.setBackgroundResource(R.drawable.wifi_list_auth_item1);
			}
			break;
		}
	}
}
