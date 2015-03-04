/*
 * 文件名称 : KeyValue.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-10-24, 上午10:16:08
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
public  class KeyValue
{
    
    @Expose
    @SerializedName("k")
    private String  key;
    
    @Expose
    @SerializedName("v")
    private String  value;
    
 


    public String getKey()
    {
        return key;
    }


    public void setKey(String key)
    {
        this.key = key;
    }


    public String getValue()
    {
        return value;
    }


    public void setValue(String value)
    {
        this.value = value;
    }


    
    
    
}