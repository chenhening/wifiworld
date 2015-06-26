package com.anynet.wifiworld.view;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * xx中后面跟了几个点 点->点点->点点点，循环动画，表示doing st
 */
public class AnimationDot extends TextView {

	private Handler mHandler;
	private static final int MSG_DOT_ANI = 506428;

	private static final int PERIOD_DOT_ANI = 250;// ms

	private static final int MIN_DOT_NUM = 1;
	private static final int MAX_DOT_NUM = 3;

	private int dotNum;
	
	private String mFrontText = "";

	public AnimationDot(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAni();
	}

	public AnimationDot(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAni();
	}

	public AnimationDot(Context context) {
		super(context);
		initAni();
	}

	private static class DotHandler extends Handler {

		private WeakReference<AnimationDot> mParent;

		public DotHandler(AnimationDot p) {
			super();
			mParent = new WeakReference<AnimationDot>(p);
		}

		@Override
		public void handleMessage(Message msg) {
			AnimationDot dotview = mParent.get();
			if (dotview == null)
				return;
			switch (msg.what) {
			case MSG_DOT_ANI:
				String str = dotview.mFrontText;
				++dotview.dotNum;
				if (dotview.dotNum > MAX_DOT_NUM) {
					dotview.dotNum = MIN_DOT_NUM;
				} else if (dotview.dotNum < MIN_DOT_NUM) {
					dotview.dotNum = MAX_DOT_NUM;
				}
				for (int i = 1; i <= dotview.dotNum; i++) {
					str = str + ".";
				}
				dotview.setText(str);
				sendEmptyMessageDelayed(MSG_DOT_ANI, PERIOD_DOT_ANI);
				break;

			default:
				break;
			}
		}
	}

	private void initAni() {
		dotNum = 0;
		if (mHandler == null) {
			mHandler = new DotHandler(this);
		}
	}
	
	public void setFrontText(final String front) {
		if (null != front) {
			this.mFrontText = front;
		}
	}

	public void show() {
		this.setVisibility(View.VISIBLE);
		mHandler.removeMessages(MSG_DOT_ANI);
		mHandler.sendEmptyMessage(MSG_DOT_ANI);
	}
	
	public void hideDot() {
		if (mHandler != null) {
			this.mHandler.removeMessages(MSG_DOT_ANI);
		}
		this.setText(mFrontText);
	}

	public void hide() {
		if (mHandler != null) {
			mHandler.removeMessages(MSG_DOT_ANI);
		}
		this.setVisibility(View.GONE);
	}
}
