package com.anynet.wifiworld;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.UserInfo;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.R;

public class SettingFeedbackActivity extends BaseActivity implements
        OnClickListener
{
    
    /**
     * 头部资源
     */

    private FeedbackAgent agent;
    
    private Conversation con;
    
    
    private EditText etFeedbackContent;
    

    private EditText etFeedbackContact;
    
    
    private static final String KEY_UMENG_CONTACT_INFO_PLAIN_TEXT = "plain";
    

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.setting_feedback);
        super.onCreate(savedInstanceState);
       
        bingdingTitleUI();
        binddingUI();
        bindingEvent();
        
        getData();
        
    }
    
    private void bingdingTitleUI()
    {
       
        
        mTitlebar.tvTitle.setText(getString(R.string.setting_title_feedback));
        
      
        
    }
   
    
    private void binddingUI()
    {
        
        etFeedbackContent = (EditText) findViewById(R.id.et_feedback_content);
        etFeedbackContact = (EditText) findViewById(R.id.et_feedback_contact);
      
    }
    
    private void bindingEvent()
    {
        mTitlebar.llFinish.setVisibility(View.VISIBLE);
        mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
        mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
        mTitlebar.llFinish.setOnClickListener(this);
        
    }
    
    private void getData()
    {
        
        agent = new FeedbackAgent(this);
        
        //获取焦点
        etFeedbackContent.requestFocus();
    
    }
    
    @Override
    public void onClick(View v)
    {
        
        switch (v.getId())
        {
        
            case R.id.setting_header_finish:
                
                
                if (!NetHelper.isNetworkAvailable(SettingFeedbackActivity.this))
                {
                    Toast.makeText(SettingFeedbackActivity.this,
                            "网络已经断开：请检查网络",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
    
                String contentStr =  etFeedbackContent.getEditableText().toString().trim();
                
                if (TextUtils.isEmpty(contentStr))
                {
                  
                    Toast.makeText(SettingFeedbackActivity.this, "反馈内容不能为空", Toast.LENGTH_SHORT).show();
                    etFeedbackContent.requestFocus();
                    return;
                }
                
                
                String contactStr = etFeedbackContact.getEditableText().toString();
                
                
                //联系方式
                UserInfo info = agent.getUserInfo();
                
                if (info == null)
                    info = new UserInfo();
                Map<String, String> contact = info.getContact();
                
                if (contact == null)
                    contact = new HashMap<String, String>();
             
                
                contact.put(KEY_UMENG_CONTACT_INFO_PLAIN_TEXT, contactStr);
                
                con = agent.getDefaultConversation();
                
                //内容
                con.addUserReply(contentStr);
  
                con = agent.getDefaultConversation();
                
                //内容
                con.addUserReply(contentStr);
                
                agent.sync();
                
                showToast("提交成功！谢谢您的宝贵意见！");
                
                SettingFeedbackActivity.this.finish();

                break;
            
            default:
                break;
        
        }
        
    }
    
}
