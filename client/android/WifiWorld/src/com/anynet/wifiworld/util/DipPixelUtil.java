/*
 * 文件名称 : DipPixelUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:43:55
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.xunlei.crystalandroid.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.FloatMath;

/**
 * Dip和Pixel之间转化
 * <p>
 */
public class DipPixelUtil
{
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @SuppressLint("DefaultLocale")
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

   
   /** 
    * 将sp值转换为px值，保证文字大小不变 
    *  
    * @param spValue 
    * @param fontScale 
    *            （DisplayMetrics类中属性scaledDensity） 
    * @return 
    */  
   public static int sp2px(Context context, int spValue) {  
       final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
       return (int) (spValue * fontScale + 0.5f);  
   }  
   
   
   /** 
    * 将px值转换为sp值，保证文字大小不变 
    *  
    * @param pxValue 
    * @param fontScale 
    *            （DisplayMetrics类中属性scaledDensity） 
    * @return 
    */  
   public static int px2sp(Context context, float pxValue) {  
       final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
       return (int) (pxValue / fontScale + 0.5f);  
   }  
 
    
}
