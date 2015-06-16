package com.anynet.wifiworld.wifi;

import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiSecuritiesV8.PskType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
		
		//设置其单击登录事件
//		view.findViewById(R.id.ll_wifi_content).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 弹出询问对话框
//				new AlertDialog.Builder(mContext).setTitle("是否Wi-Fi连接").setMessage("当前Wi-Fi已经认证可以安全上网！")
//							.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						WifiConfiguration cfgSelected = mWifiListItems.get(position).getWifiConfiguration();
//						if (cfgSelected != null) {
//							mWifiAdmin.connectToConfiguredNetwork(cfgSelected, true);
//						} else {
//							mWifiAdmin.connectToNewNetwork(mWifiListItems.get(position), true, false);
//						}
//						dialog.dismiss();
//					}
//				}).setNegativeButton("取消", null).show();
//			}
//		});
		
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
