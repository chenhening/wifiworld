package com.anynet.wifiworld.view;

import com.anynet.wifiworld.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	public SettingItemView(Context context) {
		super(context);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TypedArray是一个用来存放由context.obtainStyledAttributes获得的属性的数组
		// 在使用完成后，一定要调用recycle方法
		// 属性的名称是styleable中的名称+“_”+属性名称
		RelativeLayout.inflate(context, R.layout.item_view_setting, this);
		setBackgroundResource(R.drawable.settings_item_radius_bg_selector);
		/*
		 * setting_item_icon setting_item_next_layout setting_item_next
		 * setting_item_checkbox setting_item_text setting_item_red_point
		 * setting_item_sub_text setting_item_top_line setting_item_bottom_line
		 */
		ImageView img;
		TextView tv;
		EditText et;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.SettingItemView_backgroundImage: {
				int bgDrawable = array.getInt(attr, 0);
				if (bgDrawable == 1) {
					setBackgroundResource(R.drawable.settings_item_top);
				} else if (bgDrawable == 3) {
					setBackgroundResource(R.drawable.settings_item_middle);
				} else if (bgDrawable == 5) {
					setBackgroundResource(R.drawable.settings_item_bottom);
				}
				break;
			}
			case R.styleable.SettingItemView_icon: {
				Drawable icon = array.getDrawable(attr);
				img = (ImageView) findViewById(R.id.setting_item_icon);
				if(icon!=null) {
					img.setImageDrawable(icon);
				} else {
					img.setVisibility(View.GONE);
				}
				break;
			}

			case R.styleable.SettingItemView_label: {
				String libel = array.getString(attr);
				tv = (TextView) findViewById(R.id.setting_item_text);
				tv.setText(libel);
				break;
			}

			case R.styleable.SettingItemView_showBottomDivider: {
				boolean showt = array.getBoolean(attr, false);
				if (showt) {
					findViewById(R.id.setting_item_bottom_line).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.setting_item_bottom_line).setVisibility(View.GONE);
				}
				break;
			}

			case R.styleable.SettingItemView_showRedPoint: {
				boolean showP = array.getBoolean(attr, false);
				if (showP) {
					findViewById(R.id.setting_item_red_point).setVisibility(VISIBLE);
				} else {
					findViewById(R.id.setting_item_red_point).setVisibility(INVISIBLE);
				}
				break;
			}

			case R.styleable.SettingItemView_showTopDivider: {
				boolean showt = array.getBoolean(attr, false);
				if (showt) {
					findViewById(R.id.setting_item_top_line).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.setting_item_top_line).setVisibility(View.GONE);
				}
				break;
			}

			case R.styleable.SettingItemView_subLabel: {
				String sublibel = array.getString(attr);
				tv = (TextView) findViewById(R.id.setting_item_sub_text);
				tv.setText(sublibel);
				break;
			}

			case R.styleable.SettingItemView_subLabelColor: {
				int sublibelcolor = array.getColor(attr, Color.RED);
				tv = (TextView) findViewById(R.id.setting_item_sub_text);
				tv.setTextColor(sublibelcolor);
				break;
			}

			case R.styleable.SettingItemView_subLabelVisibility: {
				int showt = array.getInt(attr, VISIBLE);
				findViewById(R.id.setting_item_sub_text).setVisibility(showt);
				break;
			}

			case R.styleable.SettingItemView_subLabelSize: {
				float shows = array.getFloat(attr, 16.0f);
				tv = (TextView) findViewById(R.id.setting_item_sub_text);
				tv.setTextSize(shows);
				break;
			}

			case R.styleable.SettingItemView_showContent: {
				boolean showP = array.getBoolean(attr, false);
				if (showP) {
					findViewById(R.id.setting_item_content).setVisibility(VISIBLE);
				} else {
					findViewById(R.id.setting_item_content).setVisibility(INVISIBLE);
				}
				break;
			}

			case R.styleable.SettingItemView_contentColor: {
				int contentcolor = array.getColor(attr, Color.GRAY);
				tv = (TextView) findViewById(R.id.setting_item_content);
				tv.setTextColor(contentcolor);
				break;
			}
			case R.styleable.SettingItemView_contentSize: {
				float shows = array.getFloat(attr, 16.0f);
				tv = (TextView) findViewById(R.id.setting_item_content);
				tv.setTextSize(shows);
				break;
			}
			case R.styleable.SettingItemView_content: {
				String content = array.getString(attr);
				tv = (TextView) findViewById(R.id.setting_item_content);
				tv.setText(content);
				break;
			}
			}

		}
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响}
	}

	public void setContent(String content) {
		TextView tv = (TextView) findViewById(R.id.setting_item_content);
		tv.setText(content);
	}

	public void setLabel(String content) {
		TextView tv = (TextView) findViewById(R.id.setting_item_text);
		tv.setText(content);
	}

	public void setSubLabel(String content) {
		TextView tv = (TextView) findViewById(R.id.setting_item_sub_text);
		tv.setText(content);
	}
}