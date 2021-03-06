package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.hugo.android.scanner.CaptureActivity;
import cn.hugo.android.scanner.decode.EncodingHandler;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiKnock;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiWhite;
import com.anynet.wifiworld.dialog.WifiConnectDialog;
import com.anynet.wifiworld.dialog.WifiConnectDialog.DialogType;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.provider.WifiProviderRigisterActivity;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.StringCrypto;
import com.anynet.wifiworld.util.UIHelper;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiBRService;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;
import com.anynet.wifiworld.wifi.WifiCurrent;
import com.anynet.wifiworld.wifi.WifiListItem;
import com.anynet.wifiworld.wifi.WifiListScanned;
import com.google.zxing.WriterException;

public class WifiConnectUI {
	public final static String TAG = WifiConnectUI.class.getSimpleName();
	
	private View mView;
	private Activity mActivity;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private WifiListScanned mWifiListScanned;
	private WifiAuthListAdapter mWifiAuthList;
	private WifiNotAuthListAdapter mWifiNotAuthList;
	
	private ImageView mWifiConLogo;
	private AnimationDrawable mAnimWifiCon;
	private TextView mWifiName;
	private TextView mWifiStatus;
	private TextView mWifiAlias;
	private TextView mWifiAuthDesc;
	private ListView mWifiAuthListView;
	private ListView mWifiNotAuthListView;
	
	private boolean mIsWifiPassword;
	private ImageView mWifiScan;
	private AnimationDrawable mAnimSearch;
	private Animation mAnimNeedle;
	private ImageView mImageSearch;
	private ImageView mImageNeedle;
	
	private PopupWindow popupwindow;
	private ImageView mWifiMore;
	
	private WifiListItem mWifiNotAuthItem;
	
