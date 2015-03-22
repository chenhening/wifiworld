package com.anynet.wifiworld.me;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.R.id;
import com.anynet.wifiworld.R.layout;
import com.anynet.wifiworld.R.string;
import com.anynet.wifiworld.app.BaseActivity;

public class WifiProviderRigisterActivity extends BaseActivity {

	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
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
		// TODO Auto-generated method stub
		setContentView(R.layout.bussess_partner_certify_introduce);
		super.onCreate(savedInstanceState);		
		//WebView mWebView = (WebView) findViewById(R.id.sv_open);
//		mWebView.loadUrl("file:///android_asset/www/pages/bussessopen.html");
//		WebSettings webSettings= mWebView.getSettings();
//		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);  
		TextView mTVLinkLicense = (TextView) findViewById(R.id.tv_link_license);
		//SpannableString msp = new SpannableString("认证即表明您同意我们的《WifiWorld商户服务协议》");  
		
		final String sText = "认证即表明您同意我们的<br><a href=\"activity.special.scheme://127.0.0.1\">《WifiWorld商户服务协议》</a>";
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
