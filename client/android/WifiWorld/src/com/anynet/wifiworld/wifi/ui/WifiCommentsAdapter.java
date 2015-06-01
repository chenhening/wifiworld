package com.anynet.wifiworld.wifi.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import u.aly.da;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiComments;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiCommentsAdapter extends BaseAdapter {
	private final static String TAG = WifiCommentsAdapter.class.getSimpleName();
	
	private final static float ONE_SECOND = 1000.0f; //1s = 1000ms
	private final static float ONE_MINUTE = 60.0f; //1m = 60s
	private final static float ONE_HOUR = 60.0f; //1h = 60m
	private final static float ONE_DAY = 24.0f; //1d = 24h
	private final static float ONE_YEAR = 365.0f; //1y = 365d
	
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
		commentTime.setText(getTimeTag(wifiComments.SendTime));
		
		return view;
	}
	
	private String getTimeTag(long commentTime) {
		String timeTag = null;
		long deltaTime = System.currentTimeMillis() - commentTime;
		float innerMinute = deltaTime / (ONE_SECOND * ONE_MINUTE);
		float innerHour = deltaTime / (ONE_SECOND * ONE_MINUTE * ONE_HOUR);
		float innerDay = deltaTime / (ONE_SECOND * ONE_MINUTE * ONE_HOUR * ONE_DAY);
		float innerYear = deltaTime / (ONE_SECOND * ONE_MINUTE * ONE_HOUR * ONE_DAY * ONE_YEAR);
		if (innerMinute < 1.0f) {
			timeTag = "刚刚";
		} else if (innerHour < 1.0f) {
			timeTag = String.valueOf((int)(innerMinute)) + "分钟前";
		} else if (innerDay < 1.0f) {
			timeTag = String.valueOf((int)(innerHour)) + "小时前";
		} else if (innerYear < 1.0f) {
			Date date = new Date(commentTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日 HH:MM");
			timeTag = dateFormat.format(date);
		} else {
			Date date = new Date(commentTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日");
			timeTag = dateFormat.format(date);
		}
		
		return timeTag;
	}

}
