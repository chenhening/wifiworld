package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiReportSlidingFragment extends Fragment {
	private String TAG = "WifiReportSlidingFragment";
	private WifiProviderBarChartView mLineChart = null;
	private UserLocationRadarChartView mPosRadarChart = null;
	private UserTimerRadarChartView mTimerRadarChart = null;
	private ListView mlistview = null;
	private List<String> mdescriptions = new ArrayList<String>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		setRetainInstance(true);

        View view = inflater.inflate(R.layout.sliding_fragment_layout_right, container, false);
        
        //display chart
        RelativeLayout chartLayout = (RelativeLayout)view.findViewById(R.id.rl_wifi_provider_line_chart);
		//图表显示范围在占屏幕大小的90%的区域内	   
		int scrWidth = chartLayout.getLayoutParams().width; 	
		int scrHeight = chartLayout.getLayoutParams().height; 			   		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mLineChart = new WifiProviderBarChartView(getActivity());
        chartLayout.addView(mLineChart, layoutParams);
        
        chartLayout = (RelativeLayout)view.findViewById(R.id.rl_wifi_user_location_chart);
		//图表显示范围在占屏幕大小的90%的区域内	   
		scrWidth = chartLayout.getLayoutParams().width; 	
		scrHeight = chartLayout.getLayoutParams().height; 			   		
		layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mPosRadarChart = new UserLocationRadarChartView(getActivity());
        chartLayout.addView(mPosRadarChart, layoutParams);
        
        chartLayout = (RelativeLayout)view.findViewById(R.id.rl_wifi_user_timer_chart);
		//图表显示范围在占屏幕大小的90%的区域内	   
		scrWidth = chartLayout.getLayoutParams().width; 	
		scrHeight = chartLayout.getLayoutParams().height; 			   		
		layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mTimerRadarChart = new UserTimerRadarChartView(getActivity());
        chartLayout.addView(mTimerRadarChart, layoutParams);
        
        new Thread(new Runnable() {

			@Override
            public void run() {
				AnalysizeData();
            }
        
        }).start();
        
        //display lisview
        mlistview = (ListView) view.findViewById(R.id.lv_wifi_report_2); 
        mdescriptions.add("分析报告如下：");
        mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_view_item, mdescriptions));
        
        return view;
    }
	
	private static long day6 = 6*24*60*60*1000;
	private static long day5 = 5*24*60*60*1000;
	private static long day4 = 4*24*60*60*1000;
	private static long day3 = 3*24*60*60*1000;
	private static long day2 = 2*24*60*60*1000;
	private static long day1 = 1*24*60*60*1000;
	
	private static long time0to8 = 16*60*60*1000;
	private static long time8to12 = 12*60*60*1000;
	private static long time12to16 = 8*60*60*1000;
	private static long time16to20 = 4*60*60*1000;
	
	private void AnalysizeData() {
		//为了节省API调用一次性拉取一周数据下来
		final WifiDynamic record = new WifiDynamic();
        record.MacAddr = LoginHelper.getInstance(getActivity()).mWifiProfile.MacAddr;
        record.MarkLoginTime();
	    record.QueryUserInOneWeek(getActivity(), record.LoginTime, new MultiDataCallback<WifiDynamic>() {

			@Override
            public void onSuccess(List<WifiDynamic> objects) {
				long headcount[] = new long[7]; //暂时统统计一周情况
				long poscount[] = new long[4]; //暂时分东南西北四个方向
				long timecount[] = new long[5]; //暂时分5个时间段
				double router_x = LoginHelper.getInstance(getActivity()).mWifiProfile.Geometry.getLatitude();
				double router_y = LoginHelper.getInstance(getActivity()).mWifiProfile.Geometry.getLongitude();;
	            for (int i=0; i < objects.size(); ++i) {
	            	WifiDynamic one = objects.get(i);
	            	//分析时间段曲线
	            	long time = record.LoginTime - one.LoginTime;
	            	if (time > day6) {
	            		headcount[0] += 1;
	            		time = time - day6;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	} else if (time > day5) {
	            		headcount[1] += 1;
	            		time = time - day5;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	} else if (time > day4) {
	            		headcount[2] += 1;
	            		time = time - day4;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	} else if (time > day3) {
	            		headcount[3] += 1;
	            		time = time - day3;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	} else if (time > day2) {
	            		headcount[4] += 1;
	            		time = time - day2;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	} else if (time > day1) {
	            		headcount[5] += 1;
	            		time = time - day1;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	} else {
	            		headcount[6] += 1;
	            		if (time > time0to8) {
	            			timecount[0] += 1;
	            		} else if (time > time8to12) {
	            			timecount[1] += 1;
	            		} else if (time > time12to16) {
	            			timecount[2] += 1;
	            		} else if (time > time16to20) {
	            			timecount[3] += 1;
	            		} else {
	            			timecount[4] += 1;
	            		}
	            	}
	            	//分析地理位置曲线
	            	double dist_x = one.Geometry.getLatitude() - router_x;
	            	double dist_y = one.Geometry.getLongitude() - router_y;
	            	if (dist_x >= 0) {
	            		if (dist_y >= 0) {
	            			poscount[0] += 1;
	            		} else {
	            			poscount[1] += 1;
	            		}
	            	} else {
	            		if (dist_y >= 0) {
	            			poscount[3] += 1;
	            		} else {
	            			poscount[2] += 1;
	            		}
	            	}
	            }
	            mLineChart.DisplayOneWeek(headcount, 7);
	            mPosRadarChart.DisplayOneWeek(poscount, 4);
	            mTimerRadarChart.DisplayOneWeek(timecount, 5);
	            mdescriptions.clear();
	            mdescriptions.add("分析报告如下：");
	            mdescriptions.add("您的WiFi一周总共开放了：    小时");
	            mdescriptions.add("您的WiFi一周总共被使用了：    小时");
	            mdescriptions.add("您的用户主要在东南角上网，建议您将WiFi向东北角移动");
	            mdescriptions.add("您的用户主要在18-24上网，建议您在这些时段开放网络");
	            mlistview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_view_item, mdescriptions));
	            Log.d(TAG, "查询到数据" + objects.size() + "条。");
            }

			@Override
            public void onFailed(String msg) {
	            Log.d(TAG, "查询一周数据失败。");
            }
	    });
	}
}
