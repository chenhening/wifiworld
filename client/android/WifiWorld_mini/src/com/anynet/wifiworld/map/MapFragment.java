package com.anynet.wifiworld.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.MainActivity.MainFragment;

public class MapFragment extends MainFragment {
	private final static String TAG = MapFragment.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//mPageRoot = inflater.inflate(R.layout.fragment_wifi, null);
		
		return mPageRoot;
	}

	@Override
	protected void onVisible() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onInvisible() {
		// TODO Auto-generated method stub
		
	}

}
