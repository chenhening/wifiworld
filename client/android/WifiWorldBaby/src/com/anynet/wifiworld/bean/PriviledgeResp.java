/*
 * 文件名称 : CommonResp.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014-9-2, 下午4:57:15
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
 * 请在这里增加文件描述
 * <p>
 */
public class PriviledgeResp extends CommonResp
{
    /**
     * 邮箱
     */
    @Expose
    @SerializedName("mid")
    private String mid;
    
    
    /**
     * 昵称
     */
    @Expose
    @SerializedName("phone")
    private String phone;

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
   
}
