package com.anynet.wifiworld.knock;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.util.DipPixelUtil;
import com.anynet.wifiworld.util.NetHelper;

public class KnockStepFirstActivity extends BaseActivity {

	private static final String TAG = KnockStepFirstActivity.class.getSimpleName();
	StepFragment[] mSetupFragment = new StepFragment[3];
	int currentIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		currentIndex = 0;
		isAnimation = false;
		setContentView(R.layout.knock_step_setup);
		super.onCreate(savedInstanceState);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 3; i++) {
			mSetupFragment[i] = new StepFragment();
			mSetupFragment[i].setTitle("我是第" + i + "个Fragment！");
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!mSetupFragment[currentIndex].isAdded())
			ft.add(R.id.knock_fragment_container, mSetupFragment[0]);
		// .add(R.id.knock_fragment_container, mSetupFragment[1])
		// .add(R.id.knock_fragment_container,
		// mSetupFragment[2]).hide(mSetupFragment[1]).hide(mSetupFragment[2])
		ft.show(mSetupFragment[currentIndex]).commit();
		setTitleUI();
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
		step1V = findViewById(R.id.iv_knock_step_1);
		step2V = findViewById(R.id.iv_knock_step_2);
		step3V = findViewById(R.id.iv_knock_step_3);
		moveV = findViewById(R.id.iv_move33);
	}

	public void setTitleUI() {
		// TODO Auto-generated method stub
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		// mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(getString(R.string.my));
	}

	View step1V, step2V, step3V, moveV;
	static boolean isAnimation = false;

	private void moveQbar(boolean right) {
		synchronized (ACCESSIBILITY_SERVICE) {
			TranslateAnimation translateAnimation;
			float deltaX = 0;
			if (right) {
				if (currentIndex == 0) {
					deltaX = step2V.getRight() - moveV.getRight();
				} else if (currentIndex == 1) {
					deltaX = step3V.getRight() - moveV.getRight();
				}
				deltaX = DipPixelUtil.px2dip(this, deltaX);
				translateAnimation = new TranslateAnimation(0, deltaX, 0, 0);
			} else {
				if (currentIndex == 2) {
					deltaX = moveV.getRight() - step2V.getRight();
				} else if (currentIndex == 1) {
					deltaX = moveV.getRight() - step1V.getRight();
				}
				deltaX = DipPixelUtil.px2dip(this, deltaX);
				translateAnimation = new TranslateAnimation(0, -deltaX, 0, 0);
			}

			final int toleft = right ? (int) (moveV.getLeft() + deltaX) : (int) (moveV.getLeft() - deltaX);
			Log.e(TAG, "toleft:" + toleft + "deltaX:" + deltaX);
			translateAnimation.setDuration(500);
			translateAnimation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					// To change body of implemented methods use File | Settings
					// |
					// File Templates.
					isAnimation = true;
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// int left = left;
					int top = moveV.getTop();
					int width = moveV.getWidth();
					int height = moveV.getHeight();
					moveV.clearAnimation();
					moveV.layout(toleft, top, toleft + width, top + height);
					isAnimation = false;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// To change body of implemented methods use File | Settings
					// |
					// File Templates.
				}
			});
			moveV.startAnimation(translateAnimation);
		}

	}

	public void next() {
		if (isAnimation)
			return;
		if (currentIndex == 2) {
			currentIndex = 2;
		}

		if (currentIndex == 1) {
			moveQbar(true);
			currentIndex = 2;
		}
		if (currentIndex == 0) {
			moveQbar(true);
			currentIndex = 1;
		}
		reflesh();
	}

	public void pre() {
		if (isAnimation)
			return;
		if (currentIndex == 0) {
			currentIndex = 0;
		}
		if (currentIndex == 1) {
			moveQbar(false);
			currentIndex = 0;
		}
		if (currentIndex == 2) {
			moveQbar(false);
			currentIndex = 1;
		}
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
					next();
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
			mTitlebar.tvHeaderRight.setText(android.R.string.ok);
			mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					save();
				}
			});
			trx.hide(mSetupFragment[1]);
		}

		if (!mSetupFragment[currentIndex].isAdded()) {
			trx.add(R.id.knock_fragment_container, mSetupFragment[currentIndex]);
		}
		// fragments[index].onResume();
		trx.show(mSetupFragment[currentIndex]).commit();

		// 因为使用show和hide方法切换Fragment不会Fragment触发onResume/onPause方法回调，所以直接需要手动去更新一下状态
		// if (NetHelper.isNetworkAvailable(getApplicationContext())) {
		//
		// for (int i = 0; i < mSetupFragment.length; i++) {
		// if (i == currentIndex) {
		// mSetupFragment[i].startUpdte();
		// } else {
		// mSetupFragment[i].stopUpdte();
		// }
		// }
		// }
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
