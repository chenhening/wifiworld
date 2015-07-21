package com.anynet.wifiworld.provider;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.me.UserLoginActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiAdmin;

public class WifiProviderRigisterActivity extends BaseActivity {

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		//mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText("网络认证");
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		WifiInfo wifi = WifiAdmin.getInstance(this).getWifiInfo();
		if (wifi == null) {
			finish();
			return;
		}
		
		setContentView(R.layout.activity_provider_certify_introduce);
		super.onCreate(savedInstanceState);		 
		TextView mTVLinkLicense = (TextView) findViewById(R.id.tv_link_license);
		bingdingTitleUI();
		
		TextView wifiname = (TextView)findViewById(R.id.tv_wifi_connected_name);
		wifiname.setText(wifi.getSSID());
		TextView wifidesc = (TextView)findViewById(R.id.tv_wifi_connected_desc);
		wifidesc.setText("当前连接网络");
		
		final String sText = "认证即表明您同意我们的<br><a style=\"color:#ffa400\" href=\"activity.special.scheme://127.0.0.1\">《网络宝服务协议》</a>";
		mTVLinkLicense.setText(Html.fromHtml(sText));
		mTVLinkLicense.setClickable(true);
		mTVLinkLicense.setMovementMethod(LinkMovementMethod.getInstance());
		mTVLinkLicense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(WifiProviderRigisterActivity.this, WifiProviderLicenseActivity.class);
				startActivity(i);
			}
		});
		
		TextView mAcceptTv = (TextView) findViewById(R.id.certify_button);
		mAcceptTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!LoginHelper.getInstance(WifiProviderRigisterActivity.this).isLogined()) {
	  				UserLoginActivity.start(WifiProviderRigisterActivity.this);
	  				return;
	  			}
				
				//查询当前账号是否有认证，如果有认证，不再进入认证流程
				WifiProfile mWifiProfile = LoginHelper.getInstance(getApplicationContext()).getWifiProfile();
				if (mWifiProfile != null) {
					WifiProviderRigisterActivity.this.showToast("您当前的账号已经认证过网络，目前只支持一个账号绑定一个网络。");
					return;
				}
				Intent i = new Intent(WifiProviderRigisterActivity.this, WifiProviderRigisterFirstActivity.class);
				startActivity(i);
			}
		});
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
