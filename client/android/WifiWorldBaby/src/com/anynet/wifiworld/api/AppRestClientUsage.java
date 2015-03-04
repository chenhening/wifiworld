package com.anynet.wifiworld.api;
import org.json.JSONObject;

import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.CrystalIncomeSummaryResp;

public class AppRestClientUsage
{
  
    
    /**
     * 
     */
    public static void mineSummary()
    {

        AppRestClient.crystalIncomeSummary(new ResponseCallback<CrystalIncomeSummaryResp>(
                WifiWorldApplication.getInstance())
        {
            
            public void onSuccess(JSONObject paramJSONObject,
                    CrystalIncomeSummaryResp paramT)
            {
                
                System.out.println(paramT);
                
            }
            
            public void onFailure(int paramInt, Throwable paramThrowable)
            {
                System.out.println(paramInt);
            }
            
        });
    }
   
 
    
}