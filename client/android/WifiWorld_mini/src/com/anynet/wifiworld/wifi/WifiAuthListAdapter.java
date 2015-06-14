package com.anynet.wifiworld.wifi;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
			view = mLayoutInflater.inflate(R.layout.wifi_list_item, null);
		}
		
		setItemBg(position, getCount(), view);
		
		if (mWifiListItems.get(position).getFlag()) {
			TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
			wifiName.setText(mWifiListItems.get(position).getAlias());
			TextView wifialias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
			wifialias.setText("[" + mWifiListItems.get(position).getWifiName() + "]");
			TextView wifioptions = (TextView)view.findViewById(R.id.tv_wifi_free_item_options);
			wifioptions.setText("已认证, 安全, 可免费上网");
			//设置logo
			ImageView logo = (ImageView)view.findViewById(R.id.iv_wifi_item_logo);
			Bitmap bitmap = mWifiListItems.get(position).getLogo();
			if (bitmap != null)
				logo.setImageBitmap(bitmap);
			
			//设置其单击登录事件
			view.findViewById(R.id.ll_wifi_content).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 弹出询问对话框
					new AlertDialog.Builder(mContext).setTitle("是否Wi-Fi连接").setMessage("当前Wi-Fi已经认证可以安全上网！")
								.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							WifiConfiguration cfgSelected = mWifiListItems.get(position).getWifiConfiguration();
							if (cfgSelected != null) {
								mWifiAdmin.connectToConfiguredNetwork(cfgSelected, true);
							} else {
								mWifiAdmin.connectToNewNetwork(mWifiListItems.get(position), true, false);
							}
							dialog.dismiss();
						}
					}).setNegativeButton("取消", null).show();
				}	
			});
		} else {
			if (position == 0) {
				TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
				wifiName.setText("未找到认证网络");
				TextView wifialias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
				wifialias.setText("[点击了解]");
			} else {
				TextView wifiName = (TextView)view.findViewById(R.id.tv_wifi_free_item_name);
				wifiName.setText("什么是认证网络");
				TextView wifialias = (TextView)view.findViewById(R.id.tv_wifi_free_item_alias);
				wifialias.setText("[点击了解]");
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
