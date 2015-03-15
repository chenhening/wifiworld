package com.anynet.wifiworld.me;

import android.graphics.Bitmap;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class WifiProfile extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String Wifiid; //wifi的唯一标识
	public String Macid; //mac地址
	public String Password; //wifi的密码，经过base64后保存
	public String Userid; //绑定的用户账号，wifi提供者
	public WifiType Type; //wifi的类型
	public Bitmap Logo; //用户自定义的logo信息
	public String Alias; //用户自定义的wifi别名
	public BmobGeoPoint Geometry; //WiFi的地理位置
	public String Banner; //wifi的展示页内容(TODO(binfei)还需要定义更多)
	public float Income; //wifi 收入
}
