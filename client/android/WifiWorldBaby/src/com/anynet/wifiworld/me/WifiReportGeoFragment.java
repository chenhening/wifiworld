package com.anynet.wifiworld.me;

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

public class WifiReportGeoFragment extends Fragment {
	private String TAG = "WifiReportGeoFragment";
	private UserLocationRadarChartView mGeoRadarChart = null;
	private ListView mlistview = null;
	private List<String> mdescriptions = new ArrayList<String>();
	private WifiDataAnalyseHelper mDataMin = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_report_geo, container, false);
        
        //display chart
        RelativeLayout chartLayout = (RelativeLayout)view.findViewById(R.id.rl_report_geo);
		//图表显示范围在占屏幕大小的90%的区域内	   
		int scrWidth = chartLayout.getLayoutParams().width; 	
		int scrHeight = chartLayout.getLayoutParams().height; 			   		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mGeoRadarChart = new UserLocationRadarChartView(getActivity());
        chartLayout.addView(mGeoRadarChart, layoutParams);
        
        //display lisview
        mlistview = (ListView) view.findViewById(R.id.lv_wifi_report_2);
        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_view_item, mdescriptions));
        mDataMin = WifiDataAnalyseHelper.getInstance(getActivity());
        mDataMin.Start(false, new DataCallback<WifiDataAnalyseHelper>() {

			@Override
            public void onSuccess(WifiDataAnalyseHelper object) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
                    public void run() {
						mGeoRadarChart.DisplayOneWeek(mDataMin.poscount, 4);
				        mdescriptions.clear();
				        mdescriptions.add("分析报告如下：");
				        mdescriptions.add("您的用户主要在东南角上网，建议您将路由器向东北角移动");
				        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), 
				        	R.layout.list_view_item, mdescriptions));
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
