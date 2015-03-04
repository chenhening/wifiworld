/*
 * 文件名称 : DeviceStatResp.java
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

/**
 * 设备状态
 * <p>
 */
public class DeviceStatResp extends CommonResp
{
    
    public static final int DEVICE_STATE_PAUSE = 0;
    
    public static final int DEVICE_STATE_ON = 1;
    
    public static final int DEVICE_STATE_OFFLINE = 2;
    
    /**
     * 设备id
     */
    @Expose
    @SerializedName("dv_id")
    private String deviceId;
    
    /**
     * 0 暂停 1 开启 2离线 st=switch
     */
    @Expose
    @SerializedName("st")
    private int deviceSwitch = DEVICE_STATE_OFFLINE;
    
    /**
     * 上传速度
     */
    @Expose
    @SerializedName("s")
    private String speed;
    
    
    /**
     * 工作矿机数量
     */
    @Expose
    @SerializedName("c")
    private int amount;
    
    @Expose
    @SerializedName("speed")
    private String limit;
    
    
    
    
    
    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public String getDeviceId()
    {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    public int getDeviceSwitch()
    {
        return deviceSwitch;
    }
    
    public void setDeviceSwitch(int deviceSwitch)
    {
        this.deviceSwitch = deviceSwitch;
    }
    
    public String getSpeed()
    {
        return speed;
    }
    
    public void setSpeed(String speed)
    {
        this.speed = speed;
    }
    
    public double getSpeedValue()
    {
        try
        {
            return Double.parseDouble(speed);
        }
        catch (Exception e)
        {
            return 0;
        }
    }
    
    public String getLimit()
    {
        return limit;
    }
    
    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return super.toString() + " devideId:" + deviceId + " state:" + deviceSwitch + " limit:" + limit;
    }
    
}
