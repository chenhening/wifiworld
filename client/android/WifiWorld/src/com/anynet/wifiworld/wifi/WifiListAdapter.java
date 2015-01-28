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

	private List<String> mWifiList;
	private List<String> mWifiTags;
	private Context context;

	public WifiListAdapter(Context context, List<String> objects, List<String> tags) {
		super();
		mWifiList = objects;
		mWifiTags = tags;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return mWifiList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mWifiList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	@Override
    public boolean isEnabled(int position) {
        if(mWifiTags.contains(getItem(position))){
            return false;
        }
        return super.isEnabled(position);
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		TextView textView = null;
        if(mWifiTags.contains(getItem(position))){
            view = LayoutInflater.from(this.context).inflate(R.layout.wifi_tag, null);
            textView = (TextView) view.findViewById(R.id.wifi_tag_text);
        }else{                   
            view = LayoutInflater.from(this.context).inflate(R.layout.wifi_item, null);
            textView = (TextView) view.findViewById(R.id.wifi_name);
        }
        textView.setText((String)getItem(position));
        return view;
	}

}
