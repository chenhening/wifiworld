package com.anynet.wifiworld.view;

import com.anynet.wifiworld.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingEditItemView extends RelativeLayout {

	private boolean contentEditable;
	private int contentEditType;
	private static final int EDIT_TYPE_INPUTBOX = 0;
	private static final int EDIT_TYPE_SELECTBOX = 1;
	private EditText contentET;
	private TextView contentTV;
	private Button editBtn;
	public SettingEditItemView(Context context) {
		super(context);
	}

	public SettingEditItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SettingEditItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		RelativeLayout.inflate(context, R.layout.view_setting_edit_item, this);
		ImageView img;
		TextView tv;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
		contentET = (EditText) findViewById(R.id.setting_item_content_edit);
		contentTV = (TextView) findViewById(R.id.setting_item_content);
		editBtn = (Button) findViewById(R.id.setting_item_edit);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.SettingItemView_icon: {
				Drawable icon = array.getDrawable(attr);
				img = (ImageView) findViewById(R.id.setting_item_icon);
				img.setImageDrawable(icon);
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

			case R.styleable.SettingItemView_showTopDivider: {
				boolean showt = array.getBoolean(attr, false);
				if (showt) {
					findViewById(R.id.setting_item_top_line).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.setting_item_top_line).setVisibility(View.GONE);
				}
				break;
			}

			case R.styleable.SettingItemView_showContent: {
				boolean showP = array.getBoolean(attr, false);
				if (showP) {
					contentTV.setVisibility(VISIBLE);
				} else {
					contentTV.setVisibility(INVISIBLE);
				}
				break;
			}

			case R.styleable.SettingItemView_contentColor: {
				int contentcolor = array.getColor(attr, Color.GRAY);
				
				contentTV.setTextColor(contentcolor);
				break;
			}
			case R.styleable.SettingItemView_contentSize: {
				float shows = array.getFloat(attr, 16.0f);
				contentTV.setTextSize(shows);
				break;
			}
			case R.styleable.SettingItemView_content: {
				String content = array.getString(attr);
				contentTV.setText(content);
				break;
			}
			case R.styleable.SettingItemView_contentEditable: {
				contentEditable = array.getBoolean(attr, false);
				if (contentEditable) {
					contentTV.setVisibility(VISIBLE);
					contentET.setVisibility(INVISIBLE);
					contentET.setEnabled(true);					
					editBtn.setVisibility(VISIBLE);
				} else {
					contentTV.setVisibility(INVISIBLE);
					contentET.setVisibility(GONE);
					contentET.setEnabled(false);
					editBtn.setVisibility(INVISIBLE);
				}
				break;
			}
			case R.styleable.SettingItemView_contentEditType: {
				contentEditType = array.getInt(attr, EDIT_TYPE_INPUTBOX);
				switch (contentEditType) {
				case EDIT_TYPE_INPUTBOX:

					break;
				case EDIT_TYPE_SELECTBOX:

					break;

				default:
					break;
				}
				break;
			}
			}

		}
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响}
		setBackgroundResource(R.drawable.settings_item_radius_bg_selector);
		if(contentEditable) editBtn.setText(R.string.edit);
		editBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(contentEditable){
					if(View.VISIBLE == contentET.getVisibility()){
						contentET.setVisibility(View.INVISIBLE);
						editBtn.setText(R.string.edit);
						contentTV.setText(contentET.getText());
						if(mClickEditButtonListener != null){
							mClickEditButtonListener.save(contentET.getText());
						}
					}else{
						editBtn.setText(R.string.save);
						contentET.setText(contentTV.getText());
						contentET.setVisibility(View.VISIBLE);
						if(mClickEditButtonListener != null){
							mClickEditButtonListener.edit();
						}
					}
					
				}

			}
		});
	}

	public void editContent() {
		if (contentEditable) {
			switch (contentEditType) {
			case EDIT_TYPE_INPUTBOX:

				break;
			case EDIT_TYPE_SELECTBOX:

				break;

			default:
				break;
			}
		}
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

	ClickEditButtonListener mClickEditButtonListener;

	public void setClickEditButtonListener(ClickEditButtonListener m) {
		mClickEditButtonListener = m;
	}

	public void unsetClickEditButtonListener() {
		mClickEditButtonListener = null;
	}

	public interface ClickEditButtonListener {
		public void save(Editable editable);
		public void edit();
	}
}