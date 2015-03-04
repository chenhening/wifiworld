/*
 * 文件名称 : HistoryStatResp.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014-9-2, 下午4:59:42
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 水晶监控
 * <p>
 */
public class LastSpeedStatResp extends CommonResp
{
    
    public static final int COUNT = 24;
    
    /**
     * 平均速度数组
     */
    @Expose
    @SerializedName("sds")
    private double speeds[];
    
    /**
     * 当前时间
     */
    @Expose
    @SerializedName("dt")
    private long date;
    
    public LastSpeedStatResp()
    {
    }

    public boolean isEmpty() {
        return  speeds == null;
    }
    
    public void setSpeeds(double[] sps)
    {
        this.speeds = sps;
    }
    
    /**
     * 
     * 如果date是2014-09-16 12:30:21 如果传入的offsetHours=1， 则返回11:00
     * 如果传入的offsetHours=2，则返回10:00
     * 
     * @param offsetHours
     *            当前时间之前的offsetHours个小时。
     * @return 时间字符串，几点，分钟数被省略了，用0表示。
     */
    public String getTimeByOffset(int offsetHours)
    {
        long time = date * 1000 - 60 * 60 * 1000L * (24 - offsetHours);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.get(Calendar.HOUR_OF_DAY) + ":00";
    }
    
    public double getSpeed(int index)
    {
        if (speeds != null && index >= 0 && index < speeds.length)
        {
            return speeds[index];
        }
        return 0;
    }
    
    public double getMaxSpeed()
    {
        double max = 0;
        if (null == speeds)
        {
            return max;
        }
        for (double s : speeds)
        {
            max = Math.max(s, max);
        }
        return max;
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd kk:MM:ss");
        sb.append("date:").append(sdf.format(new Date(date * 1000))).append(" speed:");
        if (null != speeds)
        {
            for (int i = 0; i < speeds.length; i++)
            {
                sb.append(speeds[i]).append(" ");
            }
        }
        return sb.toString();
    }
    
}
