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
 * 主要用于水晶首页,挖矿收益：今日入账数量和未收取的数量
 * <p>
 */
public class CrystalIncomeSummaryResp extends CommonResp implements Serializable
{
    
    /**
	 * 
	 */
    private static final long serialVersionUID = -9025168434609843257L;
    
    /**
     * 今天入账红水晶
     */
    @Expose
    @SerializedName("td_in_a")
    private String todayIncome;
    
    /**
     * 未收取红水晶
     */
    @Expose
    @SerializedName("td_not_in_a")
    private String uncollect;
    
    /**
     * 新宝箱数量
     */
    @Expose
    @SerializedName("ugft")
    private int newBoxNum;
    
    public int getNewBoxNum()
    {
        return newBoxNum;
    }
    
    public void setNewBoxNum(int newBoxNum)
    {
        this.newBoxNum = newBoxNum;
    }
    
    public String getTodayIncome()
    {
        return todayIncome;
    }
    
    public void setTodayIncome(String todayIncome)
    {
        this.todayIncome = todayIncome;
    }
    
    public String getUncollect()
    {
        return uncollect;
    }
    
   
    
    public void setUncollect(String uncollect)
    {
        this.uncollect = uncollect;
    }
    
    @Override
    public String toString()
    {
        return super.toString() + " todayIncome:" + todayIncome + " uncollect:" + uncollect;
    }
    
}
