package com.anynet.wifiworld.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.MainActivity.MainFragment;

public class MeFragment extends MainFragment {
	private final static String TAG = MeFragment.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mPageRoot = inflater.inflate(R.layout.fragment_me, null);
		
		// 设置about
		this.findViewById(R.id.slv_about_app).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), AboutAppActivity.class);
				startActivity(i);
			}

		});

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
