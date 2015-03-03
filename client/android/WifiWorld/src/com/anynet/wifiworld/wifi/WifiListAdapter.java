package com.anynet.wifiworld.wifi;
import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class WifiListAdapter extends BaseAdapter {

	private List<WifiInfoScanned> mWifiList = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiTags = new ArrayList<WifiInfoScanned>();
	private int wifiFreeCnt = 0;
	private int wifiEncryptCnt = 0;
	private Context context;

	public WifiListAdapter(Context context, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		super();
		this.context = context;
		
		WifiInfoScanned freeTag = new WifiInfoScanned("Free", null, null, 0);
		mWifiList.add(freeTag);
		mWifiTags.add(freeTag);
		wifiFreeCnt = wifiFree.size();
		if (wifiFree.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("FreeDeclare", null, null, 0);
			wifiFree.add(freeDeclare);
		}
		for (int i = 0; i < wifiFree.size(); i++) {
			mWifiList.add(wifiFree.get(i));
		}
		
		WifiInfoScanned encryptTag = new WifiInfoScanned("Encrypt", null, null, 0);
		mWifiList.add(encryptTag);
		mWifiTags.add(encryptTag);
		wifiEncryptCnt = wifiEncrypt.size();
		if (wifiEncrypt.isEmpty()) {
			WifiInfoScanned encryptDeclare = new WifiInfoScanned("EncryptDeclare", null, null, 0);
			wifiEncrypt.add(encryptDeclare);
		}
		for (int i = 0; i < wifiEncrypt.size(); i++) {
			mWifiList.add(wifiEncrypt.get(i));
		}
	}
	
	public void refreshWifiList(List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		mWifiList.clear();
		
		WifiInfoScanned freeTag = new WifiInfoScanned("Free", null, null, 0);
		mWifiList.add(freeTag);
		mWifiTags.add(freeTag);
		wifiFreeCnt = wifiFree.size();
		if (wifiFree.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("FreeDeclare", null, null, 0);
			wifiFree.add(freeDeclare);
		}
		for (int i = 0; i < wifiFree.size(); i++) {
			mWifiList.add(wifiFree.get(i));
		}
		
		WifiInfoScanned encryptTag = new WifiInfoScanned("Encrypt", null, null, 0);
		mWifiList.add(encryptTag);
		mWifiTags.add(encryptTag);
		wifiEncryptCnt = wifiEncrypt.size();
		if (wifiEncrypt.isEmpty()) {
			WifiInfoScanned encryptDeclare = new WifiInfoScanned("EncryptDeclare", null, null, 0);
			wifiEncrypt.add(encryptDeclare);
		}
		for (int i = 0; i < wifiEncrypt.size(); i++) {
			mWifiList.add(wifiEncrypt.get(i));
		}
		
		notifyDataSetChanged();
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
        if(mWifiTags.contains(getItem(position)) ||
        		((WifiInfoScanned)getItem(position)).getWifi_name() == "FreeDeclare" ||
        		((WifiInfoScanned)getItem(position)).getWifi_name() == "EncryptDeclare"){
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
            if (((WifiInfoScanned)getItem(position)).getWifi_name().equals("Free")) {
            	textView.setText("挖掘到" + wifiFreeCnt + "个免费WiFi");
			} else if (((WifiInfoScanned)getItem(position)).getWifi_name().equals("Encrypt")) {
				textView.setText("扫描到" + wifiEncryptCnt + "个需要密码的WiFi");
			}
        } else {
            if (((WifiInfoScanned)getItem(position)).getWifi_name() == "FreeDeclare") {
            	view = LayoutInflater.from(this.context).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("免费WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else if (((WifiInfoScanned)getItem(position)).getWifi_name() == "EncryptDeclare") {
				view = LayoutInflater.from(this.context).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("需要密码的WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else {
				view = LayoutInflater.from(this.context).inflate(R.layout.wifi_item, null);
	            textView = (TextView) view.findViewById(R.id.wifi_name);
	            ImageView imageView = (ImageView)view.findViewById(R.id.wifi_icon);
				textView.setText(((WifiInfoScanned)getItem(position)).getWifi_name());
	            if (position >= (wifiFreeCnt + 2)) {
	                imageView.setImageResource(R.drawable.id_locked);
				} else {
					imageView.setImageResource(R.drawable.id_default);
				}
	            int signalStrength = ((WifiInfoScanned)getItem(position)).getWifi_level();
	            TextView signalLevelView = (TextView) view.findViewById(R.id.wifi_status_digit);
	            signalLevelView.setText(signalStrength + "%");
	            ImageView signalImage = (ImageView) view.findViewById(R.id.wifi_status_icon);
	            if (signalStrength >= 80) {
	            	signalImage.setImageResource(R.drawable.lock_wifi_signal_icon3);
				} else if (signalStrength >= 60) {
					signalImage.setImageResource(R.drawable.lock_wifi_signal_icon2);
				} else {
					signalImage.setImageResource(R.drawable.lock_wifi_signal_icon1);
				}
			}
        }
        return view;
	}

}
