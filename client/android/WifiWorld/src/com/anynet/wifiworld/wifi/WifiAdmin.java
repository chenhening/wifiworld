package com.anynet.wifiworld.wifi;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
	private static final String TAG = WifiAdmin.class.getSimpleName();
	
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfigurations;
    private WifiLock mWifiLock;
    
    public WifiAdmin(Context context) {
        //get wifimanager object
        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        
        //get wifiinfo object
        mWifiInfo = mWifiManager.getConnectionInfo();
    }
    
    //open wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }
    
    //close wifi
    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
    
    //check wifi status
    public int checkState() {
        return mWifiManager.getWifiState();
    }
    
    //lock wifi, when download large file
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }
    
    //unlock wifi
    public void releaseWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }
    
    //create wifilock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }
    
    //get wifi which configurated
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }
    
    //connect wifi which configurated
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true); 
    }
    
    public void startScan() {
    	Log.i(TAG, "star to scan wifi nearby");
    	
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();  
    }  
    
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }
    
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + Integer.valueOf(i + 1).toString() + ":");
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }
    
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
    
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
    
    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
    
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }
    
    public void connectWifi(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }
    
    public void disConnectionWifi(int netId){
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
}
