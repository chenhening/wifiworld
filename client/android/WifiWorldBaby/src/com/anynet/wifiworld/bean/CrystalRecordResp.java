/*
 * 文件名称 : CrystalRecordResp.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014年11月27日, 下午3:03:44
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class CrystalRecordResp extends CommonResp
{
    
    @Expose
    @SerializedName("cp")
    public int currentPage;
    
    public int nextPage;
    
    @Expose
    @SerializedName("tp")
    public int totalPage;
    
    @Expose
    @SerializedName("r_h_a")
    public double  totalValue;
    @Expose
    @SerializedName("ioi")
    public RecordItem[] recordList;
    
    public static class RecordItem
    {
        
        public static final int TYPE_EXPENSE = 1;
        
        public static final int TYPE_INCOME = 0;
        
        /**
         * 出账或入账: 0为入账，1为出账
         */
        @Expose
        @SerializedName("d")
        public int type;
        
        /**
         * 记账名称
         */
        @Expose
        @SerializedName("cn")
        public String text;
        
        /**
         * 该账目建立时间的时间戳
         */
        @Expose
        @SerializedName("ct")
        public long date;
        
        /**
         * 金额大小
         */
        @Expose
        @SerializedName("c")
        public long amount;
        
        /**
         * 可能有的扩展信息
         */
        @Expose
        @SerializedName("ext")
        public String ext;
        
        private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        public String getDateString()
        {
            
            Date d = new Date( date * 1000);
            return sdf.format(d);
            
        }
    }
    
    
    public static  String TimeStamp2Date(String timestampString, String formats){    
        Long timestamp = Long.parseLong(timestampString)*1000;    
        String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));    
        return date;    
      }
    
}
