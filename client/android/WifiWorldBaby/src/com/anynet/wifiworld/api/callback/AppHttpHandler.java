package com.anynet.wifiworld.api.callback;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.anynet.wifiworld.api.GsonHelper;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.XLLog;

public abstract class AppHttpHandler<T> extends JsonHttpResponseHandler
{
    
    private String TAG = "AppHttpHandler";
    
    private ResponseCallback<T> callback;
    
    private Class<T> entityClass;
    
    private Gson gson;
    
    private boolean isKick = false;
    
    public AppHttpHandler(ResponseCallback<T> paramResponseCallback)
    {
        this.callback = paramResponseCallback;
        this.gson = GsonHelper.getGsonInstance();
        this.entityClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }
    
    public void onFailure(int paramInt, Header[] paramArrayOfHeader, String paramString, Throwable paramThrowable)
    {
        this.callback.onFailure(paramInt, paramThrowable);
    }
    
    public void onFailure(int paramInt, Header[] paramArrayOfHeader, Throwable paramThrowable, JSONArray paramJSONArray)
    {
        this.callback.onFailure(paramInt, paramThrowable);
    }
    
    public void onFailure(int paramInt, Header[] paramArrayOfHeader, Throwable paramThrowable, JSONObject paramJSONObject)
    {
        this.callback.onFailure(paramInt, paramThrowable);
    }
    
    public void onSuccess(int paramInt, Header[] paramArrayOfHeader, JSONArray paramJSONArray)
    {
        super.onSuccess(paramInt, paramArrayOfHeader, paramJSONArray);
        XLLog.log("HTTP", "ResponseCode:", paramInt, " BODY:\n", paramJSONArray.toString());
        try
        {
            int i = paramJSONArray.length();
            ArrayList<T> localArrayList = new ArrayList<T>();
            for (int j = 0; j < i; j++)
                localArrayList.add(parseToType(paramJSONArray.getJSONObject(j)));
            this.callback.onSuccess(paramJSONArray, localArrayList);
            return;
        }
        catch (JSONException localJSONException)
        {
            localJSONException.printStackTrace();
            this.callback.onFailure(0, localJSONException);
        }
    }
    
    public void onSuccess(int paramInt, Header[] paramArrayOfHeader, JSONObject paramJSONObject)
    {
        super.onSuccess(paramInt, paramArrayOfHeader, paramJSONObject);
        
        int ret = 0;
        String retDesc = null;
        try
        {
            ret = paramJSONObject.getInt("r");
            retDesc = paramJSONObject.getString("rd");
        }
        catch (JSONException e)
        {
            XLLog.e(TAG, e.toString());
        }
        
        if (ret == -501)
        {
            
            showOneButtonDialog("session已失效,请重新登录");
            
            XLLog.e(TAG, retDesc);
            return;
        }
        // useid不合法
        else if (ret == 131)
        {
            showOneButtonDialog("useid不合法");
            XLLog.e(TAG, retDesc);
            return;
            
        }
        //sessionid不合法
        else if (ret == 132)
        {
            showOneButtonDialog("sessionid不合法");
            XLLog.e(TAG, retDesc);
            return;
        }
        
        XLLog.log("HTTP", "ResponseCode:", paramInt, " BODY:\n", paramJSONObject.toString());
        try
        {
            this.callback.onSuccess(paramJSONObject, parseToType(paramJSONObject));
            return;
        }
        catch (JsonSyntaxException localJsonSyntaxException)
        {
            localJsonSyntaxException.printStackTrace();
            this.callback.onFailure(0, localJsonSyntaxException);
        }
    }
    
    private void showOneButtonDialog(String content)
    {
        
        if (!isKick)
        {
            isKick = true;
            LoginHelper.getInstance().gotoLogin(WifiWorldApplication.getInstance(), content);
           
        }
        
    }
    
    protected T parseToType(JSONObject paramJSONObject) throws JsonSyntaxException
    {
        T instT = this.gson.fromJson(paramJSONObject.toString(), this.entityClass);
        return instT;
    }
}
