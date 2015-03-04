/*
 * 文件名称 : AppConfigResp.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014-10-10, 下午4:59:42
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
 * 蓝水晶配置数组
 * <p>
 */
public class AppConfigResp extends CommonResp implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    /**
     * 配置数组
     */
    @Expose
    @SerializedName("c")
    private KeyValue config[];


    public KeyValue[] getConfig()
    {
        return config;
    }


    public void setConfig(KeyValue[] config)
    {
        this.config = config;
    }
    
    
    
   
    
}
