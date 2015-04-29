package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.R.color;
import com.anynet.wifiworld.UserLoginActivity;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiBlack;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiFollow;
import com.anynet.wifiworld.data.WifiMessages;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.me.WifiProviderSettingActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiBRService;
import com.anynet.wifiworld.wifi.WifiConnectDialog;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListHelper;
import com.avos.avoscloud.LogUtil.log;

import android.R.integer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WifiDetailsActivity extends BaseActivity {
	private final static String TAG = WifiDetailsActivity.class.getSimpleName();
	private final static int GET_DATA_DEFAULT = 0;
	private final static int GET_DATA_FAILED = 1;
	private final static int GET_DATA_SUCCESS = 2;
	
	private Context mContext;
	
	private WifiInfoScanned mWifiInfoScanned;
	
	private ViewGroup mLayoutRoot;
	private ProgressBar mProcessBar;
	private TextView mRankText;
	private TextView mRateText;
	private ImageView mWifiLogo;
	private TextView mWifiMaster;
	private TextView mWifiBanner;
	private TextView mConnectedCnt;
	private TextView mConnectedTime;
	private TextView mWifiMessage;
	private ListView mListComments;
	private TextView mFollowView;
	private TextView mBlackView;
	
	private Handler mUpdateViewHandler = new Handler();
	private Runnable mMonitorDataRunnable = null;
//	private int mProfileFlag = GET_DATA_DEFAULT;
	private int mDynamicFlag = GET_DATA_DEFAULT;
	private int mMessagesFlag = GET_DATA_DEFAULT;
	private int mCommentsFlag = GET_DATA_DEFAULT;
	private int mFollowFlag = GET_DATA_DEFAULT;
	private List<WifiFollow> mFollowed = null;
	
	private WifiConnectDialog mReportDialog;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_details);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		mContext = this;
		
		//Get intent data
		Intent intent = getIntent();
		mWifiInfoScanned = (WifiInfoScanned) intent.getSerializableExtra("WifiSelected");
		//Set title text and back button listener
		mTitlebar.tvTitle.setText(mWifiInfoScanned.getAlias());
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		mRankText = (TextView)findViewById(R.id.wifi_account_rank);
		mRateText = (TextView)findViewById(R.id.wifi_rate);
		mConnectedCnt = (TextView)findViewById(R.id.wifi_connect_times_num);
		mConnectedTime = (TextView)findViewById(R.id.wifi_connect_time_num);
		
		mWifiMessage = (TextView)findViewById(R.id.wifi_messages_content);
		//WiFi logo image
		mWifiLogo = (ImageView)findViewById(R.id.wifi_account_portral);
		mWifiBanner = (TextView) findViewById(R.id.wifi_account_desc);
		if (mWifiInfoScanned.getWifiLogo() != null) {
			mWifiLogo.setImageBitmap(mWifiInfoScanned.getWifiLogo());
		} else {
			mWifiLogo.setImageResource(R.drawable.icon_default_portal);
		}
		if (mWifiInfoScanned.getBanner() != null) {
			mWifiBanner.setText(mWifiInfoScanned.getBanner());
		}
		mWifiMaster = (TextView) findViewById(R.id.tv_wifi_master);
		mWifiMaster.setText(mWifiInfoScanned.getSponser());
		
		mListComments = (ListView) findViewById(R.id.wifi_list_comments);
		
		//Process WiFi follow
		mFollowView = (TextView) findViewById(R.id.wifi_follow);
		mFollowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!LoginHelper.getInstance(getBaseContext()).isLogined()) {
					UserLoginActivity.start((BaseActivity) mContext);
					return;
				}
				
				if (mFollowed != null && mFollowed.size() > 0) {
					mFollowed.get(0).CancelFollow(getBaseContext(), new DataCallback<WifiFollow>() {

						@Override
						public void onSuccess(WifiFollow object) {
							Log.i(TAG, "Success to cancel follow current WiFi");
							mFollowView.setText("收藏WiFi");
							//mFollowView.setBackgroundColor(color.orange);
							mFollowed = null;
						}

						@Override
						public void onFailed(String msg) {
							Log.e(TAG, "Failed to cancel follow current WiFi");
						}
						
					});
				} else {
					WifiFollow wifiFollow = new WifiFollow();
					wifiFollow.MacAddr = mWifiInfoScanned.getWifiMAC();
					wifiFollow.Userid = LoginHelper.getInstance(getBaseContext()).getCurLoginUserInfo().getUsername();
					wifiFollow.MarkFollowTime();
					
					wifiFollow.FollowWifi(getBaseContext(), new DataCallback<WifiFollow>() {

						@Override
						public void onSuccess(WifiFollow object) {
							Log.i(TAG, "Success to follow current WiFi");
							mFollowView.setText("取消收藏");
							//mFollowView.setBackgroundColor(color.gray);
							mFollowed = new ArrayList<WifiFollow>();
							mFollowed.add(object);
						}

						@Override
						public void onFailed(String msg) {
							Log.e(TAG, "Failed to follow current WiFi");
						}
						
					});
				}
			}
		});
		
		// Process WiFi black report
		mReportDialog = new WifiConnectDialog(mContext, WifiConnectDialog.DialogType.REPORT);
		mBlackView = (TextView) findViewById(R.id.wifi_black);
		mBlackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!LoginHelper.getInstance(getBaseContext()).isLogined()) {
					UserLoginActivity.start((BaseActivity) mContext);
					return;
				}
				showWifiReportDialog("举报", mReportDialog);
