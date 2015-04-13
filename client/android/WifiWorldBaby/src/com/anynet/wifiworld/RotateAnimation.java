package com.anynet.wifiworld;

import android.graphics.Matrix;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.anynet.wifiworld.util.XLLog;


/**
 * Created by lj on 15-2-2.
 */
public class RotateAnimation {
    private static  final  String TAG = "RotateAnimation";
    private ImageView imageView;
    private Handler handler;
    public RotateAnimation(ImageView imageView,Handler handler){
        this.imageView =imageView;
        this.handler = handler;
    }
    float rotation = 0f;
    Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            rotation += 15f;
            rotation %= 360f;
            rotation(rotation);
            handler.postDelayed(this,50);
        }
    };
    private void rotation(float rotation){
        XLLog.log(TAG,"rotation " + rotation);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation, imageView.getWidth() / 2,imageView.getHeight() / 2);
       imageView.setImageMatrix(matrix);
        imageView.invalidate();
    }
    ViewTreeObserver.OnPreDrawListener l = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            handler.post(animRunnable);
            imageView.getViewTreeObserver().removeOnPreDrawListener(this);
            return false;
        }
    };
    public void start() {
        imageView.getViewTreeObserver().addOnPreDrawListener(l);
    }
    public void clear() {
        imageView.getViewTreeObserver().removeOnPreDrawListener(l);
        handler.removeCallbacks(animRunnable);
    }
}
