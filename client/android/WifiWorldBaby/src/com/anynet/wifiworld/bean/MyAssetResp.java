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

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 主要用于水晶首页
 * <p>
 */
public class MyAssetResp extends CommonResp implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 历史总额
     */
    @Expose
    @SerializedName("r_h_a")
    private double historyAmount;
    
    /**
     * 红水晶可用余额
     */
    @Expose
    @SerializedName("r_can_use")
    private double redCanUse;
    
    /**
     * 蓝水晶未到账
     */
    @Expose
    @SerializedName("wc_pkg")
    private double wechatPkg;
    
    /**
     * 蓝水晶订单中
     */
    @Expose
    @SerializedName("remark")
    private String remark;
    
    
    /**
     * 蓝水晶订单中
     */
    @Expose
    @SerializedName("new_remark")
    private String newRemark;
    
    

    public String getNewRemark()
    {
        return newRemark;
    }

    public void setNewRemark(String newRemark)
    {
        this.newRemark = newRemark;
    }

    public double getHistoryAmount()
    {
        return historyAmount;
    }

    public void setHistoryAmount(double historyAmount)
    {
        this.historyAmount = historyAmount;
    }

    public double getRedCanUse()
    {
        return redCanUse;
    }

    public void setRedCanUse(double redCanUse)
    {
        this.redCanUse = redCanUse;
    }

    public double getWechatPkg()
    {
        return wechatPkg;
    }

    public void setWechatPkg(double wechatPkg)
    {
        this.wechatPkg = wechatPkg;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    
    
   
    
}
