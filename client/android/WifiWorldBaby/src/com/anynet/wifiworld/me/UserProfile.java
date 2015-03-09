package com.anynet.wifiworld.me;

import cn.bmob.v3.BmobObject;

public class UserProfile extends BmobObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1348016396552502202L;
	
	public String Userid;
	public String PhoneNumber;
	public String Password;
	public UserType Type;
	public float Wallet;
}
