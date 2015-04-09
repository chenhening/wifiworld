package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.wifi.WifiInfoScanned;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class WifiDetailsActivity extends BaseActivity {

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		//mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		//mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_details);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		//Get intent data
		Intent intent = getIntent();
		WifiInfoScanned wifiSelected = (WifiInfoScanned) intent.getSerializableExtra("WifiSelected");
		//Set title text and back button listener
		//TextView detailsTitle = (TextView)findViewById(R.id.setting_main_title);
		mTitlebar.tvTitle.setText(wifiSelected.getWifiName());
		//ImageView backView = (ImageView)findViewById(R.id.iv_setting_header_left);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		TextView rating = (TextView)findViewById(R.id.wifi_rate);
		rating.setText(String.valueOf(wifiSelected.getWifiStrength()));
		TextView ranking = (TextView)findViewById(R.id.wifi_account_rank);
		ranking.setText("排名：" + String.valueOf(wifiSelected.getRanking()));
		TextView connectedTimes = (TextView)findViewById(R.id.wifi_connect_times_num);
		connectedTimes.setText(String.valueOf(wifiSelected.getConnectedTimes()) + "次");
		TextView connectedDuration = (TextView)findViewById(R.id.wifi_connect_time_num);
		connectedDuration.setText(String.valueOf(wifiSelected.getConnectedDuration()) + "小时");
		
		//Set WiFi logo image
		if (wifiSelected.getWifiLogo() != null) {
			ImageView logo = (ImageView)findViewById(R.id.wifi_account_portral);
			logo.setImageBitmap(byte2Bitmap(wifiSelected.getWifiLogo()));
		}
		
		//添加评论信息
		ListView listview1 = (ListView) findViewById(R.id.wifi_list_comments);
		List<String> data1 = new ArrayList<String>();
		data1.add("网络很好用，感谢主人的分享。");
		data1.add("网络很好用，感谢主人的分享。");
		data1.add("楼主是好人啊，好人一生平安。");
		data1.add("看起来楼主是个美女，求认识下，谢谢。");
		data1.add("哈啊啊啊啊啊啊啊啊啊啊啊啊啊啊。");
		data1.add("能不能早点开放给我们用啊啊啊。");
		data1.add("怎么办。");
		listview1.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view_item, data1));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private Bitmap byte2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b,  0, b.length);
		}
		return null;
	}

}
