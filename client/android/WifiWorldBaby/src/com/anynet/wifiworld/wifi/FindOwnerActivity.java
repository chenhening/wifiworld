package com.anynet.wifiworld.wifi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;

public class FindOwnerActivity extends BaseActivity {  
	  
    private ListView listView;  
    private List<String> data ;  
    private FindOwnerAdapter timelineAdapter;  
  
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
    }  
  
    private List<Map<String, Object>> getData() {  
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String time = formatter.format(curDate);
        
        Map<String, Object> map = new HashMap<String, Object>();  
        map.put("title", "好像他是我的隔壁小区的");
        map.put("time", time);
        list.add(map);  
  
        map = new HashMap<String, Object>();  
        map.put("title", "我知道，它是我们1单元的"); 
        map.put("time", time);
        list.add(map);  
  
        map = new HashMap<String, Object>();  
        map.put("title", "我来补充一下吧，他是我邻居哈哈");
        map.put("time", time);
        list.add(map);  
  
        map = new HashMap<String, Object>();  
        map.put("title", "我就是主人，感谢各位邀请我哈哈");
        map.put("time", time);
        list.add(map);  
        return list;  
    }  
  
}  
