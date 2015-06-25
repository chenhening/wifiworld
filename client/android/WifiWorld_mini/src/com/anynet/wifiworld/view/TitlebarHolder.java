package com.anynet.wifiworld.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;

public class TitlebarHolder {

	/**
	 * 头部资源
	 */
	public View llHeaderLeft;
	public ImageView ivHeaderLeft;
	public TextView tvHeaderLeft;
	public TextView tvTitle;
	public RelativeLayout llFinish;
	public TextView tvHeaderRight;
	private BaseActivity activity;
	
	public ImageView ivMySetting;

	public TitlebarHolder(BaseActivity a) {
		activity = a;
	}

	public TitlebarHolder(View v) {
	}
}
