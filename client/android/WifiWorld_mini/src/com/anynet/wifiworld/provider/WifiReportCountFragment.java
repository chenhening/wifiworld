package com.anynet.wifiworld.provider;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiDataAnalyseHelper;

public class WifiReportCountFragment extends Fragment {
	private String TAG = "WifiReportCountFragment";
	private WifiProviderBarChartView mLineChart = null;
	private ListView mlistview = null;
	private List<String> mdescriptions = new ArrayList<String>();
	private WifiDataAnalyseHelper mDataMin = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_report_count, container, false);
        
        //display chart
        RelativeLayout chartLayout = (RelativeLayout)view.findViewById(R.id.rl_report_count);
		//图表显示范围在占屏幕大小的90%的区域内	   
		int scrWidth = chartLayout.getLayoutParams().width; 	
		int scrHeight = chartLayout.getLayoutParams().height; 			   		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mLineChart = new WifiProviderBarChartView(getActivity());
        chartLayout.addView(mLineChart, layoutParams);

        //display lisview
        mlistview = (ListView) view.findViewById(R.id.lv_wifi_report_2);
        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_wifi_comments, mdescriptions));
        mDataMin = WifiDataAnalyseHelper.getInstance(getActivity());
        mDataMin.Start(false, new DataCallback<WifiDataAnalyseHelper>() {

			@Override
            public void onSuccess(WifiDataAnalyseHelper object) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
                    public void run() {
						mLineChart.DisplayOneWeek(mDataMin.headcount, 7);
				        mdescriptions.clear();
				        mdescriptions.add("分析报告如下：");
				        mdescriptions.add("您的WiFi一周总共开放了：    小时");
				        mdescriptions.add("您的WiFi一周总共被使用了：    小时");
				        mdescriptions.add("您的网络主要在周末两天被频繁使用，建议在这些时间开放。");
				        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), 
				        	R.layout.item_wifi_comments, mdescriptions));
                    }
					
				});
			}

			@Override
            public void onFailed(String msg) {
            }
        	
        });
        
        return view;
    }
}
