package com.anynet.wifiworld.wifi;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anynet.wifiworld.R;

public class WifiAuthListAdapter extends BaseAdapter {
	private final static String TAG = WifiAuthListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiListItem> mWifiListItems;
	private LayoutInflater mLayoutInflater;
	private WifiAdmin mWifiAdmin;
	
	public WifiAuthListAdapter(Context context, List<WifiListItem> wifiListItems) {
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
		
		setItemBg(position, getCount(), view);
		
		TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
		wifiName.setText(mWifiListItems.get(position).getAlias());
		TextView wifialias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
		wifialias.setText("[" + mWifiListItems.get(position).getWifiName() + "]");
		TextView wifioptions = (TextView)view.findViewById(R.id.tv_wifi_free_item_options);
		wifioptions.setText("已认证, 安全, 可免费上网");
		
		//设置其单击登录事件
		view.findViewById(R.id.ll_wifi_content).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出询问对话框
				new AlertDialog.Builder(mContext).setTitle("Wi-Fi连接").setMessage("当前Wi-Fi已经认证可以安全上网！")
							.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						WifiBRService.setWifiSupplicant(true);

						boolean connResult = false;
						WifiConfiguration cfgSelected = mWifiListItems.get(position).getWifiConfiguration();
						if (cfgSelected != null) {
							connResult = mWifiAdmin.connectToConfiguredNetwork(cfgSelected, true);
						} else {
							connResult = mWifiAdmin.connectToNewNetwork(mWifiListItems.get(position), true, false);
						}
						dialog.dismiss();
					}
				}).setNegativeButton("取消", null).show();
			}	
		});
		
		return view;
	}
	
	private void setItemBg(int pos, int itemSize, View view) {
		switch (itemSize) {
		case 1:
			
			break;
		case 2:
			if (pos == 0) {
				view.setBackgroundResource(R.drawable.wifi_free_item0);
			} else {
				view.setBackgroundResource(R.drawable.wifi_free_item2);
			}
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
