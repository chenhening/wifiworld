package com.anynet.wifiworld.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;

public class WifiListMapAdapter extends BaseAdapter {
	private final static String TAG = WifiListMapAdapter.class.getSimpleName();

	private List<WifiProfile> mWifiList = new ArrayList<WifiProfile>();
	private Context context;

	public Context getContext() {
		return context;
	}

	public WifiListMapAdapter(Context context, List<WifiProfile> wifiList) {
		super();
		this.context = context;
		mWifiList = wifiList;
	}

	public void setData(List<WifiProfile> data) {
		mWifiList = data;
	}

	@Override
	public int getCount() {
		return mWifiList.size();
	}

	@Override
	public Object getItem(int pos) {
		return (mWifiList != null && mWifiList.size() > 0) ? mWifiList.get(pos) : null;
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
		ViewHolder vh = null;
		if (view == null) {
			vh = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_wifi_map, null);
			vh.name = (TextView) view.findViewById(R.id.wifi_name);
			vh.remark = (TextView) view.findViewById(R.id.wifi_remark);
			vh.icon = (ImageView) view.findViewById(R.id.wifi_icon);
			vh.dis_digit = (TextView) view.findViewById(R.id.wifi_dis_digit);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}

		WifiProfile wifiProfile = (WifiProfile) getItem(position);

		vh.name.setText(wifiProfile.Ssid + "|" + wifiProfile.Alias);

		if (wifiProfile.ExtAddress != null) {
			vh.remark.setText(wifiProfile.ExtAddress);
		} else {
			vh.remark.setVisibility(View.GONE);
		}

		//vh.icon.setImageResource(R.drawable.icon_invalid);

		// int wifiDistance = (infoScanned).mWifiDistance;
		// distanceView.setText(String.valueOf(wifiDistance));
		vh.dis_digit.setVisibility(View.GONE);

		return view;
	}

	public final class ViewHolder {
		public TextView name;
		public TextView remark;
		public ImageView icon;
		public TextView dis_digit;
	}
}
