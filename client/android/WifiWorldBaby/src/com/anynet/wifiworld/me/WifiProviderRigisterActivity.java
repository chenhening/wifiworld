package com.anynet.wifiworld.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;

public class WifiProviderRigisterActivity extends BaseActivity {

	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
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
		setContentView(R.layout.wifi_provider_certify_introduce);
		super.onCreate(savedInstanceState);		 
		TextView mTVLinkLicense = (TextView) findViewById(R.id.tv_link_license);
		
		final String sText = "认证即表明您同意我们的<br><a href=\"activity.special.scheme://127.0.0.1\">《网络宝商户服务协议》</a>";
		mTVLinkLicense.setText(Html.fromHtml(sText));
		mTVLinkLicense.setClickable(true);
		mTVLinkLicense.setMovementMethod(LinkMovementMethod.getInstance());
		mTVLinkLicense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(WifiProviderRigisterActivity.this,
						WifiProviderRigisterLicenseActivity.class);
				startActivity(i);
			}
		});
		TextView mAcceptTv = (TextView) findViewById(R.id.certify_button);
		mAcceptTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(WifiProviderRigisterActivity.this,
						WifiProviderRigisterFirstActivity.class);
				startActivity(i);
			}
		});
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
