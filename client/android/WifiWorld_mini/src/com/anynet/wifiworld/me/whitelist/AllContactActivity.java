
package com.anynet.wifiworld.me.whitelist;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;

public class AllContactActivity extends BaseActivity {
    private ContactUpdateReceiver receiver;
    private final SparseArray<ArrayList<Contact>> contactMaps = new SparseArray<ArrayList<Contact>>();
    private final SparseArray<String> charMaps = new SparseArray<String>();
    private final HashMap<String, Integer> keyMaps = new HashMap<String, Integer>();
    PinnedHeaderListView listView;
    AllContactsAdapter adapter;
    SideBar sideBar;

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText("添加白名单");
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setContentView(R.layout.activity_all_contact);
        super.onCreate(savedInstanceState);
        bingdingTitleUI();
        
        new AppApplication(this);

        listView = (PinnedHeaderListView) findViewById(R.id.listview_all_contacts);
        adapter = new AllContactsAdapter(this);
        listView.setAdapter(adapter);
        sideBar = (SideBar) findViewById(R.id.sidebar_all_contacts);
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.Action_All_Contacts_Changed);
        //filter.addAction(Consts.Action_Delete_One_Contact_From_All);
        receiver = new ContactUpdateReceiver();
        registerReceiver(receiver, filter);

        updateData();
    }

    class OnTouchingLetterChangedListener implements SideBar.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (keyMaps.get(s) != null) {
                int idx = keyMaps.get(s);
                listView.setSelection(idx);
            }
        }

    }

    private void updateData() {
        ContactHelper.splitAllContacts(contactMaps, charMaps);
        adapter.setData(contactMaps, charMaps);
        int pos = 0;
        String[] chars = new String[charMaps.size()];
        for (int i = 0; i < charMaps.size(); i++) {
            int idx = charMaps.keyAt(i);
            keyMaps.put(charMaps.get(idx), pos);
            chars[i] = charMaps.get(i);
            pos += contactMaps.get(idx).size() + 1;
        }
        sideBar.setChars(chars);
        sideBar.invalidate();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void onContactDeleted(long id) {
        for (int i = 0; i < AppApplication.AllContacts.size(); i++) {
            Contact type = AppApplication.AllContacts.get(i);
            if (id == type.getContactId()) {
                AppApplication.AllContacts.remove(i);
                updateData();
                break;
            }
        }
    }

    class ContactUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Consts.Action_All_Contacts_Changed.equals(action)) {
                updateData();
            } //else if (Consts.Action_Delete_One_Contact_From_All.equals(action)) {
                //long id = intent.getLongExtra(Consts.Extra_Contact_ID, -1L);
                //onContactDeleted(id);
            //}
        }
    }
}
