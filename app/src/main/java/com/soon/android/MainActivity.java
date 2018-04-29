package com.soon.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.soon.android.adapterDataModels.DrawerItemModel;
import com.soon.android.adapters.DrawerItemAdapter;
import com.soon.android.db.StoreShoppingCar;
import com.soon.android.fragments.OrderFragment;
import com.soon.android.fragments.TakeOutHomeFragment;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private SlidingRootNav slidingRootNav;

    @BindView(R.id.list)
    RecyclerView recyclerView;//侧滑抽屉中的选项

    @BindView(R.id.user_icon)
    CircleImageView userIcon;

    @BindView(R.id.user_name)
    TextView userName;

    @BindView(R.id.user_phone)
    TextView userPhone;

    private List<DrawerItemModel> drawerItemList = new ArrayList<>();

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;//底部导航栏

    @BindView(R.id.open_login_activity)
    LinearLayout openLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化BaiduMap
        SDKInitializer.initialize(getApplicationContext());
        // 初始化Bmob
        Bmob.initialize(this, "84aaecd322d3f4afa028222b754f2f98");
        setContentView(R.layout.activity_main);


        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menu_left_drawer)//注入抽屉菜单
        //过场动画
                .withDragDistance(210) //水平动画. Default == 180dp
                .withRootViewScale(0.8f) //设置主view的缩放比例0~0.7. 默认值 == 0.65f
                .withRootViewElevation(10) //主view垂直方向的值 0~10dp. 默认值 == 8
                .withRootViewYTranslation(4) //主view y轴方向的过场0~4. 默认值 == 0
                //.addRootTransformation(customTransformation)// 添加自定义过场
        //初始化菜单行为
                .withMenuOpened(false) //初始化菜单的状态(打开/关闭) 默认值 == false
                .withMenuLocked(false) //锁定菜单，true时不能打开或关闭菜单 默认值 == false.
                .withGravity(SlideGravity.LEFT) //设置菜单从哪个方向出来，
                .withSavedState(savedInstanceState) //是否保存菜单的状态
                .inject();

        ButterKnife.bind(this);

        // 点击登录
        openLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.actionStart(MainActivity.this);
            }
        });


        drawerItemList.add(new DrawerItemModel(R.drawable.ic_address, "收货地址"));
        drawerItemList.add(new DrawerItemModel(R.drawable.ic_collection, "我的收藏"));
        drawerItemList.add(new DrawerItemModel(R.drawable.ic_discounts, "我的优惠"));
        drawerItemList.add(new DrawerItemModel(R.drawable.ic_service, "服务中心"));
        drawerItemList.add(new DrawerItemModel(R.drawable.ic_setting, "账号设置"));
        drawerItemList.add(new DrawerItemModel(R.drawable.ic_cooperation, "退出登录"));

        //recyclerView = (RecyclerView) findViewById(R.id.list);
        DrawerItemAdapter adapter = new DrawerItemAdapter(R.layout.left_drawer_item_option, drawerItemList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:
                        AddressListActivity.actionStart(MainActivity.this);
                        break;
                    case 5:
                        BmobUser.logOut();
                        LoginActivity.actionStart(MainActivity.this);
                        break;
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //设置选中事件，切换对应的碎片
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId){
                    case R.id.tab_take_out:
                        replaceFragment(new TakeOutHomeFragment());
                        break;
                    case R.id.tab_order:
                        replaceFragment(new OrderFragment());
                        break;
                    default:
                        break;
                }
            }
        });
        replaceFragment(new TakeOutHomeFragment());//初始化，默认展示外卖界面
        DataSupport.deleteAll(StoreShoppingCar.class);//初始化，删除购物车里所有的数据
    }

    @Override
    protected void onStart() {
        super.onStart();
        BmobUser currentUser = BmobUser.getCurrentUser();
        if(currentUser != null){
            SharedPreferences preferences = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String nickname = preferences.getString("nickname","");
            String filename = preferences.getString("icon","");
            if(!filename.equals("")){
                String imageUrl = Environment.getExternalStorageDirectory() + "/" + filename;
                Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
                userIcon.setImageBitmap(bitmap);
            }
            userName.setText(nickname.equals("") ? currentUser.getObjectId() : nickname);
            userPhone.setText(currentUser.getUsername());
            openLoginActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonalCenterActivity.actionStart(MainActivity.this);
                }
            });
        }else{
            // 点击登录
            openLoginActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.actionStart(MainActivity.this);
                }
            });

        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.commit();
    }

    public SlidingRootNav getSlidingRootNav() {
        return slidingRootNav;
    }

    public void setSlidingRootNav(SlidingRootNav slidingRootNav) {
        this.slidingRootNav = slidingRootNav;
    }
}
