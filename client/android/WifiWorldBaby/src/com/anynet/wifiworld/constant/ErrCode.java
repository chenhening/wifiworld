/*
 * 文件名称 : ErrCode.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-9-16, 下午1:50:32
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.constant;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class ErrCode
{
    //--通用接口--
    
    //登录态失效，校验sessionid失败
    public static int LOGIN_STAT_ERROR = -501;
    
    //参数不合法，指传入的必要参数为空，cookie值没有等错误
    public static int PARAM_ERROR = 13;
    
    //正常、接口访问成功
    public static int OK = 0;
    
    //系统繁忙，后台数据库异常或访问其他接口失败
    public static int SYSTEM_BUSY = 99;
    
    //--Privilege通用接口--
    
  
    //非白名单
    public static int  WHITE_FAIL_NOPRIVILEGE = -100;
    
    
    public static int  WHITE_FAIL_OLDPRIVILEGE = -1;
    
    
    //资料未完善
    public static int  WHITE_FAIL_MATERIAL_NEED_COMPLETE = -101;
    
    
    
}
