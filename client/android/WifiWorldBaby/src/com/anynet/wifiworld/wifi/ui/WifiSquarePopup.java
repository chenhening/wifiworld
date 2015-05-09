package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.R.color;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.me.WifiProviderRigisterFirstActivity;
import com.anynet.wifiworld.me.WifiProviderRigisterLicenseActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiFragment;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListHelper;
import com.anynet.wifiworld.wifi.WifiSpeedTester;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class WifiSquarePopup extends PopupWindow {
	private final static String TAG = WifiSquarePopup.class.getSimpleName();
	
	private Context mContext;
	private MainFragment mFragment;
	
	private View mPopupView;
	private WifiSpeedTester mWifiSpeedTester;
	private LinearLayout mWifiSpeedLayout;
	private LinearLayout mWifiShareLayout;
	private LinearLayout mWifiLouderLayout;
	
	private ListView mCommentsListView;
	private WifiCommentsAdapter mWifiCommentsAdapter;
	private List<WifiComments> mWifiCommentsList = new ArrayList<WifiComments>();
	
	public enum SquareType {
		SPEED_TESTER,
		SHARE_PAGE,
		COMMENTS_PAGE
	}
	
	public WifiSquarePopup(Context context, MainFragment mainFragment) {
		mContext = context;
		mFragment = mainFragment;
		
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupView = layoutInflater.inflate(R.layout.wifi_popup_view, null);
		this.setContentView(mPopupView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(false);
		this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		// this.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// this.showAtLocation(this, Gravity.BOTTOM, 0, 0);
		
		initWifiSquarePopup();
	}
	
	public void displayLayout(SquareType squareType) {
		switch (squareType) {
		case SPEED_TESTER:
			mWifiSpeedLayout.setVisibility(View.VISIBLE);
			mWifiShareLayout.setVisibility(View.GONE);
			mWifiLouderLayout.setVisibility(View.GONE);
			break;
		case SHARE_PAGE:
			mWifiSpeedLayout.setVisibility(View.GONE);
			mWifiShareLayout.setVisibility(View.VISIBLE);
			mWifiLouderLayout.setVisibility(View.GONE);
			break;
		case COMMENTS_PAGE:
			mWifiSpeedLayout.setVisibility(View.GONE);
			mWifiShareLayout.setVisibility(View.GONE);
			mWifiLouderLayout.setVisibility(View.VISIBLE);
			// WiFi comments list
			loadWifiComments(mPopupView);
			break;
		default:
			break;
		}
	}
	
	public void show(View view) {
		Log.i(TAG, "Show Wifi Square PopupWindow");
		this.setFocusable(false);
		this.setOutsideTouchable(true);
		this.setAnimationStyle(R.style.PopupAnimation);
		this.showAsDropDown(view);
	}

	public void addComment(WifiComments comments) {
		mWifiCommentsList.add(comments);
		mWifiCommentsAdapter.refreshCommentsList();
		setListViewHeightBasedOnChildren(mCommentsListView);
	}
	
	private void initWifiSquarePopup() {
		mWifiSpeedLayout = (LinearLayout) mPopupView.findViewById(R.id.wifi_speed_layout);
		mWifiShareLayout = (LinearLayout) mPopupView.findViewById(R.id.wifi_share_layout);
		mWifiLouderLayout = (LinearLayout) mPopupView.findViewById(R.id.wifi_louder_layout);

		// speed test UI
		initSpeedTestLayout();
		// shared UI
		initShareLayout();
		// comments UI
		initCommentsLayout();
	}
	
	private void initSpeedTestLayout() {
		mWifiSpeedTester = new WifiSpeedTester(mPopupView);
		Button testBtn = (Button) mPopupView.findViewById(R.id.start_button);
		testBtn.setOnClickListener(mWifiSpeedTester);
		setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				mWifiSpeedTester.stopSpeedTest();
			}
		});
	}
	
	private void initShareLayout() {
		TextView mTVLinkLicense = (TextView) mWifiShareLayout.findViewById(R.id.tv_link_license);
		final String sText = "认证即表明您同意我们的<br><a href=\"activity.special.scheme://127.0.0.1\">《网络宝服务协议》</a>";
		mTVLinkLicense.setText(Html.fromHtml(sText));
		mTVLinkLicense.setClickable(true);
		mTVLinkLicense.setMovementMethod(LinkMovementMethod.getInstance());
		mTVLinkLicense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext, WifiProviderRigisterLicenseActivity.class);
				mContext.startActivity(i);
			}
		});
		TextView mAcceptTv = (TextView) mWifiShareLayout.findViewById(R.id.certify_button);
		mAcceptTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果用户未登陆提醒其登陆
				if (!mFragment.checkIsLogined()) {
					mFragment.showToast("需要登录之后才能认证。");
					return;
				}

				// 验证当前登录用户是否已经登记了wifi，目前只支持一人绑定一个wifi
				WifiProfile wifi = LoginHelper.getInstance(mContext).mWifiProfile;
				if (wifi != null) {
					mFragment.showToast("你已经绑定了一个Wi-Fi，当前支持一个账号绑定一个Wi-Fi");
					return;
				}

				// TODO（binfei） 验证当前wifi是否已经被人绑定了，如果绑定可以提请申诉
				Intent i = new Intent(mContext, WifiProviderRigisterFirstActivity.class);
				mContext.startActivity(i);
			}
		});
	}
	
	private void initCommentsLayout() {
		final TextView send_btn = (TextView) mPopupView.findViewById(R.id.comment_btn);
		send_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.WIFI_COMMENT");
				((WifiFragment) mFragment).startActivityForResult(intent, WifiFragment.WIFI_COMMENT);
			}
		});
		WifiProfile wifiProfile = LoginHelper.getInstance(mContext).getWifiProfile();
		WifiInfoScanned wifiInfoScanned = WifiListHelper.getInstance(mContext).mWifiInfoCur;
		if (wifiProfile != null) {
			if (wifiProfile.MacAddr.equals(wifiInfoScanned.getWifiMAC())) {
				send_btn.setBackgroundColor(color.gray);
				send_btn.setClickable(false);
			}
		}
	}

	private void loadWifiComments(View popupView) {
		WifiInfoScanned wifiInfoScanned = WifiListHelper.getInstance(mContext).mWifiInfoCur;

		mCommentsListView = (ListView) popupView.findViewById(R.id.wifi_list_comments);
		WifiComments wifiComments = new WifiComments();
		wifiComments.QueryByMacAddress(mContext, wifiInfoScanned.getWifiMAC(), new MultiDataCallback<WifiComments>() {

			@Override
			public boolean onSuccess(List<WifiComments> objects) {
				mWifiCommentsList.clear();
				for (WifiComments obj : objects) {
					mWifiCommentsList.add(obj);
				}
				mWifiCommentsAdapter = new WifiCommentsAdapter(mContext, mWifiCommentsList);
				mCommentsListView.setAdapter(mWifiCommentsAdapter);
				setListViewHeightBasedOnChildren(mCommentsListView);
				return false;
			}

			@Override
			public boolean onFailed(String msg) {
				Log.e(TAG, msg + ". Failed to pull wifi comments");
				return false;
			}
		});
	}
	
	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
	
}
