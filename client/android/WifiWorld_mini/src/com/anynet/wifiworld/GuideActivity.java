package com.anynet.wifiworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class GuideActivity extends Activity {

	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;
	private List<View> views;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initViewPager();
		
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				finish();
			}
			
		}, 5000);
	}

	private void initViewPager() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.viewpage_guide_one, null));
		views.add(inflater.inflate(R.layout.viewpage_guide_two, null));
		views.add(inflater.inflate(R.layout.viewpage_guide_three, null));
		viewPagerAdapter = new ViewPagerAdapter(views);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(viewPagerAdapter);
	}
}
