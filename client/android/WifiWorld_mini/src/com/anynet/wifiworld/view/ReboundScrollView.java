package com.anynet.wifiworld.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class ReboundScrollView extends ScrollView {
	private final static String TAG = ReboundScrollView.class.getSimpleName();
	
    private View inner;
    private float y;
    private Rect normal = new Rect();
    boolean firstMoveDown = true;

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            inner = getChildAt(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (inner == null) {
            return super.onTouchEvent(ev);
        }
        else {
            commOnTouchEvent(ev);
        }

        return super.onTouchEvent(ev);
    }

    public void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();
                //Log.d(TAG, "action down: " + y);
                break;
            case MotionEvent.ACTION_UP:
            	//Log.d(TAG, "action up: " + isNeedAnimation());
                if (isNeedAnimation()) {
                    animation();
                }
                firstMoveDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
        		//Log.d(TAG, "action move: " + isNeedMove());
        		if (firstMoveDown) {
					y = ev.getY();
					firstMoveDown = false;
				}
                final float preY = y;
                float nowY = ev.getY();
                int deltaY = (int)((preY - nowY) / 2);
                //scrollBy(0, deltaY);

                y = nowY;
                if (isNeedMove()) {
                    if (normal.isEmpty()) {
                        normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
                    }
                    inner.layout(inner.getLeft(), inner.getTop() - deltaY, inner.getRight(), inner.getBottom() - deltaY);
                }
                break;

            default:
                break;
        }
    }

    public void animation() {
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(), normal.top);
        ta.setDuration(200);
        inner.startAnimation(ta);
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);

        normal.setEmpty();

    }

    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    public boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

}

