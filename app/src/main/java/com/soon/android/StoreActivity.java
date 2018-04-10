package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.soon.android.adapters.StoreActivityFragmentPagerAdaper;
import com.soon.android.bmobBean.Store;
import com.soon.android.db.StoreShoppingCar;
import com.soon.android.utils.DecimalUtil;
import com.soon.android.utils.LoginUtils;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreActivity extends AppCompatActivity {

//    @BindView(R.id.send_out_price)
//    TextView sendOutPrice;
//
//    @BindView(R.id.transportation_expense)
//    TextView transportationExpense;
//
//    @BindView(R.id.rating_num)
//    TextView ratingNum;
//
//    @BindView(R.id.dispatch_time)
//    TextView dispatchTime;
//
//    @BindView(R.id.sales_volume)
//    TextView salesVolume;

    @BindView(R.id.background_img)
    ImageView backgroundImg;

    @BindView(R.id.navigation_tab_strip)
    NavigationTabStrip navigationTabStrip;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.close_account)
    Button closeAccount;

    @BindView(R.id.sum_price)
    TextView sumPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        Store store = (Store)getIntent().getSerializableExtra("canteen_data");
        actionBar.setTitle(store.getName());
//        salesVolume.setText("月售" + canteenListItemModel.getDispatchTime() + "单");
//        sendOutPrice.setText("起送￥" + canteenListItemModel.getSendOutPrice());
//        transportationExpense.setText("配送￥" + String.valueOf(canteenListItemModel.getTransportationExpense()));
//        ratingNum.setText(String.valueOf("评分" +canteenListItemModel.getRatingNum()));
//        dispatchTime.setText("送达" + canteenListItemModel.getDispatchTime() + "分钟");
        Glide.with(MyApplication.getContext()).load(store.getImage().getFileUrl()).into(backgroundImg);

        StoreActivityFragmentPagerAdaper storeActivityFragmentPagerAdaper = new StoreActivityFragmentPagerAdaper(getSupportFragmentManager());
        viewPager.setAdapter(storeActivityFragmentPagerAdaper);
        navigationTabStrip.setTitles("点菜", "评价");
        navigationTabStrip.setViewPager(viewPager);

        closeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginUtils.login().get("userid").equals("")){
                    LoginActivity.actionStart(StoreActivity.this);
                }else{
                    Store s = (Store)getIntent().getSerializableExtra("canteen_data");
                    SubmitOrderActivity.actionStart(StoreActivity.this, s);
                }
            }
        });
    }

    public static void actionStart(Context context, Store store){
        Intent intent = new Intent(context, StoreActivity.class);
        intent.putExtra("canteen_data", store);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Store store = (Store)getIntent().getSerializableExtra("canteen_data");
        List<StoreShoppingCar> chooseFoodListData = DataSupport.where("storeObectId = ?", store.getObjectId()).find(StoreShoppingCar.class);
        //计算订单总金额
        float sum = 0;
        for (StoreShoppingCar s : chooseFoodListData){
            sum += s.getPrice() * s.getDiscount() * 0.1 * s.getSum();
        }
        sumPrice.setText("￥" + DecimalUtil.decimalForTwo(sum));
    }
}
