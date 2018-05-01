package com.soon.android.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.soon.android.R;
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        List<Discover> list = new ArrayList<>();
        List<Image> images = new ArrayList<>();
//        Discover discover1 = new Discover();
//        Discover discover2 = new Discover();
//        Discover discover3 = new Discover();
//        Discover discover4 = new Discover();
//        Discover discover5 = new Discover();
//        discover1.setUsername("张三");
//        discover2.setUsername("李四");
//        discover3.setUsername("王二");
//        discover4.setUsername("张三");
//        discover5.setUsername("李四");
//
//        discover1.setComment("");
//        discover2.setComment("");
//        discover3.setComment("");
//        discover4.setComment("");
//        discover5.setComment("");
//
//        discover1.setTime("");
//        discover2.setTime("");
//        discover3.setTime("");
//        discover4.setTime("");
//        discover5.setTime("");

        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));
        images.add(new Image("http://p3.so.qhmsg.com/bdr/_240_/t01530591c4a0e6c033.jpg",250,250));

//        discover1.setImages(images);
//        discover2.setImages(images);
//        discover3.setImages(images);
//        discover4.setImages(images);
//        discover5.setImages(images);
//
//        list.add(discover1);
//        list.add(discover2);
//        list.add(discover3);
//        list.add(discover4);
//        list.add(discover5);
        List<List<Image>> list = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            list.add(images);
        }
        recyclerView.setAdapter(new DiscoverListAdapter(getContext(), list));
    }
}
