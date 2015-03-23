package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiProviderListActivity extends BaseActivity {

	private ListView providerList;
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
		LoginHelper mLoginHelper = LoginHelper.getInstance(getApplicationContext());
		if (!mLoginHelper.isLogined()) {
			Toast.makeText(this, "未登录！", Toast.LENGTH_LONG).show();
			finish();
		}

		setWifiProfileList(mLoginHelper.getCurLoginUserInfo().PhoneNumber);

		providerList = (ListView) findViewById(R.id.listview);

		LostAdapter = new QuickAdapter<WifiProfile>(this, R.layout.item_list) {
			@Override
			protected void convert(BaseAdapterHelper helper, WifiProfile wifiProfile) {
				helper.setText(R.id.tv_ssid, "SSID:" + wifiProfile.Ssid)
						.setText(R.id.tv_ctime, "WIFI加入时间:" + wifiProfile.getCreatedAt())
						.setText(R.id.tv_income, "收入:" + Float.toString(wifiProfile.Income));
			}
		};

		providerList.setAdapter(LostAdapter);
		findViewById(R.id.tv_add_wifi).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), WifiProviderActivity.class));
			}
		});
		bingdingTitleUI();
		providerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), WifiProviderDetailActivity.class);
				i.putExtra("mac", LostAdapter.getItem(position).MacAddr);
				startActivity(i);
			}
		});
	}

	private void setWifiProfileList(String Sponser) {
		// TODO Auto-generated method stub
		BmobQuery<WifiProfile> bmobQuery = new BmobQuery<WifiProfile>();
		bmobQuery.addWhereEqualTo("Sponser", Sponser);
		Log.d("findObjects", "开始查询setWifiProfileList");
		bmobQuery.findObjects(this, new FindListener<WifiProfile>() {

			@Override
			public void onSuccess(List<WifiProfile> elem) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "返回数据：" + elem.size() + "条", Toast.LENGTH_LONG).show();
				LostAdapter.addAll(elem);
				LostAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "获取数据错误 arg0:" + arg0 + " arg1:" + arg1, Toast.LENGTH_LONG)
						.show();
				// startActivity(new
				// Intent(getApplicationContext(),WifiProviderActivity.class));
			}
		});
		Log.d("findObjects", "结束查询setWifiProfileList");
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
