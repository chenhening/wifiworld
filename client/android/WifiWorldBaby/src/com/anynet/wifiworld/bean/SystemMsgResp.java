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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 获取系统信息
 * <p>
 */
public class SystemMsgResp extends CommonResp
{

    /**
     * 过去7天产量
     */
    @Expose
    @SerializedName("m")
    public Msg msg[];
    
    /**
     * 当前页数
     */
    @Expose
    @SerializedName("cp")
    public int currentPage;
    
    
    /**
     * 总页数
     */
    @Expose
    @SerializedName("tp")
    public int totalPage;

    public Msg[] getMsg()
    {
        return msg;
    }

    public void setMsg(Msg[] msg)
    {
        this.msg = msg;
    }

   
    
    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public int getTotalPage()
    {
        return totalPage;
    }

    public void setTotalPage(int totalPage)
    {
        this.totalPage = totalPage;
    }

    public String getAllMsg()
    {
        
        StringBuilder sbBuilder = new StringBuilder();
        
        for (Msg m:msg)
        {
            sbBuilder.append(m.getContent()).append("  ");
        }
        
        return sbBuilder.toString().trim();
        
    }
    
    
    
    
}
