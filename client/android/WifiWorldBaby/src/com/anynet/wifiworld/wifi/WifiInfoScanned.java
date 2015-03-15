package com.anynet.wifiworld.wifi;

import java.io.Serializable;

public class WifiInfoScanned implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mWifiName;
	private String mWifiPwd;
	private String mWifiType;
	private Integer mWifiStrength;
	
	public WifiInfoScanned(String name, String pwd, String type, Integer strenghth) {
		mWifiName = name;
		mWifiPwd = pwd;
		mWifiType = type;
		mWifiStrength = strenghth;
	}

	public String getWifiName() {
		return mWifiName;
	}

	public void setWifiName(String wifiName) {
		this.mWifiName = wifiName;
	}

	public String getWifiPwd() {
		return mWifiPwd;
	}

	public void setWifiPwd(String wifiPwd) {
		this.mWifiPwd = wifiPwd;
	}

	public String getWifiType() {
		return mWifiType;
	}

	public void setWifiType(String wifiType) {
		this.mWifiType = wifiType;
	}

	public Integer getWifiStrength() {
		return mWifiStrength;
	}

	public void setWifiStrength(Integer wifiStrength) {
		this.mWifiStrength = wifiStrength;
	}
	
}
