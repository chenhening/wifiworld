package com.anynet.wifiworld;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.util.DipPixelUtil;
import com.anynet.wifiworld.util.DisplayUtil;
import com.anynet.wifiworld.R;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class NewVersionActivity extends BaseActivity {

	private ImageView animate_bg;


	private boolean animatePolicy1;
	private View enter;

	public static void startActivity(BaseActivity baseActivity) {
		Intent i = new Intent(baseActivity, NewVersionActivity.class);
		baseActivity.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.welcome_activity);
		super.onCreate(savedInstanceState);
		animate_bg = (ImageView) findViewById(R.id.animate_bg);
		enter = findViewById(R.id.enter);
		enter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.startActivity(NewVersionActivity.this,true);
				finish();
			}
		});

		//animate_bg.setPivotX(DisplayUtil.getDisplayWidth(this) );
		//animate_bg.setPivotY(DisplayUtil.getDisplayHeight(this) );
		//animate_bg.setScaleX(1.1f);
		//animate_bg.setScaleY(1.1f);
		animate_bg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				Matrix matrix = new Matrix();
				matrix.postScale(1.1f,1.1f,animate_bg.getWidth() / 2,animate_bg.getHeight()/2);
				animate_bg.setImageMatrix(matrix);
				animate_bg.getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
		startAnimate();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		boolean right = true;
		float left = 0;
		@Override
		public void run() {
			if (right) {
				left += 1.5f;

				if (maxLeft <= left) {
					left = maxLeft;
					right = !right;
				}
			} else {
				left -= 1.5f;
				if (left <= 0) {
					left = 0;
					right = !right;
				}
			}
			translate(left, 0);

			animate_bg.invalidate();
			handler.postDelayed(runnable, 100);
		}
	};

	private void translate(float left, float top) {
		//Log.d(TAG,"translate x:" + left);
		Matrix matrix = new Matrix();
		//matrix.postScale(1.1f,1.1f);
		matrix.postScale(1.1f,1.1f,animate_bg.getWidth() / 2,animate_bg.getHeight()/2);
		matrix.postTranslate(left, top);
		animate_bg.setImageMatrix(matrix);
	}
	private float maxLeft = 0f;
	public void startAnimate() {
		maxLeft = DisplayUtil.getDisplayWidth(this) * 0.1f / 2;
		animatePolicy1 = true;
		handler.post(runnable);

	}

	public void stopAnimate() {
		if (animatePolicy1) {
			handler.removeCallbacks(runnable);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAnimate();
	}
}