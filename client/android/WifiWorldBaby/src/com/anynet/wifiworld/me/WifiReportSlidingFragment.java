package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.anynet.wifiworld.R;

public class WifiReportSlidingFragment extends Fragment {
	private WifiProviderLineChartView mLineChart = null;
	
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
		RelativeLayout.LayoutParams layoutParams = 
			new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		//居中显示
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLineChart = new WifiProviderLineChartView(getActivity());
        chartLayout.addView(mLineChart, layoutParams);
        
        //display lisview 
        ListView listview1 = (ListView) view.findViewById(R.id.lv_wifi_report_1);
        listview1.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData()));
        
        ListView listview2 = (ListView) view.findViewById(R.id.lv_wifi_report_2);
        listview2.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData()));
        
        
        return view;
    }
	
	private List<String> getData(){
        
        List<String> data = new ArrayList<String>();
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");
         
        return data;
    }
}
