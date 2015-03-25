package com.anynet.wifiworld.me;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;

import com.anynet.wifiworld.util.LoginHelper;

public class WifiProviderWebPlugin extends CordovaPlugin {
	static String TAG = "WifiProviderWebPlugin";
	//TODO(binfei), context == null will be a bug
	private LoginHelper mLoginHelper = LoginHelper.getInstance(null);

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) {  
        JSONObject jsonObj = new JSONObject();//可以返回给JS的JSON数据  
        if(TAG.equals(action)){  
            try {   
                  
                jsonObj.put("userid", mLoginHelper.getCurLoginUserInfo().PhoneNumber); 
                
                callbackContext.success(jsonObj);
                //返回成功时，将Java代码处理过的JSON数据返回给JS  
            } catch (Exception e) {  
            	callbackContext.error("JSON Exception" + e.getMessage());
                return false;  
            }    
              
        }  
        return true;
    }

    // --------------------------------------------------------------------------
    // LOCAL METHODS
    // --------------------------------------------------------------------------
    
}
