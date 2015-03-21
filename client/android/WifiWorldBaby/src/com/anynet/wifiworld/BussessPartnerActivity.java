package com.anynet.wifiworld;

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

import com.anynet.wifiworld.app.BaseActivity;

public class BussessPartnerActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bussess_partner_open);
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
				Intent i = new Intent(BussessPartnerActivity.this,
						BussessPartnerLicenseActivity.class);
				startActivity(i);
			}
		});
		Button mAcceptBtn = (Button) findViewById(R.id.btn_accept);
		mAcceptBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(BussessPartnerActivity.this,
						BussessPartnerFirstActivity.class);
				startActivity(i);
			}
		});
	}

	  private class MxgsaSpan extends ClickableSpan implements OnClickListener{
	        @Override
	        public void onClick(View widget) {
	            // TODO Auto-generated method stub
	            //具体代码，可以是跳转页面，可以是弹出对话框，下面是跳转页面
	        	getApplicationContext().startActivity(new Intent(BussessPartnerActivity.this,BussessPartnerLicenseActivity.class));
	        }
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
