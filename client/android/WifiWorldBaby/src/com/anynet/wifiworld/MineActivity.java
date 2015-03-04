package com.anynet.wifiworld;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.*;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.*;
//import com.xunlei.common.member.XLLog;
import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.report.ReportUtil;
import com.anynet.wifiworld.util.DipPixelUtil;
import com.anynet.wifiworld.util.DisplayUtil;
import com.anynet.wifiworld.R;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class MineActivity extends BaseActivity
{
    
    private WebView wbBox;
    private RelativeLayout loading;
    private String boxUrlString = "?r=mword";
    private ImageView ivWorld;
    private View header;
    private LinearLayout bottom;
    private View rlRoot;
    private TextView tvCrystalTodayCollected;
    private TextView tvBoxIncome;
    private TextView tvBoxTips;
    private TextView tvClose;
    private int todayMineIncome;
    private int todayBoxIncome;
    private int openedBoxCnt;
    private int missedBoxCnt;
    private TextView tvDiggerSpeed;
    private int lastTotalSpeed;
    CookieManager cookieManager;
    private long enterTime;
    private long leaveTime;

    private boolean isFromWelcomeActivity = false;
    public static void startActivity(BaseActivity baseActivity,int todayMineIncome,int todayBoxIncome,int openedBoxCnt,int missedBoxCnt,int lastTotalSpeed,boolean isFromWelcomeActivity){
    	Intent i = new Intent(baseActivity,MineActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("todayBoxIncome", todayBoxIncome);
        i.putExtra("todayMineIncome",todayMineIncome);
        i.putExtra("openedBoxCnt",openedBoxCnt);
        i.putExtra("missedBoxCnt",missedBoxCnt);
        i.putExtra("lastTotalSpeed",lastTotalSpeed);
        i.putExtra("isFromWelcomeActivity",isFromWelcomeActivity);
        if (isFromWelcomeActivity){
            baseActivity.overridePendingTransition(R.anim.welcome_enter,0);
        }
        baseActivity.startActivity(i);
        ReportUtil.reportEnterMineActivity(baseActivity);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        setContentView(R.layout.crystal_box);
        super.onCreate(savedInstanceState);
        Intent i  = getIntent();
        todayBoxIncome = i.getIntExtra("todayBoxIncome",0);
        todayMineIncome = i.getIntExtra("todayMineIncome",0);
        openedBoxCnt = i.getIntExtra("openedBoxCnt",0);
        missedBoxCnt = i.getIntExtra("missedBoxCnt",0);
        lastTotalSpeed = i.getIntExtra("lastTotalSpeed",0);
        isFromWelcomeActivity = i.getBooleanExtra("isFromWelcomeActivity",false);
        binddingUI();
        if (!isFromWelcomeActivity) {
            startAnimate();
        } else  {
            boxUrlString = "?r=mword&first=1";
        }
        enterTime = currentSeconds();

        initWebView();
        initCookie();
        Log.d(TAG,"cookie:" + cookieManager.getCookie(AppRestClient.getAbsoluteUrl("")));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) wbBox.getLayoutParams();
        layoutParams.topMargin = -DisplayUtil.getStatusBarHeight(this);
    }

    public long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private void binddingUI() {
        mTitlebar.llHeaderLeft.setVisibility(View.GONE);
        mTitlebar.tvTitle.setText(R.string.crystal_fragment_title);

        mTitlebar.llFinish.setVisibility(View.VISIBLE);
        mTitlebar.llHeaderMy.setVisibility(View.VISIBLE);
        mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
        mTitlebar.llFinish.setVisibility(View.GONE);
        ivWorld = (ImageView) findViewById(R.id.ivWorld);
        header = findViewById(R.id.header);
        bottom = (LinearLayout) findViewById(R.id.bottom);
        tvClose.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!isFromWelcomeActivity) {
            setCrystalIncome();
            setBoxTips();
            setLastSpeed();
            tvClose.setVisibility(View.GONE);
        } else {
            ivWorld.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivWorld.getLayoutParams();
        layoutParams.topMargin = -DisplayUtil.getStatusBarHeight(this);
    }
    private  void setLastSpeed() {
        tvDiggerSpeed.setText(lastTotalSpeed + "");
    }


    private void setCrystalIncome() {
        // 今日矿机收益
        tvCrystalTodayCollected.setText( formatIncome(todayMineIncome));
        // 今日宝箱收益
        tvBoxIncome.setText( formatIncome(todayBoxIncome));
    }

    private void setBoxTips() {
        String boxTipsStr = String.format("已开启%d个宝箱",openedBoxCnt );
        tvBoxTips.setText(boxTipsStr);
    }


    private String formatIncome(int income){
        String str = "--";
        if (income > 999999) {
            str = "999999+";
        } else {
            str = income  + "" ;
        }
        return str;
    }

    private Runnable animRunnable  = new Runnable() {
        public float cnt;
        private  static  final  int STEP = 60;
        private  static  final  int STEP_TIME = 20;

        private float estimateAnimateTime = 0;

        @Override
        public void run() {
            cnt++;
            RelativeLayout.LayoutParams headerLayoutParams = (RelativeLayout.LayoutParams) header.getLayoutParams();
            if (-headerLayoutParams.topMargin < header.getHeight()){
                headerLayoutParams.topMargin -= STEP;
                if (-headerLayoutParams.topMargin > header.getHeight()){
                    headerLayoutParams.topMargin = -header.getHeight();
                }
                header.requestLayout();
            }
            RelativeLayout.LayoutParams bottomLayoutParams = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
            if (-bottomLayoutParams.bottomMargin < bottom.getHeight()){
                bottomLayoutParams.bottomMargin -= STEP;
                float alpha = 1f - (cnt / estimateAnimateTime());
           //     ivWorld.setImageAlpha(alpha);
                ivWorld.setAlpha((int)(alpha * 255));
                if (-bottomLayoutParams.bottomMargin >= bottom.getHeight()){
                    bottomLayoutParams.bottomMargin = -bottom.getHeight();
                //    ivWorld.setAlpha(0f);
                    ivWorld.setAlpha(0);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    tvClose.setVisibility(View.VISIBLE);
                    enterAniming = false;
                } else {
                    handler.postDelayed(this,STEP_TIME);
                }
                bottom.requestLayout();

            }
        }
        private float estimateAnimateTime(){
            if (estimateAnimateTime == 0) {
                estimateAnimateTime = (DipPixelUtil.dip2px(MineActivity.this, (93 + 93 + 1 + 52)) + STEP) / STEP;
            }
            return  estimateAnimateTime;
        }
    };

    private Runnable finishAnimRunnable  = new Runnable() {
        public float cnt;
        private  static  final  int STEP = 60;
        private  static  final  int STEP_TIME = 20;

        private float estimateAnimateTime = 0;

        @Override
        public void run() {
            cnt++;
            RelativeLayout.LayoutParams headerLayoutParams = (RelativeLayout.LayoutParams) header.getLayoutParams();
            if (headerLayoutParams.topMargin < 0){
                headerLayoutParams.topMargin += STEP;
                if (headerLayoutParams.topMargin > 0){
                    headerLayoutParams.topMargin = 0;
                }
                header.requestLayout();
            }
            RelativeLayout.LayoutParams bottomLayoutParams = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
            if (bottomLayoutParams.bottomMargin < 0){
                bottomLayoutParams.bottomMargin += STEP;
                float alpha = (cnt / estimateAnimateTime());
                //     ivWorld.setImageAlpha(alpha);
                ivWorld.setAlpha((int)(alpha * 255));
                if (bottomLayoutParams.bottomMargin > 0 ){
                    bottomLayoutParams.bottomMargin = 0;
                    //    ivWorld.setAlpha(0f);
                    ivWorld.setAlpha(255);
                    superFinish();
                } else {
                    handler.postDelayed(this,STEP_TIME);
                }
                bottom.requestLayout();

            }
        }
        private float estimateAnimateTime(){
            if (estimateAnimateTime == 0) {
                estimateAnimateTime = (DipPixelUtil.dip2px(MineActivity.this, (93 + 93 + 1 + 52)) + STEP) / STEP;
            }
            return  estimateAnimateTime;
        }
    };


    private  void startAnimate() {
        header.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public boolean onPreDraw() {
                handler.post(animRunnable);
                enterAniming = true;
                header.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

    }

    private void initWebView() {
        WebSettings webSettings = wbBox.getSettings();

        //        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //
        //        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //
        //        //设置支持缩放
        webSettings.setBuiltInZoomControls(false);

        //设置Web视图
        wbBox.setWebViewClient(new WebViewBoxClient());
        wbBox.getSettings().setDefaultTextEncodingName("utf-8");
    }
    
    
    private void initCookie()
    {
        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie(); //移除上一次的session
        
        //AppRestClient.getAbsoluteUrl("") + boxUrlString  AppRestClient.getCookie()
        
        String cookieString = AppRestClient.getCookie();
        
        String cookieStr[] = cookieString.split(";");
        
        if (cookieStr.length > 3)
        {
            //Cookie同步
            cookieManager.setCookie(AppRestClient.getAbsoluteUrl(""), cookieStr[0].trim()); //设置cookie
            cookieManager.setCookie(AppRestClient.getAbsoluteUrl(""), cookieStr[1].trim()); //设置cookie
            cookieManager.setCookie(AppRestClient.getAbsoluteUrl(""), cookieStr[2].trim()); //设置cookie
            cookieManager.setCookie(AppRestClient.getAbsoluteUrl(""), cookieStr[3].trim()); //设置cookie

        }
        else
        {
            showToast("session 失效");
        }
        CookieSyncManager.getInstance().sync(); //同步
        

    }


    private static final String TEMP_URL = "http://10.10.159.33/box/index.html";
    private boolean loadUrlError = false;
    private void getData()
    {
        loadUrlError = false;
        startLoading();
        wbBox.loadUrl(AppRestClient.getAbsoluteUrl("") + boxUrlString);
//        wbBox.loadUrl(TEMP_URL);


    }

    private  RotateAnimation rotateAnimation;
    private  void startLoading() {
        loading.setVisibility(View.VISIBLE);
        ImageView circle = (ImageView)loading.findViewById(R.id.circle);
        circle.setVisibility(View.VISIBLE);
        View loadingTips = loading.findViewById(R.id.enterTips);
        loadingTips.setVisibility(View.VISIBLE);
//        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.infinite_rotate);
//        LinearInterpolator lir = new LinearInterpolator();
//        rotateAnimation.setInterpolator(lir);
//        circle.startAnimation(rotateAnimation);
        rotateAnimation = new RotateAnimation(circle,handler);
        rotateAnimation.start();
    }
//    private void networkErrorInLoading() {
////      //  TextView networkTips = (TextView) loading.findViewById(R.id.networkTips);
////        //networkTips.setVisibility(View.VISIBLE);
//        quitLoading();
//        View loadingTips = loading.findViewById(R.id.enterTips);
//        loadingTips.setVisibility(View.INVISIBLE);
//
//    }

    private  void quitLoading() {
        View circle = loading.findViewById(R.id.circle);
        if (rotateAnimation != null){
            rotateAnimation.clear();
        }
        circle.setVisibility(View.INVISIBLE);
        View loadingTips = loading.findViewById(R.id.enterTips);
        loadingTips.setVisibility(View.INVISIBLE);

    }

    private  void enterMine() {
        quitLoading();
        AlphaAnimation anim = new AlphaAnimation(1f,0f);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        loading.startAnimation(anim);
    }
    
    // 
    private Handler handler = new Handler();

    
    //Web视图    
    private class WebViewBoxClient extends WebViewClient
    {
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
           // XLLog.d(TAG,"onPageFinished");
            if (!url.startsWith("http")){
                return;
            }
            quitLoading();
            enterMine();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            loadUrlError = true;
           // XLLog.d(TAG,"onReceivedError");
            view.loadUrl("file:///android_asset/error/sorry.html");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       // XLLog.d(TAG, "onStart");
        getData();
    }

    @Override
    protected void onStop()
    {
       // XLLog.d(TAG, "onStop");
        quitLoading();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  XLLog.d(TAG,"onPause");
        leaveTime = currentSeconds();
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
       // XLLog.d(TAG,"onResume");
        super.onResume();
        long current = currentSeconds();
        if (current - enterTime >= Const.MINE_ACTIVITY_TIME) {
            ReportUtil.reportMineActivity(leaveTime - enterTime,current - enterTime);
            enterTime  = current;
        }
    }

    private  boolean  enterAniming = false;
    @Override
    public void finish() {
        if (enterAniming) {
            return;
        }
        if (isFromWelcomeActivity) {
            superFinish();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            handler.post(finishAnimRunnable);
        }
    }
    public void superFinish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        leaveTime = currentSeconds();
        ReportUtil.reportMineActivity(leaveTime - enterTime,leaveTime - enterTime);
    }
}
