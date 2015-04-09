package com.anynet.wifiworld.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;

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
	
	//public LinearLayout llHeaderMy;
	public ImageView ivMySetting;

	public TitlebarHolder(BaseActivity a) {
		this.activity = a;
		llHeaderLeft = activity.findViewById(R.id.ll_setting_header_left);
		ivHeaderLeft = (ImageView) activity.findViewById(R.id.iv_setting_header_left);
		tvHeaderLeft = (TextView) activity.findViewById(R.id.tv_setting_header_left);
		tvTitle = (TextView) activity.findViewById(R.id.setting_main_title);
		llFinish = (RelativeLayout) activity.findViewById(R.id.setting_header_finish);
		tvHeaderRight = (TextView) activity.findViewById(R.id.tv_header_right);
		
		//llHeaderMy = (LinearLayout) activity.findViewById(R.id.ll_header_my);
		ivMySetting = (ImageView) activity.findViewById(R.id.lv_my_setting_image);
		
		if (null != llHeaderLeft) {
			llHeaderLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
		}

//		if (null != tvHeaderRight) {
//		    tvHeaderRight.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    activity.finish();
//                }
//            });
//        }
	}

	public TitlebarHolder(View v) {
		llHeaderLeft = v.findViewById(R.id.ll_setting_header_left);
		ivHeaderLeft = (ImageView) v.findViewById(R.id.iv_setting_header_left);
		tvHeaderLeft = (TextView) v.findViewById(R.id.tv_setting_header_left);
		tvTitle = (TextView) v.findViewById(R.id.setting_main_title);
		llFinish = (RelativeLayout) v.findViewById(R.id.setting_header_finish);
		tvHeaderRight = (TextView) v.findViewById(R.id.tv_header_right);
		ivMySetting = (ImageView) v.findViewById(R.id.lv_my_setting_image);
		//llHeaderMy=(LinearLayout) v.findViewById(R.id.ll_header_my);

	}
}
