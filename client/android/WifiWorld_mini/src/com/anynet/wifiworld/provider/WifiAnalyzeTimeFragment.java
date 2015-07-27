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
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiDataAnalyseHelper;

public class WifiAnalyzeTimeFragment extends Fragment {
	private String TAG = "WifiReportSlidingFragment";
	private UserTimerRadarChartView mTimerRadarChart = null;
	private ListView mlistview = null;
	private List<String> mdescriptions = new ArrayList<String>();
	private WifiDataAnalyseHelper mDataMin = null;
	
	private void customView(View view) {
		TextView titleTV = (TextView)view.findViewById(R.id.tv_provider_title);
		titleTV.setText("使用时间段统计：");
		view.findViewById(R.id.tv_online_num).setVisibility(View.GONE);
		view.findViewById(R.id.iv_online_bg).setVisibility(View.GONE);
		TextView shareTV = (TextView)view.findViewById(R.id.tv_wifi_share);
		shareTV.setText("总结如下：");
		view.findViewById(R.id.tb_wifi_share).setVisibility(View.GONE);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    	Bundle savedInstanceState) {
		setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_provider_template, container, false);
        customView(view);
        //display chart
        RelativeLayout chartLayout = (RelativeLayout)view.findViewById(R.id.rl_provider_display);
		//图表显示范围在占屏幕大小的90%的区域内	   
		int scrWidth = chartLayout.getLayoutParams().width; 	
		int scrHeight = chartLayout.getLayoutParams().height; 			   		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mTimerRadarChart = new UserTimerRadarChartView(getActivity());
        chartLayout.addView(mTimerRadarChart, layoutParams);

        //display lisview
//        mlistview = (ListView) view.findViewById(R.id.lv_detail_list);
//        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_wifi_comments, mdescriptions));
        mDataMin = WifiDataAnalyseHelper.getInstance(getActivity());
        mDataMin.Start(false, new DataCallback<WifiDataAnalyseHelper>() {

			@Override
            public void onSuccess(WifiDataAnalyseHelper object) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
                    public void run() {
						mTimerRadarChart.DisplayOneWeek(mDataMin.timecount, 5);
//				        mdescriptions.clear();
//				        mdescriptions.add("分析报告如下：");
//				        mdescriptions.add("您的用户主要在18-24上网，建议您在这些时段开放网络");
//				        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), 
//				        	R.layout.item_wifi_comments, mdescriptions));
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
