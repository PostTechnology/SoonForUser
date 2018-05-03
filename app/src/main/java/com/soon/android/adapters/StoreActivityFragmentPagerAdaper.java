package com.soon.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.soon.android.fragments.CommentFragment;
import com.soon.android.fragments.FoodViewPagerFragment;

/**
 * Created by LYH on 2018/1/30.
 */

public class StoreActivityFragmentPagerAdaper extends FragmentPagerAdapter {

    private Fragment fragment;

    public StoreActivityFragmentPagerAdaper(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                fragment =  new FoodViewPagerFragment();
                break;
            case 1:
                fragment =  new CommentFragment();
                break;
            default:
                fragment =  new FoodViewPagerFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
