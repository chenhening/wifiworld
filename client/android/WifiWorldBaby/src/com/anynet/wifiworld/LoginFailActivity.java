package com.anynet.wifiworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.constant.ErrCode;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.R;

public class LoginFailActivity extends BaseActivity implements OnClickListener
{
    
    private ImageView ivLoginFailLogo;
    
    private TextView tvLoginFailRemind;
    
    /**
     * 失败确认
     */
    private LinearLayout llLoginFailConfirm;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        setContentView(R.layout.login_fail);
        super.onCreate(savedInstanceState);
        
        bingdingTitleUI();
        binddingEvent();

      
        
    }

    
    private void bingdingTitleUI()
    {
        
        mTitlebar.llFinish.setVisibility(View.INVISIBLE);
        mTitlebar.tvTitle.setText(getString(R.string.login_fail_title));
        
    }
    
    private void binddingEvent()
    {
        mTitlebar.llHeaderLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                exitLogin();
            }
        });
        
        llLoginFailConfirm.setOnClickListener(this);
        
    }
    
    
    private void exitLogin()
    {
        
        //手动退出
        LoginHelper.getInstance().logout(LoginHelper.LOGOUT_BY_USER);
        
        //跳转到登录页面
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setClass(this, LoginActivity.class);      
        startActivity(i);
        finish();
    }
    
    
    @Override
    public void onPause()
    {
        
        super.onPause();
        
    }
    
    @Override
    protected void onResume()
    {
        
        super.onResume();
        
    }
    
    @Override
    protected void onStop()
    {
        
        super.onStop();
    }
    
    @Override
    public void onClick(View v)
    {
        
        switch (v.getId())
        {
            case R.id.ll_login_fail_confirm:
                
                exitLogin();
               
            default:
                break;
        
        }
        
    }
    
    
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {    
            exitLogin(); 
        }  
        return false;  
          
    }  
    
}
