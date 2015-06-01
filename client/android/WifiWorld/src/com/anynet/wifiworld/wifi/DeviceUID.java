package com.anynet.wifiworld.wifi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.util.Log;

public class DeviceUID {
	private final static String TAG = DeviceUID.class.getSimpleName();
	
	public static String getLocalIpAddress() {
        try {
              String ipv4;
              List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
              for (NetworkInterface ni: nilist) {
            	  List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
            	  for (InetAddress address: ialist) {
            		  if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress())) {
            			  return ipv4;
            		  }
            	  }
              }
        } catch (SocketException ex) {
        	Log.e(TAG, ex.toString());
        }
        return null;
    }
	
	public static String getLocalMacAddressFromIp(Context context) {
	     String mac_s= "";
	     try {
	    	 byte[] mac;
	    	 NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
	    	 mac = ne.getHardwareAddress();
	    	 mac_s = byte2hex(mac);
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	     return mac_s;
	}
	
	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if(stmp.length() == 1) {
				hs = hs.append("0").append(stmp);
			} else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	 }
}
