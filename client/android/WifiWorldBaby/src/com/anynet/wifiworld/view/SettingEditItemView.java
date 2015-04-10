package com.anynet.wifiworld.view;

import im.yixin.sdk.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;
import com.ta.utdid2.android.utils.StringUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingEditItemView extends RelativeLayout {

	private boolean contentEditable;
	private int contentEditType;
	private static final int EDIT_TYPE_INPUTBOX = 0;
	private static final int EDIT_TYPE_SELECTBOX = 1;
	private EditText contentET;
	private TextView contentTV;
	private Button editBtn;
	private Context mContext;

	private PopupWindow pWindow = null;
	private ListView list = null; // 下拉表

	private OptionsAdapter adapter = null; // 下拉框适配器
	private List<String> datas = null; // 下拉框数据
	private RelativeLayout layout = null; // 父控件
	private ImageView image = null; // 下拉箭头
	private int p_width = -1; // 下拉框宽度

	public SettingEditItemView(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	public SettingEditItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public SettingEditItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
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
		if (contentEditable)
			editBtn.setText(R.string.edit);
		if(contentEditType == EDIT_TYPE_SELECTBOX){
			contentET.setVisibility(View.INVISIBLE);
		}
		edited = false;

		if(contentEditable) editBtn.setText(R.string.edit);
		editBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (contentEditable) {
					if (contentEditType == EDIT_TYPE_INPUTBOX) {
						if (View.VISIBLE == contentET.getVisibility()) {
							contentET.setVisibility(View.INVISIBLE);
							editBtn.setText(R.string.edit);
							contentTV.setText(contentET.getText());
							if (mClickEditButtonListener != null) {
								mClickEditButtonListener.onSave(contentET.getText().toString());
							}
						} else {
							editBtn.setText(R.string.save);
							contentET.setText(contentTV.getText());
							contentET.setVisibility(View.VISIBLE);
							if (mClickEditButtonListener != null) {
								mClickEditButtonListener.beforeEdit();
							}
						}
					} else if (contentEditType == EDIT_TYPE_SELECTBOX) {
						contentET.setVisibility(View.INVISIBLE);
						if (edited) {
							editBtn.setText(R.string.edit);
							edited = false;
							if (mClickEditButtonListener != null) {
								mClickEditButtonListener.onSave(contentTV.getText());
							}
							return;
						} else {
							editBtn.setText(R.string.edit);
							if (mClickEditButtonListener != null) {
								mClickEditButtonListener.beforeEdit();
							}
							changPopState(contentET);
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
		public void onSave(CharSequence charSequence);

		public void beforeEdit();
	}

	boolean edited = false;
	/** 初始化下拉框
	 * 
	 * @param par
	 *            父控件 */
	private void popWindow(final View par) {
		if (pWindow == null) {

			// 布局文件
			View v = LayoutInflater.from(mContext).inflate(R.layout.list, null);
			list = (ListView) v.findViewById(R.id.list);
			list.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// R.String.butian代表的是“不填”
					contentTV.setText(datas.get(arg2).toString()); // 将当前点击的item中的字符串显示出来
					edited = true;
					editBtn.setText(R.string.save);
					if (pWindow != null) { // 关闭下拉框
						changPopState(par);
					}
				}
			});
			adapter = new OptionsAdapter(mContext, datas); // 根据数据，设置下拉框显示
			list.setAdapter(adapter);
			list.setDivider(null); // 屏蔽下拉框每个item之间的线条
			/** 两种不同长度的下拉框，主要是为了适应屏幕的大小 */
			if (p_width > 0) {
				pWindow = new PopupWindow(v, par.getWidth(), 150);
			} else {
				pWindow = new PopupWindow(v, par.getWidth(), 300);
			}
			pWindow.setFocusable(true);
			pWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.knock_background)));
			pWindow.setOutsideTouchable(true);
			pWindow.update();
		}
		pWindow.showAsDropDown(contentET);
	}

	/** 显示或者隐藏下拉框
	 * 
	 * @param v */
	private void changPopState(View v) {
		if (pWindow == null) {
			popWindow(v);
			return;
		}
		if (!pWindow.isShowing()) {
			popWindow(v);
		} else {
			if (pWindow != null) {
				pWindow.dismiss();
			}
		}
	}

	public void setDatas(List<String> datas) {
		this.datas = datas;
		if (adapter != null) {
			adapter.setDatas(datas);
			adapter.notifyDataSetInvalidated();
		}
	}

	public class OptionsAdapter extends BaseAdapter {

		private Context context = null;
		private List<String> datas = null;

		public OptionsAdapter(Context context, List<String> d) {
			this.context = context;
			this.datas = d;
		}

		public int getCount() {
			return datas.size();
		}

		public Object getItem(int arg0) {
			return datas.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		/** @author ZYJ
		 * @功能 一个简单TextView显示 */
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = LayoutInflater.from(context).inflate(R.layout.list_view_item, null);
			TextView textStr = (TextView) view.findViewById(R.id.item);
			textStr.setText("\t" + getItem(arg0).toString());
			return view;
		}

		public void setDatas(List<String> datas) {
			this.datas = datas;
		}

	}
}