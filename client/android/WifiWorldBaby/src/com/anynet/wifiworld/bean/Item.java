/*
 * 文件名称 : Item.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014-9-2, 下午8:02:54
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
 * 市场订单项
 * <p>
 */
public class Item
{
    
    /**
     * 当前时间
     */
    @Expose
    @SerializedName("dt")
    private String date;
    
    /**
     * 挂单数量
     */
    @Expose
    @SerializedName("total")
    private double total;
    
    /**
     * 价格
     */
    @Expose
    @SerializedName("p")
    private double price;
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public double getTotal()
    {
        return total;
    }
    
    public void setTotal(double total)
    {
        this.total = total;
    }
    
    public double getPrice()
    {
        return price;
    }
    
    public void setPrice(double price)
    {
        this.price = price;
    }
    
}
