package com.anynet.wifiworld.knock;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.BaseFragment;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.util.DipPixelUtil;
import com.anynet.wifiworld.util.LoginHelper;

public class KnockStepFirstActivity extends BaseActivity {

	private static final String TAG = KnockStepFirstActivity.class.getSimpleName();
	BaseFragment[] mSetupFragment = new BaseFragment[4];
	int currentIndex = 0;
	private WifiQuestions mQuestions;
	private boolean mAskOrAnswer = false; // true: ask, false: answer

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentIndex = 0;
		isAnimation = false;
		setContentView(R.layout.knock_step_setup);
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String whoami = intent.getStringExtra("whoami");
		if (whoami != null && whoami.equals("WifiDetailsActivity")) {
			mAskOrAnswer = true;
			mQuestions = (WifiQuestions) intent.getSerializableExtra("data");
			for (int i = 0; i < 3; i++) {
				mSetupFragment[i] = new AnswerFragment(mQuestions.Question.get(i));
			}
		} else {
			mQuestions = new WifiQuestions();
			mQuestions.MacAddr = LoginHelper.getInstance(getApplicationContext()).mWifiProfile.MacAddr;
			for (int i = 0; i < 3; i++) {
				mSetupFragment[i] = new StepFragment(mQuestions.Question.get(i));
			}
		}
		initView();
	}

	private void initView() {
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
				next();
			}
		});
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
			step1V = findViewById(R.id.iv_knock_step_1);
			step2V = findViewById(R.id.iv_knock_step_2);
			step3V = findViewById(R.id.iv_knock_step_3);
			moveV = findViewById(R.id.iv_move33);
			float deltaX = 0;
			Display display  = this.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x +30;
			int height = size.y;
			//deltaX = width/3.0f;
			int s = DipPixelUtil.dip2px(this, 16);
			Log.e(TAG,
					"Display:("+DipPixelUtil.px2dip(this,width)+","+DipPixelUtil.px2dip(this,height)+")step1V.left:" + step1V.getLeft() + " Right:" + step1V.getRight() + " with:" + step1V.getWidth()
							+ "   step2V.left:" + step2V.getLeft() + " right:" + step2V.getRight() + " with:"
							+ step2V.getWidth() + "   step3V.Left:" + step3V.getLeft() + " Right:" + step3V.getRight()
							+ " with:" + step3V.getWidth() + "   moveV.Left:" + moveV.getLeft() + " Right:"
							+ moveV.getRight() + " with:" + moveV.getWidth());
			if (right) {
				if (currentIndex == 0) {
					deltaX = step2V.getRight() - moveV.getRight();
				} else if (currentIndex == 1) {
					deltaX = step3V.getRight()+s - moveV.getRight();
				}
				// deltaX = DipPixelUtil.px2dip(this, deltaX);
				translateAnimation = new TranslateAnimation(0, deltaX, 0, 0);
			} else {
				if (currentIndex == 2) {
					deltaX = moveV.getRight() - step2V.getRight();
				} else if (currentIndex == 1) {
					deltaX = moveV.getRight() - step1V.getRight();
				}
				// deltaX = DipPixelUtil.px2dip(this, deltaX);
				translateAnimation = new TranslateAnimation(0, -deltaX, 0, 0);
			}

			final int toRight = right ? (int) (moveV.getRight() + deltaX) : (int) (moveV.getRight() - deltaX);
			Log.e(TAG, "toleft:" + toRight + "deltaX:" + deltaX);
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
					moveV.layout(toRight - width, top, toRight, top + height);
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

		// 提取数据
		if (!mAskOrAnswer
				&& !((StepFragment) mSetupFragment[currentIndex]).getData(mQuestions.Question.get(currentIndex))) {
			showToast("问题的每一项都不能为空，请补全。");
			return;
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
		if (mAskOrAnswer) {
			// 验证答案并提交
			for (int i = 0; i < 3; ++i) {
				if (((AnswerFragment) mSetupFragment[i]).getRightAnswer() != 1) {
					showToast("问题回答的不完全正确请稍后再试。");
					break;
				}
			}
			// 保存敲门数据到本地
			if (mQuestions.MacAddr != null)
				LoginHelper.getInstance(getApplicationContext()).mKnockList.add(mQuestions.MacAddr);
		} else {
			if (!mAskOrAnswer
					&& !((StepFragment) mSetupFragment[currentIndex]).getData(mQuestions.Question.get(currentIndex))) {
				showToast("问题的每一项都不能为空，请补全。");
				return;
			}
			mQuestions.StoreRemote(getApplicationContext(), new DataCallback<WifiQuestions>() {

				@Override
				public void onSuccess(WifiQuestions object) {
					showToast("上传敲门问题成功。");
				}

				@Override
				public void onFailed(String msg) {
					showToast("当前网络不稳定，上传问题失败，请稍后再试。");
				}

			});
		}
		finish();
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
