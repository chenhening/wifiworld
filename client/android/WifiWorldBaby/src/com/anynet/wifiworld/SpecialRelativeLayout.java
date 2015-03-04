package com.anynet.wifiworld;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.anynet.wifiworld.util.DisplayUtil;

public class SpecialRelativeLayout extends RelativeLayout {

    public SpecialRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < 2; i++) {
            if (h == DisplayUtil.getDisplayHeight(getContext())) {
                View child = getChildAt(i);
                if (child != null) {
                    RelativeLayout.LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                    layoutParams.topMargin = 0;
                }
            } else {
                View child = getChildAt(i);
                if (child != null) {
                    RelativeLayout.LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                    layoutParams.topMargin = -DisplayUtil.getStatusBarHeight(getContext());
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

}