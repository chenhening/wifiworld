package com.anynet.wifiworld.wifi;

import java.util.List;

import com.anynet.wifiworld.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) public class WifiPeerListAdapter extends BaseAdapter {
	private final static String TAG = WifiPeerListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<WifiP2pDevice> mWifiP2pDevices;
	
	public WifiPeerListAdapter(Context context, List<WifiP2pDevice> list) {
		mContext = context;
		mWifiP2pDevices = list;
	}

	public void refreshPeerList(List<WifiP2pDevice> peerList) {
		mWifiP2pDevices = peerList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mWifiP2pDevices.size();
	}

	@Override
	public Object getItem(int index) {
		return mWifiP2pDevices.get(index);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view = contentView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.wifi_item, null);
		}
		
		TextView alias = (TextView) view.findViewById(R.id.wifi_alias);
		ImageView icon = (ImageView) view.findViewById(R.id.wifi_icon);
		TextView name = (TextView) view.findViewById(R.id.wifi_name);
		TextView remark = (TextView) view.findViewById(R.id.wifi_remark);
		ImageView signalIcon = (ImageView) view.findViewById(R.id.wifi_status_icon);
		TextView signalLevel = (TextView) view.findViewById(R.id.wifi_status_digit);
		Button wifiDetails = (Button) view.findViewById(R.id.wifi_details_btn);
		
		alias.setVisibility(View.GONE);
		signalIcon.setVisibility(View.GONE);
		signalLevel.setVisibility(View.GONE);
		wifiDetails.setVisibility(View.GONE);
		
		icon.setImageResource(R.drawable.icon_invalid);
		name.setText(mWifiP2pDevices.get(position).deviceName);
		remark.setText(mWifiP2pDevices.get(position).deviceAddress);
		
		return view;
	}

}
