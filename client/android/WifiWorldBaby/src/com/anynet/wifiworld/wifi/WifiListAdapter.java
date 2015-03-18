package com.anynet.wifiworld.wifi;
import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class WifiListAdapter extends BaseAdapter {
	private final static String TAG = WifiListAdapter.class.getSimpleName();

	private List<WifiInfoScanned> mWifiList = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiTags = new ArrayList<WifiInfoScanned>();
	private int wifiFreeCnt = 0;
	private int wifiEncryptCnt = 0;
	private Context context;

	public WifiListAdapter(Context context, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		super();
		this.context = context;
		
		WifiInfoScanned freeTag = new WifiInfoScanned("Free",null, null, null, 0, null);
		mWifiList.add(freeTag);
		mWifiTags.add(freeTag);
		wifiFreeCnt = wifiFree.size();
		if (wifiFree.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("FreeDeclare", null,null, null, 0, null);
			wifiFree.add(freeDeclare);
		}
		for (int i = 0; i < wifiFree.size(); i++) {
			mWifiList.add(wifiFree.get(i));
		}
		
		WifiInfoScanned encryptTag = new WifiInfoScanned("Encrypt", null,null, null, 0, null);
		mWifiList.add(encryptTag);
		mWifiTags.add(encryptTag);
		wifiEncryptCnt = wifiEncrypt.size();
		if (wifiEncrypt.isEmpty()) {
			WifiInfoScanned encryptDeclare = new WifiInfoScanned("EncryptDeclare", null,null, null, 0, null);
			wifiEncrypt.add(encryptDeclare);
		}
		for (int i = 0; i < wifiEncrypt.size(); i++) {
			mWifiList.add(wifiEncrypt.get(i));
		}
	}
	
	public void refreshWifiList(List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		mWifiList.clear();
		
		WifiInfoScanned freeTag = new WifiInfoScanned("Free",null, null, null, 0, null);
		mWifiList.add(freeTag);
		mWifiTags.add(freeTag);
		wifiFreeCnt = wifiFree.size();
		if (wifiFree.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("FreeDeclare", null,null, null, 0, null);
			wifiFree.add(freeDeclare);
		}
		for (int i = 0; i < wifiFree.size(); i++) {
			mWifiList.add(wifiFree.get(i));
		}
		
		WifiInfoScanned encryptTag = new WifiInfoScanned("Encrypt", null,null, null, 0, null);
		mWifiList.add(encryptTag);
		mWifiTags.add(encryptTag);
		wifiEncryptCnt = wifiEncrypt.size();
		if (wifiEncrypt.isEmpty()) {
			WifiInfoScanned encryptDeclare = new WifiInfoScanned("EncryptDeclare", null,null, null, 0, null);
			wifiEncrypt.add(encryptDeclare);
		}
		for (int i = 0; i < wifiEncrypt.size(); i++) {
			mWifiList.add(wifiEncrypt.get(i));
		}
		
		notifyDataSetChanged();
	}
	
	public void getDataFromDB(WifiInfoScanned infoScanned) {
		WifiProfile wifiPro = new WifiProfile();
		wifiPro.QueryByMacAddress(this.context, infoScanned.getWifiMAC(), new DataCallback<WifiProfile>() {
			
			@Override
			public void onSuccess(WifiProfile object) {
				Log.i(TAG, "SSID: " + object.Ssid);
				Log.i(TAG, "Mac: " + object.MacAddr);
				Log.i(TAG, "Alias: " + object.Alias);
				Log.i(TAG, "Password: " + object.Password);
				Log.i(TAG, "Banner: " + object.Banner);
				Log.i(TAG, "Type: " + object.Type);
				Log.i(TAG, "Sponser: " + object.Sponser);
				Log.i(TAG, "Geometry: " + object.Geometry);
				Log.i(TAG, "Income: " + object.Income);
				
			}
			
			@Override
			public void onFailed(String msg) {
				Log.i(TAG, "Query database failed");
				
			}
		});
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
        		((WifiInfoScanned)getItem(position)).getWifiName() == "FreeDeclare" ||
        		((WifiInfoScanned)getItem(position)).getWifiName() == "EncryptDeclare"){
            return false;
        }
        return super.isEnabled(position);
    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		TextView textView = null;
        if(mWifiTags.contains(getItem(position))){
            view = LayoutInflater.from(this.context).inflate(R.layout.wifi_tag, null);
            textView = (TextView) view.findViewById(R.id.wifi_tag_text);
            if (((WifiInfoScanned)getItem(position)).getWifiName().equals("Free")) {
            	textView.setText("挖掘到" + wifiFreeCnt + "个免费WiFi");
			} else if (((WifiInfoScanned)getItem(position)).getWifiName().equals("Encrypt")) {
				textView.setText("扫描到" + wifiEncryptCnt + "个需要密码的WiFi");
			}
        } else {
            if (((WifiInfoScanned)getItem(position)).getWifiName() == "FreeDeclare") {
            	view = LayoutInflater.from(this.context).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("免费WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else if (((WifiInfoScanned)getItem(position)).getWifiName() == "EncryptDeclare") {
				view = LayoutInflater.from(this.context).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("需要密码的WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else {
				view = LayoutInflater.from(this.context).inflate(R.layout.wifi_item, null);
	            textView = (TextView) view.findViewById(R.id.wifi_name);
				textView.setText(((WifiInfoScanned)getItem(position)).getWifiName());
				
				TextView remarkText = (TextView) view.findViewById(R.id.wifi_remark);
				if (((WifiInfoScanned)getItem(position)).getRemark() != null) {
					remarkText.setText(((WifiInfoScanned)getItem(position)).getRemark());
				} else {
					remarkText.setVisibility(View.GONE);
				}
				
				ImageView imageView = (ImageView)view.findViewById(R.id.wifi_icon);
	            if (position >= (wifiFreeCnt + 2)) {
	                imageView.setImageResource(R.drawable.id_locked);
				} else {
					imageView.setImageResource(R.drawable.id_default);
				}
	            
	            int signalStrength = ((WifiInfoScanned)getItem(position)).getWifiStrength();
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
	            
	            Button wifiDetails = (Button)view.findViewById(R.id.wifi_details_btn);
	            wifiDetails.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.DETAILS_DISPLAY");
						Bundle wifiData = new Bundle();
						wifiData.putSerializable("WifiSelected", ((WifiInfoScanned)getItem(position)));
						intent.putExtras(wifiData);
						getDataFromDB((WifiInfoScanned)getItem(position));
						context.startActivity(intent);
					}
				});
			}
        }
        return view;
	}

}
