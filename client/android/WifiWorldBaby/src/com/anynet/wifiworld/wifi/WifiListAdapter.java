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
	private int wifiAuthCnt = 0; // 表示平台认证的wifi
	private Context mContext;

	public WifiListAdapter(Context context, List<WifiInfoScanned> wifiAuth, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		super();
		this.mContext = context;

		refreshWifiList(wifiAuth, wifiFree, wifiEncrypt);
	}

	public void refreshWifiList(List<WifiInfoScanned> wifiAuth, List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		mWifiList.clear();

		// 设置认证wifi的分界标志
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

		// 设置免费的分界标志
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

		// 设置加密分界标志
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
		if (mWifiTags.contains(getItem(position)) || ((WifiInfoScanned) getItem(position)).getWifiName() == "FreeDeclare"
				|| ((WifiInfoScanned) getItem(position)).getWifiName() == "EncryptDeclare"
				|| ((WifiInfoScanned) getItem(position)).getWifiName() == "AuthDeclare") {
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		TextView textView = null;
		int type = -1;
		ViewHolder viewHolder = null;
		if (view != null) {
			if (view.getTag() instanceof ViewHolder) {
				viewHolder = (ViewHolder) view.getTag();
				type = viewHolder.getHolderType();
			}
		}

		final WifiInfoScanned infoScanned = (WifiInfoScanned) getItem(position);
		if (mWifiTags.contains(infoScanned)) {
			TagViewHolder tv = null;
			if (viewHolder != null && type == 0) {
				tv = (TagViewHolder) view.getTag();
			} else {
				tv = new TagViewHolder();
				view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_tag, null);
				tv.tag = (TextView) view.findViewById(R.id.wifi_tag_text);
				view.setTag(tv);
			}

			if ((infoScanned).getWifiName().equals("Auth")) {
				tv.tag.setText("获取到" + wifiAuthCnt + "个认证WiFi");
			} else if ((infoScanned).getWifiName().equals("Free")) {
				tv.tag.setText("挖掘到" + wifiFreeCnt + "个不需要密码WiFi");
			} else if ((infoScanned).getWifiName().equals("Encrypt")) {
				tv.tag.setText("扫描到" + wifiEncryptCnt + "个需要密码的WiFi");
			}
		} else {

			if (((infoScanned.getWifiName() == "AuthDeclare") || ((infoScanned).getWifiName() == "FreeDeclare") || ((infoScanned).getWifiName() == "EncryptDeclare"))) {
				DeclareViewHolder dv = null;
				if (viewHolder != null && type == 1) {
					dv = (DeclareViewHolder) view.getTag();
				} else {
					dv = new DeclareViewHolder();
					view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_declare, null);
					dv.name_dec = (TextView) view.findViewById(R.id.wifi_name_dec);
					view.setTag(dv);
				}
				textView = dv.name_dec;
				if (infoScanned.getWifiName() == "AuthDeclare") {
					textView.setText("认证WiFi出现在这里");
					textView.setTextColor(Color.GRAY);
				} else if ((infoScanned).getWifiName() == "FreeDeclare") {
					textView.setText("不需要密码WiFi出现在这里");
					textView.setTextColor(Color.GRAY);
				} else if ((infoScanned).getWifiName() == "EncryptDeclare") {
					textView.setText("需要密码的WiFi出现在这里");
					textView.setTextColor(Color.GRAY);
				}
			} else {
				ItemViewHolder iv = null;
				if (viewHolder != null && type == 2) {
					iv = (ItemViewHolder) view.getTag();
				} else {
					iv = new ItemViewHolder();
					view = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_item, null);
					iv.alias = (TextView) view.findViewById(R.id.wifi_alias);
					iv.icon = (ImageView) view.findViewById(R.id.wifi_icon);
					iv.name = (TextView) view.findViewById(R.id.wifi_name);
					iv.remark = (TextView) view.findViewById(R.id.wifi_remark);
					iv.signalIcon = (ImageView) view.findViewById(R.id.wifi_status_icon);
					iv.signalLevel = (TextView) view.findViewById(R.id.wifi_status_digit);
					iv.wifiDetails = (Button) view.findViewById(R.id.wifi_details_btn);
					view.setTag(iv);
				}

				if (infoScanned.getAlias() != null) {
					iv.name.setText(infoScanned.getAlias());
					iv.alias.setText("[" + infoScanned.getWifiName() + "]");
				} else {
					iv.name.setText((infoScanned).getWifiName());
					iv.alias.setVisibility(View.GONE);
				}

				if (infoScanned.getRemark() == null || infoScanned.getRemark() == "") {
					iv.remark.setVisibility(View.GONE);
				} else {
					iv.remark.setText((infoScanned).getRemark());
				}

				if (position >= (wifiAuthCnt + wifiFreeCnt + 3)) {
					iv.icon.setImageResource(R.drawable.icon_crack_failed);
				} else if (position >= (wifiAuthCnt + 2)) {
					iv.icon.setImageResource(R.drawable.icon_invalid);
				} else {
					if (infoScanned != null && infoScanned.getWifiLogo() != null) {
						iv.icon.setImageBitmap(infoScanned.getWifiLogo());
					} else {
						iv.icon.setImageResource(R.drawable.icon_invalid);
					}
				}

				int signalStrength = (infoScanned).getWifiStrength();
				iv.signalLevel.setText(signalStrength + "%");
				if (signalStrength >= 80) {
					iv.signalIcon.setImageResource(R.drawable.lock_wifi_signal_icon3);
				} else if (signalStrength >= 60) {
					iv.signalIcon.setImageResource(R.drawable.lock_wifi_signal_icon2);
				} else {
					iv.signalIcon.setImageResource(R.drawable.lock_wifi_signal_icon1);
				}

				final int pos = position;
				iv.wifiDetails.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// mWifiHandleDB.queryWifiDynamic4Display(infoScanned);
						// getWifiProfiles(infoScanned);
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

	public interface ViewHolder {
		public int getHolderType();
	}

	public final class TagViewHolder implements ViewHolder {
		public static final int type = 0;
		public TextView tag;

		@Override
		public int getHolderType() {
			// TODO Auto-generated method stub
			return type;
		}
	}

	public final class DeclareViewHolder implements ViewHolder {
		public static final int type = 1;
		public TextView name_dec;

		@Override
		public int getHolderType() {
			// TODO Auto-generated method stub
			return type;
		}
	}

	public final class ItemViewHolder implements ViewHolder {
		public static final int type = 2;
		public TextView remark;
		public TextView name;
		public TextView alias;
		public ImageView icon;
		public TextView signalLevel;
		public ImageView signalIcon;
		public Button wifiDetails;

		@Override
		public int getHolderType() {
			// TODO Auto-generated method stub
			return type;
		}
	}

}
