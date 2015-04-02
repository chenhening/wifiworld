package com.anynet.wifiworld.me;

import java.util.List;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.util.LoginHelper;
import com.skyfishjy.library.RippleBackground;

public class WifiOnlineSlidingFragment extends Fragment {
	private boolean bOnAnimating = false;
	private RippleBackground rippleBackground = null;
	private ImageView center_image = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
        rippleBackground = (RippleBackground)view.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        bOnAnimating = true;
        
        //得到wifi上传的logo信息展示
        center_image = (ImageView)view.findViewById(R.id.centerImage);
        Bitmap bitmap = null;//LoginHelper.getInstance(getActivity()).mWifiProfile.Logo;
        if (bitmap == null) {//如果logo设置为空，那么采用统一的默认图片
        	center_image.setImageResource(R.drawable.ic_launcher);
        } else {
        	center_image.setImageBitmap(bitmap);
        }
        center_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bOnAnimating) {
					rippleBackground.stopRippleAnimation();
				} else {
					rippleBackground.startRippleAnimation();
				}
				bOnAnimating = !bOnAnimating;
			}
        });
        
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
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        return view;
    }
	
	private void addMarkerOnView(List<WifiDynamic> objects) {
		BmobGeoPoint mBmobGeoPoint = LoginHelper.getInstance(getActivity()).mWifiProfile.Geometry;
		double center_x = mBmobGeoPoint.getLatitude();
		double center_y = mBmobGeoPoint.getLongitude();
		for (int i=0; i < objects.size(); ++i) {
			WifiDynamic one = objects.get(i);
			double distance_x = one.Geometry.getLatitude() - center_x;
			double distance_y = one.Geometry.getLongitude() - center_y;
			ImageView image = new ImageView(getActivity());
			image.setX(center_image.getX() + (float) distance_x);
			image.setY(center_image.getY() + (float) distance_y);
			image.setImageResource(R.drawable.ic_launcher);
		}
	}
}
