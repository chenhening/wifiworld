package com.anynet.wifiworld.me;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

import com.anynet.wifiworld.R;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.skyfishjy.library.RippleBackground;

public class WifiOnlineSlidingFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
        RippleBackground rippleBackground = (RippleBackground)view.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        return view;
    }
}