	private GlobalHandler wifiListHandler = new GlobalHandler() {
		
		@Override
		public void onWifiListRefreshed() {
			updateWifiConnectionContent();
			if (mWifiAuthList != null) {
				mWifiAuthList.refreshWifiList(mWifiListScanned.getAuthList());
				UIHelper.setListViewHeightBasedOnChildren(mWifiAuthListView);
			}
			if (mWifiNotAuthList != null) {
				mWifiNotAuthList.refreshWifiList(mWifiListScanned.getNotAuthList());
				UIHelper.setListViewHeightBasedOnChildren(mWifiNotAuthListView);
			}
			
			//停止搜索动画
			doSearchAnimation(false);
		}
	};
	
	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};
	
	private OnWifiStatusListener mWifiStatusListener = new OnWifiStatusListener() {
		@Override
		public void onWifiConnected(String str) {
			//Log.d(TAG, "connectui onWifiConnected:" + str);
			savePwdInputed();
			doConnectingAnimation(false);
			
			mWifiStatus.setText(str);
			mWifiListScanned.refresh();
		}
		
		@Override
		public void onWifiDisconnected(String str) {
			//Log.d(TAG, "connectui onWifiDisconnected:" + str);
			//forgetPwdInputed(mWifiNotAuthItem);
			doConnectingAnimation(false);
			
			mWifiStatus.setText(str);
			mWifiListScanned.refresh();
		}
		
		@Override
		public void onWifiConnecting(String str) {
			Log.d(TAG, "connectui onWifiConnecting:" + str);
			mWifiStatus.setText(str);
			doConnectingAnimation(true);
			updateWifiConnectingContent();
		}

		@Override
		public void onWifiDisconnecting(String str) {
			//Log.d(TAG, "connectui onWifiDisconnecting:" + str);
			mWifiStatus.setText(str);
		}

		@Override
		public void onSupplicantChanged(String statusStr, boolean isDisconnected) {
			Log.d(TAG, "connectui onSupplicantChanged:" + statusStr);
			mWifiStatus.setText(statusStr);
			if (isDisconnected) {
				forgetPwdInputed(mWifiNotAuthItem);
				doConnectingAnimation(false);
				Toast.makeText(mActivity, "连接失败", Toast.LENGTH_SHORT).show();
				mWifiListScanned.refresh();
			} else {
				//Log.d(TAG, "connectui doConnectingAnimation:" + statusStr);
				doConnectingAnimation(true);
				updateWifiConnectingContent();
			}
		}
		
		@Override
		public void onWrongPassword() {
			//Log.d(TAG, "connectui onWrongPassword: " + mWifiCurrent.getWifiNetworkID());
			if (mIsWifiPassword && mWifiNotAuthItem != null) {
				forgetPwdInputed(mWifiNotAuthItem);
				showWifiConnectDialog(mWifiNotAuthItem, WifiConnectDialog.DialogType.PASSWORD);
			}
			mWifiListScanned.refresh();
		}

		@Override
		public void onScannableAvaliable() {
			//Log.d(TAG, "connectui onScannableAvaliable:");
			//仍需要做修改
			mWifiListScanned.refresh();
		}
		
		@Override
		public void onWifiStatChanged(boolean isEnabled) {
			//Log.d(TAG, "connectui onWifiStatChanged:" + isEnabled);
		}
	};
	
	private OnItemClickListener mAuthItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WifiListItem wifiListItem = mWifiListScanned.getAuthList().get(position);
			//1.先验证是否是认证wifi
			if (wifiListItem.isAuthWifi()) {
				//2.再验证当前认证wifi是否打开了共享
				if (wifiListItem.getWifiType() == WifiListItem.WifiType.AUTH_WIFI) {
					//3.再判断是否需要敲门
					LoginHelper login = LoginHelper.getInstance(mActivity);
					String userId = login.getUserid();
					final String mac = wifiListItem.getWifiMac();
					List<WifiWhite> whiteList = wifiListItem.getWifiWhites();
					if (login.canAccessDirectly(mac) || login.mKnockList.contains(mac)
						|| (whiteList != null && whiteList.size() > 0 && userId != null && isContainUserId(userId, whiteList))) {
						showWifiConnectDialog(wifiListItem, WifiConnectDialog.DialogType.DEFAULT);
					} else { //进入敲门
						final WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mActivity, DialogType.DEFAULT);
						wifiConnectDialog.setTitle("网络敲门");
						String sourceStr = "当前网络主人未对您完全开放，您需要敲门后才能访问。是否去敲门？";
						wifiConnectDialog.setDefaultContent(Html.fromHtml(sourceStr));
						wifiConnectDialog.setLeftBtnStr("取消");
						wifiConnectDialog.setRightBtnStr("确定");
						wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 拉取敲门问题
								final WifiKnock wifiQuestions = new WifiKnock();
								wifiQuestions.QueryByMacAddress(mActivity, mac, new DataCallback<WifiKnock>() {

										@Override
										public void onSuccess(WifiKnock object) {
											KnockStepFirstActivity.start(mActivity, "WifiDetailsActivity", object);
										}

										@Override
										public void onFailed(String msg) {
											KnockStepFirstActivity.start(mActivity, "WifiDetailsActivity", wifiQuestions);
										}
								});
								wifiConnectDialog.dismiss();
							}
						});
						wifiConnectDialog.show();
					}
				}
				else {
					WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mActivity, DialogType.DEFAULT);
					wifiConnectDialog.setTitle("网上访问关闭");
					String sourceStr = "当前认证Wi-Fi已被主人临时关闭，无法连接到网络，请稍后再试。";
					wifiConnectDialog.setDefaultContent(Html.fromHtml(sourceStr));
					wifiConnectDialog.setLeftBtnStr("太小气了");
					wifiConnectDialog.setRightBtnStr("我知道了");
					wifiConnectDialog.show();
				}
			} else {
				WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mActivity, DialogType.DEFAULT);
		    	
				if (position == 0) {
					wifiConnectDialog.setTitle("什么是认证网络");
					String sourceStr = "认证网络是指将您本人持有的Wi-Fi(包括密码)注册到云平台," +
							"并完善个性化的别名LOGO等信息,有助于您身边的人更直接的了解您,并通过合理方式使用您的Wi-Fi. 云平台会记录所有使用过您网络的用户," +
							"并提供每周用网报告和相关收益.您可以随时认证和取消认证,我们诚挚的希望您共享自己本人持有网络,请不要恶意注册他人网络.";
					wifiConnectDialog.setDefaultContent(Html.fromHtml(sourceStr));
				} else {
					wifiConnectDialog.setTitle("如何认证网络");
					String sourceStr = "<ol>1. 先连接上您自己的Wi-Fi,将手机靠近您的路由器位置.</ol><br/>" +
							"<ol>2. 点击右上角加号按钮,选择下拉菜单中的认证网络.</ol><br/><ol>3. 按照说明步骤操作</ol><br/><ol>4. 目前我们只支持家庭个人网络,企业级别的无线网络由于其" +
							"复杂性暂时无法完美支持敬请谅解.</ol>";
					wifiConnectDialog.setDefaultContent(Html.fromHtml(sourceStr));
				}
			    	wifiConnectDialog.setLeftBtnStr("不明白");
			    	wifiConnectDialog.setRightBtnStr("对我有帮助");
			    	wifiConnectDialog.show();
			}
		}
	};
	
	private OnItemClickListener mNotAuthItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mWifiNotAuthItem = mWifiListScanned.getNotAuthList().get(position);
			if (mWifiNotAuthItem.isLocalWifi() || mWifiNotAuthItem.isOpenWifi()) {
				showWifiConnectDialog(mWifiNotAuthItem, WifiConnectDialog.DialogType.DEFAULT);
			} else {
				showWifiConnectDialog(mWifiNotAuthItem, WifiConnectDialog.DialogType.PASSWORD);
			}
		}
	};
	
	public WifiConnectUI(View view, Activity activity) {
		mView = view;
		mActivity = activity;
		mWifiAdmin = WifiAdmin.getInstance(mActivity);
		mWifiCurrent = WifiCurrent.getInstance(mActivity);
		mWifiListScanned = WifiListScanned.getInstance(mActivity, wifiListHandler);
		WifiBRService.setOnWifiStatusListener(mWifiStatusListener);
		WifiBRService.bindWifiService(mActivity, conn);
		
		initViewHolder();
		initMorePopWindows();
		
		//启动动画，更新WiFi扫描列表
		doSearchAnimation(true);
		mWifiListScanned.refresh();
		
		//设置初始标签信息
		mIsWifiPassword = false;
	}
	
	private void updateWifiConnectingContent() {
		String wifiCurMac;
		String wifiCurName;
//		if (mWifiNotAuthItem != null) {
//			wifiCurMac = mWifiNotAuthItem.getWifiMac();
//			wifiCurName = mWifiNotAuthItem.getWifiName();
//		} else {
			WifiInfo wifiInfoCur = mWifiAdmin.getWifiInfo();
			if (wifiInfoCur == null || wifiInfoCur.getBSSID() == null) {
				return;
			}
			wifiCurMac = wifiInfoCur.getBSSID();
			wifiCurName = wifiInfoCur.getSSID();
//		}
		
		List<WifiListItem> wifiAuth = mWifiListScanned.getAuthList();
		List<WifiListItem> wifiNotAuth = mWifiListScanned.getNotAuthList();
		boolean wifiCurFound = false;
		for (WifiListItem wifiListItem : wifiAuth) {
			if (wifiCurMac.equals(wifiListItem.getWifiMac())) {
				wifiCurFound = true;
				wifiAuth.remove(wifiListItem);
				break;
			}
		}
		if (!wifiCurFound) {
			for (WifiListItem wifiListItem : wifiNotAuth) {
				if (wifiCurMac.equals(wifiListItem.getWifiMac())) {
					wifiCurFound = true;
					wifiNotAuth.remove(wifiListItem);
					break;
				}
			}
		}
		if (wifiCurFound) {
			if (mWifiAuthList != null) {
				mWifiAuthList.refreshWifiList(wifiAuth);
				UIHelper.setListViewHeightBasedOnChildren(mWifiAuthListView);
			}
			if (mWifiNotAuthList != null) {
				mWifiNotAuthList.refreshWifiList(wifiNotAuth);
				UIHelper.setListViewHeightBasedOnChildren(mWifiNotAuthListView);
			}
		}
		mWifiName.setText(WifiAdmin.convertToNonQuotedString(wifiCurName));
		mWifiAuthDesc.setVisibility(View.VISIBLE);
		mWifiAuthDesc.setText("WiFi牵线中...");
	}
	
	private void updateWifiConnectionContent() {
		//更新标题栏视图以及当前连接Wi-Fi信息
		if (mWifiCurrent.isConnected()) {
			mWifiMore.setVisibility(View.VISIBLE);
			WifiListItem item = mWifiCurrent.getWifiListItem();
			if (item != null && item.isAuthWifi()) {
				mWifiName.setText(item.getAlias());
				mWifiAlias.setVisibility(View.VISIBLE);
				mWifiAlias.setText("[" + mWifiCurrent.getWifiName() + "]");
				mWifiAuthDesc.setVisibility(View.VISIBLE);
				mWifiAuthDesc.setText(item.getBanner());
				Bitmap logo = item.getLogo();
				if (logo != null) {
					mWifiConLogo.setImageBitmap(logo);
				} else {
					mWifiConLogo.setImageResource(mWifiCurrent.getDefaultLogoID());
				}
				
				//弹出窗口显示信息
				popupwindow.getContentView().findViewById(R.id.ll_more_auth).setVisibility(View.GONE);
				//popupwindow.getContentView().findViewById(R.id.ll_more_comment).setVisibility(View.VISIBLE);
				popupwindow.getContentView().findViewById(R.id.ll_more_create_code).setVisibility(View.VISIBLE);
			} else {
				mWifiAlias.setVisibility(View.INVISIBLE);
				mWifiName.setText(mWifiCurrent.getWifiName());
				mWifiAuthDesc.setVisibility(View.VISIBLE);
				mWifiAuthDesc.setText("[未认证]");
				mWifiConLogo.setImageResource(mWifiCurrent.getDefaultLogoID());
				
				//弹出窗口显示信息
				popupwindow.getContentView().findViewById(R.id.ll_more_auth).setVisibility(View.VISIBLE);
				//popupwindow.getContentView().findViewById(R.id.ll_more_comment).setVisibility(View.GONE);
				popupwindow.getContentView().findViewById(R.id.ll_more_create_code).setVisibility(View.GONE);
			}
		} else if (mWifiCurrent.isConnecting()) {
			updateWifiConnectingContent();
		} else if (!WifiBRService.getWifiSupplicantState()) {
				mWifiMore.setVisibility(View.INVISIBLE);
				mWifiAlias.setVisibility(View.INVISIBLE);
				mWifiName.setText("未连接任何Wi-Fi");
				mWifiAuthDesc.setVisibility(View.INVISIBLE);
				mWifiAuthDesc.setText("[未认证]");
				mWifiStatus.setText("断开连接");
				mWifiConLogo.setImageResource(R.drawable.ic_wifi_disconnected);
		}
	}
    
    private void savePwdInputed() {
    	Log.d(TAG, "connectui: savePwdInputed " + mIsWifiPassword + mWifiCurrent.getWifiName());
    	if (mIsWifiPassword) {
			mWifiAdmin.saveConfig();
			mIsWifiPassword = false;
		}
    }
    
    private void forgetPwdInputed(WifiListItem wifiItem) {
    		if (!mIsWifiPassword || wifiItem == null) {
			return;
		}
    		WifiConfiguration config = mWifiAdmin.getWifiConfiguration(wifiItem.getScanResult(), null);
    		if (config != null) {
    			Log.d(TAG, "connectui forgetPwdInputed " + config.networkId + config.SSID);
    			mWifiAdmin.forgetNetwork(config);
		}
    		mIsWifiPassword = false;	
    }
	
	private void initViewHolder() {
		//扫一扫连网
		mWifiScan = (ImageView)mView.findViewById(R.id.iv_wifi_scan);
		mWifiScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(mActivity, CaptureActivity.class);
				mActivity.startActivity(i);
				mActivity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_part_out);
			}
		});
		
		//点击搜索附近WiFi
		mImageNeedle = (ImageView)mView.findViewById(R.id.iv_wifi_search_needle);
		mAnimNeedle = AnimationUtils.loadAnimation(mActivity, R.animator.animation_needle);
		mAnimNeedle.setInterpolator(new BounceInterpolator());
		mImageSearch = (ImageView)mView.findViewById(R.id.iv_wifi_search_heart);
		mImageSearch.setImageResource(R.animator.animation_search);
		mAnimSearch = (AnimationDrawable)mImageSearch.getDrawable();
		mView.findViewById(R.id.rl_wifi_search).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				if (!mAnimSearch.isRunning()) {
					doSearchAnimation(true);
					mWifiListScanned.refresh();
				} else {
					doSearchAnimation(false);
				}
			}
			
		});
		
		mWifiConLogo = (ImageView)mView.findViewById(R.id.iv_wifi_connected_logo);
		mWifiName = (TextView)mView.findViewById(R.id.tv_wifi_connected_name);
		mWifiStatus = (TextView)mView.findViewById(R.id.tv_wifi_options);
		mWifiAlias = (TextView)mView.findViewById(R.id.tv_wifi_connected_alias);
		mWifiAuthDesc = (TextView)mView.findViewById(R.id.tv_wifi_connected_desc);
		//点击主页的wifi连接页面，如果是认证的进入认证详细页面
		mView.findViewById(R.id.ll_wifi_connected_parent).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiListItem item = mWifiCurrent.getWifiListItem();
				if (item != null && item.isAuthWifi()) { //如果是认证，显示认证信息
					Intent i = new Intent(mActivity, WifiDetailsActivity.class);
					Bundle wifiData = new Bundle();
					wifiData.putSerializable(WifiProfile.TAG, item.getWifiProfile());
					i.putExtras(wifiData);
					mActivity.startActivity(i);
				} else if (item != null && !item.isAuthWifi()) {
					Intent i = new Intent(mActivity, WifiDetailsActivity.class);
					List<String> data = new ArrayList<String>();
					data.add(item.getWifiName());
					data.add(item.getWifiMac());
					i.putStringArrayListExtra(WifiNotAuthListAdapter.TAG, (ArrayList<String>) data);
					mActivity.startActivity(i);
				}
			}
			
		});
		
		mWifiAuthListView = (ListView)mView.findViewById(R.id.lv_wifi_free_list);
		mWifiAuthList = new WifiAuthListAdapter(mActivity, mWifiListScanned.getAuthList());
		mWifiAuthListView.setAdapter(mWifiAuthList);
		mWifiAuthListView.setOnItemClickListener(mAuthItemClickListener);
		
		mWifiNotAuthListView = (ListView)mView.findViewById(R.id.lv_wifi_encrypt_list);
		mWifiNotAuthList = new WifiNotAuthListAdapter(mActivity, mWifiListScanned.getNotAuthList());
		mWifiNotAuthListView.setAdapter(mWifiNotAuthList);
		mWifiNotAuthListView.setOnItemClickListener(mNotAuthItemClickListener);
		
		//设置wifi弹出式下拉菜单
		initMorePopWindows();
		mWifiMore = (ImageView) mView.findViewById(R.id.iv_wifi_plus);
		mWifiMore.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				if (popupwindow != null&&popupwindow.isShowing()) {
	                popupwindow.dismiss();
	                return;
	            } else {
	                popupwindow.showAsDropDown(v, 0, 16);
	            }
            }
			
		});
	}
	
    //-----------------------------------------------------------------------------------------------------------------
    private void doSearchAnimation(boolean start) {
    	if (start) {
    		mAnimSearch.start();
    		mImageNeedle.startAnimation(mAnimNeedle);
    	} else {
    		mAnimSearch.stop();
    		mAnimSearch.selectDrawable(0);
    		mImageNeedle.clearAnimation();
    	}
    }
    
    private void doConnectingAnimation(boolean start) {
		if (start) {
			if (mAnimWifiCon != null && mAnimWifiCon.isRunning())
			      return;
		
			mWifiConLogo.setImageResource(R.animator.animation_connecting);
			mAnimWifiCon = (AnimationDrawable)mWifiConLogo.getDrawable();
			mAnimWifiCon.start();
		} else if(mAnimWifiCon != null) {
		    if (!mAnimWifiCon.isRunning())
			         return;
			
			mAnimWifiCon.stop();
			mAnimWifiCon.selectDrawable(0);
		}
    }
    
    private void showWifiConnectDialog(final WifiListItem wifiListItem, final DialogType dialogType) {
	    final WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mActivity, dialogType);
	    	
	    	wifiConnectDialog.setTitle("连接到：" + wifiListItem.getWifiName());
	    	wifiConnectDialog.setLeftBtnStr("取消");
	    	wifiConnectDialog.setRightBtnStr("确定");
	    	
	    	if (dialogType == DialogType.PASSWORD) {
	    		wifiConnectDialog.clearPwdEditText();
	    	}
	    	
	    	wifiConnectDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	    	
	    	wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				boolean connResult = false;
				switch (dialogType) {
				case DEFAULT:
					if (wifiListItem.isAuthWifi() || wifiListItem.isOpenWifi()) {
						connResult = mWifiAdmin.connectToNewNetwork(wifiListItem, true);
					} else if (wifiListItem.isLocalWifi()) {
						//mIsWifiPassword = true;
						WifiConfiguration cfgSelected = mWifiAdmin.getWifiConfiguration(wifiListItem);
						connResult = mWifiAdmin.connectToConfiguredNetwork(cfgSelected, true);
					} else {
						Toast.makeText(mActivity, "错误的WiFi类型", Toast.LENGTH_LONG).show();
					}
					break;
				case PASSWORD:
					if (wifiListItem.isEncryptWifi()) {
						String inputedPwd = wifiConnectDialog.getPwdContent();
						if (inputedPwd.equals("")) {
							Toast.makeText(mActivity, "请输入密码。", Toast.LENGTH_LONG).show();
							return;
						}
						wifiListItem.setWifiPwd(inputedPwd);
						mIsWifiPassword = true;
						connResult = mWifiAdmin.connectToNewNetwork(wifiListItem, true);
						//shutdown soft keyboard if soft keyboard is actived
						InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(mActivity.INPUT_METHOD_SERVICE);
						if (imm.isActive()) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
					break;
	
				default:
					break;
				}
				dialog.dismiss();
				if (!connResult) {
					Toast.makeText(mActivity, "无法连接到网络：" + wifiListItem.getWifiName(), Toast.LENGTH_LONG).show();
				}
			}
		});
	    	
	    wifiConnectDialog.show();
    }
    
    private void initMorePopWindows() {
        if (popupwindow == null) {
    			// 获取自定义布局文件pop.xml的视图  
			final LayoutInflater layoutInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View customView = layoutInflater.inflate(R.layout.popupwindow_more, null, false);  
    			popupwindow = new PopupWindow(customView);
	    		popupwindow.setWidth(LayoutParams.WRAP_CONTENT);    
	    		popupwindow.setHeight(LayoutParams.WRAP_CONTENT); 
	    		popupwindow.setTouchable(true);
	    		popupwindow.setFocusable(true);
	    		popupwindow.setBackgroundDrawable(new BitmapDrawable());
	    		popupwindow.setAnimationStyle(R.style.PopupFadeAnimation);
	    		popupwindow.setOutsideTouchable(true);
	                
	    		//认证
	    		customView.findViewById(R.id.ll_more_auth).setOnClickListener(new OnClickListener() {
	
	        		@Override
	            public void onClick(View v) {
        			Intent i = new Intent(mActivity, WifiProviderRigisterActivity.class);
	        		mActivity.startActivity(i);
	        		popupwindow.dismiss();
	            }
	                	
	        });
	    		
    		//测一测
    		customView.findViewById(R.id.ll_more_test).setOnClickListener(new OnClickListener() {

        		@Override
            public void onClick(View v) {
        			if (mWifiCurrent.isConnected()) {
        				Intent i = new Intent(mActivity, WifiTestActivity.class);
        				i.putExtra("WifiSpeedName", mWifiCurrent.getWifiName());
        				i.putExtra("WifiSpeedEncrypt", mWifiCurrent.getWifiListItem().getEncryptStr());
		        		mActivity.startActivity(i);
		        		popupwindow.dismiss();
				} else {
					Toast.makeText(mActivity, "请保证WiFi已连接", Toast.LENGTH_SHORT).show();
				}
            }
	                	
	        });
	    		
    		//评论
    		customView.findViewById(R.id.ll_more_comment).setOnClickListener(new OnClickListener() {

        		@Override
            public void onClick(View v) {
	        		Intent i = new Intent("com.anynet.wifiworld.wifi.ui.WIFI_COMMENT");
	        		mActivity.startActivity(i);
	        		popupwindow.dismiss();
            }
	                	
	        });
    		
            //断开网络
            customView.findViewById(R.id.ll_more_disconnect).setOnClickListener(new OnClickListener() {

        			@Override
                public void onClick(View v) {
        				WifiInfo wifiInfo = mWifiAdmin.getWifiInfo();
	    				if (wifiInfo != null) {
	    					mWifiAdmin.disConnectionWifi(wifiInfo.getNetworkId());
	    				}
	    				popupwindow.dismiss();
                }
                	
            });

            //生成二维码
            customView.findViewById(R.id.ll_more_create_code).setOnClickListener(new OnClickListener() {

        			@Override
                public void onClick(View v) {
        			popupwindow.dismiss();
        			
        			WifiListItem wifiCurInfo = mWifiCurrent.getWifiListItem();
        			if (wifiCurInfo == null || !wifiCurInfo.isAuthWifi()) {//如果网络没有连接不生成二维码
        				((BaseActivity) mActivity).showToast("只有在连接到网络并且认证成功的情况下，才能生成二维码。");
        				return;
        			}
        				
        			View customView = layoutInflater.inflate(R.layout.popupwindow_display_scan, null, false);  
        			PopupWindow image_display_popwin = new PopupWindow(customView);
        			image_display_popwin.setWidth(LayoutParams.WRAP_CONTENT);
        			image_display_popwin.setHeight(LayoutParams.WRAP_CONTENT);
        			image_display_popwin.setTouchable(true);
        			image_display_popwin.setFocusable(true);
        			image_display_popwin.setBackgroundDrawable(new BitmapDrawable());
        			image_display_popwin.setAnimationStyle(R.style.PopupFadeAnimation);
        			image_display_popwin.setOutsideTouchable(true);
        			ImageView image = (ImageView) customView.findViewById(R.id.iv_display_scan);
        			try {
        				JSONArray jsonarray = new JSONArray();
        				jsonarray.put(wifiCurInfo.getWifiName());
        				jsonarray.put(wifiCurInfo.getWifiMac());
        				jsonarray.put(wifiCurInfo.getWifiPwd());
        				jsonarray.put(wifiCurInfo.getEncryptType());
        				String object = jsonarray.toString();
        				String encryptObj = StringCrypto.encryptDES(object, CaptureActivity.KEY);
	                    image.setImageBitmap(EncodingHandler.createQRCode(encryptObj, 640));
                    } catch (WriterException e) {
	                    e.printStackTrace();
                    } catch (Exception e) {
						e.printStackTrace();
					}
        			image_display_popwin.showAtLocation(mView, Gravity.CENTER, 0, 0);	
                }
                	
            });
		}
	}
    
    private void updateMorePopWindowsLayout() {
    	WifiListItem item = mWifiCurrent.getWifiListItem();
		if (item != null && item.isAuthWifi()) { //如果是认证，显示认证信息
			popupwindow.getContentView().findViewById(R.id.ll_more_auth).setVisibility(View.GONE);
			//popupwindow.getContentView().findViewById(R.id.ll_more_comment).setVisibility(View.VISIBLE);
			//popupwindow.getContentView().findViewById(R.id.ll_more_scan).setVisibility(View.VISIBLE);
			popupwindow.getContentView().findViewById(R.id.ll_more_create_code).setVisibility(View.VISIBLE);
		} else {
			popupwindow.getContentView().findViewById(R.id.ll_more_auth).setVisibility(View.VISIBLE);
			//popupwindow.getContentView().findViewById(R.id.ll_more_comment).setVisibility(View.GONE);
			//popupwindow.getContentView().findViewById(R.id.ll_more_scan).setVisibility(View.GONE);
			popupwindow.getContentView().findViewById(R.id.ll_more_create_code).setVisibility(View.GONE);
		}
    }
    
    private boolean isContainUserId(String userId, List<WifiWhite> wifiWhites) {
		for (WifiWhite wifiWhite : wifiWhites) {
			if (wifiWhite.Whiteid.equals(userId)) {
				return true;
			}
		}
		
		return false;
	}
}
