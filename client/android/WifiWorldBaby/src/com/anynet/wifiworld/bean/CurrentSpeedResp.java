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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.anynet.wifiworld.util.XLLog;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class CurrentSpeedResp extends CommonResp
{
    
    /**
     * 过去6天产量
     */
    @Expose
    @SerializedName("s")
    private String speed;
    
    public String getSpeed()
    {
        return speed;
    }
    
    public double getSpeedValue()
    {
        try
        {
            double tempCurrentSpeed = Double.parseDouble(speed);
            return tempCurrentSpeed;
        }
        catch (Exception e)
        {
            XLLog.e(TAG, e.getMessage(), " currentSpeed:", speed);
            return 0;
        }
    }
    
    @Override
    public String toString()
    {
        
        return super.toString() + " speed:" + speed;
    }
    
    public void setSpeed(String speed)
    {
        this.speed = speed;
    }
}
