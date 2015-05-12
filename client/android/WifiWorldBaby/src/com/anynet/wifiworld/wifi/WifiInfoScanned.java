package com.anynet.wifiworld.wifi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.data.WifiWhite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private BmobGeoPoint mGeometry;
	private String mRemark;
	private String Alias;
	private String Sponser;
	
	private int networkId = -1;
	
	private boolean isAuthWifi = false;
	private boolean isLocalSave = false;
	
	private byte[] mWifiLogo;
	private String mBanner;
	
	private float mRating;
	private int mRanking;
	private long ConnectedTimes;
	private int ConnectedDuration;
	
	private List<String> mComments = new ArrayList<String>();
	private List<String> mMessages = new ArrayList<String>();
	
	private List<WifiWhite> mWifiWhites;
	//public int mWifiDistance;
	
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
	
	public WifiInfoScanned(String name, String mac, String pwd, String type, Integer strength,
			BmobGeoPoint geometry, String remark) {
		mWifiName = name;
		mWifiMAC = mac;
		mWifiPwd = pwd;
		mWifiEncryptType = type;
		mWifiStrength = strength;
		mGeometry = geometry;
		mRemark = remark;
	}
	
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

	public long getConnectedTimes() {
		return ConnectedTimes;
	}

	public void setConnectedTimes(long connectedTimes) {
		ConnectedTimes = connectedTimes;
	}

	public int getConnectedDuration() {
		return ConnectedDuration;
	}

	public void setConnectedDuration(int connectedDuration) {
		ConnectedDuration = connectedDuration;
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

	public void setWifiLogo(byte[] wifiLogo) {
		this.mWifiLogo = wifiLogo;
	}
	
	public Bitmap getWifiLogo() {
		if (mWifiLogo != null)
			return Bytes2Bimap(mWifiLogo);
		else
			return null;
	}
	
	public Bitmap Bytes2Bimap(byte[] b) {
		if (b != null && b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public List<String> getComments() {
		return mComments;
	}

	public void setComments(List<String> mComments) {
		this.mComments = mComments;
	}
	
	public void addComment(String comment) {
		mComments.add(comment);
	}

	public List<String> getMessages() {
		return mMessages;
	}

	public void setMessages(List<String> mMessages) {
		this.mMessages = mMessages;
	}
	
	public void addMessage(String message) {
		mMessages.add(message);
	}

	public boolean isAuthWifi() {
		return isAuthWifi;
	}

	public void setAuthWifi(boolean isAuthWifi) {
		this.isAuthWifi = isAuthWifi;
	}

	public String getBanner() {
		return mBanner;
	}

	public void setBanner(String mBanner) {
		this.mBanner = mBanner;
	}

	public String getAlias() {
		return Alias;
	}

	public void setAlias(String alias) {
		Alias = alias;
	}

	public String getSponser() {
	    return Sponser;
    }

	public void setSponser(String sponser) {
	    Sponser = sponser;
    }

	public boolean isLocalSave() {
		return isLocalSave;
	}

	public void setLocalSave(boolean isLocalSave) {
		this.isLocalSave = isLocalSave;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	public List<WifiWhite> getWifiWhites() {
		return mWifiWhites;
	}

	public void setWifiWhites(List<WifiWhite> mWifiWhites) {
		this.mWifiWhites = mWifiWhites;
	}
	
}
