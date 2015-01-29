package com.anynet.wifiworld.wifi;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfigurations;
    WifiLock mWifiLock;
    public WifiAdmin(Context context) {
        //get wifimanager object
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //get wifiinfo object
        mWifiInfo=mWifiManager.getConnectionInfo();
    }
    //open wifi
    public void openWifi() {
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }
    //close wifi
    public void closeWifi() {
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
    //check wifi status
    public int checkState() {
        return mWifiManager.getWifiState();
    }
    //lock wifi
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }
    //unlock wifi
    public void releaseWifiLock() {
        if(mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }
    //create wifilock
    public void createWifiLock() {  
        mWifiLock=mWifiManager.createWifiLock("test");  
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
        //连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true); 
    }
    public void startScan() {
        mWifiManager.startScan();
        //得到扫描结果
        mWifiList=mWifiManager.getScanResults();
        //得到配置好的网络连接  
        mWifiConfigurations=mWifiManager.getConfiguredNetworks();  
    }  
    //得到网络列表  
    public List<ScanResult> getWifiList(){  
        return mWifiList;  
    }  
    //查看扫描结果  
    public StringBuffer lookUpScan(){  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<mWifiList.size();i++){  
            sb.append("Index_" + new Integer(i + 1).toString() + ":");  
             // 将ScanResult信息转换成一个字符串包    
            // 其中把包括：BSSID、SSID、capabilities、frequency、level    
            sb.append((mWifiList.get(i)).toString()).append("\n");  
        }  
        return sb;    
    }  
    public String getMacAddress(){  
        return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();  
    }  
    public String getBSSID(){  
        return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();  
    }  
    public int getIpAddress(){  
        return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();  
    }  
    //得到连接的ID  
    public int getNetWordId(){  
        return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();  
    }  
    //得到wifiInfo的所有信息  
    public String getWifiInfo(){  
        return (mWifiInfo==null)?"NULL":mWifiInfo.toString();  
    }  
    //添加一个网络并连接  
    public void addNetWork(WifiConfiguration configuration){  
        int wcgId=mWifiManager.addNetwork(configuration);  
        mWifiManager.enableNetwork(wcgId, true);  
    }  
    //断开指定ID的网络  
    public void disConnectionWifi(int netId){  
        mWifiManager.disableNetwork(netId);  
        mWifiManager.disconnect();  
    }  
}  
