package com.anynet.wifiworld.me;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class AboutAppActivity extends BaseActivity {
	//IPC
		private Intent mIntent = null;
		private Activity activity = this;
		
		private void bingdingTitleUI() {
			mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
			mTitlebar.tvTitle.setText("关于网络宝");
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
			setContentView(R.layout.activity_about_app);
			super.onCreate(savedInstanceState);
			bingdingTitleUI();
			
			//设置系统版本
			TextView version = (TextView) this.findViewById(R.id.tv_app_version);
			version.setText(AppInfoUtil.getVersionName(this));
			
			//点击设置自动更新按钮
			this.findViewById(R.id.slv_click_update).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UmengUpdateAgent.forceUpdate(getApplicationContext());
				}
				
			});
			
			//问题反馈按钮
			this.findViewById(R.id.slv_feedback).setOnClickListener(new OnClickListener() {

				@Override
                public void onClick(View v) {
					FeedbackAgent agent = new FeedbackAgent(activity);
					agent.startFeedbackActivity();
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
