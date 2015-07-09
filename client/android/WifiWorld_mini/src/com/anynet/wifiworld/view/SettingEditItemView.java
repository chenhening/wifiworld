package com.anynet.wifiworld.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anynet.wifiworld.R;

public class SettingEditItemView extends RelativeLayout implements OnClickListener {

	private boolean contentEditable;
	private int contentEditType;
	private static final int EDIT_TYPE_NORMAL = 0;
	private static final int EDIT_TYPE_INPUTBOX = 1;
	private static final int EDIT_TYPE_SELECTBOX = 2;

	private static final int EDIT_MODE = 0;
	private static final int VIEW_MODE = 1;

	private EditText contentET;
	private TextView contentTV;
	private ImageView editBtn;
	private TextView contentHint;
	private Context mContext;

	private PopupWindow pWindow = null;
	private ListView list = null; // 下拉表

	private OptionsAdapter adapter = null; // 下拉框适配器
	private List<String> datas = null; // 下拉框数据
	private RelativeLayout layout = null; // 父控件
	private ImageView image = null; // 下拉箭头
	private int p_width = -1; // 下拉框宽度
	
	private boolean mIsFocus = false;

	public SettingEditItemView(Context context) {
		super(context);
		mContext = context;
		edited = false;
	}

	public SettingEditItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		edited = false;
	}

	public SettingEditItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		RelativeLayout.inflate(context, R.layout.item_view_setting_edit, this);
		
		contentET = (EditText) findViewById(R.id.setting_item_content_edit);
		contentTV = (TextView) findViewById(R.id.setting_item_content);
		contentHint = (TextView) findViewById(R.id.setting_item_content_hint);
		editBtn = (ImageView) findViewById(R.id.setting_item_edit);
		
		FrameLayout fl = (FrameLayout) findViewById(R.id.fl_setting_point_centent);
		fl.setOnClickListener(this);
		
		edited = false;
		editBtn.setVisibility(View.GONE);
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.SettingItemView_label: {
				String libel = array.getString(attr);
				TextView tv = (TextView) findViewById(R.id.setting_item_text);
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
			case R.styleable.SettingItemView_contentHint: {
				String sublibel = array.getString(attr);
				if (sublibel != null && !sublibel.equals("")) {
					contentHint.setText(sublibel);
				} else {
					contentHint.setText(R.string.input);
				}
				break;
			}

			case R.styleable.SettingItemView_contentHintColor: {
				int sublibelcolor = array.getColor(attr, Color.RED);
				contentHint.setTextColor(sublibelcolor);
				break;
			}

			case R.styleable.SettingItemView_contentHintVisibility: {
				int showt = array.getInt(attr, VISIBLE);
				findViewById(R.id.setting_item_content_hint).setVisibility(showt);
				break;
			}

			case R.styleable.SettingItemView_contentHintSize: {
				float shows = array.getFloat(attr, 16.0f);
				contentHint.setTextSize(shows);
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
				contentET.setVisibility(View.INVISIBLE);
				if (content != null && !content.equals("")) {
					contentTV.setText(content);
					contentTV.setVisibility(View.VISIBLE);
					contentHint.setVisibility(View.INVISIBLE);
				} else {
					contentTV.setText("");
					contentTV.setVisibility(View.INVISIBLE);
					contentHint.setVisibility(View.VISIBLE);
				}

				break;
			}
			case R.styleable.SettingItemView_contentEditable: {
				contentEditable = array.getBoolean(attr, false);
				if (contentEditable) {
					contentTV.setVisibility(INVISIBLE);
					contentET.setVisibility(VISIBLE);
					contentET.setEnabled(true);
					//editBtn.setVisibility(VISIBLE);
				} else {
					contentTV.setVisibility(VISIBLE);
					contentET.setVisibility(GONE);
					contentET.setEnabled(false);
					//editBtn.setVisibility(INVISIBLE);
				}
				break;
			}
			case R.styleable.SettingItemView_contentEditType: {
				contentEditType = array.getInt(attr, EDIT_TYPE_NORMAL);
				break;
			}
			case R.styleable.SettingItemView_listData: {
				CharSequence[] list = array.getTextArray(attr);
				datas = new ArrayList<String>();
				if (list != null && list.length > 0) {
					datas.clear();
					for (CharSequence charSequence : list) {
						datas.add(charSequence.toString());
					}
				}
				break;
			}
			}

		}
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响}

		if (contentEditable) {
			if (contentEditType == EDIT_TYPE_SELECTBOX) {
				contentET.setVisibility(View.INVISIBLE);
				contentHint.setVisibility(View.GONE);
				contentTV.setVisibility(VISIBLE);
			} else if (EDIT_TYPE_INPUTBOX == contentEditType) {
				contentET.setVisibility(View.VISIBLE);
				contentHint.setVisibility(View.GONE);
				contentTV.setVisibility(View.GONE);
			} else if (EDIT_TYPE_NORMAL == contentEditType) {
				contentET.setVisibility(View.INVISIBLE);
				contentHint.setVisibility(View.GONE);
				contentTV.setVisibility(VISIBLE);
			}

			//editBtn.setText(R.string.edit);
			//editBtn.setOnClickListener(this);
		} else {
			editBtn.setVisibility(View.INVISIBLE);
			contentET.setVisibility(View.GONE);
			contentHint.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		
		if(!gainFocus){
			if (EDIT_TYPE_INPUTBOX == contentEditType  && contentET.getText()!=null && !contentET.getText().equals("")) {
				contentET.setText(contentTV.getText());
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (contentEditType == EDIT_TYPE_INPUTBOX) {
			if (View.VISIBLE == contentET.getVisibility()) {
				//editBtn.setText(R.string.edit);
				show(VIEW_MODE);
				if (mClickEditButtonListener != null) {
					mClickEditButtonListener.onSave(contentET.getText().toString());
				}
			} else {
				// contentET.setFocusable(true);
				// contentET.setFocusableInTouchMode(true);
				// contentET.requestFocus();
				// contentET.requestFocusFromTouch();
				//editBtn.setText(R.string.save);
				show(EDIT_MODE);
				if (mClickEditButtonListener != null) {
					mClickEditButtonListener.beforeEdit();
				}
			}
		} else if (contentEditType == EDIT_TYPE_SELECTBOX) {
			contentET.setVisibility(View.INVISIBLE);
			contentTV.requestFocus();
			mIsFocus = !mIsFocus;
			if (mIsFocus) {
				contentET.setTextColor(Color.BLACK);
			} else {
				contentET.setTextColor(Color.GRAY);
			}
			if (edited) {
				//editBtn.setText(R.string.edit);
				edited = false;
				if (mClickEditButtonListener != null) {
					mClickEditButtonListener.onSave(contentTV.getText());
				}
				return;
			} else {
				//.setText(R.string.edit);
				if (mClickEditButtonListener != null) {
					mClickEditButtonListener.beforeEdit();
				}
				changPopState(contentET);
			}
		}
		if (mClickButtonListener != null) {
			CharSequence text = contentTV.getText();
			mClickButtonListener.onClick(text);
		}
	}

	private void show(int mode) {
		switch (mode) {
		case EDIT_MODE: {
			if (contentTV.getText() != null && !contentTV.getText().equals("")) {
				contentET.setText(contentTV.getText());
			} else if (contentHint.getText() != null && !contentHint.getText().equals("")) {
				contentET.setHint(contentHint.getText());
			}
			contentTV.setVisibility(View.INVISIBLE);
			contentET.setVisibility(VISIBLE);
			contentHint.setVisibility(INVISIBLE);
			break;
		}
		case VIEW_MODE: {
			contentET.setVisibility(INVISIBLE);
			if (contentET.getText() != null && !contentET.getText().equals("")) {
				contentTV.setText(contentET.getText());
				contentTV.setVisibility(View.VISIBLE);
				contentHint.setVisibility(INVISIBLE);
			} else {
				contentTV.setVisibility(View.INVISIBLE);
				contentHint.setVisibility(VISIBLE);
			}

			break;
		}
		default: {

		}
		}
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
	
	public String getEditContent() {
		return contentET.getText().toString();
	}
	
	public void setEditContent(String content) {
		contentET.setText(content);
		contentET.setSelection(contentET.getText().toString().length());
		contentET.setTextColor(Color.GRAY);
	}
	
	public void setContent(String content) {
		contentTV.setText(content);
		contentTV.setTextColor(Color.GRAY);
	}
	
	public String getContent() {
		return contentTV.getText().toString();
	}
	
	public void setContentEdited(String content) {
		contentTV.setText(content);
		contentTV.setTextColor(Color.BLACK);
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
	ClickButtonListener mClickButtonListener;

	public ClickButtonListener getmClickButtonListener() {
		return mClickButtonListener;
	}

	public void setClickButtonListener(ClickButtonListener mClickButtonListener) {
		this.mClickButtonListener = mClickButtonListener;
	}

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

	public interface ClickButtonListener {
		public void onClick(CharSequence charSequence);
	}

	public void setClickEditListner(OnClickListener onClickListener) {
		FrameLayout fl = (FrameLayout) findViewById(R.id.fl_setting_point_centent);
		fl.setOnClickListener(onClickListener);
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
					setContentEdited(datas.get(arg2).toString()); // 将当前点击的item中的字符串显示出来
					edited = true;
					//.setText(R.string.save);
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
			pWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeResource(
					getResources(), R.drawable.knock_background)));
			pWindow.setOutsideTouchable(true);
			pWindow.update();
		}
		pWindow.showAsDropDown(par);
	}

	/** 显示或者隐藏下拉框
	 * 
	 * @param v */
	public void changPopState(View v) {
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
		private LayoutInflater mInflater;

		public OptionsAdapter(Context context, List<String> d) {
			this.context = context;
			this.datas = d;
			mInflater = LayoutInflater.from(mContext);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_sample_textview_list, null);
				holder.date = (TextView) convertView.findViewById(R.id.item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.date.setText("\t" + getItem(position).toString());
			return convertView;
		}

		public void setDatas(List<String> datas) {
			this.datas = datas;
		}

		public final class ViewHolder {
			public TextView date;
		}

	}
}