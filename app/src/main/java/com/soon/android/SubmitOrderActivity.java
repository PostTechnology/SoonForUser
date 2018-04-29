package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.soon.android.adapters.SubmitOrderFoodListAdapter;
import com.soon.android.bmobBean.Address;
import com.soon.android.bmobBean.Goods;
import com.soon.android.bmobBean.Order;
import com.soon.android.bmobBean.Store;
import com.soon.android.db.StoreShoppingCar;
import com.soon.android.utils.DecimalUtil;
import com.soon.android.utils.JsonUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SubmitOrderActivity extends AppCompatActivity {

    private List<StoreShoppingCar> chooseFoodListData = new ArrayList<>();//购物车中的商品信息

    @BindView(R.id.food_list)
    RecyclerView foodList;

    @BindView(R.id.delivery_cost)
    TextView deliveryCost;//配送费

    @BindView(R.id.sum_price)
    TextView sumPrice;

    @BindView(R.id.submit_button)
    Button sumOrder;

    @BindView(R.id.remark)
    EditText remark;

    private static final String TAG = "SubmitOrderActivity";

    private Address addresses;//存放从服务器查询到的用户地址信息

    private String createOrderId;//提交订单后，在数据库创建的订单的id

    private float sum = 0;//订单的总金额

    private Handler handler = new Handler(){

        public void  handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    addresses = (Address) msg.obj;
                    loadAddress(addresses, R.id.address);
                    break;
                case 1:
                    createOrderId = msg.obj.toString();
                    finish();
                    PayActivity.actionStartForResult(SubmitOrderActivity.this, createOrderId, 1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("确认订单");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        //加载用户地址信息
        final BmobUser currentUser = BmobUser.getCurrentUser();
        queryByUserObjectId(currentUser.getObjectId());

        final Store store = (Store)getIntent().getSerializableExtra("canteen_data");

        //从本地数据库中购物车数据表 查找购物车中的商品信息
        chooseFoodListData = DataSupport.where("storeObectId = ?", store.getObjectId()).find(StoreShoppingCar.class);
        SubmitOrderFoodListAdapter adapter = new SubmitOrderFoodListAdapter(R.layout.submit_order_food_list_item, chooseFoodListData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);
        foodList.setAdapter(adapter);

        deliveryCost.setText("￥" + store.getDeliveryCost());

        //计算订单总金额

        for (StoreShoppingCar s : chooseFoodListData){
            sum += s.getPrice() * s.getDiscount() * 0.1 * s.getSum();
        }
        sumPrice.setText("￥" + DecimalUtil.decimalForTwo(sum));

        sumOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order order = new Order();
                //注意：不能调用gameScore.setObjectId("")方法
                order.setUserObjectId(currentUser.getObjectId());
                order.setStoreObjectId(store.getObjectId());
                order.setRemark(remark.getText().toString());
                //设置商品json字符串
                order.setGoodsList(JsonUtil.toArray(chooseFoodListData));
                //设置收货人信息json字符串
                order.setDeliveryAddress(JsonUtil.toString(addresses));
                order.setSumPrice(sum);
                order.setStatus(0);
                order.setStoreName(store.getName());
                order.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if(e==null){
                            Message message = new Message();
                            message.what = 1;
                            message.obj = objectId;
                            handler.sendMessage(message);
                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });


            }
        });
    }

    //通过id查询用户的收货地址
    public void queryByUserObjectId(String userObjectId) {
        BmobQuery<Address> query = new BmobQuery<Address>();
        query.addWhereEqualTo("userObjectId", userObjectId);
        query.addWhereEqualTo("defaultAddress", true);
        query.setLimit(50);
        query.findObjects(new FindListener<Address>() {
            @Override
            public void done(List<Address> object, BmobException e) {
                if(e==null){
                    Message message = new Message();
                    message.what = 0;
                    message.obj = object.get(0);
                    handler.sendMessage(message);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //加载用户的收获地址
    private void loadAddress(Address address, int layoutResId){
        View view = findViewById(layoutResId);
        ((TextView)view.findViewById(R.id.name)).setText(address.getName());
        ((TextView)view.findViewById(R.id.tel)).setText(address.getTel());
        ((TextView)view.findViewById(R.id.location)).setText(address.getLocation());
        ((TextView)view.findViewById(R.id.doorNum)).setText(address.getDoorNum());
    }

    public static void actionStart(Context context, Store store){
        Intent intent = new Intent(context, SubmitOrderActivity.class);
        intent.putExtra("canteen_data", store);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    finish();
                }
                break;
            default:
        }
    }
}
