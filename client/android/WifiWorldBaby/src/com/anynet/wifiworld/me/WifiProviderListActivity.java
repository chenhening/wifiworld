package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;


public class WifiProviderListActivity extends BaseActivity {

	private ListView providerList;
	private List<WifiProfile> lWifiProfile = new ArrayList<WifiProfile>();
	private QuickAdapter<WifiProfile> LostAdapter;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.provider_list);
		super.onCreate(savedInstanceState);

		lWifiProfile = getWifiProfileList("");

		providerList = (ListView) findViewById(R.id.listview);
		
		LostAdapter = new QuickAdapter<WifiProfile>(this, R.layout.item_list) {
			@Override
			protected void convert(BaseAdapterHelper helper, WifiProfile wifiProfile) {
				helper.setText(R.id.tv_ssid, wifiProfile.Wifiid)
						.setText(R.id.tv_ctime, wifiProfile.ctime.toString())
						.setText(R.id.tv_income, Float.toString(wifiProfile.Income));
			}
		};
		providerList.setAdapter(LostAdapter);
		findViewById(R.id.tv_add_wifi).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),WifiProviderActivity.class));
			}
		});
		bingdingTitleUI();
	}

	private List<WifiProfile> getWifiProfileList(String Userid) {
		// TODO Auto-generated method stub
		BmobQuery<WifiProfile> bmobQuery = new BmobQuery<WifiProfile>();
		bmobQuery.addWhereContains(Userid, "Userid");
		bmobQuery.findObjects(this, new FindListener<WifiProfile>() {
			
			@Override
			public void onSuccess(List<WifiProfile> elem) {
				// TODO Auto-generated method stub
				LostAdapter.addAll(elem);
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				//startActivity(new Intent(getApplicationContext(),WifiProviderActivity.class));
			}
		});
		
		return null;
	}

	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
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

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

}
