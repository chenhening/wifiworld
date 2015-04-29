package com.anynet.wifiworld.wifi.ui;

import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiComments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiCommentsAdapter extends BaseAdapter {
	private final static String TAG = WifiCommentsAdapter.class.getSimpleName();
	
	private List<WifiComments> mWifiCommentsList;
	private Context mContext;
	
	public WifiCommentsAdapter(Context context, List<WifiComments> wifiComments) {
		mContext = context;
		mWifiCommentsList = wifiComments;
	}

	public void refreshCommentsList() {
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mWifiCommentsList.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return mWifiCommentsList.get(index);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view = contentView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.wifi_comment_item, null);
		}
		
		ImageView userIcon = (ImageView)view.findViewById(R.id.user_icon);
		TextView userName = (TextView)view.findViewById(R.id.user_name);
		TextView commentString = (TextView)view.findViewById(R.id.wifi_comment);
		TextView commentTime = (TextView)view.findViewById(R.id.comment_time);
		
		WifiComments wifiComments = mWifiCommentsList.get(position);
		commentString.setText(wifiComments.Comment);
		userName.setText(wifiComments.UserId);
		
		return view;
	}

}
