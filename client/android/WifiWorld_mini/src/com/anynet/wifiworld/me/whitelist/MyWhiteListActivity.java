package com.anynet.wifiworld.me.whitelist;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiWhite;
import com.anynet.wifiworld.util.LoginHelper;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
public class MyWhiteListActivity extends BaseActivity {

	//IPC
	public static List<WifiWhite> mListData;
	private ListAdapter mAdapter;
	private SwipeMenuListView mListView;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText("我的白名单");
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_whitelist);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//设置白名单
		this.findViewById(R.id.siv_add_from_contact).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
	            Intent i = new Intent();
	            i.setClass(getApplicationContext(), AllContactActivity.class);
	            startActivity(i);
            }
			
		});
		
		//查询服务器
		WifiWhite records = new WifiWhite();
		records.MyUserid = LoginHelper.getInstance(this).getCurLoginUserInfo().getUsername();
		records.QueryWhitersByUser(this, records.MyUserid, new MultiDataCallback<WifiWhite>() {

			@Override
            public boolean onSuccess(List<WifiWhite> objects) {
	            //分析一周的上网记录
				mListData = objects;
				displayList();
				return true;
            }

			@Override
            public boolean onFailed(String msg) {
				Log.d("WifiUsedListActivity", "当前网络不稳定，请稍后再试：" + msg);
	            showToast("当前网络不稳定，请稍后再试：" + msg);
	            return false;
            }
			
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}
	
	private void displayList() {
		mListView = (SwipeMenuListView) findViewById(R.id.listView);
		mAdapter = new ListAdapter();
		mListView.setAdapter(mAdapter);
		
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.LTGRAY));
				// set item width
				openItem.setWidth(dp2px(60));
				// set item title
				openItem.setTitle("名片");
				// set item title fontsize
				openItem.setTitleSize(14);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.LTGRAY));
				// set item width
				deleteItem.setWidth(dp2px(60));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				WifiWhite item = mListData.get(position);
				switch (index) {
				case 0:
					break;
				case 1:
					mListData.remove(position);
					mAdapter.notifyDataSetChanged();
					item.delete(getApplicationContext());
					break;
				}
				return false;
			}
		});
		
		// set SwipeListener
		mListView.setOnSwipeListener(new OnSwipeListener() {
			
			@Override
			public void onSwipeStart(int position) {
			}
			
			@Override
			public void onSwipeEnd(int position) {
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(getApplicationContext(), position + " long click", 0).show();
				return false;
			}
		});
	}
	
	class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public WifiWhite getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_my_whitelist, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			//holder.iv_icon.setImageResource(R.drawable.naicha);
			holder.tv_wifi_name.setText(mListData.get(position).Whiteid);
			holder.tv_wifi_type.setText(mListData.get(position).getTypeDesc());
			
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_wifi_name;
			TextView tv_wifi_type;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_white_logo);
				tv_wifi_name = (TextView) view.findViewById(R.id.tv_white_id);
				tv_wifi_type = (TextView) view.findViewById(R.id.tv_add_white_type);
				view.setTag(this);
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
