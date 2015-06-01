/*
 * 文件名称 : AppInfoUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-9-16, 上午10:28:34
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class AppInfoUtil
{
    
    /**
     * 检查应用程序是否在Debug状态
     * @param context
     * @return
     */
    public static boolean isApkDebugable(Context context)
    {
        try
        {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        catch (Exception e)
        {
        }
        return false;
    }
    
    
    
    public static String getVersionName(Context context)
    {
        
        String version;
        try
        {
            //获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionName;
            return version;
        }
        
        catch (NameNotFoundException e)
        {

            return "";
        }
    }

    public static long getLastUpdateTime(Context context) {
        List<PackageInfo> pkgInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo:pkgInfos) {
            if (packageInfo.packageName.equals(context.getPackageName())){
                return  packageInfo.lastUpdateTime;
            }
        }
        return 0;
    }
    
    
    public static void WriteTxtFile(String strcontent,String strFilePath)
    {
      //每次写入时，都换行写
      String strContent=strcontent+"\n";
      try {
           File file = new File(strFilePath);
           if (!file.exists()) {
            Log.d("TestFile", "Create the file:" + strFilePath);
            file.createNewFile();
           }
           RandomAccessFile raf = new RandomAccessFile(file, "rw");
           raf.seek(file.length());
           raf.write(strContent.getBytes());
           raf.close();
      } catch (Exception e) {
           Log.e("TestFile", "Error on write File.");
          }
    }
    
}
