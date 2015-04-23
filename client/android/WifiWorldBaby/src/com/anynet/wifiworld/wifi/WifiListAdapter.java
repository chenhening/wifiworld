package com.anynet.wifiworld.wifi;
import java.util.ArrayList;
import java.util.List;

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

import com.anynet.wifiworld.R;


public class WifiListAdapter extends BaseAdapter {
	private final static String TAG = WifiListAdapter.class.getSimpleName();

	private List<WifiInfoScanned> mWifiList = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiTags = new ArrayList<WifiInfoScanned>();
	private int wifiFreeCnt = 0;
	private int wifiEncryptCnt = 0;
	private int wifiAuthCnt = 0; //表示平台认证的wifi
	private Context mContext;

	public WifiListAdapter(Context context, List<WifiInfoScanned> wifiAuth, List<WifiInfoScanned> wifiFree, 
		List<WifiInfoScanned> wifiEncrypt) {
		super();
		this.mContext = context;
		
		refreshWifiList(wifiAuth, wifiFree, wifiEncrypt);
	}
	
	public void refreshWifiList(List<WifiInfoScanned> wifiAuth, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		mWifiList.clear();
		
		//设置认证wifi的分界标志
		WifiInfoScanned AuthTag = new WifiInfoScanned("Auth");
		mWifiList.add(AuthTag);
		mWifiTags.add(AuthTag);
		wifiAuthCnt = wifiAuth.size();
		if (wifiAuth.isEmpty()) {
			WifiInfoScanned freeDeclare = new WifiInfoScanned("AuthDeclare");
			wifiAuth.add(freeDeclare);
		}
		for (int i = 0; i < wifiAuth.size(); i++) {
			mWifiList.add(wifiAuth.get(i));
		}
		
		//设置免费的分界标志
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
		
		//设置加密分界标志
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
		
		Log.i(TAG, "refresh wifi list...");
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
        		((WifiInfoScanned)getItem(position)).getWifiName() == "EncryptDeclare" ||
        		((WifiInfoScanned)getItem(position)).getWifiName() == "AuthDeclare"){
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
            if ((infoScanned).getWifiName().equals("Auth")) {
            	textView.setText("获取到" + wifiAuthCnt + "个认证WiFi");
        	} else if ((infoScanned).getWifiName().equals("Free")) {
            	textView.setText("挖掘到" + wifiFreeCnt + "个不需要密码WiFi");
			} else if ((infoScanned).getWifiName().equals("Encrypt")) {
				textView.setText("扫描到" + wifiEncryptCnt + "个需要密码的WiFi");
			}
        } else {
	        	if (infoScanned.getWifiName() == "AuthDeclare") {
	        		view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_declare, null);
	                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
					textView.setText("认证WiFi出现在这里");
					textView.setTextColor(Color.GRAY);
	        	}else if ((infoScanned).getWifiName() == "FreeDeclare") {
	            	view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_declare, null);
                textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("不需要密码WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else if ((infoScanned).getWifiName() == "EncryptDeclare") {
				view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_declare, null);
                	textView = (TextView) view.findViewById(R.id.wifi_name_dec);
				textView.setText("需要密码的WiFi出现在这里");
				textView.setTextColor(Color.GRAY);
			} else {
				view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_item, null);
	            	textView = (TextView) view.findViewById(R.id.wifi_name);
	            if (infoScanned.getAlias() != null) {
	            		textView.setText(infoScanned.getAlias());
	            		TextView nameTextView = (TextView) view.findViewById(R.id.wifi_alias);
	            		nameTextView.setText("[" + infoScanned.getWifiName() + "]");
	            } else {
	            		textView.setText((infoScanned).getWifiName());
				}
				
				TextView remarkText = (TextView) view.findViewById(R.id.wifi_remark);
				if (infoScanned.getRemark() == null || infoScanned.getRemark() == "") {
					remarkText.setVisibility(View.GONE);
				} else {
					remarkText.setText((infoScanned).getRemark());
				}
				
				ImageView imageView = (ImageView)view.findViewById(R.id.wifi_icon);
	            if (position >= (wifiAuthCnt + wifiFreeCnt + 3)) {
					imageView.setImageResource(R.drawable.icon_crack_failed);
				} else {
					imageView.setImageResource(R.drawable.icon_invalid);
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
	            
	            final int pos = position;
	            Button wifiDetails = (Button)view.findViewById(R.id.wifi_details_btn);
	            wifiDetails.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						//mWifiHandleDB.queryWifiDynamic4Display(infoScanned);
						//getWifiProfiles(infoScanned);
						if (pos < wifiAuthCnt + 1) {
							Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.DETAILS_DISPLAY");
							Bundle wifiData = new Bundle();
							wifiData.putSerializable("WifiSelected", infoScanned);
							intent.putExtras(wifiData);
							mContext.startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setClass(mContext, FindOwnerActivity.class);
							Bundle wifiData = new Bundle();
							wifiData.putSerializable("WifiSelected", infoScanned);
							intent.putExtras(wifiData);
							mContext.startActivity(intent);
						}
					}
				});
			}
        }
        return view;
	}

}
