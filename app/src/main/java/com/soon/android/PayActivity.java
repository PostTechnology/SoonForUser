package com.soon.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.Order;
import com.soon.android.bmobBean.Store;
import com.soon.android.utils.DecimalUtil;
import com.soon.android.utils.ProgressDialogUtil;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class PayActivity extends AppCompatActivity {

    @BindView(R.id.pay)
    Button pay;

    @BindView(R.id.store_name)
    TextView name;//店铺的名称

    @BindView(R.id.sum_price)
    TextView sumPrice;//订单的价格

    private Order order;//查询到的订单,即本次订单

    private String storeName;//本次订单的店铺名称

    private ProgressDialog progressDialog;

    private Handler handler = new Handler(){

        public void  handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    order = (Order) msg.obj;
                    queryStoreName(order.getStoreObjectId());
                    break;
                case 1:
                    storeName = msg.obj.toString();
                    //Toast.makeText(PayActivity.this, storeName, Toast.LENGTH_SHORT).show();
                    name.setText(storeName);
                    sumPrice.setText("￥" + DecimalUtil.decimalForTwo(order.getSumPrice()));
                    progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("支付");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        progressDialog = ProgressDialogUtil.getProgressDialog(PayActivity.this, null, "Waiting...", false);

        final Intent intent = getIntent();
        final String orderId = intent.getStringExtra("orderId");
        queryByOrderId(orderId);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order o = new Order();
                o.setStatus(1);
                o.update(orderId, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Log.i("bmob","更新成功");
                        }else{
                            Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                Intent intent1 = new Intent();
                intent.putExtra("orderId", orderId);//返回orderId，用于给订单碎片中的立即支付按钮修改对应订单的数据
                setResult(RESULT_OK, intent1);
                finish();
            }
        });


    }

    //根据订单id查询对应的订单
    private void queryByOrderId(String orderId){
        progressDialog.show();
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.getObject(orderId, new QueryListener<Order>() {
            @Override
            public void done(Order object, BmobException e) {
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

    //根据订单中的storeId查询store的name
    private void queryStoreName(String storeId){

        BmobQuery<Store> query = new BmobQuery<Store>();
        query.getObject(storeId, new QueryListener<Store>() {
            @Override
            public void done(Store object, BmobException e) {
                if(e==null){
                    Message message = new Message();
                    message.what = 1;
                    message.obj = object.getName();
                    handler.sendMessage(message);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }

        });
    }

    public static void actionStartForResult(Context context, String orderId, int requestCode){
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("orderId", orderId);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void actionStartForResultByFragment(Fragment fragment, String orderId, int requestCode){
        Intent intent = new Intent(fragment.getActivity(), PayActivity.class);
        intent.putExtra("orderId", orderId);
        fragment.startActivityForResult(intent, requestCode);
    }
}
