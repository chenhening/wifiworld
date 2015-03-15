package com.anynet.wifiworld.data;

import java.sql.Time;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class UserWifiUsedInfo extends BmobObject {

	private static final long serialVersionUID = 1L;

	public String Userid; //用户wifi用户的if
	public BmobGeoPoint Geometry; //用户使用网络的位置
	public Time LoginTime; //用户登陆网络的时间
	public Time LogoutTime; //用户退出网络的时间
	
	
}
