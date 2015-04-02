package com.anynet.wifiworld.map;
import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiType;
import com.anynet.wifiworld.wifi.WifiInfoScanned;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class WifiListMapAdapter extends BaseAdapter {
	private final static String TAG = WifiListMapAdapter.class.getSimpleName();

	private List<WifiInfoScanned> mWifiList = new ArrayList<WifiInfoScanned>();
	private Context context;

	public WifiListMapAdapter(Context context, List<WifiInfoScanned> wifiList) {
		super();
		this.context = context;
		mWifiList = wifiList;
	}
	
	@Override
	public int getCount() {
		return mWifiList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mWifiList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	@Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view != null) {
			return view;
		}
		final WifiInfoScanned infoScanned = (WifiInfoScanned)getItem(position);
		view = LayoutInflater.from(this.context).inflate(R.layout.wifi_item_map, null);
	    TextView textView = (TextView) view.findViewById(R.id.wifi_name);
		textView.setText((infoScanned).getWifiName());
		
		TextView remarkText = (TextView) view.findViewById(R.id.wifi_remark);
		if ((infoScanned).getRemark() != null) {
			remarkText.setText((infoScanned).getRemark());
		} else {
			remarkText.setVisibility(View.GONE);
		}
		
		ImageView imageView = (ImageView)view.findViewById(R.id.wifi_icon);
		imageView.setImageResource(R.drawable.id_default);
	    
	    int wifiDistance = (infoScanned).mWifiDistance;
	    TextView distanceView = (TextView) view.findViewById(R.id.wifi_dis_digit);
	    distanceView.setText(String.valueOf(wifiDistance));
	    
        return view;
	}

}
