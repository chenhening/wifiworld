package com.anynet.wifiworld.wifi;
import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private Context mContext;
	private WifiHandleDB mWifiHandleDB;
	
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handle display wifi details message");
			int value = msg.what;
			if (value == ((MainActivity)mContext).GET_WIFI_DETAILS) {
				WifiProfile wifiProfile = (WifiProfile)msg.obj;
				WifiInfoScanned wifiInfoScanned = new WifiInfoScanned();
				wifiInfoScanned.setWifiName(wifiProfile.Ssid);
				wifiInfoScanned.setWifiLogo(wifiProfile.Logo);
				
				Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.DETAILS_DISPLAY");
				Bundle wifiData = new Bundle();
				wifiData.putSerializable("WifiSelected", wifiInfoScanned);
				intent.putExtras(wifiData);
				mContext.startActivity(intent);
			}
			super.handleMessage(msg);
		}
	};

	public WifiListAdapter(Context context, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		super();
		this.mContext = context;
		mWifiHandleDB = WifiHandleDB.getInstance(context, mHandler);
		
		WifiInfoScanned freeTag = new WifiInfoScanned("Free");
		mWifiList.add(freeTag);
		mWifiTags.add(freeTag);
		wifiFreeCnt = wifiFree.size();
		if (wifiFree.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("FreeDeclare");
			wifiFree.add(freeDeclare);
		}
		for (int i = 0; i < wifiFree.size(); i++) {
			mWifiList.add(wifiFree.get(i));
		}
		
		WifiInfoScanned encryptTag = new WifiInfoScanned("Encrypt");
		mWifiList.add(encryptTag);
		mWifiTags.add(encryptTag);
		wifiEncryptCnt = wifiEncrypt.size();
		if (wifiEncrypt.isEmpty()) {
			WifiInfoScanned encryptDeclare = new WifiInfoScanned("EncryptDeclare");
			wifiEncrypt.add(encryptDeclare);
		}
		for (int i = 0; i < wifiEncrypt.size(); i++) {
			mWifiList.add(wifiEncrypt.get(i));
		}
	}
	
	public void refreshWifiList(List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		mWifiList.clear();
		
		WifiInfoScanned freeTag = new WifiInfoScanned("Free");
		mWifiList.add(freeTag);
		mWifiTags.add(freeTag);
		wifiFreeCnt = wifiFree.size();
		if (wifiFree.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("FreeDeclare");
			wifiFree.add(freeDeclare);
		}
		for (int i = 0; i < wifiFree.size(); i++) {
			mWifiList.add(wifiFree.get(i));
		}
		
		WifiInfoScanned encryptTag = new WifiInfoScanned("Encrypt");
		mWifiList.add(encryptTag);
		mWifiTags.add(encryptTag);
		wifiEncryptCnt = wifiEncrypt.size();
		if (wifiEncrypt.isEmpty()) {
			WifiInfoScanned encryptDeclare = new WifiInfoScanned("EncryptDeclare");
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
		final WifiInfoScanned infoScanned = (WifiInfoScanned)getItem(position);
        if(mWifiTags.contains(infoScanned)){
            view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_tag, null);
            textView = (TextView) view.findViewById(R.id.wifi_tag_text);
            if ((infoScanned).getWifiName().equals("Free")) {
            	textView.setText("挖掘到" + wifiFreeCnt + "个免费WiFi");
			} else if ((infoScanned).getWifiName().equals("Encrypt")) {
				textView.setText("扫描到" + wifiEncryptCnt + "个需要密码的WiFi");
			}
        } else {
            if ((infoScanned).getWifiName() == "FreeDeclare") {
            	view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("免费WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else if ((infoScanned).getWifiName() == "EncryptDeclare") {
				view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("需要密码的WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else {
				view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_item, null);
	            textView = (TextView) view.findViewById(R.id.wifi_name);
				textView.setText((infoScanned).getWifiName());
				
				TextView remarkText = (TextView) view.findViewById(R.id.wifi_remark);
				if ((infoScanned).getRemark() != null) {
					remarkText.setText((infoScanned).getRemark());
				} else {
					remarkText.setVisibility(View.GONE);
				}
				
				ImageView imageView = (ImageView)view.findViewById(R.id.wifi_icon);
	            if (position >= (wifiFreeCnt + 2)) {
	                imageView.setImageResource(R.drawable.id_locked);
				} else {
					imageView.setImageResource(R.drawable.id_default);
				}
	            
	            int signalStrength = (infoScanned).getWifiStrength();
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
						mWifiHandleDB.queryWifiProfile4Display(infoScanned);
					}
				});
			}
        }
        return view;
	}

}
