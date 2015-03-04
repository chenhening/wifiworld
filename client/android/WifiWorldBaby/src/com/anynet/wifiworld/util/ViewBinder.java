/*
 * 文件名称 : ViewBinder.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014年9月5日, 上午10:51:12
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.util;

import java.lang.reflect.Field;

import android.view.View;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class ViewBinder
{
    
    public static void bindingView(View parent, Object obj)
    {
        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field fld : fields)
        {
            String name = fld.getName();
            View view = parent.findViewWithTag(name);
            if (null != view)
            {
                fld.setAccessible(true);
                try
                {
                    fld.set(obj, view);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
