/*
 * Copyright (C) 2015 AnyNet Corporation.  All rights reserved.
 * @Author: buffer(179770346@qq.com or binfeix.li@intel.com)
 * @Time: 2015/01/22
 * 
 * 	v0.1: Implement onAttach()
 */
package com.anynet.wifiworld.ui.map;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.anynet.wifiworld.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
	private MapView mapView;
	private AMap aMap;
	private View mapLayout;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mapLayout = inflater.inflate(R.layout.basicmap_activity, null);
		mapView = (MapView) mapLayout.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		
        return mapLayout;
    }
	
	@Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
	
	@Override
    public void onStop() {
        super.onStop();
        System.out.println("Fragment-->onStop");
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
	
	@Override
    public void onStart() {
        super.onStart();
        mapView.startLayoutAnimation();
    }
	
	@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
