/*
 * 文件名称 : Log.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:19:35
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志工具，在发布版重会去掉正文
 * 
 * @author
 * 
 */
public class XLLog
{
    
    private static boolean LOG = true;
    
    public static void setLog(boolean openLog)
    {
        LOG = openLog;
    }
    

    public static void d(String tag, String content)
    {
        
        if (LOG && !TextUtils.isEmpty(content))
        {
            Log.d(tag, content);
        }
    }
    
    public static void d(String tag, Object... args)
    {
        if (LOG && null != args)
        {
            
            // 计算日志的长度，减少内存开销。 估计意义不大。
            // int length = 0;
            // for(Object obj : args) {
            // length += obj.toString().length();
            // }
            
            StringBuffer buffer = new StringBuffer(args.length * 10);
            for (Object obj : args)
            {
                buffer.append(obj).append(" ");
            }
            
            Log.d(tag, buffer.toString());
        }
    }
    
    
    public static void w(String tag, String content)
    {
        
        if (LOG && !TextUtils.isEmpty(content))
        {
            Log.w(tag, content);
        }
    }
    
    public static void w(String tag, Object... args)
    {
        if (LOG && null != args)
        {
            
            // 计算日志的长度，减少内存开销。 估计意义不大。
            // int length = 0;
            // for(Object obj : args) {
            // length += obj.toString().length();
            // }
            
            StringBuffer buffer = new StringBuffer(args.length * 10);
            for (Object obj : args)
            {
                buffer.append(obj).append(" ");
            }
            
            Log.w(tag, buffer.toString());
        }
    }
    
    public static void e(String tag, String content)
    {
        
        if (LOG && !TextUtils.isEmpty(content))
        {
            Log.e(tag, content);
        }
    }
    
    public static void e(String tag, Throwable e)
    {
        if (LOG && e != null)
        {
            e.printStackTrace();
        }
    }
    
    public static void e(String tag, Object... args)
    {
        if (LOG && null != args)
        {
            
            // 计算日志的长度，减少内存开销。 估计意义不大。
            // int length = 0;
            // for(Object obj : args) {
            // length += obj.toString().length();
            // }
            
            StringBuffer buffer = new StringBuffer(args.length * 10);
            for (Object obj : args)
            {
                buffer.append(obj).append(" ");
            }
            
            Log.e(tag, buffer.toString());
        }
    }
    
    /**
     * 输入日志到Logcat，性能比log(String, String)方法高一些。因为该方法少了拼接大量字符串的工作。
     * 
     * @param tag
     * @param format
     *            日志格式字符串。
     * @param args
     *            填充字段。
     */
    public static void logF(String tag, String format, Object... args)
    {
        if (LOG && null != format && null != args)
        {
            
            String content = String.format(format, args);
            
            Log.d(tag, content);
        }
    }
    
    public static void log(String tag, Object... args)
    {
        if (LOG && null != args)
        {
            
            // 计算日志的长度，减少内存开销。 估计意义不大。
            // int length = 0;
            // for(Object obj : args) {
            // length += obj.toString().length();
            // }
            
            StringBuffer buffer = new StringBuffer(args.length * 10);
            for (Object obj : args)
            {
                buffer.append(obj).append(" ");
            }
            
            Log.d(tag, buffer.toString());
        }
    }
}
