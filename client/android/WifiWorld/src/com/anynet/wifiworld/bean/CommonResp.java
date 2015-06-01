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
public class CommonResp
{
    
    protected final String TAG = this.getClass().getSimpleName();
    
    /**
     * 返回值
     */
    @Expose
    @SerializedName("r")
    private int returnCode;
    
    /**
     * 返回值描述
     */
    @Expose
    @SerializedName("rd")
    private String returnDesc;
    
    public int getReturnCode()
    {
        return returnCode;
    }
    
    public void setReturnCode(int returnCode)
    {
        this.returnCode = returnCode;
    }
    
    public String getReturnDesc()
    {
        return returnDesc;
    }
    
    public void setReturnDesc(String returnDesc)
    {
        this.returnDesc = returnDesc;
    }
    
    public boolean isOK()
    {
        return returnCode == 0;
    }
    
    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + " rtnCode:" + returnCode + " rtnDes:" + returnDesc;
    }
    
}
