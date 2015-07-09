package com.anynet.wifiworld.knock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.BaseFragment;
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
	private int moveRight = 0;
	
	public static void start(Context context, String whoami,WifiQuestions data){
		Intent i = new Intent(context, KnockStepFirstActivity.class);
		if(whoami != null){
			i.putExtra("whoami", whoami);
			if(data != null)i.putExtra("data", data);
		}		
		context.startActivity(i);
		((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);  
	}
	
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
				mSetupFragment[i] = new AnswerFragment(mQuestions.Question.get(i),i);
			}
		} else {
			mQuestions = (WifiQuestions) intent.getSerializableExtra("data");
			mQuestions.MacAddr = LoginHelper.getInstance(getApplicationContext()).mWifiProfile.MacAddr;
			for (int i = 0; i < 3; i++) {
				mSetupFragment[i] = new StepFragment(mQuestions.Question.get(i),i);
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
		
		step1V = findViewById(R.id.iv_knock_step_1);
		step2V = findViewById(R.id.iv_knock_step_2);
		step3V = findViewById(R.id.iv_knock_step_3);
		moveV = findViewById(R.id.iv_move33);
	}

	public void setTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(R.string.next_step);
		mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				next();
			}
		});
		
		mTitlebar.tvTitle.setText("网络敲门");
	}

	View step1V, step2V, step3V, moveV;
	static boolean isAnimation = false;

	private void moveQbar(boolean right) {
		synchronized (ACCESSIBILITY_SERVICE) {
			TranslateAnimation translateAnimation;
			float deltaX = 0;
			int s = DipPixelUtil.dip2px(this, 16);
			if (right) {
				if (currentIndex == 0) {
					deltaX = step2V.getRight() - moveV.getRight();
				} else if (currentIndex == 1) {
					deltaX = step3V.getRight() + s - moveV.getRight();
				}
				translateAnimation = new TranslateAnimation(0, deltaX, 0, 0);
			} else {
				if (currentIndex == 2) {
					deltaX = moveV.getRight() - step2V.getRight();
				} else if (currentIndex == 1) {
					deltaX = moveV.getRight() - step1V.getRight();
				}
				translateAnimation = new TranslateAnimation(0, -deltaX, 0, 0);
			}

			moveRight = right ? (int) (moveV.getRight() + deltaX) : (int) (moveV.getRight() - deltaX);
			Log.e(TAG, "toleft:" + moveRight + "deltaX:" + deltaX);
			translateAnimation.setDuration(500);
			translateAnimation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					isAnimation = true;
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					int top = moveV.getTop();
					int width = moveV.getWidth();
					int height = moveV.getHeight();
					moveV.clearAnimation();
					moveV.layout(moveRight - width, top, moveRight, top + height);
					isAnimation = false;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
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
					finish();
					return;
				}
			}
			// 保存敲门数据到本地
			if (mQuestions.MacAddr != null) {
				LoginHelper.getInstance(getApplicationContext()).mKnockList.add(mQuestions.MacAddr);
				showToast("恭喜敲门成功啦，可以上网啦！");
			}
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
					showToast("当前网络不稳定，上传问题失败，请稍后再试: " + msg);
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
					finish();
				}
			});
			mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
			mTitlebar.tvHeaderRight.setText(R.string.next_step);
			mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
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
			mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
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
			mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
			mTitlebar.tvHeaderRight.setText(android.R.string.ok);
			mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
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
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub

		super.onAttachFragment(fragment);
		Log.e(TAG, "onAttachFragment");
		if (fragment instanceof AnswerFragment) {
			int id = ((AnswerFragment) fragment).getFragmentID();
			if (mSetupFragment[id] == null)
				mSetupFragment[id] = (AnswerFragment) fragment;
		}
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
		int top = moveV.getTop();
		int width = moveV.getWidth();
		int height = moveV.getHeight();
		//Toast.makeText(this, "moveRight:" + moveRight, Toast.LENGTH_LONG).show();
		moveV.layout(moveRight - width, top, moveRight, top + height);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
