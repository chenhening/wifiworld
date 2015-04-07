package com.anynet.wifiworld.knock;

import android.os.Bundle;
import android.view.View;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.SettingFeedbackActivity;
import com.anynet.wifiworld.app.BaseActivity;

public class KnockStepFirstActivity extends BaseActivity {

	StepFragment mSetupFragment[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setContentView(R.layout.knock_step_setup);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mSetupFragment = new StepFragment[3];
		for(int i=0;i<3;i++){
			mSetupFragment[i]= new StepFragment();
		}
		// fragments = new MainFragment[] { wifiFragment, mapFragment,
		// discoverFragment, meFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mSetupFragment[0])
				.add(R.id.fragment_container, mSetupFragment[1]).add(R.id.fragment_container, mSetupFragment[2])
				.hide(mSetupFragment[1]).hide(mSetupFragment[2]).show(mSetupFragment[0]).commit();
		setTitleUI();
	}

	public void setTitleUI() {
		// TODO Auto-generated method stub
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(getString(R.string.my));

	}

	public void next() {

	}

	public void pre() {

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
