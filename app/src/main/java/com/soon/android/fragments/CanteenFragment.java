package com.soon.android.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.soon.android.MyApplication;
import com.soon.android.R;
import com.soon.android.StoreActivity;
import com.soon.android.adapters.CanteenListAdapter;
import com.soon.android.bmobBean.Order;
import com.soon.android.bmobBean.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CanteenFragment extends Fragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Store> canteenListData = new ArrayList<>();//存放食堂信息列表的数据

    private static final String TAG = "CanteenFragment";

    @BindView(R.id.canteen_list)
    RecyclerView canteensRecyclerView;

    public static final int STORE_LIST_DATA = 0;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STORE_LIST_DATA:
                    canteenListData = (List<Store>) msg.obj;
                    loadCanteenListData();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    public CanteenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getActivity().getSharedPreferences("locatPosition", Context.MODE_PRIVATE);
        final Float lng = pref.getFloat("Lng", -1);
        final Float lat = pref.getFloat("Lat", -1);
        Toast.makeText(getActivity(), lng + ":" + lat, Toast.LENGTH_SHORT).show();

        if (canteenListData.size() == 0 || canteenListData.isEmpty()){
            //queryStoreListByLocation(28.7362898584, 115.8207631296);
            queryStoreListByLocation(lat, lng);
        }else{
            loadCanteenListData();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryStoreListByLocation(lat, lng);
            }
        });
    }

    //从服务器获取食堂列表信息
    public void queryStoreListByLocation(double longitude, double latitude) {
        BmobQuery<Store> query = new BmobQuery<Store>();
        //查询在某一范围的店家
        query.addWhereLessThan("longitude", longitude + 0.01);
        query.addWhereLessThan("longitude", latitude + 0.01);
        //执行查询方法
        query.findObjects(new FindListener<Store>() {
            @Override
            public void done(List<Store> object, BmobException e) {
                if (e == null) {
                    Message message = new Message();
                    message.what = STORE_LIST_DATA;
                    message.obj = object;
                    handler.sendMessage(message);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //显示从服务器查询到的食堂数据，并设置点击事件
    private void loadCanteenListData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        canteensRecyclerView.setLayoutManager(layoutManager);
        CanteenListAdapter adapter = new CanteenListAdapter(R.layout.canteen_list_item, canteenListData);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Store store = new Store();
                BmobFile imageFile = canteenListData.get(position).getImage();
                String canteenName = canteenListData.get(position).getName();
                int salesVolume = canteenListData.get(position).getSalesVolume();
                float ratingNum = canteenListData.get(position).getRating();
                store.setImage(imageFile);
                store.setName(canteenName);
                store.setSalesVolume(salesVolume);
                store.setRating(ratingNum);
                store.setObjectId(canteenListData.get(position).getObjectId());
                store.setDeliveryCost(canteenListData.get(position).getDeliveryCost());
//                int dispatchTime = canteenListData.get(position).getDispatchTime();
//                float sendOutPrice = canteenListData.get(position).getSendOutPrice();
//                float transportationExpense = canteenListData.get(position).getTransportationExpense();
//                canteenListItemModel.setImageRes(imgRes);
//                canteenListItemModel.setCanteenName(canteenName);
//                canteenListItemModel.setRatingNum(ratingNum);
//                canteenListItemModel.setDispatchTime(dispatchTime);
//                canteenListItemModel.setSendOutPrice(sendOutPrice);
//                canteenListItemModel.setTransportationExpense(transportationExpense);
//                canteenListItemModel.setSalesVolume(salesVolume);
                StoreActivity.actionStart(getActivity(), store);
            }
        });
        canteensRecyclerView.setAdapter(adapter);
    }

    //查询指定id的店铺的评分
    private void queryStoreRating(String storeObjectId) {
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("storeObjectId", storeObjectId);
        query.average(new String[]{"rating"});
        query.findStatistics(Order.class, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    if (ary != null) {
                        try {
                            JSONObject obj = ary.getJSONObject(0);
                            int average = obj.getInt("_averageRating");//_(关键字)+首字母大写的列名
                            Toast.makeText(MyApplication.getContext(), "评分" + average, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MyApplication.getContext(), "查询成功，无数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
