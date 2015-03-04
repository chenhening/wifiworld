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

import java.util.Arrays;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 我的矿机
 * <p>
 */
public class HistoryStatResp extends CommonResp
{
    
    /**
     * 最大历史记录数量,包含今天
     */
    public static int HISTORY_COUNT = 8;
    
    public HistoryStatResp()
    {
    }
    
    /**
     * produces最近8天的产出（包含今天）, 保留小数点后3位
     */
    @Expose
    @SerializedName("pds")
    private double produces[];
    
    /**
     * 历史累计
     */
    @Expose
    @SerializedName("h_t")
    private double historyTotal;
    
    /**
     * 当前时间
     */
    @Expose
    @SerializedName("dt")
    private long date;
    
    public long getDate()
    {
        return date;
    }
    
    public void setDate(long date)
    {
        this.date = date;
    }
    
    public double[] getProduces()
    {
        return produces;
    }
    
    public double getProduce(int index)
    {
        if (null != produces && index >= 0 && index < produces.length)
        {
            return produces[index];
        }
        return 0;
    }
    
    public double getMaxProduce()
    {
        double max = 0;
        if (null != produces)
        {
            for (double m : produces)
            {
                max = Math.max(max, m);
            }
        }
        return max;
    }
    
    public void setProduces(double[] produces)
    {
        this.produces = produces;
    }
    
    public double getHistoryTotal()
    {
        return historyTotal;
    }
    
    //最近7日统计
    public double getWeekTotal()
    {
        
        double weekTotal = 0;
        for (double i : produces)
        {
            weekTotal = weekTotal + i;
        }
        return weekTotal;
    }
    
    public void setHistoryTotal(double historyTotal)
    {
        this.historyTotal = historyTotal;
    }
    
    @Override
    public String toString()
    {
        return super.toString() + " " + "HistoryStatResp [produces=" + Arrays.toString(produces) + ", historyTotal=" + historyTotal + ", date=" + date + "]";
    }
    
}
