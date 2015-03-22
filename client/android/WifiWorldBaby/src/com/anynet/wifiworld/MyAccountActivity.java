package com.anynet.wifiworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.StringCrypto;
import com.anynet.wifiworld.view.SettingItemView;

public class MyAccountActivity extends BaseActivity {

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my_account_title));
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
		setContentView(R.layout.user_homepage_activity);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		TextView tvName = (TextView) findViewById(R.id.person_name);
		tvName.setText(LoginHelper.getInstance(getApplicationContext())
				.getCurLoginUserInfo().PhoneNumber);
		SettingItemView si = (SettingItemView) findViewById(R.id.siv_account);
		TextView tvContent = (TextView) si
				.findViewById(R.id.setting_item_content);
		tvContent.setText(LoginHelper.getInstance(getApplicationContext())
				.getCurLoginUserInfo().PhoneNumber);

		si = (SettingItemView) findViewById(R.id.siv_psw);
		String psw = (LoginHelper.getInstance(getApplicationContext()).getCurLoginUserInfo()).Password;
		try {
			if (psw != null && !psw.equals(""))
				si.setContent(StringCrypto.decryptDES(psw, UserProfile.CryptoKey));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		si = (SettingItemView) findViewById(R.id.siv_alias);
		si.setContent("Steven");

		si = (SettingItemView) findViewById(R.id.siv_sex);
		si.setContent("男");
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
