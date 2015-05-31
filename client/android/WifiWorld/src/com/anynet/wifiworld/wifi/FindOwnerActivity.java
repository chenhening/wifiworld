package com.anynet.wifiworld.wifi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiFindHistory;
import com.anynet.wifiworld.util.LocationHelper;

public class FindOwnerActivity extends BaseActivity {  
	  
    private ListView listView;  
    private List<String> data ;  
    private FindOwnerAdapter timelineAdapter;  
    private WifiFindHistory mClues = new WifiFindHistory(); //线索
    private WifiInfoScanned mWifiInfoScanned;
    
    private EditText mEdit;
  
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText("寻找Wi-Fi主人");
	}
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {   
        setContentView(R.layout.activity_find_owner);
        super.onCreate(savedInstanceState); 
        bingdingTitleUI();
  
        listView = (ListView) this.findViewById(R.id.lv_find_thread);  
        listView.setDividerHeight(0);  
        timelineAdapter = new FindOwnerAdapter(this, getData());  
        listView.setAdapter(timelineAdapter);
        
        mEdit = (EditText) this.findViewById(R.id.wifi_input_frame);
        
        //服务器上拉取线索
        Intent intent = getIntent();
		mWifiInfoScanned = (WifiInfoScanned) intent.getSerializableExtra("WifiSelected");
        mClues.QueryRemote(this, mWifiInfoScanned.getWifiMAC(), new DataCallback<WifiFindHistory>() {

			@Override
            public void onFailed(String msg) {
            }

			@Override
            public void onSuccess(WifiFindHistory object) {
				timelineAdapter.clear();
				for (Map<String, Object> item : object.Clues)
					timelineAdapter.addItem(item);
				timelineAdapter.notifyDataSetChanged();
            }
        	
        });
        
        //设置发送
        this.findViewById(R.id.tv_button_sms).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				String message = mEdit.getText().toString();
				if (message.length() <= 0) {
					showToast("请输入线索。");
					return;
				}
				mEdit.setText("");
				//添加到列表中
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		        String time = formatter.format(curDate);
				Map<String, Object> map = new HashMap<String, Object>();
		        map.put("title", message);
		        map.put("time", time);
		        map.put("location", LocationHelper.getInstance(getApplicationContext()).getLocalDescription());
		        timelineAdapter.addItem(map);
		        timelineAdapter.notifyDataSetChanged();
				//添加到数据库中
				mClues.MacAddr = mWifiInfoScanned.getWifiMAC();
				mClues.Clues = timelineAdapter.getList();
				mClues.StoreRemote(getApplicationContext(), new DataCallback<WifiFindHistory>() {

					@Override
                    public void onSuccess(WifiFindHistory object) {
	                    showToast("添加线索成功");
                    }

					@Override
                    public void onFailed(String msg) {
	                    // TODO Auto-generated method stub
	                    
                    }
					
				});
            }
        	
        });
    }  
  
    private List<Map<String, Object>> getData() {  
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        Map<String, Object> map = new HashMap<String, Object>();  
        map.put("title", "发现新大陆啦，赶快来留下你的线索一起来寻找主人吧！");
        map.put("time", "侏罗纪一亿五千万年前");
        map.put("location", "新大陆");
        list.add(map);
        
        return list;  
    }  
  
}  
