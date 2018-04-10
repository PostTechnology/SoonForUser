package com.soon.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.soon.android.fragments.CanteenFragment;
import com.soon.android.fragments.OthersFragment;
import com.soon.android.fragments.SupermarketFragment;

/**
 * Created by LYH on 2018/1/25.
 */

public class TakeOutHomeFragmentPagerAdapter extends FragmentPagerAdapter {

    private Fragment fragment;

    public TakeOutHomeFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                fragment =  new CanteenFragment();
                break;
            case 2:
                fragment =  new SupermarketFragment();
                break;
            case 3:
                fragment =  new OthersFragment();
                break;
            default:
                fragment =  new CanteenFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
