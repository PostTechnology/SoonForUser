package com.soon.android.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.soon.android.ChooseAddressActivity;
import com.soon.android.MainActivity;
import com.soon.android.MyApplication;
import com.soon.android.R;
import com.soon.android.adapters.TakeOutHomeFragmentPagerAdapter;
import com.soon.android.utils.LBSUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakeOutHomeFragment extends Fragment {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;//用于显示

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.floating_search_view)
    FloatingSearchView floatingSearchView;

    private TakeOutHomeFragmentPagerAdapter mTakeOutHomeFragmentPagerAdapter;

    private TabLayout.Tab one;

    private TabLayout.Tab two;

    private TabLayout.Tab three;

    public TakeOutHomeFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.positionText)
    TextView positionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_take_out_home, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //使用适配器将ViewPager与Fragment绑定在一起
        mTakeOutHomeFragmentPagerAdapter = new TakeOutHomeFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mTakeOutHomeFragmentPagerAdapter);

        //将TabLayout和ViewPager绑定在一起，使双方各自的改变都能直接影响另一方，解放了开发人员对双方变动事件的监听
        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置  自定义tab样式
        one = mTabLayout.getTabAt(0);
        one.setCustomView(R.layout.tab_layout_item);
        setTabStyle(one, R.drawable.ic_roundness_canteen, R.drawable.ic_canteen, R.string.tab_first);

        two = mTabLayout.getTabAt(1);
        two.setCustomView(R.layout.tab_layout_item);
        setTabStyle(two, R.drawable.ic_roundness_supermarket, R.drawable.ic_canteen, R.string.tab_second);

        three = mTabLayout.getTabAt(2);
        three.setCustomView(R.layout.tab_layout_item);
        setTabStyle(three, R.drawable.ic_roundness_others, R.drawable.ic_canteen, R.string.tab_third);

        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions(permissions, 1);
        }else{
            LBSUtils.requestLocation();
        }

        positionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseAddressActivity.actionStart(MyApplication.getContext(), LBSUtils.getMessage());
            }
        });

        final MainActivity mainActivity = (MainActivity)getActivity();
        floatingSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {

                mainActivity.getSlidingRootNav().openMenu();
            }

            @Override
            public void onMenuClosed() {
                mainActivity.getSlidingRootNav().closeMenu();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LBSUtils.getMessage().get(1) != null && LBSUtils.getMessage().get(1) != ""){
            positionText.setText(LBSUtils.getMessage().get(1));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MyApplication.getContext(), "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    LBSUtils.requestLocation();
                }else{
                    Toast.makeText(MyApplication.getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            }
            default:break;
        }
    }

    //设置tab的样式
    private void setTabStyle(TabLayout.Tab tab,int backgroundImgRes, int imageRes, int text){
        ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.background_img);
        imageView.setImageResource(backgroundImgRes);
        ImageView cImageView = (ImageView) tab.getCustomView().findViewById(R.id.image);
        cImageView.setImageResource(imageRes);
        TextView textView = (TextView) tab.getCustomView().findViewById(R.id.name);
        textView.setText(text);
    }
}
