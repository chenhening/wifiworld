package com.anynet.wifiworld.wifi;
import java.util.List;

import com.anynet.wifiworld.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;


public class WifiListAdapter extends BaseAdapter {

	private int resource;
	private SectionIndexer mIndexer;
	private List<WifiInfoScaned> mList;
	private Context context;

	public WifiListAdapter(Context context, int textViewResourceId, List<WifiInfoScaned> objects) {
		resource = textViewResourceId;
		mList = objects;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WifiInfoScaned wifi_info = (WifiInfoScaned) getItem(position);  
        LinearLayout layout = null;  
        if (convertView == null) {  
            layout = (LinearLayout) LayoutInflater.from(this.context).inflate(resource, null);  
        } else {  
            layout = (LinearLayout) convertView;  
        }  
        TextView name = (TextView) layout.findViewById(R.id.wifi_name);  
        LinearLayout sortKeyLayout = (LinearLayout) layout.findViewById(R.id.sort_key_layout);  
        TextView sortKey = (TextView) layout.findViewById(R.id.sort_key);  
        name.setText(wifi_info.getWifi_name());  
        int section = mIndexer.getSectionForPosition(position);  
        if (position == mIndexer.getPositionForSection(section)) {  
            sortKey.setText(wifi_info.getWifi_sectoin());  
            sortKeyLayout.setVisibility(View.VISIBLE);  
        } else {  
            sortKeyLayout.setVisibility(View.GONE);  
        }  
        return layout;
	}
	
	public void setIndexer(SectionIndexer indexer) {  
        mIndexer = indexer;  
    } 

}
