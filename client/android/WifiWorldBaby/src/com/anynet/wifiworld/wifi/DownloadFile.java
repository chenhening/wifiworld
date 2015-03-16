package com.anynet.wifiworld.wifi;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class DownloadFile extends AsyncTask<String, Integer, String> {
	private final static String TAG = DownloadFile.class.getSimpleName();
	
	private View mContext;
	private WifiSpeedTester.DownloadedFileParams mParams;
	
	public DownloadFile(View context, WifiSpeedTester.DownloadedFileParams param) {
		mContext = context;
		mParams = param;
		resetDownloadedParams();
	}

	@Override
	protected String doInBackground(String... params) {
		int currentByte = 0;
		int fileLength = 0;
		long startTime = 0;
		long intervalTime = 0;

		byte[] b = null;

		int bytecount = 0;
		URL urlx = null;
		URLConnection con = null;
		InputStream stream = null;
		try {
			String downloadFileURL = params[0];
			Log.d(TAG, "Start to download File: " + downloadFileURL);
			urlx = new URL(downloadFileURL);
			con = urlx.openConnection();
			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			fileLength = con.getContentLength();
			stream = con.getInputStream();
			mParams.totalBytes = fileLength;
			b = new byte[fileLength];
			startTime = System.currentTimeMillis();
			while ((currentByte = stream.read()) != -1) {
				mParams.downloadedBytes++;
				intervalTime = System.currentTimeMillis() - startTime;
				if (intervalTime == 0) {
					mParams.speed = 1000;
				} else {
					mParams.speed = (mParams.downloadedBytes / intervalTime) * 1000;
				}
				if (bytecount < fileLength) {
					b[bytecount++] = (byte) currentByte;
				}
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
		
		return null;
	}

	@Override
	protected void onCancelled() {
		resetDownloadedParams();
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
	private void resetDownloadedParams() {
		mParams.downloadedBytes = 0;
		mParams.downloadedPercent = 0f;
		mParams.netWorkType = null;
		mParams.speed = 1024;
		mParams.totalBytes = 0;
	}

}
