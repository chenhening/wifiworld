package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.util.Log;

public class WifiAdmin {
	private static final String TAG = WifiAdmin.class.getSimpleName();
	private static final int MAX_PRIORITY = 99999;
	public static final WifiSecurities ConfigSec = WifiSecurities.newInstance();
	
	private static WifiAdmin wifiAdmin = null;
    private WifiManager mWifiManager;
    private WifiLock mWifiLock;
    
    public static WifiAdmin getInstance(Context context) {
        if(wifiAdmin == null) {
            wifiAdmin = new WifiAdmin(context);
            return wifiAdmin;
        }
        return null;
    }
    
    private WifiAdmin(Context context) {
        //get WIFI manager object
        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }
    
//    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
//       if (!this.openWifi()) {
//            return false;
//       }
//       
//       while (this.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
//    	   try {
//    		   Thread.currentThread();
//    		   Thread.sleep(100);
//           } catch (InterruptedException ie) {
//        	   
//           }
//       }
//       
//       WifiConfiguration wifiConfig = null;
//       boolean isExisted = false;
//       int networkId = -1;
//       for (int i = this.mWifiConfigurations.size() - 1; i >= 0; i--)
//       {
//    	   wifiConfig = mWifiConfigurations.get(i);
//    	   String wifiSSID = wifiConfig.SSID;
//           if (wifiSSID.substring(1, wifiSSID.length()-1).equals(SSID)) {
//               networkId = wifiConfig.networkId;
//               isExisted = true;
//               break;
//           }
//       }
//       if (!isExisted) {
//    	   wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
//           if (wifiConfig == null) {
//        	   Log.i(TAG, "Failed to create wifi configuration info");
//        	   return false;  
//           }
//           networkId = mWifiManager.addNetwork(wifiConfig);
//           if (networkId != -1)
//           {
//        	   mWifiManager.saveConfiguration();
//           }
//       } else {
//    	   this.mWifiInfo = mWifiManager.getConnectionInfo();
//           if (this.mWifiInfo != null && SSID.equals(this.mWifiInfo.getSSID())) {
//        	   Log.i(TAG, "Have connect wifi: " + SSID);
//               return true;
//           }
//           
//           int encryptionType = getKeyMgmtType(Type);
//           wifiConfig.allowedKeyManagement.set(encryptionType);
////           if (encryptionType != 0)
////           {
////        	   wifiConfig.preSharedKey = Password;
////           }
//           mWifiManager.updateNetwork(wifiConfig);
//       }
//       
//       boolean bRet = false;
//       if (networkId != -1) {
//           mWifiManager.disconnect();
//           bRet = mWifiManager.enableNetwork(networkId, true);
//       }
//         
//       return bRet;
//    }
//    
//    private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
//    	WifiConfiguration config = new WifiConfiguration();
//        config.allowedAuthAlgorithms.clear();
//        config.allowedGroupCiphers.clear();
//        config.allowedKeyManagement.clear();
//        config.allowedPairwiseCiphers.clear();
//        config.allowedProtocols.clear();
//        config.SSID = "\"" + SSID + "\"";
//        if(Type == WifiCipherType.WIFICIPHER_NOPASS) {
//        	config.wepKeys[0] = "";
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
//       } else if(Type == WifiCipherType.WIFICIPHER_WEP) {
//           config.preSharedKey = "\""+Password+"\"";
//           config.hiddenSSID = true;
//           config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//           config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//           config.wepTxKeyIndex = 0;
//       } else if(Type == WifiCipherType.WIFICIPHER_WPA) {
//    	   config.preSharedKey = Password;
//    	   config.hiddenSSID = true;
//    	   config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//    	   config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//    	   config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//    	   config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//    	   config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//    	   //maybe for WiFi AP
////    	   config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
////         config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//    	   config.status = WifiConfiguration.Status.ENABLED;
//       } else {
//           return null;
//       }
//       return config;
//    }

    private int getMaxPriority(final WifiManager wifiManager) {
		final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
		int pri = 0;
		for(final WifiConfiguration config : configurations) {
			if(config.priority > pri) {
				pri = config.priority;
			}
		}
		return pri;
	}
    
    private void sortByPriority(final List<WifiConfiguration> configurations) {
		java.util.Collections.sort(configurations, new Comparator<WifiConfiguration>() {

			@Override
			public int compare(WifiConfiguration object1, WifiConfiguration object2) {
				return object1.priority - object2.priority;
			}
		});
	}
    
    private int shiftPriorityAndSave(final WifiManager wifiMgr) {
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		sortByPriority(configurations);
		final int size = configurations.size();
		for(int i = 0; i < size; i++) {
			final WifiConfiguration config = configurations.get(i);
			config.priority = i;
			wifiMgr.updateNetwork(config);
		}
		wifiMgr.saveConfiguration();
		return size;
	}
    
    //connect WiFi which is configurated
    public void connectToConfiguredNetwork(int index) {
    	List<WifiConfiguration> wifiCfg = mWifiManager.getConfiguredNetworks();
        if (index > wifiCfg.size()) {
            return;
        }
        this.mWifiManager.enableNetwork(wifiCfg.get(index).networkId, true); 
    }
    
