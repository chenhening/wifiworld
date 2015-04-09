package com.anynet.wifiworld.knock;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.util.NetHelper;

public class KnockStepFirstActivity extends BaseActivity {

	StepFragment[] mSetupFragment = new StepFragment[3];
	int currentIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setContentView(R.layout.knock_step_setup);
		super.onCreate(savedInstanceState);
		initView();
		currentIndex = 0;
	}

	private void initView() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 3; i++) {
			mSetupFragment[i] = new StepFragment();
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
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		// mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(getString(R.string.my));

	}

	public void next() {
		if (currentIndex == 2)
			currentIndex = 2;
		if (currentIndex == 1)
			currentIndex = 2;
		if (currentIndex == 0)
			currentIndex = 1;
		reflesh();
	}

	public void pre() {
		if (currentIndex == 0)
			currentIndex = 0;
		if (currentIndex == 1)
			currentIndex = 0;
		if (currentIndex == 2)
			currentIndex = 1;
		reflesh();
	}

	private void save() {
		// TODO Auto-generated method stub
		
	}
	
	private void reflesh() {
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		if (currentIndex == 0) {
			trx.hide(mSetupFragment[1]);
			mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
			mTitlebar.tvHeaderLeft.setVisibility(View.INVISIBLE);
			mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			mTitlebar.tvHeaderRight.setText(R.string.next_step);
			mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					next();
				}
			});
		} else if (currentIndex == 1) {
			trx.hide(mSetupFragment[0]);
			trx.hide(mSetupFragment[2]);
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mTitlebar.tvHeaderLeft.setVisibility(View.VISIBLE);
			mTitlebar.tvHeaderLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pre();
				}
			});
			mTitlebar.tvHeaderRight.setText(R.string.next_step);
			mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					save();
				}


			});
		} else {
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mTitlebar.tvHeaderLeft.setVisibility(View.VISIBLE);
			mTitlebar.tvHeaderLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pre();
				}
			});
			mTitlebar.tvHeaderRight.setText(R.string.next_step);
			mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					next();
				}
			});
			trx.hide(mSetupFragment[1]);
		}

		if (!mSetupFragment[currentIndex].isAdded()) {
			trx.add(R.id.fragment_container, mSetupFragment[currentIndex]);
		}
		// fragments[index].onResume();
		trx.show(mSetupFragment[currentIndex]).commit();

		// 因为使用show和hide方法切换Fragment不会Fragment触发onResume/onPause方法回调，所以直接需要手动去更新一下状态
		if (NetHelper.isNetworkAvailable(getApplicationContext())) {

			for (int i = 0; i < mSetupFragment.length; i++) {
				if (i == currentIndex) {
					mSetupFragment[i].startUpdte();
				} else {
					mSetupFragment[i].stopUpdte();
				}
			}
		}
		// mSetupFragment[currentIndex].setSelected(false);
		// // 把当前tab设为选中状态
		// mSetupFragment[currentIndex].setSelected(true);
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
