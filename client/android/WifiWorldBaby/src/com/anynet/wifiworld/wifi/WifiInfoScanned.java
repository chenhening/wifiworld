package com.anynet.wifiworld.wifi;

import java.io.Serializable;

import cn.bmob.v3.datatype.BmobGeoPoint;

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
	private BmobGeoPoint mGeometry;
	
	private float mRating;
	private int mRanking;
	private int ConnectedTimes;
	private int ConnectedDuration;
	
	public float getRating() {
		return mRating;
	}

	public void setRating(float rating) {
		mRating = rating;
	}

	public int getRanking() {
		return mRanking;
	}

	public void setRanking(int ranking) {
		mRanking = ranking;
	}

	public int getConnectedTimes() {
		return ConnectedTimes;
	}

	public void setConnectedTimes(int connectedTimes) {
		ConnectedTimes = connectedTimes;
	}

	public int getConnectedDuration() {
		return ConnectedDuration;
	}

	public void setConnectedDuration(int connectedDuration) {
		ConnectedDuration = connectedDuration;
	}
	
	public WifiInfoScanned() {
		mWifiName = null;
		mWifiMAC = null;
		mWifiPwd = null;
		mWifiEncryptType = null;
		mWifiStrength = 0;
		mRemark = null;
		mGeometry = null;
	}
	
	public WifiInfoScanned(String name) {
		mWifiName = name;
		mWifiMAC = null;
		mWifiPwd = null;
		mWifiEncryptType = null;
		mWifiStrength = 0;
		mRemark = null;
		mGeometry = null;
	}
	
	public WifiInfoScanned(String name, String mac, String pwd, String type, Integer strenghth,
			BmobGeoPoint geometry, String remark) {
		mWifiName = name;
		mWifiMAC = mac;
		mWifiPwd = pwd;
		mWifiEncryptType = type;
		mWifiStrength = strenghth;
		mGeometry = geometry;
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

	public BmobGeoPoint getGeometry() {
		return mGeometry;
	}

	public void setGeometry(BmobGeoPoint mGeometry) {
		this.mGeometry = mGeometry;
	}
	
}
