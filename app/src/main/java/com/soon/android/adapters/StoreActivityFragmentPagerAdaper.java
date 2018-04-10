package com.soon.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.soon.android.fragments.CanteenFragment;
import com.soon.android.fragments.CommentViewPagerFragment;
import com.soon.android.fragments.FoodViewPagerFragment;
import com.soon.android.fragments.OthersFragment;
import com.soon.android.fragments.SupermarketFragment;

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
            case 1:
                fragment =  new FoodViewPagerFragment();
                break;
            case 2:
                fragment =  new CommentViewPagerFragment();
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
