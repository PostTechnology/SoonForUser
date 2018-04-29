package com.soon.android.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soon.android.MyApplication;
import com.soon.android.R;
import com.soon.android.adapters.OrderListAdapter;
import com.soon.android.bmobBean.Order;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private String userId;

    private List<Order> orderListData = new ArrayList<>();

    private static final String TAG = "OrderFragment";

    @BindView(R.id.order_list)
    RecyclerView orderList;

    private Handler handler = new Handler(){

        public void  handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    orderListData = (List<Order>)msg.obj;
                    loadOrderList();
                    break;
                default:
                    break;
            }
        }
    };

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);
        BmobUser currentUser = BmobUser.getCurrentUser();
        userId = currentUser.getObjectId();
        queryOrdersByUserId(userId);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //根据用户id查询对应的订单
    private void queryOrdersByUserId(String userId){
        Toast.makeText(MyApplication.getContext(), "id:" + userId, Toast.LENGTH_SHORT).show();
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("userObjectId", userId);
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> object, BmobException e) {
                if(e==null){
                    Message message = new Message();
                    message.what = 0;
                    message.obj = object;
                    handler.sendMessage(message);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //加载订单列表
    private void loadOrderList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderList.setLayoutManager(layoutManager);
        OrderListAdapter adapter = new OrderListAdapter(R.layout.order_list_item, orderListData);
        orderList.setAdapter(adapter);
    }

}
