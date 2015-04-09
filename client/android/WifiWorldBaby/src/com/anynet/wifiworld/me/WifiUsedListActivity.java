package com.anynet.wifiworld.me;

import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.util.LoginHelper;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
public class WifiUsedListActivity extends BaseActivity {

	//IPC
	private List<WifiDynamic> mListData;
	private ListAdapter mAdapter;
	private SwipeMenuListView mListView;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivMySetting.setVisibility(View.GONE);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_used_list);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//查询服务器
		WifiDynamic records = new WifiDynamic();
		records.Userid = LoginHelper.getInstance(this).getCurLoginUserInfo().PhoneNumber;
		records.MarkLoginTime();
		records.QueryWiFiInOneWeek(this, records.LoginTime, new MultiDataCallback<WifiDynamic>() {

			@Override
            public void onSuccess(List<WifiDynamic> objects) {
	            //分析一周的上网记录
				mListData = objects;
				displayList();
            }

			@Override
            public void onFailed(String msg) {
				Log.d("WifiUsedListActivity", "当前网络不稳定，请稍后再试：" + msg);
	            //showToast("当前网络不稳定，请稍后再试：" + msg);
	            //finish();
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
		// TODO Auto-generated method stub
		
		super.onResume();
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
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
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
				WifiDynamic item = mListData.get(position);
				switch (index) {
				case 0:
					break;
				case 1:
					mListData.remove(position);
					mAdapter.notifyDataSetChanged();
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
				Toast.makeText(getApplicationContext(), position + " long click", 0).show();
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
		public WifiDynamic getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_wifi_used_list, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			WifiDynamic item = getItem(position);
			//holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
			holder.tv_wifi_name.setText("employeehost");
			holder.tv_wifi_alias.setText("王思聪家的wifi");
			holder.tv_wifi_addr.setText("武汉市江汉区xxx小区");
			holder.tv_connect_count.setText("已连接46次");
			holder.tv_connect_time.setText("已连接17小时");
			return convertView;
		}

		class ViewHolder {
			//ImageView iv_icon;
			TextView tv_wifi_name;
			TextView tv_wifi_alias;
			TextView tv_wifi_addr;
			TextView tv_connect_count;
			TextView tv_connect_time;

			public ViewHolder(View view) {
				//iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_wifi_name = (TextView) view.findViewById(R.id.tv_wifi_used_ssid);
				tv_wifi_alias = (TextView) view.findViewById(R.id.tv_wifi_used_alias);
				tv_wifi_addr = (TextView) view.findViewById(R.id.tv_wifi_used_addr);
				tv_connect_count = (TextView) view.findViewById(R.id.tv_wifi_used_count);
				tv_connect_time = (TextView) view.findViewById(R.id.tv_wifi_used_time);
				view.setTag(this);
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
