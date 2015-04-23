package com.anynet.wifiworld.wifi;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FindOwnerAdapter extends BaseAdapter {  
	  
    private Context context;  
    private List<Map<String, Object>> list;  
    private LayoutInflater inflater;  
  
    public FindOwnerAdapter(Context context, List<Map<String, Object>> list) {  
        super();  
        this.context = context;  
        this.list = list;  
    } 
    
    public void clear() {
    	list.clear();
    	notifyDataSetChanged();
    }
    
    public void addItem(Map<String, Object> item) {
    	list.add(item);
    }
    
    public List<Map<String, Object>> getList() {
    	return list;
    }
  
    @Override  
    public int getCount() {  
  
        return list.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return position;  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder viewHolder = null;  
        if (convertView == null) {  
            inflater = LayoutInflater.from(parent.getContext());  
            convertView = inflater.inflate(R.layout.item_find_owner, null);  
            viewHolder = new ViewHolder();  
  
            viewHolder.title = (TextView) convertView.findViewById(R.id.title); 
            viewHolder.time = (TextView) convertView.findViewById(R.id.show_time);
            viewHolder.location = (TextView) convertView.findViewById(R.id.tv_clues_location);
            convertView.setTag(viewHolder);  
        } else {  
            viewHolder = (ViewHolder) convertView.getTag();  
        }  
        
        Map<String, Object> object = list.get(position);
        String titleStr = object.get("title").toString();  
        viewHolder.title.setText(titleStr);  
        String timestr = object.get("time").toString();  
        viewHolder.time.setText(timestr);
        String locationstr = object.get("location").toString();  
        viewHolder.location.setText("标记地点： " + locationstr);
  
        return convertView;  
    }  
  
    static class ViewHolder {  
        public TextView time;  
        public TextView title; 
        public TextView location;
    }  
}  
