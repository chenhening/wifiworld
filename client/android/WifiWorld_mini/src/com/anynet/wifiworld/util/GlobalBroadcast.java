
package com.anynet.wifiworld.util;

import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anynet.wifiworld.WifiWorldApplication;

public class GlobalBroadcast {
    private static Set<BroadcastListener> sListenerList =new HashSet<BroadcastListener>();
    private static Set<String> sFilterFlag = new HashSet<String>();
    private static BasicReceiver sRecerver = new BasicReceiver();
    private static final String TAG = GlobalBroadcast.class.getSimpleName(); 
    
    /**
     * when we register a broadcastListener ,we must unregister at other time.
     *  
     * @param lis
     */
    public static void registerBroadcastListener(BroadcastListener lis) {
        synchronized (sListenerList) {
            sListenerList.add(lis);
        }
        
        
        if(!sFilterFlag.contains(lis.getClass().getName())){
            sFilterFlag.add(lis.getClass().getName());
            WifiWorldApplication.getInstance().registerReceiver(sRecerver, lis.getIntentFilter());
        }                
    }

    /**
     * the unregister must be used with register
     * 
     * @param lis
     */
    public static void unregisterBroadcastListener(BroadcastListener lis) {
        if (sListenerList.isEmpty()) {
            synchronized (sListenerList){
                sFilterFlag.clear();
            }
            
            try {
                WifiWorldApplication.getInstance().unregisterReceiver(sRecerver);
            } catch (Exception e) {
                if(e != null){
                    Log.e(TAG, e.getMessage());
                }
            }
        }else{
            synchronized (sListenerList){
                sListenerList.remove(lis);
            }            
        }        
    }
    
    
    public static class BasicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Object[] arr;
            
            synchronized (sListenerList) {
                arr = sListenerList.toArray();
            }
           
            for (Object obj : arr) {
                BroadcastListener lis = (BroadcastListener) obj;
                lis.setIntent(intent);
                lis.onEvent(action);
            }
        }
    }    
}