//				WifiBlack wifiBlack = new WifiBlack();
//				wifiBlack.MacAddr = mWifiInfoScanned.getWifiMAC();
//				wifiBlack.Userid = LoginHelper.getInstance(getBaseContext())
//						.getCurLoginUserInfo().getUsername();
//				wifiBlack.BlackType = WifiBlack.Type.WrongPwd;
//				wifiBlack.Content = "shit, shit";
//				wifiBlack.MarkReportTime();
//
//				wifiBlack.ReportWifi(getBaseContext(), new DataCallback<WifiBlack>() {
//
//					@Override
//					public void onSuccess(WifiBlack object) {
//						Log.i(TAG, "Success to report WiFi");
//					}
//
//					@Override
//					public void onFailed(String msg) {
//						Log.e(TAG, "Failed to report WiFi:" + msg);
//					}
//
//				});
			}
		});
		
		pullDataFromDB();
		initializeProcessBar();
		monitorDataDownload();
		
		//敲门
		if (LoginHelper.getInstance(this).mKnockList.contains(mWifiInfoScanned.getWifiMAC())) {
			((TextView)this.findViewById(R.id.tv_wifi_used_addr)).setHint("已经敲过门，可直接使用网络");
			this.findViewById(R.id.ll_wifi_knock).setEnabled(false);
		} else {
			this.findViewById(R.id.ll_wifi_knock).setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					//拉取敲门问题
					WifiQuestions wifiQuestions = new WifiQuestions();
					wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifiInfoScanned.getWifiMAC(), new DataCallback<WifiQuestions>() {
						
						@Override
						public void onSuccess(WifiQuestions object) {
							KnockStepFirstActivity.start(WifiDetailsActivity.this.getActivity(), "WifiDetailsActivity", object);
						}
						
						@Override
						public void onFailed(String msg) {
							showToast("获取敲门信息失败，请稍后重试:" + msg);
						}
					});
				}
				
			});
		}
	}

	protected Context getActivity() {
		return this;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		mUpdateViewHandler.removeCallbacks(mMonitorDataRunnable);
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onSop");
		super.onStop();
	}
	
	private void showWifiReportDialog(String title, final WifiConnectDialog wifiConnectDialog) {
		wifiConnectDialog.setTitle(title);

		wifiConnectDialog.setLeftBtnStr("取消");
		wifiConnectDialog.setRightBtnStr("确定");

		wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		wifiConnectDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		});

		wifiConnectDialog.show();
	}
	
	private void pullDataFromDB() {
//		WifiProfile wifiProfile = new WifiProfile();
//		String queryTag = WifiProfile.LOGO + "," + WifiProfile.BANNER;
//		wifiProfile.QueryTagByMacAddr(this, mWifiInfoScanned.getWifiMAC(), queryTag, new DataCallback<WifiProfile>() {
//			
//			@Override
//			public void onSuccess(WifiProfile object) {
//				Log.i(TAG, "Success to query wifi profile from server:" + mWifiInfoScanned.getWifiMAC());
//				mWifiInfoScanned.setWifiLogo(object.getLogo());
//				mWifiInfoScanned.setBanner(object.Banner);
//				mProfileFlag = GET_DATA_SUCCESS;
//			}
//			
//			@Override
//			public void onFailed(String msg) {
//				Log.e(TAG, "Failed to query wifi profile:" + mWifiInfoScanned.getWifiMAC()
//						+ ": " + msg);
//				mProfileFlag = GET_DATA_FAILED;
//			}
//		});
		
		WifiDynamic wifiDynamic = new WifiDynamic();
		wifiDynamic.QueryConnectedTimes(this, mWifiInfoScanned.getWifiMAC(), new DataCallback<Long>() {

			@Override
			public void onSuccess(Long object) {
				Log.i(TAG, "Success to query wifi dynamic from server:" + mWifiInfoScanned.getWifiMAC());
				mWifiInfoScanned.setConnectedTimes(object);
				mDynamicFlag = GET_DATA_SUCCESS;
			}

			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi dynamic:" + mWifiInfoScanned.getWifiMAC()
						+ ": " + msg);
				mDynamicFlag = GET_DATA_FAILED;
			}
		});
		
		WifiMessages wifiMessages = new WifiMessages();
		wifiMessages.QueryByMacAddress(this, mWifiInfoScanned.getWifiMAC(), new DataCallback<WifiMessages>() {
			
			@Override
			public void onSuccess(WifiMessages object) {
				Log.i(TAG, "Success to query wifi messages from server:");
				//for (WifiMessages wifiMessages : object) {
					mWifiInfoScanned.addMessage(object.Message);
				//}
				mMessagesFlag = GET_DATA_SUCCESS;
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi messages:" + mWifiInfoScanned.getWifiMAC()
						+ ": " + msg);
				mMessagesFlag = GET_DATA_FAILED;
			}
		});
		
		WifiComments wifiComments = new WifiComments();
		wifiComments.QueryByMacAddress(this, mWifiInfoScanned.getWifiMAC(), new MultiDataCallback<WifiComments>() {
			
			@Override
			public void onSuccess(List<WifiComments> object) {
				Log.i(TAG, "Success to query wifi comments from server:" + object.size());
				for (WifiComments wifiComments : object) {
					mWifiInfoScanned.addComment(wifiComments.Comment);
				}
				mCommentsFlag = GET_DATA_SUCCESS;
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi comments:" + mWifiInfoScanned.getWifiMAC()
						+ ": " + msg);
				mCommentsFlag = GET_DATA_FAILED;
				
			}
		});
		
		if (LoginHelper.getInstance(this).isLogined()) {
			WifiFollow wifiFollow = new WifiFollow();
			wifiFollow.QueryWifiByMac(this, mWifiInfoScanned.getWifiMAC(),
				LoginHelper.getInstance(this).getCurLoginUserInfo().getUsername(), new MultiDataCallback<WifiFollow>() {

					@Override
					public void onSuccess(List<WifiFollow> objects) {
						Log.i(TAG, "Success to find wifi info from wifi follow table");
						mFollowView.setText("取消收藏");
						//mFollowView.setBackgroundColor(color.gray);
						mFollowed = objects;
						mFollowFlag = GET_DATA_SUCCESS;
					}

					@Override
					public void onFailed(String msg) {
						showToast("收藏或取消失败，请稍后再试");
						Log.e(TAG, "Failed to find wifi follow info: " + msg);
						//mFollowView.setText("收藏WiFi");
						//mFollowView.setBackgroundColor(color.orange);
						mFollowFlag = GET_DATA_FAILED;
					}
				});
		} else {
			mFollowFlag = GET_DATA_FAILED;
		}
		
	}
	
	private void initializeProcessBar() {
		mProcessBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		mLayoutRoot = (ViewGroup) this.findViewById(android.R.id.content).getRootView();
		mProcessBar.setIndeterminate(true);
		mProcessBar.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout rl = new RelativeLayout(this);
		rl.setGravity(Gravity.CENTER);
		rl.addView(mProcessBar);
		mLayoutRoot.addView(rl, params);
	}
	
	private void monitorDataDownload() {
		if (mMonitorDataRunnable == null) {
			mMonitorDataRunnable = new Runnable() {
				
				@Override
				public void run() {
//					if (mProfileFlag == GET_DATA_SUCCESS) {
//						if (mWifiInfoScanned.getWifiLogo() != null) {
//							mWifiLogo.setImageBitmap(mWifiInfoScanned.getWifiLogo());
//						}
//						if (mWifiInfoScanned.getBanner() != null) {
//							mWifiBanner.setText(mWifiInfoScanned.getBanner());
//						}
//					}
					if (mDynamicFlag == GET_DATA_SUCCESS) {
						mRankText.setText("排名：" + String.valueOf(mWifiInfoScanned.getRanking()));
						mRateText.setText(String.valueOf(mWifiInfoScanned.getWifiStrength()));
						mConnectedCnt.setText(String.valueOf(mWifiInfoScanned.getConnectedTimes()) + "次");
						mConnectedTime.setText(String.valueOf(mWifiInfoScanned.getConnectedDuration()) + "小时");
					}
					if (mMessagesFlag == GET_DATA_SUCCESS) {
						if (mWifiInfoScanned.getMessages().size() > 0) {
							mWifiMessage.setText(mWifiInfoScanned.getMessages().get(0));
						}
					}
					if (mCommentsFlag == GET_DATA_SUCCESS) {
						List<String> comment_item = mWifiInfoScanned.getComments();
						mListComments.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_item, comment_item));
					}
//					Log.i(TAG, "flags: " + mProfileFlag + ", " + mCommentsFlag + ", " + mDynamicFlag
//							+ ", " + mMessagesFlag + ", " + mFollowFlag);
					if (mCommentsFlag != GET_DATA_DEFAULT && mDynamicFlag != GET_DATA_DEFAULT
							&& mMessagesFlag != GET_DATA_DEFAULT && mFollowFlag != GET_DATA_DEFAULT
							/*&& mProfileFlag != GET_DATA_DEFAULT*/) {
						mProcessBar.setVisibility(View.GONE);
						mLayoutRoot.removeView(mProcessBar);
						return;
					}
					
					mUpdateViewHandler.postDelayed(this, 500);
				}
			};
			mUpdateViewHandler.post(mMonitorDataRunnable);
		}
	}
}
