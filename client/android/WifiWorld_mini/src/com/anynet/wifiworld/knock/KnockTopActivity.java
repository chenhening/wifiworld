package com.anynet.wifiworld.knock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.BaseActivity;

public class KnockTopActivity extends BaseActivity {

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText("敲门");
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

		setContentView(R.layout.knock_setting);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();

		RelativeLayout switchRL = (RelativeLayout) findViewById(R.id.tv_setting_knock);
		switchRL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ksCB = (CheckBox) findViewById(R.id.knock_switch);
				if (ksCB.isChecked()) {
					ksCB.setChecked(false);
					findViewById(R.id.specific_setting_layout).setVisibility(View.GONE);
				} else {
					ksCB.setChecked(true);
					findViewById(R.id.specific_setting_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.question_setting).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							KnockStepFirstActivity.start(KnockTopActivity.this, null, null);
						}
					});
				}

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
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
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
