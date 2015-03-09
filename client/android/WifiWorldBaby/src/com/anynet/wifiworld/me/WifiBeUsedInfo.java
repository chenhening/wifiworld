package com.anynet.wifiworld.me;

import java.sql.Time;

import cn.bmob.v3.BmobObject;

public class WifiBeUsedInfo extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String Wifiid; //wifi的唯一标识
	public String NetUserid; //区别于Userid，这里指的是蹭网的用户id
	public Time LoginTime; //用户登陆网络的时间
	public Time LogoutTime; //用户退出网络的时间
}
