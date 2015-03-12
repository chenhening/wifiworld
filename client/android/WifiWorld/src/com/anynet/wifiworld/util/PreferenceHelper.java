package com.xunlei.crystalandroid.util;
import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 操作Preference的工具类
 * @author liuzongyao
 *
 */
public class PreferenceHelper
{
    
    //默认
    private String prefName = "DcdnPref";
    
    private  SharedPreferences mPreferences;
    
    private Context globalContext;
    
    private static  PreferenceHelper  preHelper =  new PreferenceHelper();
    
     public static  PreferenceHelper getInstance()
    {
        
         return  preHelper;
    }
   
   
    /**
     * 初始化
     * 
     * @param context
     * @param prefName
     */
    public void init(Context context)
    {
        this.globalContext = context;
        
         mPreferences = globalContext.getSharedPreferences(prefName,
                Context.MODE_PRIVATE);
    }
    
    public void setLong(String id, long value)
    {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(id, value);
        editor.commit();
    }
    
    public long getLong(String id, long defaulValue)
    {
        return mPreferences.getLong(id, defaulValue);
    }
    
    
    public void setInt(String id, int value)
    {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(id, value);
        editor.commit();
    }
    
    public int getInt(String id, int defaulValue)
    {
        return mPreferences.getInt(id, defaulValue);
    }
    
    public void setBoolean(String id, boolean value)
    {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(id, value);
        editor.commit();
    }
    
    public boolean getBoolean(String id, boolean defaulValue)
    {
        
        return mPreferences.getBoolean(id, defaulValue);
    }
    
    public void setString(String id, String value)
    {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(id, value);
        editor.commit();
    }
    
    public String getString(String id, String defaulValue)
    {
        return mPreferences.getString(id, defaulValue);
    }
    
    /**
     * 删除Preference中的某条数据
     * */
    public void removeDataFromPref(String key)
    {
        Editor edit = mPreferences.edit();
        edit.remove(key);
        edit.commit();
    }
    
    
    public  boolean contains(String key)
    {
        return mPreferences.contains(key);
    }
 
    
}
