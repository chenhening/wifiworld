package com.anynet.wifiworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.R;

/**
 * 开关
 * 
 * @author admin
 * 
 */
public class SlipButton extends ImageView implements View.OnClickListener {// OnTouchListener,
	private static final String TAG = "SlipButton";

	private boolean switchState = false; // 记录当前按钮是否打开,true为打开,flase为关闭
	private boolean sliping = false; // 记录用户是否在滑动的变量
	private float nowX; // 按下时的x,当前的x,
	private float lastDownX;
	// private Rect bgOnRect, bgOffRect; // 打开和关闭状态下,游标的Rect
	private OnChangedListener changeListener = null; // 状态监听

	private Bitmap bgOnBitmap, bgOffBitmap;// , cursorBitmap;

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public interface OnChangedListener {
		abstract void OnChanged(View v, boolean CheckState);
	}

	public void SetOnChangedListener(OnChangedListener l) {// 设置监听器,当状态修改的时候
		changeListener = l;
	}

	/**
	 * 修改开关状态,包括值和对应的图案
	 * 
	 * @param newState
	 *            -要设定的开关的状态
	 * @param callListener
	 *            -此次设定是否触发onChangedListener
	 */
	public void setSwitchState(boolean newState, boolean callListener) {

		boolean lastState = this.switchState;

		this.switchState = newState;
		if (switchState) {
			nowX = bgOnBitmap.getWidth();
			setImageResource(R.drawable.crystal_monitor_toggle_btn_checked);
		} else {
			nowX = 0;
			setImageResource(R.drawable.crystal_monitor_toggle_btn_unchecked);
		}
		invalidate();

		if (callListener && changeListener != null && lastState != newState) {
			XLLog.log(TAG, "state changed to : " + newState);
			changeListener.OnChanged(this, newState);
		}
	}

	public boolean getSwitchState() {
		XLLog.log(TAG, "get state : " + this.switchState);
		return this.switchState;
	}

	private void init() {
		bgOnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crystal_monitor_toggle_btn_checked);
		bgOffBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crystal_monitor_toggle_btn_unchecked);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (switchState) {
			nowX = bgOnBitmap.getWidth();
		} else {
			nowX = 0;
		}
		invalidate();
	}

	// 点击直接切换开关
	@Override
	public void onClick(View v) {
		switchSwitch();
	}

	private void switchSwitch() {
		boolean lastState = switchState;
		if (switchState) {
			switchState = false;
			setImageResource(R.drawable.crystal_monitor_toggle_btn_checked);
		} else {
			switchState = true;
			setImageResource(R.drawable.crystal_monitor_toggle_btn_checked);
		}
		if (changeListener != null && lastState != switchState) {
			XLLog.log(TAG, "state changed to : " + switchState);
			changeListener.OnChanged(this, switchState);
		}
	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// setMeasuredDimension(bgOnBitmap.getWidth(), bgOnBitmap.getHeight());
//		setMeasuredDimension(110, 64);
//	}

}
