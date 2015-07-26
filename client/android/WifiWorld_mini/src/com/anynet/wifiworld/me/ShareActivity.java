package com.anynet.wifiworld.me;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;

public class ShareActivity extends BaseActivity {
	
	// IPC
		private Intent mIntent = null;
		private Activity activity = this;

		private void bingdingTitleUI() {
			mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
			mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
			mTitlebar.tvTitle.setText("邀请好友扫一扫");
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
			setContentView(R.layout.activity_share);
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
			InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
			View v = getCurrentFocus();
			if (imm != null && v != null) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}

		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onRestoreInstanceState(savedInstanceState);
		}

		@Override
		protected void onRestart() {
			// TODO Auto-generated method stub
			super.onRestart();
		}

}
