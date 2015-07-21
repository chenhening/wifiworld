package com.anynet.wifiworld.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.anynet.wifiworld.wifi.WifiSpeedTester;

import android.util.Log;
import android.widget.Toast;

import android.os.Process;

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
	
	protected boolean downloadFile(String fileUrl, WifiSpeedTester.DownloadedFileParams params) {
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
			if (fileSize < 1024) {
				params.downloadedBytes = -1;
				stopDownload();
			}
			stream = con.getInputStream();
			params.totalBytes = fileSize;
			b = new byte[fileSize];
			startTime = System.currentTimeMillis();
			while ((currentByte = stream.read(b, 0, fileSize)) != -1 && !mStopFlag) {
				intervalTime = System.currentTimeMillis() - startTime;
				params.downloadedBytes += currentByte;
				if (intervalTime == 0) {
					params.speed = 1000;
				} else {
					params.speed = (params.downloadedBytes / intervalTime) * 1000;
				}
				Log.i(TAG, "Downloaded bytes: " + currentByte);
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
		
		return true;
	}

	public void stopDownload() {
		Log.i(TAG, "Stop Test Speed:Download file");
		mStopFlag = true;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Start Running...............");
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		downloadFile(mFileUrl, mParams);
		super.run();
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
	}
}
