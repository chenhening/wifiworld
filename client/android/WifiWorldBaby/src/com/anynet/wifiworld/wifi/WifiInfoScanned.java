package com.anynet.wifiworld.wifi;

import java.io.Serializable;

public class WifiInfoScanned implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mWifiName;
	private String mWifiMAC;
	private String mWifiPwd;
	private String mWifiEncryptType;
	private Integer mWifiStrength;
	private String mRemark;
	
	public WifiInfoScanned() {
		mWifiName = null;
		mWifiMAC = null;
		mWifiPwd = null;
		mWifiEncryptType = null;
		mWifiStrength = 0;
		mRemark = null;
	}
	
	public WifiInfoScanned(String name, String mac, String pwd, String type, Integer strenghth, String remark) {
		mWifiName = name;
		mWifiMAC = mac;
		mWifiPwd = pwd;
		mWifiEncryptType = type;
		mWifiStrength = strenghth;
		mRemark = remark;
	}

	public String getWifiMAC() {
		return mWifiMAC;
	}

	public void setWifiMAC(String wifiMAC) {
		this.mWifiMAC = wifiMAC;
	}

	public String getRemark() {
		return mRemark;
	}

	public void setRemark(String remark) {
		this.mRemark = remark;
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

	public String getEncryptType() {
		return mWifiEncryptType;
	}

	public void setEncryptType(String wifiType) {
		this.mWifiEncryptType = wifiType;
	}

	public Integer getWifiStrength() {
		return mWifiStrength;
	}

	public void setWifiStrength(Integer wifiStrength) {
		this.mWifiStrength = wifiStrength;
	}
	
}
