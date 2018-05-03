package com.soon.android.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.soon.android.R;
import com.soon.android.adapterDataModels.Discover;
import com.soon.android.adapterDataModels.Image;
import com.soon.android.adapters.DiscoverListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    @BindView(R.id.discover_toolbar)
    Toolbar toolbar;

    @BindView(R.id.discover_list)
    ListView recyclerView;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("附近的");
        }
        setHasOptionsMenu(true);//加上这句话，menu才会显示出来
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Discover> list = new ArrayList<>();
        Discover discover1 = new Discover();
        Discover discover2 = new Discover();
        Discover discover3 = new Discover();
        Discover discover4 = new Discover();
        Discover discover5 = new Discover();
        discover1.setUsername("张三");
        discover2.setUsername("李四");
        discover3.setUsername("王二");
        discover4.setUsername("匿名用户");
        discover5.setUsername("麻子");

        discover1.setComment("好吃，推荐！干净！卫生！送餐快！");
        discover2.setComment("肉多 味道也特别好");
        discover3.setComment("配送快，餐品保存完好，主动联系，态度很好");
        discover4.setComment("系统默认好评");
        discover5.setComment("系统默认好评");

        discover1.setTime("5分钟前");
        discover2.setTime("15分钟前");
        discover3.setTime("30分钟前");
        discover4.setTime("1小时前");
        discover5.setTime("5小时前");

        list.add(discover1);
        list.add(discover2);
        list.add(discover3);
        list.add(discover4);
        list.add(discover5);

        for(int i = 0; i < 5; i++){
            List<Image> images = new ArrayList<>();
            List<Integer> index = new ArrayList<>();
            int randNum = (int)(Math.random()*(9-0+1));
            for(int j = 0; j < randNum; j++){
                int randIntex = ((int)(1+Math.random()*(18-1+1)));
                while (index.contains(randIntex)){
                    randIntex = ((int)(1+Math.random()*(18-1+1)));
                }
                index.add(randIntex);
                images.add(new Image("file:///android_asset/img" + randIntex + ".jpg",250,250));
            }
            list.get(i).setImages(images);
        }


//        List<List<Image>> list = new ArrayList<>();
//        for(int i = 0; i < 9; i++){
//            list.add(images);
//        }
        recyclerView.setAdapter(new DiscoverListAdapter(getContext(), list));
    }
}
