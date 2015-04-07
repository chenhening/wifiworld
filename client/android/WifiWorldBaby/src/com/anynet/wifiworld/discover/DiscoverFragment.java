package com.anynet.wifiworld.discover;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dlnetwork.Data;

public class DiscoverFragment extends MainFragment {
	private static String ADKey = "f5dca10991ec3c4e";
	private static String ADSecret = "04b094304eef23f6";
	
	private void bingdingTitleUI() {

		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mPageRoot = inflater.inflate(R.layout.fragment_discover, null);
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		
		AdManager.getInstance(getActivity()).init(ADKey, ADSecret, false);
		AdView adView = new AdView(getActivity(), AdSize.FIT_SCREEN); // 实例化广告条
		FrameLayout adLayout = (FrameLayout)findViewById(R.id.fl_ads_banner); // 获取要嵌入广告条的布局
		adLayout.addView(adView);// 将广告条加入到布局中
		
		//OffersManager.getInstance(getActivity()).showOffersWall();

        // People using
        final RelativeLayout peopleUsingLayout = (RelativeLayout) findViewById(R.id.people_using);
        peopleUsingLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.showOffers(v.getContext());
            }
        });

        // People using
        final RelativeLayout peoplePlayingLayout = (RelativeLayout) findViewById(R.id.people_playing);
        peoplePlayingLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.showOffers(v.getContext());
            }
        });

        // People using
        final RelativeLayout peopleWatchingLayout = (RelativeLayout) findViewById(R.id.people_watching);
        peopleWatchingLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.showOffers(v.getContext());
            }
        });

        // People using
        final RelativeLayout peopleBuyingLayout = (RelativeLayout) findViewById(R.id.people_buying);
        peopleBuyingLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.showOffers(v.getContext());
            }
        });

		return mPageRoot;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
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
