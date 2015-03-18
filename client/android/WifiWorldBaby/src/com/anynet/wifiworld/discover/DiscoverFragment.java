package com.anynet.wifiworld.discover;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.youmi.android.AdManager;
import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

public class DiscoverFragment extends MainFragment {
	private static String ADKey = "f5dca10991ec3c4e";
	private static String ADSecret = "04b094304eef23f6";
	
	private void bingdingTitleUI() {

		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mPageRoot = inflater.inflate(R.layout.fragment_find, null);
		super.onCreateView(inflater, container, savedInstanceState);
		//bingdingTitleUI();
		/*OffersManager.getInstance(getActivity()).showOffersWall();
		// 自 Youmi Android OfferWall SDK v5.0.0 起, 支持全屏积分墙退出监听回调
		OffersManager.getInstance(getActivity()).showOffersWall(
			new Interface_ActivityListener() {

			@Override
            public void onActivityDestroy(Context arg0) {
	            showToast("广告平台积分墙展示成功。");
            }
			
		});*/
		
		return mPageRoot;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//AdManager.getInstance(getActivity()).init(ADKey, ADSecret, false);
		//OffersManager.getInstance(getActivity()).onAppLaunch();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		//OffersManager.getInstance(getActivity()).onAppExit();
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
}
