
package com.anynet.wifiworld.me.whitelist;

import com.anynet.wifiworld.me.whitelist.DisplayUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SideBar extends View {

    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    String[] chars = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    public void setChars(String[] cs) {
        chars = cs;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (chars.length > 0) {
            if (showBkg) {
                canvas.drawColor(Color.parseColor("#00000000"));
            }
            int height = getHeight();
            int width = getWidth();
            int singleHeight = height / chars.length;
            for (int i = 0; i < chars.length; i++) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(DisplayUtil.dip2px(10, getContext()));
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setAntiAlias(true);
                if (i == choose) {
                    paint.setColor(Color.parseColor("#3399ff"));
                    paint.setFakeBoldText(true);
                }
                float xPos = width / 2 - paint.measureText(chars[i]) / 2;
                float yPos = singleHeight * (i + 0.5f);
                canvas.drawText(chars[i], xPos, yPos, paint);
                paint.reset();
            }
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (chars.length > 0) {
            final int action = event.getAction();
            final float y = event.getY();
            final int oldChoose = choose;
            final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
            final int c = (int) (y / getHeight() * chars.length);

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    showBkg = true;
                    if (oldChoose != c && listener != null) {
                        if (c >= 0 && c < chars.length) {
                            listener.onTouchingLetterChanged(chars[c]);
                            choose = c;
                            invalidate();
                        }
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (oldChoose != c && listener != null) {
                        if (c >= 0 && c < chars.length) {
                            listener.onTouchingLetterChanged(chars[c]);
                            choose = c;
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    showBkg = false;
                    choose = -1;
                    invalidate();
                    break;
            }
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
