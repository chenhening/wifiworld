package com.anynet.wifiworld.api.callback;

import java.lang.ref.WeakReference;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.anynet.wifiworld.util.XLLog;

public class ResponseCallback<T>
{
    private static final String TAG = "ResponseCallback";
    
    private WeakReference<Context> context;
    
    public ResponseCallback()
    {
    }
    
    public ResponseCallback(Context paramContext)
    {
        this.context = new WeakReference<Context>(paramContext);
    }
    
    public void onFailure(int paramInt, Throwable paramThrowable)
    {
        
        XLLog.e(TAG, "onFailure with status code: " + paramInt + " and error: " + paramThrowable.getMessage());
        
    }
    
    public void onSuccess(JSONArray paramJSONArray, List<T> paramList)
    {
        XLLog.log(TAG, "onSuccess with object list returned: " + paramList.size());
        
    }
    
    public void onSuccess(JSONObject paramJSONObject, T paramT)
    {
       
        
        XLLog.log(TAG, "onSuccess with object returned");
        
    }
    
  
    
}
