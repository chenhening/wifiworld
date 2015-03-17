package com.anynet.wifiworld.wifi;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class NetworkTester extends Thread {
	private final static String TAG = NetworkTester.class.getSimpleName();
	
	private String mFileUrl;
	private WifiSpeedTester.DownloadedFileParams mParams;
	private boolean mStopFlag;
	
	public NetworkTester(String fileUrl, WifiSpeedTester.DownloadedFileParams params) {
		mFileUrl = fileUrl;
		mParams = params;
		mStopFlag = false;
	}
	
	protected byte[] downloadFile(String fileUrl, WifiSpeedTester.DownloadedFileParams params) {
		int currentByte = 0;
		int fileSize = 0;
		long startTime = 0;
		long intervalTime = 0;

		byte[] b = null;

		URL urlx = null;
		URLConnection con = null;
		InputStream stream = null;
		try {
			String downloadFileURL = fileUrl;
			Log.d(TAG, "Start to download File: " + downloadFileURL);
			urlx = new URL(downloadFileURL);
			con = urlx.openConnection();
			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			fileSize = con.getContentLength();
			stream = con.getInputStream();
			params.totalBytes = fileSize;
			b = new byte[4096];
			startTime = System.currentTimeMillis();
			while ((currentByte = stream.read(b, 0, 4096)) != -1 && !mStopFlag) {
				intervalTime = System.currentTimeMillis() - startTime;
				params.downloadedBytes += currentByte;
				if (intervalTime == 0) {
					params.speed = 1000;
				} else {
					params.speed = (params.downloadedBytes / intervalTime) * 1000;
				}
				Log.i(TAG, "Wifi speed is: " + params.speed);
			}
		} catch (Exception e) {
			Log.e("exception : ", e.getMessage() + "");
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception e) {
				Log.e("exception : ", e.getMessage());
			}

		}
		
		return b;
	}

	public void stopDownload() {
		Log.i(TAG, "Stop Test Speed:Download file");
		mStopFlag = true;
	}
	
	@Override
	public void run() {
		super.run();
		downloadFile(mFileUrl, mParams);
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
	}
}
