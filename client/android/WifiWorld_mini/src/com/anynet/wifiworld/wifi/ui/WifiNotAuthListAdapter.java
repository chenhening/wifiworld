package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiListItem;

public class WifiNotAuthListAdapter extends BaseAdapter {
	public final static String TAG = WifiNotAuthListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiListItem> mWifiListItems;
	private LayoutInflater mLayoutInflater;
	
	public WifiNotAuthListAdapter(Context context, List<WifiListItem> wifiListItems) {
		mContext = context;
		mWifiListItems = wifiListItems;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		WifiAdmin.getInstance(mContext);
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
			view = mLayoutInflater.inflate(R.layout.item_wifi_list, null);
		}
		
		TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
		TextView wifiAlias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
		TextView wifiOptions = (TextView)view.findViewById(R.id.tv_wifi_free_item_options);
		
		final WifiListItem wifiListItem = mWifiListItems.get(position);
		wifiAlias.setVisibility(View.GONE);
		wifiName.setText(wifiListItem.getWifiName());
		wifiOptions.setText(wifiListItem.getOptions());
		
		//根据wifi信号强度显示不同的信号图
		ImageView logo = (ImageView)view.findViewById(R.id.iv_wifi_item_logo);
		logo.setImageResource(wifiListItem.getDefaultLogo());
		
		setItemBg(position, getCount(), view.findViewById(R.id.ll_wifi_listitem));
		
		//设置非认证的详细页面
		view.findViewById(R.id.iv_wifi_item_more).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, WifiDetailsActivity.class);
				List<String> data = new ArrayList<String>();
				data.add(wifiListItem.getWifiName());
				data.add(wifiListItem.getWifiMac());
				i.putStringArrayListExtra(WifiNotAuthListAdapter.TAG, (ArrayList<String>) data);
				mContext.startActivity(i);
			}
			
		});
		
		return view;
	}

	private void setItemBg(int pos, int itemSize, View view) {
		LinearLayout bg_logo = (LinearLayout)view.findViewById(R.id.ll_wifi_auth_item);
		bg_logo.setBackgroundResource(0);
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
