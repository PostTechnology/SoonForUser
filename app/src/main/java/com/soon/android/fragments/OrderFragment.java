package com.soon.android.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.soon.android.EvaluateActivity;
import com.soon.android.MyApplication;
import com.soon.android.PayActivity;
import com.soon.android.R;
import com.soon.android.StoreActivity;
import com.soon.android.adapters.OrderListAdapter;
import com.soon.android.bmobBean.Goods;
import com.soon.android.bmobBean.Order;
import com.soon.android.bmobBean.Store;
import com.soon.android.db.StoreShoppingCar;
import com.soon.android.utils.LoginUtils;
import com.soon.android.utils.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private String userId;

    private List<Order> orderListData = new ArrayList<>();

    private static final String TAG = "OrderFragment";

    private ProgressDialog progressDialog;

    @BindView(R.id.order_list)
    RecyclerView orderList;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    orderListData = (List<Order>) msg.obj;
                    loadOrderList();
                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
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
        userId = LoginUtils.login().get("userid");
        if (orderListData.size() == 0 || orderListData.isEmpty()){
            queryOrdersByUserId(userId);
        }else {
            loadOrderList();
        }

        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), null, "Waiting...", false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryOrdersByUserId(userId);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //根据用户id查询对应的订单
    private void queryOrdersByUserId(String userId) {
        Toast.makeText(MyApplication.getContext(), "id:" + userId, Toast.LENGTH_SHORT).show();
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("userObjectId", userId);
        query.order("-createdAt");
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> object, BmobException e) {
                if (e == null) {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = object;
                    handler.sendMessage(message);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //加载订单列表
    private void loadOrderList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderList.setLayoutManager(layoutManager);
        OrderListAdapter adapter = new OrderListAdapter(R.layout.order_list_item, orderListData);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                switch (view.getId()) {
                    case R.id.buy_again:
                        progressDialog.show();
                        //根据店铺id，查询对应的店铺数据
                        BmobQuery<Store> query = new BmobQuery<Store>();
                        query.getObject(orderListData.get(position).getStoreObjectId(), new QueryListener<Store>() {
                            @Override
                            public void done(Store store, BmobException e) {
                                progressDialog.dismiss();
                                StoreActivity.actionStart(getActivity(), store);
                            }
                        });
                        break;
                    case R.id.evaluate:
                        progressDialog.show();
                        //根据店铺id，查询对应的店铺数据
                        BmobQuery<Store> query1 = new BmobQuery<Store>();
                        query1.getObject(orderListData.get(position).getStoreObjectId(), new QueryListener<Store>() {
                            @Override
                            public void done(Store store, BmobException e) {
                                progressDialog.dismiss();
                                EvaluateActivity.actionStartForResultByFragment(OrderFragment.this, store, orderListData.get(position).getObjectId(), 3);
                            }
                        });
                        break;
                    case R.id.confirm_receipt:
                        Gson gson = new Gson();
                        String goodList = orderListData.get(position).getGoodsList();
                        List<StoreShoppingCar> storeShoppingCars = gson.fromJson(goodList, new TypeToken<List<StoreShoppingCar>>() {
                        }.getType());
                        //对应的商品销量+1
                        for (StoreShoppingCar s : storeShoppingCars) {
                            Goods goods = new Goods();
                            goods.setObjectId(s.getGoodsObectId());
                            goods.increment("salesVolume");
                            goods.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    Log.d(TAG, "对应的商品销量+1成功");
                                }
                            });
                        }

                        //对应的店铺销量+1
                        Store store = new Store();
                        store.setObjectId(orderListData.get(position).getStoreObjectId());
                        store.increment("salesVolume");
                        store.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                Log.d(TAG, "店铺销量+1成功");
                            }
                        });

                        changeOrderStatus(orderListData.get(position).getObjectId(), userId, 3);
                        break;
                    case R.id.pay:
                        PayActivity.actionStartForResultByFragment(OrderFragment.this, orderListData.get(position).getObjectId(), 2);
                        break;
                    case R.id.cancel_order:
                        changeOrderStatus(orderListData.get(position).getObjectId(), userId, -1);
                        break;
                    default:
                }
            }
        });

        orderList.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
            case 3:
                if (resultCode == RESULT_OK) {
                    progressDialog.show();
                    queryOrdersByUserId(userId);
                }
                break;
            default:
        }
    }

    //修改订单状态
    private void changeOrderStatus(String orderId, final String userId, int status) {
        progressDialog.show();
        Order order = new Order();
        order.setStatus(status);
        order.update(orderId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "更新成功");
                    queryOrdersByUserId(userId);
                } else {
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
