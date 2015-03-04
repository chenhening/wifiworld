/*
 * 文件名称 : Msg.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-9-18, 下午3:52:22
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
public class Msg
{
    
    /**
     * id
     */
    @Expose
    @SerializedName("id")
    private long id;
    
    /**
     * 标题
     */
    @Expose
    @SerializedName("t")
    private String title;
    
    
    /**
     * content
     */
    @Expose
    @SerializedName("c")
    private String content;
    
    /**
     * 消息更新时间
     */
    @Expose
    @SerializedName("dt")
    private long date;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    
    
    
}
