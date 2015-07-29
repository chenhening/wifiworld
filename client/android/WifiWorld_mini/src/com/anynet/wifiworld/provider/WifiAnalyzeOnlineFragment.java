package com.anynet.wifiworld.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiAnalyzeOnlineFragment extends Fragment {
	private final static String TAG = WifiAnalyzeOnlineFragment.class.getSimpleName();
	
	private View mRootView = null;
	private TextView mTvOnline = null;
	private ToggleButton mTbWifiShare = null;
	private RelativeLayout mRlOnlineContent;
	private int mCenterX, mCenterY;
	private ArrayList<ImageView> mPhones = new ArrayList<ImageView>();
	private ListView mlistview = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_provider_template, container, false);
		
		mRlOnlineContent = (RelativeLayout)mRootView.findViewById(R.id.rl_provider_display);
        mTvOnline = (TextView)mRootView.findViewById(R.id.tv_online_num);
        //center_image = (ImageView)mRootView.findViewById(R.id.centerImage);
		
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
		
        mCenterX = mRlOnlineContent.getWidth() / 2;
        mCenterY = mRlOnlineContent.getHeight() / 2;
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
			int x = (int) (mCenterX + distance_x*10000);
			int y = (int) (mCenterY + distance_y*10000);
			image.setImageResource(R.drawable.ic_location_current);
			RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(64, 64);
			relLayoutParams.leftMargin = x;
			relLayoutParams.topMargin = y;
			mRlOnlineContent.addView(image, relLayoutParams);
			mPhones.add(image);
		}
		
		mTvOnline.setText(String.valueOf(records.size()));
	}
	
	private void StartDisaplay() {
		//数据库里面去查询当前正在线上的用户
        WifiDynamic record = new WifiDynamic();
        mlistview = (ListView) mRootView.findViewById(R.id.lv_detail_list);
        final SimpleAdapter accountAdapter = new SimpleAdapter(getActivity(), getData(null), R.layout.item_wifi_comments,
        		new String[]{"content"}, new int[]{R.id.tv_detail_wifi_comments});
        mlistview.setAdapter(accountAdapter);
        record.MacAddr = LoginHelper.getInstance(getActivity()).mWifiProfile.MacAddr;
        record.MarkLoginTime();
        record.QueryUserCurrent(getActivity(), record.LoginTime, new MultiDataCallback<WifiDynamic>() {

			@Override
			public boolean onSuccess(List<WifiDynamic> objects) {
				addMarkerOnView(objects);
				//设置mOnlineAccount列表
				getData(objects);
				accountAdapter.notifyDataSetChanged();
				return false;
			}

			@Override
			public boolean onFailed(String msg) {
				if(mTvOnline != null)mTvOnline.setText("0");
				return false;
			}
        	
        });
        
        //设置打开和关闭按钮
        mTbWifiShare = (ToggleButton)mRootView.findViewById(R.id.tb_wifi_share);
        mTbWifiShare.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mTvOnline.setText("0");
					for (int i=0; i<mPhones.size(); ++i) {
						mRlOnlineContent.removeView(mPhones.get(i));
					}
					mPhones.clear();
				} else {
					StartDisaplay();
				}
				
				LoginHelper.getInstance(getActivity()).mWifiProfile.setShared(isChecked);
				LoginHelper.getInstance(getActivity()).mWifiProfile.update(getActivity());
			}
		});
	}
	
	private List<Map<String, Object>> getData(List<WifiDynamic> objects) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (objects != null) {
        	for (WifiDynamic dynamic : objects) {
        		Map<String, Object> map = new HashMap<String, Object>();
                map.put("content", dynamic.Userid);
                list.add(map);
			}
		}
        
        return list;
    }

}