    /**
	 * Connect to a configured network.
	 * @param config
	 * @param numOpenNetworksKept Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT
	 * @return
	 */
	public boolean connectToConfiguredNetwork(final Context ctx, WifiConfiguration config, boolean reassociate) {
		final String security = ConfigSec.getWifiConfigurationSecurity(config);
		
		int oldPri = config.priority;
		// Make it the highest priority.
		int newPri = getMaxPriority(mWifiManager) + 1;
		if(newPri > MAX_PRIORITY) {
			newPri = shiftPriorityAndSave(mWifiManager);
			config = getWifiConfiguration(config, security);
			if(config == null) {
				return false;
			}
		}
		
		// Set highest priority to this configured network
		config.priority = newPri;
		int networkId = mWifiManager.updateNetwork(config);
		if(networkId == -1) {
			return false;
		}
		
		// Do not disable others
		if(!mWifiManager.enableNetwork(networkId, false)) {
			config.priority = oldPri;
			return false;
		}
		
		if(!mWifiManager.saveConfiguration()) {
			config.priority = oldPri;
			return false;
		}
		
		// We have to retrieve the WifiConfiguration after save.
		config = getWifiConfiguration(config, security);
		if(config == null) {
			return false;
		}
		
		WifiReenable.schedule(ctx);
		
		// Disable others, but do not save.
		// Just to force the WifiManager to connect to it.
		if(!mWifiManager.enableNetwork(config.networkId, true)) {
			return false;
		}
		
		final boolean connect = reassociate ? mWifiManager.reassociate() : mWifiManager.reconnect();
		if(!connect) {
			return false;
		}
		
		return true;
	}
    
    private int parseBitSet(BitSet kmtBitSet) {
    	for (int i = 0; i < kmtBitSet.size(); i++) {
			if (kmtBitSet.get(i)) {
				return i;
			}
		}
    	
    	return -1;
    }

    public static int getWifiStrength(int dBm) {
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
        return mWifiManager.getConfiguredNetworks();
    }
    
    public List<ScanResult> scanWifi() {
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
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        return filterWifiScanned(scanResults);
    }
    
    private List<ScanResult> filterWifiScanned(List<ScanResult> wifiList) {
    	ArrayList<ScanResult> wifiItems = new ArrayList<ScanResult>();
    	HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
    	
    	int wifiPos = 0;
    	for (int i = 0; i < wifiList.size(); i++) {
			ScanResult result = wifiList.get(i);
			if (!result.SSID.isEmpty()) {
				String key = result.SSID + " " + result.capabilities;
				if (!signalStrength.containsKey(key)) {
					signalStrength.put(key, wifiPos++);
					wifiItems.add(result);
				} else {
					int position = signalStrength.get(key);
					ScanResult updateItem = wifiItems.get(position);
					if (calculateSignalStength(updateItem.level)
							> calculateSignalStength(result.level)) {
						wifiItems.set(position, updateItem);
					}
				}
			}
		}
    	
    	return wifiItems;
    }
    
    public int calculateSignalStength(int level){
        return WifiManager.calculateSignalLevel(level, 5) + 1;
    }
    
    public WifiInfo getWifiConnection() {
    	return mWifiManager.getConnectionInfo();
    }
    
    public String getWifiNameConnection() {
    	return mWifiManager.getConnectionInfo().getSSID();
    }
    
    public void connectWifi(WifiConfiguration configuration) {
        int wcgId = this.mWifiManager.addNetwork(configuration);
        this.mWifiManager.enableNetwork(wcgId, true);
    }
    
    public void disConnectionWifi(int netId){
        this.mWifiManager.disableNetwork(netId);
        this.mWifiManager.disconnect();
    }
    
    public WifiConfiguration getWifiConfiguration(final WifiInfoScanned hotspot) {
		final String ssid = convertToQuotedString(hotspot.getWifiName());
		if(ssid.length() == 0) {
			return null;
		}
		
		String hotspotSecurity = hotspot.getEncryptType();
		if(hotspotSecurity == null) {
			return null;
		}
		
		final List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
		if(configurations == null) {
			return null;
		}

		for(final WifiConfiguration config : configurations) {
			if(config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			final String configSecurity = ConfigSec.getWifiConfigurationSecurity(config);
			if(hotspotSecurity.equals(configSecurity)) {
				return config;
			}
		}
		return null;
	}
    
    public WifiConfiguration getWifiConfiguration(final ScanResult hotspot, String hotspotSecurity) {
		final String ssid = convertToQuotedString(hotspot.SSID);
		if(ssid.length() == 0) {
			return null;
		}
		
		final String bssid = hotspot.BSSID;
		if(bssid == null) {
			return null;
		}
		
		if(hotspotSecurity == null) {
			hotspotSecurity = ConfigSec.getScanResultSecurity(hotspot);
		}
		
		final List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
		if(configurations == null) {
			return null;
		}

		for(final WifiConfiguration config : configurations) {
			if(config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			if(config.BSSID == null || bssid.equals(config.BSSID)) {
				final String configSecurity = ConfigSec.getWifiConfigurationSecurity(config);
				if(hotspotSecurity.equals(configSecurity)) {
					return config;
				}
			}
		}
		return null;
	}
	
	public WifiConfiguration getWifiConfiguration(final WifiConfiguration configToFind, String security) {
		final String ssid = configToFind.SSID;
		if(ssid.length() == 0) {
			return null;
		}
		
		final String bssid = configToFind.BSSID;
		
		if(security == null) {
			security = ConfigSec.getWifiConfigurationSecurity(configToFind);
		}
		
		final List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();

		for(final WifiConfiguration config : configurations) {
			if(config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			if(config.BSSID == null || bssid == null || bssid.equals(config.BSSID)) {
				final String configSecurity = ConfigSec.getWifiConfigurationSecurity(config);
				if(security.equals(configSecurity)) {
					return config;
				}
			}
		}
		return null;
	}
    
    public static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        
        final int lastPos = string.length() - 1;
        if(lastPos > 0 && (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string;
        }
        
        return "\"" + string + "\"";
    }
    
    public static String convertToNonQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        
        final int lastPos = string.length() - 1;
        if(lastPos > 0 && (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string.substring(1, lastPos);
        }
        
        return string;
    }
    
}
