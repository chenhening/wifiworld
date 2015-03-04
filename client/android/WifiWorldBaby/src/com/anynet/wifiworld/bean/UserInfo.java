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
public class UserInfo
{
    
    /**
     * 用户id
     */
    @Expose
    @SerializedName("id")
    private String userId;
    
    
    
    /**
     * 可用水晶币数量
     */
    @Expose
    @SerializedName("cin_a")
    private double coinAmount;
    
    
    
    /**
     * 可用水晶数量
     */
    @Expose
    @SerializedName("cs_a")
    private double crystalAmount;
    
    
    
    /**
     * 密码状态
     */
    @Expose
    @SerializedName("p")
    private int passwdStat;
    
}
