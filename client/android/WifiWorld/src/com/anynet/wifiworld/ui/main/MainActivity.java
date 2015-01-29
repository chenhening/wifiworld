package com.anynet.wifiworld.ui.main;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.ui.map.MapFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends FragmentActivity {

	private LayoutInflater mLayoutInflater;
	private Class<?> mFragmentArray[] = { WifiFragment.class, MapFragment.class, DiscoverFragment.class, MineFragment.class};
	private final static String mTextViewArray[] = { "连接", "附近", "发现", "我" };
	private int mImageViewArray[] = { R.drawable.tab_wifi, R.drawable.tab_map, R.drawable.tab_discover, R.drawable.tab_mine };
	
	public FragmentTabHost mTabHost;
	private int previousTab = 0;
	
	public TextView mTitleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		mLayoutInflater = LayoutInflater.from(this);
		
		initTabView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private View getTabItemView(int index) {
		View view = mLayoutInflater.inflate(R.layout.tab_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextViewArray[index]);
		return view;
	}
	
	@SuppressLint("NewApi") private void initTabView() {

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		for (int i = 0; i < mFragmentArray.length; ++i) {
			TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, mFragmentArray[i], null);
		}
		mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
		mTabHost.setCurrentTab(previousTab);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabName) {
				if (tabName.equals(mTextViewArray[1])) {
					if (mTabHost.getCurrentTab() < previousTab) {
					}
				}

				if (tabName.equals(mTextViewArray[2])) {
				}
				
				if (tabName.equals(mTextViewArray[0])) {
				}
				previousTab = mTabHost.getCurrentTab();
			}
		});
	}

}
