package com.anynet.wifiworld.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.ViewBinder;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.view.TitlebarHolder;

public class BaseFragment extends Fragment
{
    
    // 设置页面的View根节点
    protected View mPageRoot;
    
    protected BaseActivity mActivity;
    
    // 标题栏的句柄。子类的OnCreateView方法中，先inflate视图，然后再调用super.onCreateView
    protected TitlebarHolder mTitlebar;
    
    public String TAG = "BaseFragment";
    
    @Override
    public void onAttach(Activity activity)
    {
        TAG = getClass().getSimpleName();
        XLLog.log(TAG, "onAttach. to " + activity.getClass().getSimpleName());
        mActivity = (BaseActivity) activity;
        getArguments();
        super.onAttach(activity);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        XLLog.log(TAG, "onCreateView.");
        if (null != mPageRoot)
        {
            mTitlebar = new TitlebarHolder(mPageRoot);
            bindingView();
            return mPageRoot;
        }
        else
        {
            TextView mDefaultText = new TextView(mActivity);
            mDefaultText.setText(R.string.app_name);
            return mDefaultText;
        }
    }
    
    @Override
    public void onDetach()
    {
        XLLog.log(TAG, "onDetach.");
        super.onDetach();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        XLLog.log(TAG, "onCreate.", this.hashCode());
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume()
    {
        XLLog.log(TAG, "onResume.");
        super.onResume();
    }
    
    @Override
    public void onPause()
    {
        XLLog.log(TAG, "onPause.");
        super.onPause();
    }
    
    @Override
    public void onStart()
    {
        XLLog.log(TAG, "onStart.");
        super.onStart();
    }
    
    @Override
    public void onStop()
    {
        XLLog.log(TAG, "onStop.");
        super.onStop();
    }
    
    @Override
    public void onDestroyView()
    {
        XLLog.log(TAG, "onDestroyView.");
        super.onDestroyView();
    }
    
    @Override
    public void onDestroy()
    {
        XLLog.log(TAG, "onDestroy.");
        super.onDestroy();
    }
    
    /**
     * 从当前Fragment页面查找这个View。
     * 子Fragment需要在onCreateView中，把inflate的View赋给mPageRoot。
     * 
     * @param id
     * @return
     */
    protected View findViewById(int id)
    {
        View v = null;
        if (null != mPageRoot)
        {
            v = mPageRoot.findViewById(id);
        }
        return v;
    }
    
    @Override
    public void startActivity(Intent intent)
    {
        if (null != mActivity)
        {
            mActivity.startActivity(intent);
        }
        else
        {
            super.startActivity(intent);
        }
    }
    
    /**
     * 启动一个新的Activity，带动画的。
     * 
     * @param activityClass
     *            Activity的类对象
     * @param animation
     *            是否开启动画
     */
    protected void startActivityWithAnimation(Class<?> activityClass, boolean animation)
    {
        Intent intent = new Intent(mActivity, activityClass);
        startActivity(intent);
        if (animation)
        {
        }
    }
    
    /**
     * 启动一个新的Activity，带动画的。
     * 
     * @param activityClass
     *            Activity的类对象
     * @param animation
     *            是否开启动画
     */
    protected void startActivityWithAnimation(Class<?> activityClass, boolean animation, Intent intent)
    {
        intent.setClassName(mActivity, activityClass.getName());
        startActivity(intent);
        if (animation)
        {
        }
    }
    
    /**
     * 启动一个新的Activity，不带动画的。 调用startActivityWithAnimation(activityClass,
     * false)实现。
     * 
     * @param activityClass
     *            Activity的类对象
     */
    protected void startActivityWithAnimation(Class<?> activityClass)
    {
        startActivityWithAnimation(activityClass, true);
    }
    
    /**
     * 用于直接传带有自有数据的intent
     * 
     * @param in
     */
    protected void startActivityWithAnimation(final Intent in)
    {
        if (null != in)
        {
            startActivityWithAnimation(in, true);
        }
    }
    
    /**
     * 用于直接传带有自有数据的intent
     * 
     * @param in
     * @param anim
     */
    protected void startActivityWithAnimation(final Intent in, final boolean anim)
    {
        this.startActivity(in);
        if (anim)
        {
            
        }
    }
    
    protected void startActivityWithExtraAnimation(Class<?> activityClass, int enterAnim, int exitAnim)
    {
        Intent intent = new Intent(mActivity, activityClass);
        startActivity(intent);
        mActivity.overridePendingTransition(enterAnim, exitAnim);
    }
    
    /**
     * 当Activity接收到BackPressed事件时,首先会调用当前显示的Fragment的onBackPressed()方法,
     * Fragment可以截获该事件。 如果Fragment截获了该事件，则返回true；
     * 如果返回false，Activity将采用默认的处理方式，放回到上一个Fragment。
     * 
     * @return
     */
    public boolean onBackPressed()
    {
        return false;
    }
    
    /**
     * 当Activity接收到MenuPressed事件时,首先会调用当前显示的Fragment的onMenuPressed()方法,
     * Fragment可以截获该事件。 如果Fragment截获了该事件，则返回true； 如果返回false，Activity将采用默认的处理方式。
     * 
     * @return
     */
    public boolean onMenuPressed()
    {
        return false;
    }
    
    public String getFragmentTAG()
    {
        return TAG;
    }
    
    /**
     * 获取string，替代Fragment的是getString()方法。不要直接使用Fragment的是getString，否则可能报异常：
     * Fragment is not attached to activity.
     * 
     * @param id
     * @return
     */
    public String getResouceString(int id)
    {
        return mActivity.getString(id);
    }
    
    public String getResouceString(int resId, Object... formatArgs)
    {
        return mActivity.getString(resId, formatArgs);
    }
    
    /**
     * 同getResouceString()
     * 
     * @param resId
     * @return
     */
    public Drawable getResouceDrawable(int resId)
    {
        return mActivity.getResources().getDrawable(resId);
    }
    
    /**
     * return application context
     * 
     * @return
     */
    protected Context getApplicationContext()
    {
        return mActivity.getApplicationContext();
    }
    
    public void onGlobalVisibleRectChange(int width, int height)
    {
        
    }
    
    protected void showOrUpdateWaitingDialog(String text)
    {
        if (null != mActivity)
        {
            mActivity.showWaitingDialog(text);
        }
    }
    
    public void showOrUpdateWaitingDialog(int resId)
    {
        showOrUpdateWaitingDialog(getString(resId));
    }
    
    protected void dismissWaitingDialog()
    {
        if (null != mActivity)
        {
            mActivity.dismissWaitingDialog();
        }
    }
    
    public void showToast(String str)
    {
        mActivity.showToast(str);
    }
    
    public void showToast(int resId)
    {
        mActivity.showToast(resId);
    }
    
    public void bindingView()
    {
        
        if (null == mPageRoot)
        {
            return;
        }
        
        ViewBinder.bindingView(mPageRoot, this);
    }
    
    /**
     * 小数位数
     * 
     * @param initValue
     * @param fractionalNum
     * @return
     */
    protected String formatNumByRange(double initValue, int fractionalNum)
    {
        
        if (initValue <= 0.0f)
        {
            return getResources().getString(R.string.zero);
        }
        else if (initValue < 0.001f)
        {
            return getResources().getString(R.string.small_to_dot001);
        }
        else
        {
            
            return String.format("%." + fractionalNum + "f", initValue);
        }
        
    }
}
