/*
 * 文件名称 : DisplayUtil.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014-7-21, 下午4:16:34
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class DisplayUtil
{
    
    public  static int getDisplayHeight(Context context)
    {
        
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        
        manager.getDefaultDisplay().getMetrics(dm);
        
        return dm.heightPixels;
        
    }

    public  static int getDisplayWidth(Context context)
    {

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();

        manager.getDefaultDisplay().getMetrics(dm);

        return dm.widthPixels;

    }

    public static int getStatusBarHeight(Context context)
    {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusBarHeight;
    }



}
