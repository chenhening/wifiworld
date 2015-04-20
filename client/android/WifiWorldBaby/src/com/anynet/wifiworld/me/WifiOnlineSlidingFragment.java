package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.skyfishjy.library.RippleBackground;

public class WifiOnlineSlidingFragment extends Fragment {
	private View mRootView = null;
	private boolean bOnAnimating = false;
	private RippleBackground rippleBackground = null;
	private ImageView center_image = null;
	private TextView tv_online = null;
	private ArrayList<ImageView> mPhones = new ArrayList<ImageView>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
        rippleBackground = (RippleBackground)mRootView.findViewById(R.id.content);
        StartDisaplay();
        bOnAnimating = true;
        
        //得到wifi上传的logo信息展示
        center_image = (ImageView)mRootView.findViewById(R.id.centerImage);
        center_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SetOpenOrCloseStatus();
			}
        });
        
        tv_online = (TextView)mRootView.findViewById(R.id.tv_online_count);
        
        return mRootView;
    }
	
	private void addMarkerOnView(List<WifiDynamic> objects) {
		ArrayList<String> records = new ArrayList<String>();
		BmobGeoPoint mBmobGeoPoint = LoginHelper.getInstance(getActivity()).mWifiProfile.Geometry;
		if (mBmobGeoPoint == null) 
			return;
		
		double center_x = mBmobGeoPoint.getLatitude();
		double center_y = mBmobGeoPoint.getLongitude();
		for (int i=0; i < objects.size(); ++i) {
			WifiDynamic one = objects.get(i);
			if (records.contains(one.Userid)) {
				continue;
			}
			records.add(one.Userid);
			double distance_x = one.Geometry.getLatitude() - center_x;
			double distance_y = one.Geometry.getLongitude() - center_y;
			ImageView image = new ImageView(getActivity());
			int x = (int) (center_image.getLeft() + distance_x*1000000);
			int y = (int) (center_image.getTop() + distance_y*1000000);
			image.setImageResource(R.drawable.icon_phone);
			RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(64, 64);
			relLayoutParams.leftMargin = x;
			relLayoutParams.topMargin = y;
			rippleBackground.addView(image, relLayoutParams);
			mPhones.add(image);
		}
		
		tv_online.setText(records.size() + " 人");
	}
	
	private void StartDisaplay() {
		rippleBackground.startRippleAnimation();
		//数据库里面去查询当前正在线上的用户
        WifiDynamic record = new WifiDynamic();
        record.MacAddr = LoginHelper.getInstance(getActivity()).mWifiProfile.MacAddr;
        record.MarkLoginTime();
        record.QueryUserCurrent(getActivity(), record.LoginTime, new MultiDataCallback<WifiDynamic>() {

			@Override
			public void onSuccess(List<WifiDynamic> objects) {
				addMarkerOnView(objects);
			}

			@Override
			public void onFailed(String msg) {
				tv_online.setText("0 人");
			}
        	
        });
        
        //设置打开和关闭按钮
        mRootView.findViewById(R.id.tb_open_close_wifi).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				SetOpenOrCloseStatus();
            }
        	
        });
	}
	
	private void SetOpenOrCloseStatus() {
		if (bOnAnimating) {
			rippleBackground.stopRippleAnimation();
			tv_online.setText("0 人");
			for (int i=0; i<mPhones.size(); ++i) {
				rippleBackground.removeView(mPhones.get(i));
			}
			mPhones.clear();
		} else {
			StartDisaplay();
		}
		
		bOnAnimating = !bOnAnimating;
		
		WifiProfile wifi = new WifiProfile();
		wifi.MacAddr = LoginHelper.getInstance(getActivity()).mWifiProfile.MacAddr;
		wifi.setShared(bOnAnimating);
		wifi.StoreRemote(getActivity(), new DataCallback<WifiProfile>() {

			@Override
			public void onSuccess(WifiProfile object) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailed(String msg) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
