package com.anynet.wifiworld.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;

public class TitlebarHolder {

	/**
	 * 头部资源
	 */
	public ImageView ivHeaderLeft;
	public TextView tvHeaderLeft;
	public RelativeLayout rlHeaderLeft;
	public TextView tvTitle;
	public ImageView ivHeaderRight;
	public TextView tvHeaderRight;
	public RelativeLayout rlHeaderRight;
	
	private BaseActivity activity;
	
	public TitlebarHolder(BaseActivity a) {
		activity = a;
		ivHeaderLeft = (ImageView) activity.findViewById(R.id.iv_setting_header_left);
		tvHeaderLeft = (TextView) activity.findViewById(R.id.tv_setting_header_left);
		tvTitle = (TextView) activity.findViewById(R.id.setting_main_title);
		ivHeaderRight = (ImageView) activity.findViewById(R.id.iv_setting_header_right);
		tvHeaderRight = (TextView) activity.findViewById(R.id.tv_setting_header_right);
		if (ivHeaderLeft != null) {
			ivHeaderLeft.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
		}
		if (tvHeaderLeft != null) {
			tvHeaderLeft.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
		}
	}

	public TitlebarHolder(View v) {
		ivHeaderLeft = (ImageView) v.findViewById(R.id.iv_setting_header_left);
		tvTitle = (TextView) v.findViewById(R.id.setting_main_title);
		ivHeaderRight = (ImageView) v.findViewById(R.id.iv_setting_header_right);
		
		if (ivHeaderLeft != null) {
			ivHeaderLeft.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
		}
	}
}
