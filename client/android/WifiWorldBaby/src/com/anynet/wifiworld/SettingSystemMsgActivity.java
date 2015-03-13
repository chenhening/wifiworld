package com.anynet.wifiworld;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.bean.Msg;
import com.anynet.wifiworld.bean.SystemMsgResp;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.dao.DBHelper;
import com.anynet.wifiworld.dao.SysMessage;
import com.anynet.wifiworld.util.PreferenceHelper;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.R;
public class SettingSystemMsgActivity extends BaseActivity implements OnRefreshListener2<ListView>
{
	private PullToRefreshListView lvSystemMsgList;
	private static long ONE_DAY = 1000 * 60 * 60 * 24;
	
    //存放系统信息数据
    private  List<SysMessage> systemMsgList = new ArrayList<SysMessage>();
    
    private SysMessageAdapter adapter = null;
    
    private int totalPage = 0; //总页数
    private int currentLoadedPage = -1;
    private boolean sumed = false; //是否已经初始化了数据
    long maxSysMsgId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.setting_system_msg);
        super.onCreate(savedInstanceState);
        
        bingdingTitleUI();
        
        binddingUI();
        bindingData();
        
    }
    
    @Override
    protected void onResume()
    {
        if (!sumed)
        {
            sumed = true;
            loadData(0);
        }
        super.onResume();
    }
    
    private void bingdingTitleUI()
    {
        
        mTitlebar.tvTitle.setText(getString(R.string.setting_title_system_msg));
        
    }
    
    private void binddingUI()
    {
        lvSystemMsgList.setMode(Mode.BOTH);
        lvSystemMsgList.setEmptyView(LayoutInflater.from(this).inflate(R.layout.system_message_list_empty_view, null));
        adapter = new SysMessageAdapter();
        lvSystemMsgList.setAdapter(adapter);
        lvSystemMsgList.setOnRefreshListener(this);
    }
    
    private void bindingData()
    {
        maxSysMsgId = PreferenceHelper.getInstance().getLong(Const.MAX_SYS_MSG_ID, 0);
    }
    
    
    public void deleteMsg(Long id)
    {
        adapter.notifyDataSetChanged();
        
    }
    
    public class SysMessageAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return systemMsgList.size();
        }
        
        @Override
        public Object getItem(int position)
        {
            return null;
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            
            final SysMessage sysMessage = systemMsgList.get(position);
            
            final ViewHolder holder;
            View view = convertView;
            if (view == null)
            {
              
                view = LayoutInflater.from(SettingSystemMsgActivity.this).inflate(R.layout.setting_system_msg_list_item, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.ivMsgNew = (ImageView) view.findViewById(R.id.iv_person_device_list_new);
                holder.ivMsgContent = (TextView) view.findViewById(R.id.tv_msg_content);
                holder.ivTime = (TextView) view.findViewById(R.id.tv_msg_time);
                
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }
            
            //设置内容
            
            holder.ivMsgContent.setText(sysMessage.getContent());
            holder.ivTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(sysMessage.getDate() * 1000)));
            //未读
            if (sysMessage.getState() == 1)
            {
                holder.ivMsgNew.setVisibility(View.VISIBLE);
            }
            
            //已读
            else
            {
                holder.ivMsgNew.setVisibility(View.INVISIBLE);
            }
            
            return view;
        }
        
        class ViewHolder
        {
            ImageView ivMsgNew;
            
            TextView ivMsgContent;
            TextView ivTime;
        }
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
    }

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		systemMsgList.clear();
        currentLoadedPage = -1;
        totalPage = 0;
        loadData(currentLoadedPage + 1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(currentLoadedPage + 1);
	}
    
	public void loadData(int pageIndex) {
		AppRestClient.getSystemMsg(maxSysMsgId, pageIndex, Const.PAGE_SIZE,  new ResponseCallback<SystemMsgResp>(){
			@Override
            public void onSuccess(JSONObject paramJSONObject, SystemMsgResp paramT)
            {
				XLLog.log(TAG, "on load msg success:", paramT.toString());
                if (paramT.isOK()) {
                	if (0 != paramT.totalPage && paramT.totalPage >= paramT.currentPage) {
                        totalPage = paramT.totalPage;
                        currentLoadedPage = paramT.currentPage;
                        Msg[] tmp = paramT.msg;
                        if (null != tmp && tmp.length > 0)
                        {
                        	long maxIdtmp = maxSysMsgId;
                        	long msgId;
                            for (Msg msg : tmp)
                            {
                            	msgId = msg.getId();
                            	SysMessage sysMsg;
                            	if (msgId > maxSysMsgId) {
                            		maxIdtmp = msgId > maxIdtmp ? msgId : maxIdtmp;
                            		sysMsg = new SysMessage(null, msg.getId(), msg.getContent(), msg.getTitle(), 1, msg.getDate(), 1);
                            	} else {
                            		sysMsg = new SysMessage(null, msg.getId(), msg.getContent(), msg.getTitle(), 1, msg.getDate(), 2);
                            	}
                            	
                            	// 排序，根據ID從大到小排列
                            	int index = 0;
                            	for (index = 0; index < systemMsgList.size(); index++) {
                            		SysMessage sysMsgTmp = systemMsgList.get(index);
                            		if (sysMsg.getServerId() > sysMsgTmp.getServerId()) {
                            			break;
                            		}
                            	}
                            	systemMsgList.add(index, sysMsg);
                            }
                            PreferenceHelper.getInstance().setLong(Const.MAX_SYS_MSG_ID, maxIdtmp);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        //超出了分页范围
                        showToast(R.string.crystal_record_no_more);
                    }
                } else {
                    showToast(paramT.getReturnDesc());
                }
                
              
                lvSystemMsgList.onRefreshComplete();
                super.onSuccess(paramJSONObject, paramT);
            }
			
			@Override
            public void onFailure(int paramInt, Throwable paramThrowable)
            {
                super.onFailure(paramInt, paramThrowable);
                XLLog.e(TAG, "on load msg failure: ", paramInt);
                showToast(R.string.net_error_try_later);
                lvSystemMsgList.onRefreshComplete();
            }
		});
	}
}
