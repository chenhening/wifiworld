package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.DataListenerHelper;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LocationHelper;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.os.Parcel;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;

public class WifiAdmin {
	private static final String TAG = WifiAdmin.class.getSimpleName();
	private static final int MAX_PRIORITY = 99999;
	public static final WifiSecurities ConfigSec = WifiSecurities.newInstance();
	
	private static WifiAdmin wifiAdmin = null;
    private WifiManager mWifiManager;
    private WifiLock mWifiLock;
    private int mNumOpenNetworksKept;
    
    private Context mContext;
    
    public static WifiAdmin getInstance(Context context) {
        if(wifiAdmin == null) {
            wifiAdmin = new WifiAdmin(context);
        }
        return wifiAdmin;
    }
    
    private WifiAdmin(Context context) {
        //get WIFI manager object
        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        mContext = context;
        mNumOpenNetworksKept =  Settings.Secure.getInt(context.getContentResolver(),
	            Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
    }

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
    
    /**
	 * Ensure no more than numOpenNetworksKept open networks in configuration list.
	 * @param wifiMgr
	 * @param numOpenNetworksKept
	 * @return Operation succeed or not.
	 */
	private boolean checkForExcessOpenNetworkAndSave(final WifiManager wifiMgr, final int numOpenNetworksKept) {
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		sortByPriority(configurations);
		
		boolean modified = false;
		int tempCount = 0;
		for(int i = configurations.size() - 1; i >= 0; i--) {
			final WifiConfiguration config = configurations.get(i);
			if(ConfigSec.isOpenNetwork(ConfigSec.getWifiConfigurationSecurity(config))) {
				tempCount++;
				if(tempCount >= numOpenNetworksKept) {
					modified = true;
					wifiMgr.removeNetwork(config.networkId);
				}
			}
		}
		if(modified) {
			return wifiMgr.saveConfiguration();
		}
		
		return true;
	}
    
    /**
	 * Configure a network, and connect to it.
	 * @param scanResult
	 * @param password Password for secure network or is ignored.
	 * @return
	 */
	public boolean connectToNewNetwork(final Context ctx, final WifiInfoScanned wifiInfoScanned,
			boolean reassociate, boolean localSave) {
		
		if(ConfigSec.isOpenNetwork(wifiInfoScanned.getEncryptType())) {
			checkForExcessOpenNetworkAndSave(mWifiManager, mNumOpenNetworksKept);
		}
		
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = convertToQuotedString(wifiInfoScanned.getWifiName());
		config.BSSID = wifiInfoScanned.getWifiMAC();
		ConfigSec.setupSecurity(config, wifiInfoScanned.getEncryptType(), wifiInfoScanned.getWifiPwd());
		
		int id = -1;
		try {
			config.priority = getMaxPriority(mWifiManager) + 1;
			id = mWifiManager.addNetwork(config);
			config.networkId = id;
		} catch(NullPointerException e) {
			Log.e(TAG, "Weird!! Really!! What's wrong??", e);
			return false;
		}
		if(id == -1) {
			Log.e(TAG, "Failed to add config to network");
			return false;
		}
		
		if (localSave) {
			if(!mWifiManager.saveConfiguration()) {
				Log.e(TAG, "Failed to save config");
				return false;
			}
			
			config = getWifiConfiguration(config, wifiInfoScanned.getEncryptType());
			if(config == null) {
				Log.e(TAG, "Failed to get config from local configed wifi list");
				return false;
			}
		}
		
		if(!mWifiManager.enableNetwork(config.networkId, true)) {
			Log.e(TAG, "Failed to enable current network and disable others");
			return false;
		}
		
		final boolean connect = reassociate ? mWifiManager.reassociate() : mWifiManager.reconnect();
		if(!connect) {
			Log.e(TAG, "Failed to reassociate or reconnect");
			return false;
		}
		
		return connect;
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
	public boolean connectToConfiguredNetwork(Context ctx, WifiConfiguration config, boolean reassociate) {
		//final String security = ConfigSec.getWifiConfigurationSecurity(config);
		
		//int oldPri = config.priority;
		// Make it the highest priority.
		//if(newPri > MAX_PRIORITY) {
		//	newPri = shiftPriorityAndSave(mWifiManager);
		//	config = getWifiConfiguration(config, security);
		//	if(config == null) {
		//		Log.e(TAG, "Failed to find configed wifi under local wificonfiguration list");
		//		return false;
		//	}
		//}
		
		// Set highest priority to this configured network
		config.priority = getMaxPriority(mWifiManager) + 1;
		int networkId = mWifiManager.updateNetwork(config);
		config.networkId = networkId;
		if(networkId == -1) {
			Log.e(TAG, "Unable to update new priority info to network");
			return false;
		}
		
		// Do not disable others
		//if(!mWifiManager.enableNetwork(config.networkId, false)) {
		//	config.priority = oldPri;
		//	Log.e(TAG, "Failed to enable current select network");
		//	return false;
		//}
		
		//if(!mWifiManager.saveConfiguration()) {
		//	config.priority = oldPri;
		//	Log.e(TAG, "Failed to save current configuration to local");
		//	return false;
		//}
		
		// We have to retrieve the WifiConfiguration after save.
		//config = getWifiConfiguration(config, security);
		//if(config == null) {
		//	Log.e(TAG, "Failed to retrieve current wifi configuration");
		//	return false;
		//}
		
		//WifiReenable.schedule(ctx);
		
		// Disable others, but do not save.
		// Just to force the WifiManager to connect to it.
		if(!mWifiManager.enableNetwork(config.networkId, true)) {
			Log.e(TAG, "Failed to enable current network and disable others");
			return false;
		}
		
		final boolean connect = reassociate ? mWifiManager.reassociate() : mWifiManager.reconnect();
		if(!connect) {
			Log.e(TAG, "Failed to reassociate or reconnect");
			return false;
		}
		
		return true;
	}

	//----------------------------------------------------------------------
	public boolean checkWifiPwd(String ssid, String bssid, String pwd, final DataCallback<Boolean> callback) {
		//保存旧的，用新的连接，一旦新的连接失败就再恢复旧的
		WifiConfiguration conf = getExistWifiConf(ssid, bssid);
		try {
			if (conf != null) {
				mWifiManager.removeNetwork(conf.networkId);
			} else {
				conf = new WifiConfiguration();
				conf.SSID = convertToQuotedString(ssid);
				conf.BSSID = bssid;
			}
			conf.preSharedKey = "\"" + pwd + "\"";
			conf.priority = getMaxPriority(mWifiManager) + 1;
			conf.networkId = mWifiManager.addNetwork(conf);
			//mWifiManager.saveConfiguration();
		} catch(NullPointerException e) {
			Log.e(TAG, "Weird!! Really!! What's wrong??", e);
			return false;
		}
		
		if(conf.networkId == -1) {
			Log.e(TAG, "Failed to add config to network");
			return false;
		}
		
		if(!mWifiManager.enableNetwork(conf.networkId, true)) {
			Log.e(TAG, "Failed to enable current network");
			return false;
		}
		
		if (!mWifiManager.reassociate()) {
			Log.e(TAG, "Failed to reassociate network");
			return false;
		}
		
		//监听密码是否成功
		final WifiConfiguration _conf = conf;
		final Timer timer = new Timer();
		final IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		final BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
            public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
		        	Log.i(TAG, "supplicant state changed action");
		        	int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
	            	if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
	            		if (callback != null) {
	            			timer.cancel();
	            			mWifiManager.removeNetwork(_conf.networkId);
	            			mContext.unregisterReceiver(this);
	            			callback.onFailed("密码验证失败，请重新输入密码。");
	            			return;
	            		}
	            	}
	            	
		            WifiInfo info = getWifiConnecting();
		            SupplicantState state = info.getSupplicantState();
		            if (state == SupplicantState.COMPLETED && info.getNetworkId() == _conf.networkId){ //验证当前网络登录的网络是否是测试网络
		            	if (callback != null) {
		            		timer.cancel();
		            		mContext.unregisterReceiver(this);
		            		callback.onSuccess(true);
		            	}
		            } 
		            Log.e(TAG, state.toString());
		        }
            }
		};
		
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				mWifiManager.removeNetwork(_conf.networkId);
				mContext.unregisterReceiver(receiver);
    			callback.onFailed("密码验证失败，请重新输入密码。");
			}
			
		}, 10000);
		
		mContext.registerReceiver(receiver, filter);
		return true;
	}
    
    public static int getWifiStrength(int dBm) {
    	if(dBm <= -100)
            return 0;
        else if(dBm >= -50)
        	return 100;
        else
        	return 2 * (dBm + 100);
    }
    
    public static BmobGeoPoint getWifiGeometry(Context ctx, int dBm) {
    	LocationHelper locationHelper = LocationHelper.getInstance(ctx);
    	locationHelper.refreshLocation();
    	return new BmobGeoPoint(locationHelper.getLongitude() + dBm*0.000001, locationHelper.getLatitude() + dBm*0.000001);
    }
    
    //open WIFI
    public Boolean openWifi() {
    	Boolean bRet = true;
        //if (!this.mWifiManager.isWifiEnabled()) {
            bRet = this.mWifiManager.setWifiEnabled(true);
        //}
        return bRet;
    }
    
    //close WIFI
    public void closeWifi() {
        //if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        //}
    }
    
    //check WIFI status
    public int checkState() {
        return this.mWifiManager.getWifiState();
    }
    
    //get WIFI status
    public boolean isWifiEnabled() {
    	if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
    			|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			return true;
		} else {
			return false;
		}
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
    
    public WifiInfo getWifiConnecting() {
    		return mWifiManager.getConnectionInfo();
    }
    
    public WifiInfo getWifiConnected() {
    		if (mWifiManager.getConnectionInfo() != null
    			&& mWifiManager.getConnectionInfo().getNetworkId() != -1) {
    			return mWifiManager.getConnectionInfo();
    		}
    		return null;
    }
    
    public String getWifiNameConnection() {
    	WifiInfo wifiinfo = getWifiConnected();
    	return wifiinfo == null?null:wifiinfo.getSSID();

    }
    
    public void connectWifi(WifiConfiguration configuration) {
        int wcgId = this.mWifiManager.addNetwork(configuration);
        this.mWifiManager.enableNetwork(wcgId, true);
    }
    
    public void disConnectionWifi(int netId){
    		//mWifiManager.enableNetwork(netId, false);
    		//mWifiManager.saveConfiguration();
    		mWifiManager.disableNetwork(netId);
    		mWifiManager.disconnect();
    		mWifiManager.removeNetwork(netId);
    }
    
    public boolean forgetNetwork(WifiConfiguration wifiConfiguration) {
	    	return mWifiManager.removeNetwork(wifiConfiguration.networkId)
					&& mWifiManager.saveConfiguration();
    }
    
    public boolean forgetNetwork(WifiInfo wifiInfo) {
	    	return mWifiManager.removeNetwork(wifiInfo.getNetworkId())
					&& mWifiManager.saveConfiguration();
    }
    
    public WifiConfiguration getWifiConfiguration(WifiInfoScanned hotspot) {
		String ssid = convertToQuotedString(hotspot.getWifiName());
		if(ssid.length() == 0) {
			return null;
		}
		
		String hotspotSecurity = hotspot.getEncryptType();
		if(hotspotSecurity == null) {
			return null;
		}
		
		List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
		if(configurations == null) {
			return null;
		}

		for(WifiConfiguration config : configurations) {
			if(config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			String configSecurity = ConfigSec.getWifiConfigurationSecurity(config);
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
	
    //获取存在的wifi配置
    public WifiConfiguration getExistWifiConf(String SSID, String BSSID) {
		List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
		for(final WifiConfiguration config : configurations) {
			if(config.SSID != null && (SSID.equals(config.SSID) || SSID.equals(convertToNonQuotedString(SSID))) &&
				config.BSSID != null && (BSSID.equals(config.BSSID) || BSSID.equals(convertToNonQuotedString(BSSID)))) {
				return config;
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
