/*
 * 文件名称 : InputMethodUtil.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014年9月26日, 下午4:50:00
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.xunlei.crystalandroid.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 关于输入法键盘的工具
 * <p>
 */
public class InputMethodUtil
{
    public static void hiddenInput(Context ctx, View v) {
        if (ctx instanceof Context && v instanceof View) {
            InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void showInput(Context ctx) {
        if (ctx instanceof Context) {
            InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
}
