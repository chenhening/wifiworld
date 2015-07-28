package com.anynet.wifiworld.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UIHelper {
	private final static String TAG = UIHelper.class.getSimpleName();
	
   public static void setListViewHeightBasedOnChildren(ListView listView) {
	   ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null || listAdapter.getCount() == 0) {
			return;
		}

		int totalHeight = 0;
		//for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(0, null, listView);
			listItem.measure(0, 0);
			totalHeight = listItem.getMeasuredHeight() * listAdapter.getCount();
		//}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
   }
}
