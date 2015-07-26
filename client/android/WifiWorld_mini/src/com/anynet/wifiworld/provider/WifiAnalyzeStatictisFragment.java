package com.anynet.wifiworld.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiDataAnalyseHelper;
import com.anynet.wifiworld.data.WifiDynamic;

public class WifiAnalyzeStatictisFragment extends Fragment {
	private final static String TAG = WifiAnalyzeStatictisFragment.class.getSimpleName();
	
	private WifiProviderBarChartView mLineChart = null;
	private ListView mlistview = null;
	private WifiDataAnalyseHelper mDataMin = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_provider_template, container, false);
        
        //display chart
        RelativeLayout chartLayout = (RelativeLayout)view.findViewById(R.id.rl_provider_display);
		//图表显示范围在占屏幕大小的90%的区域内	   
		int scrWidth = chartLayout.getLayoutParams().width; 	
		int scrHeight = chartLayout.getLayoutParams().height; 			   		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//居中显示
		mLineChart = new WifiProviderBarChartView(getActivity());
        chartLayout.addView(mLineChart, layoutParams);

        //display lisview
        mlistview = (ListView) view.findViewById(R.id.lv_detail_list);
        final SimpleAdapter accountAdapter = new SimpleAdapter(getActivity(), getData(null), R.layout.item_wifi_comments,
        		new String[]{"content"}, new int[]{R.id.tv_detail_wifi_comments});
        mlistview.setAdapter(accountAdapter);
        mDataMin = WifiDataAnalyseHelper.getInstance(getActivity());
        mDataMin.Start(false, new DataCallback<WifiDataAnalyseHelper>() {

			@Override
            public void onSuccess(WifiDataAnalyseHelper object) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
                    public void run() {
						mLineChart.DisplayOneWeek(mDataMin.headcount, 7);
                    }
					
				});
			}

			@Override
            public void onFailed(String msg) {
            }
        	
        });
        
        return view;
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
