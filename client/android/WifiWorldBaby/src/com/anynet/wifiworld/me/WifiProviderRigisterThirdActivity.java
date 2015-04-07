package com.anynet.wifiworld.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.R.layout;
import com.anynet.wifiworld.R.string;
import com.anynet.wifiworld.app.BaseActivity;

public class WifiProviderRigisterThirdActivity extends BaseActivity {
	//IPC
	private Intent mIntent = null;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(R.string.next_step);
		mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIntent.setClass(WifiProviderRigisterThirdActivity.this,
						WifiProviderRigisterCompleteActivity.class);
				startActivity(mIntent);
			}
		});
		mTitlebar.tvTitle.setText(getString(R.string.merchant_certify));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntent = getIntent();
		setContentView(R.layout.wifi_provider_certify_third);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
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

}
