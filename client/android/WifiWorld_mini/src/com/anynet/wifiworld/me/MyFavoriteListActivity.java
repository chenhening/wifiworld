package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiFollow;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.ui.WifiDetailsActivity;
import com.anynet.wifiworld.wifi.ui.WifiNotAuthListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
public class MyFavoriteListActivity extends BaseActivity {
	private final static String TAG = MyFavoriteListActivity.class.getSimpleName();
	
	//IPC
	private Context mContext;
	public static List<WifiFollow> mListData;
	public static List<WifiProfile> mWifiProfiles;
	private ListAdapter mAdapter;
	private SwipeMenuListView mListView;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText("我的收藏");
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
		mContext = this;
		findViewById(R.id.ll_whitelist_setting).setVisibility(View.GONE);
		TextView tvtile = (TextView) findViewById(R.id.tv_white_list_title);
		tvtile.setText("当前收藏的网络列表(向左滑动可删除)");
		
		//查询服务器
		WifiFollow records = new WifiFollow();
		records.Userid = LoginHelper.getInstance(this).getCurLoginUserInfo().getUsername();
		records.QueryWifiByUser(mContext, records.Userid, new MultiDataCallback<WifiFollow>() {

			@Override
            public boolean onSuccess(List<WifiFollow> objects) {
	            //分析一周的上网记录
				mListData = objects;
				List<String> macs = new ArrayList<String>();
				for (WifiFollow wifiFollow : objects) {
					macs.add(wifiFollow.MacAddr);
				}
				if (macs.size() > 0) {
					WifiProfile wifiProfile = new WifiProfile();
					wifiProfile.BatchQueryByMacAddress(mContext, macs, false, new MultiDataCallback<WifiProfile>() {

						@Override
						public boolean onSuccess(List<WifiProfile> objects) {
							mWifiProfiles = objects;
							displayList(objects);
							return false;
						}

						@Override
						public boolean onFailed(String msg) {
							Log.d(TAG, "获取WiFi信息失败，请稍后再试：" + msg);
				            showToast("获取WiFi信息失败，请稍后再试：" + msg);
							return false;
						}
						
					});
				}
				
				return true;
            }

			@Override
            public boolean onFailed(String msg) {
				Log.d(TAG, "当前网络不稳定，请稍后再试：" + msg);
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
	
	private void displayList(final List<WifiProfile> wifiProfiles) {
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
				WifiFollow item = mListData.get(position);
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
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				WifiFollow wifiFollow = mListData.get(position);
				WifiProfile wifiProfile = getWifiProfile(wifiFollow.MacAddr, wifiProfiles);
				if (wifiProfile != null) {
					Intent i = new Intent(mContext, WifiDetailsActivity.class);
					Bundle wifiData = new Bundle();
					wifiData.putSerializable(WifiProfile.TAG, wifiProfile);
					i.putExtras(wifiData);
					mContext.startActivity(i);
				} else {
					Intent i = new Intent(mContext, WifiDetailsActivity.class);
					List<String> data = new ArrayList<String>();
					data.add(wifiFollow.WifiSSID);
					data.add(wifiFollow.MacAddr);
					i.putStringArrayListExtra(WifiNotAuthListAdapter.TAG, (ArrayList<String>) data);
					mContext.startActivity(i);
				}
				
			}
		});
	}
	
	class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public WifiFollow getItem(int position) {
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
			WifiFollow wifiFollow = mListData.get(position);
			WifiProfile wifiProfile = getWifiProfile(wifiFollow.MacAddr, mWifiProfiles);
			//holder.iv_icon.setImageResource(R.drawable.naicha);
			if (wifiProfile != null) {
				holder.iv_icon.setImageBitmap(wifiProfile.getLogo());
				holder.tv_wifi_name.setText(wifiProfile.Alias);
			} else {
				holder.tv_wifi_name.setText("未认证");
			}
			holder.tv_wifi_type.setText(mListData.get(position).WifiSSID);
			
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
	
	private WifiProfile getWifiProfile(String macAddress, List<WifiProfile> wifiProfiles) {
		if (wifiProfiles != null) {
			for (int idx=0; idx<wifiProfiles.size(); ++idx) {
				if (wifiProfiles.get(idx).MacAddr.equals(macAddress)) {
					return wifiProfiles.get(idx);
				}
			}
		}
		
		return null;
	}
}
