package com.anynet.wifiworld.wifi.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anynet.wifiworld.R;

public class WifiCommentsListAdapter extends BaseAdapter {
	private final static String TAG = WifiCommentsListAdapter.class.getSimpleName();
	
	private Context mContext;
	private List<String> mWifiComments;
	private LayoutInflater mLayoutInflater;
	
	public WifiCommentsListAdapter(Context context, List<String> comments) {
		mContext = context;
		mWifiComments = comments;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mWifiComments.size();
	}

	@Override
	public Object getItem(int pos) {
		return mWifiComments.get(pos);
	}

	@Override
	public long getItemId(int idx) {
		return idx;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_wifi_comments, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 
		holder.mComments.setText(mWifiComments.get(position));
		return convertView;
	}
	
	class ViewHolder {
		private TextView mComments;
		
		public ViewHolder(View view) {
			view.setTag(this);
			mComments = (TextView)view.findViewById(R.id.tv_detail_wifi_comments);
		}
	}
}
