
package com.anynet.wifiworld.util;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;


public abstract class NetworkStateListener extends BroadcastListener {
 
    /* (non-Javadoc)
     * @see com.common.event.broadcast.BroadcastListener#onEvent(java.lang.String)
     * {@hide}
     */
    public final void onEvent(String action) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
            onNetworkStateChange(mIntent);
        }
    }

    @Override
    protected final IntentFilter getIntentFilter() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return filter;
    }

    public abstract void onNetworkStateChange(Intent intent);

}
