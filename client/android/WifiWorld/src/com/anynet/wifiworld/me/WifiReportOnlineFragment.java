package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class WifiReportOnlineFragment extends Fragment {
	private View mRootView = null;
	private boolean bOnAnimating = false;
	private RippleBackground rippleBackground = null;
	private ImageView center_image = null;
	private TextView tv_online = null;
	private ArrayList<ImageView> mPhones = new ArrayList<ImageView>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_report_online, container, false);
        rippleBackground = (RippleBackground)mRootView.findViewById(R.id.content);        
        bOnAnimating = true;
        
        //得到wifi上传的logo信息展示
        tv_online = (TextView)mRootView.findViewById(R.id.tv_online_count);
        center_image = (ImageView)mRootView.findViewById(R.id.centerImage);
        /*center_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SetOpenOrCloseStatus();
			}
        });*/
        StartDisaplay();
        return mRootView;
    }
	
	private void addMarkerOnView(List<WifiDynamic> objects) {
		ArrayList<String> records = new ArrayList<String>();
		if (LoginHelper.getInstance(getActivity()).mWifiProfile == null)
			return;
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
			public boolean onSuccess(List<WifiDynamic> objects) {
				addMarkerOnView(objects);
				return false;
			}

			@Override
			public boolean onFailed(String msg) {
				if(tv_online != null)tv_online.setText("0 人");
				return false;
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
		
		LoginHelper.getInstance(getActivity()).mWifiProfile.setShared(bOnAnimating);
		LoginHelper.getInstance(getActivity()).mWifiProfile.update(getActivity());
	}
}
