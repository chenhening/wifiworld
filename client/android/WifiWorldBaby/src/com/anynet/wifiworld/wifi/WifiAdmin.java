package com.anynet.wifiworld.wifi;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
	private static final String TAG = WifiAdmin.class.getSimpleName();
	
	private static  WifiAdmin wifiAdmin = null;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo = null;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfigurations;
    private WifiLock mWifiLock;
    
    public enum WifiCipherType {
    	WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }
    
    public static WifiAdmin getInstance(Context context) {
        if(wifiAdmin == null) {
            wifiAdmin = new WifiAdmin(context);
            return wifiAdmin;
        }
        return null;
    }
    
    private WifiAdmin(Context context) {
        //get WIFI manager object
        this.mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //get WIFI info object
        this.mWifiInfo = mWifiManager.getConnectionInfo();
    }
    
    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
       if (!this.openWifi()) {  
            return false;  
       }
       
       while (this.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
    	   try {
    		   Thread.currentThread();
    		   Thread.sleep(100);
           } catch (InterruptedException ie) {
        	   
           }  
       }
       
       WifiConfiguration wifiConfig = null;
       boolean isExisted = false;
       int networkId = -1;
       for (int i = this.mWifiConfigurations.size() - 1; i >= 0; i--)
       {
    	   wifiConfig = mWifiConfigurations.get(i);
    	   String wifiSSID = wifiConfig.SSID;
           if (wifiSSID.substring(1, wifiSSID.length()-1).equals(SSID)) {
               networkId = wifiConfig.networkId;
               isExisted = true;
               break;
           }
       }
       if (!isExisted) {
    	   wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
           if (wifiConfig == null) {
        	   Log.i(TAG, "Failed to create wifi configuration info");
        	   return false;  
           }
           networkId = mWifiManager.addNetwork(wifiConfig);
           if (networkId != -1)
           {
        	   mWifiManager.saveConfiguration();
           }
       } else {
    	   this.mWifiInfo = mWifiManager.getConnectionInfo();
           if (this.mWifiInfo != null && SSID.equals(this.mWifiInfo.getSSID())) {
        	   Log.i(TAG, "Have connect wifi: " + SSID);
               return true;
           }
           
           int encryptionType = getKeyMgmtType(Type);
           wifiConfig.allowedKeyManagement.set(encryptionType);
//           if (encryptionType != 0)
//           {
//        	   wifiConfig.preSharedKey = Password;
//           }
           mWifiManager.updateNetwork(wifiConfig);
       }
       
       boolean bRet = false;
       if (networkId != -1) {
           mWifiManager.disconnect();
           bRet = mWifiManager.enableNetwork(networkId, true);
       }
         
       return bRet;
    }  

    public WifiConfiguration isExsits(String str) {
        Iterator<WifiConfiguration> localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if(!localIterator.hasNext()) return null;
            localWifiConfiguration = (WifiConfiguration) localIterator.next();
        }while(!localWifiConfiguration.SSID.equals("\"" + str + "\""));
        return localWifiConfiguration;
    }
    
    private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
    	WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if(Type == WifiCipherType.WIFICIPHER_NOPASS) {
        	config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
       } else if(Type == WifiCipherType.WIFICIPHER_WEP) {
           config.preSharedKey = "\""+Password+"\"";
           config.hiddenSSID = true;
           config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
           config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
           config.wepTxKeyIndex = 0;
       } else if(Type == WifiCipherType.WIFICIPHER_WPA) {
    	   config.preSharedKey = Password;
    	   config.hiddenSSID = true;
    	   config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
    	   config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
    	   config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
    	   config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
    	   config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
    	   //maybe for WiFi AP
//    	   config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//         config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
    	   config.status = WifiConfiguration.Status.ENABLED;
       } else {
           return null;
       }
       return config;
    }
    
    private int parseBitSet(BitSet kmtBitSet) {
    	for (int i = 0; i < kmtBitSet.size(); i++) {
			if (kmtBitSet.get(i)) {
				return i;
			}
		}
    	
    	return -1;
    }
    
    public WifiAdmin.WifiCipherType getWifiType(BitSet kmtBitSet) {
    	int type = parseBitSet(kmtBitSet);
		if (type == WifiConfiguration.KeyMgmt.NONE) {
			return WifiAdmin.WifiCipherType.WIFICIPHER_NOPASS;
		} else if (type == WifiConfiguration.KeyMgmt.WPA_PSK) {
			return WifiAdmin.WifiCipherType.WIFICIPHER_WPA;
		} else {
			return WifiAdmin.WifiCipherType.WIFICIPHER_WEP;
		}
	}
    
    private int getKeyMgmtType(WifiCipherType type) {  
        if (type == WifiCipherType.WIFICIPHER_NOPASS) {
            return WifiConfiguration.KeyMgmt.NONE;  
        }
        if (type == WifiCipherType.WIFICIPHER_WEP) {
        	//WPA_EAP
            return WifiConfiguration.KeyMgmt.IEEE8021X;
        }
        else if (type == WifiCipherType.WIFICIPHER_WPA) {
            return WifiConfiguration.KeyMgmt.WPA_PSK;
        }
        
        return WifiConfiguration.KeyMgmt.NONE;
    }

    public int getWifiStrength(int dBm) {
    	if(dBm <= -100)
            return 0;
        else if(dBm >= -50)
        	return 100;
        else
        	return 2 * (dBm + 100);
    }
    
    //open WIFI
    public Boolean openWifi() {
    	Boolean bRet = true;
        if (!this.mWifiManager.isWifiEnabled()) {
            bRet = this.mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }
    
    //close WIFI
    public void closeWifi() {
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        }
    }
    
    //check WIFI status
    public int checkState() {
        return this.mWifiManager.getWifiState();
    }
    
    //lock WIFI, when download large file
    public void acquireWifiLock() {
        this.mWifiLock.acquire();
    }
    
    //unlock WIFI
    public void releaseWifiLock() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.acquire();
        }
    }
    
    //create WIFI lock
    public void createWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("test");
    }
    
    //get WIFI which configurated
    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfigurations;
    }
    
    //connect WiFi which configurated
    public void connetionConfiguration(int index) {
        if (index > this.mWifiConfigurations.size()) {
            return;
        }
        this.mWifiManager.enableNetwork(this.mWifiConfigurations.get(index).networkId, true); 
    }
    
    public void startScan() {
    	Log.i(TAG, "start to scan wifi nearby");
    	
    	//may need some time to open WiFi
    	while (this.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
    		try {
    			Thread.currentThread();
    			Thread.sleep(100);
            } catch (InterruptedException ie) {
            	
            }
        }
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();  
    }  
    
    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }
    
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + Integer.valueOf(i + 1).toString() + ":");
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }
    
    public WifiInfo getWifiConnection() {
    	this.mWifiInfo = this.mWifiManager.getConnectionInfo();
    	return this.mWifiInfo;
    }
    
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
    
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
    
    public String getSSID() {
    	mWifiInfo = mWifiManager.getConnectionInfo();
    	return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }
    
    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
    
    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }
    
    public void connectWifi(WifiConfiguration configuration) {
        int wcgId = this.mWifiManager.addNetwork(configuration);
        this.mWifiManager.enableNetwork(wcgId, true);
    }
    
    public void disConnectionWifi(int netId){
        this.mWifiManager.disableNetwork(netId);
        this.mWifiManager.disconnect();
    }
}
