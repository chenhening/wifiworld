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

public class WifiOnlineSlidingFragment extends Fragment implements OnTouchListener {
    private float start_x = 0;
    private float end_x = 0;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
        RippleBackground rippleBackground = (RippleBackground)view.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        view.setOnTouchListener(this);
        return view;
    }
	
	@Override
    public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			start_x = event.getRawX();
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			end_x = event.getRawX();
			if (Math.abs(start_x - end_x) > 100) {
				if (getFragmentManager().getBackStackEntryCount()==0) {
		            Fragment secondFragment = new WifiOnlineSlidingFragment();
		            FragmentManager fm = getFragmentManager();
		            FragmentTransaction fragmentTransaction = fm.beginTransaction();
		            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(getActivity(), fragmentTransaction, this, secondFragment, R.id.fragment_place);
		            fragmentTransactionExtended.addTransition(FragmentTransactionExtended.FLIP_HORIZONTAL);
		            fragmentTransactionExtended.commit();
		        }else{
		            getFragmentManager().popBackStack();
		        }
			}
        }  
	    return true;
    }
}
