package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.soon.android.adapterDataModels.AddressListChangeModel;
import com.soon.android.adapterDataModels.CitySortModel;
import com.soon.android.adapters.AddressListChangeAdapter;
import com.soon.android.adapters.CitySortAdapter;
import com.soon.android.bmobBean.Address;
import com.soon.android.customView.WaveSideBarView;
import com.soon.android.db.AddressList;
import com.soon.android.utils.LBSUtils;
import com.soon.android.utils.PinyinComparator;
import com.soon.android.utils.PinyinUtils;
import com.soon.android.utils.TitleItemDecoration;
import com.wang.avi.AVLoadingIndicatorView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ChooseAddressActivity extends AppCompatActivity implements CitySortAdapter.OnItemClickListener{

    @BindView(R.id.choose_address_toolbar)
    Toolbar toolbar;

    @BindView(R.id.position_select)
    LinearLayout positionSelect;

    @BindView(R.id.city_select)
    FrameLayout citySelect;

    @BindView(R.id.your_city)
    TextView yourCity;

    @BindView(R.id.load_text)
    AVLoadingIndicatorView loadTextAvi;

    @BindView(R.id.load_location)
    AVLoadingIndicatorView loadLocationAvi;

    @BindView(R.id.location_now)
    TextView locationNow;

    @BindView(R.id.location_image)
    ImageView locationImage;

    @BindView(R.id.relocation)
    TextView relocation;

    @BindView(R.id.rv_city)
    RecyclerView mRecyclerView;

    @BindView(R.id.city_side_bar)
    WaveSideBarView mSideBar ;

    @BindView(R.id.address_list)
    RecyclerView recyclerView;

    private boolean yourCityIsClicked = false;

    private CitySortAdapter mAdapter;

    private LinearLayoutManager manager;

    private List<CitySortModel> mDateList;

    private TitleItemDecoration mDecoration;

    private List<AddressListChangeModel> addressListChangeModelList = new ArrayList<>();

    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator mComparator;

    // 定时器
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    loadLocationAvi.setVisibility(View.VISIBLE);
                    loadTextAvi.setVisibility(View.VISIBLE);
                    locationNow.setVisibility(View.GONE);
                    locationImage.setVisibility(View.GONE);

                    // 开启定位
                    LBSUtils.requestLocation();
                    locationNow.setText(LBSUtils.getMessage().get(1));
                    yourCity.setText(LBSUtils.getMessage().get(0));
                    break;
                case 1:
                    loadLocationAvi.hide();
                    loadTextAvi.hide();
                    locationNow.setVisibility(View.VISIBLE);
                    locationImage.setVisibility(View.VISIBLE);
                default:
                    break;
            }
        }
    };


    private Handler addressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    List<Address> addressList = (ArrayList) msg.obj;
                    for(Address address : addressList){
                        AddressList data = new AddressList();
                        data.clone(address);
                        data.save();
                        addressListChangeModelList.add(new AddressListChangeModel(address.getLocation(), address.getDoorNum(),
                                address.getName() + "(" + (address.getGender() ? "先生" : "女士") + ") " + address.getTel()));
                    }

                    AddressListChangeAdapter adapter = new AddressListChangeAdapter(R.layout.item_address, addressListChangeModelList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ChooseAddressActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_receiving_address);
        ButterKnife.bind(this);
//        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();
        initCitySelect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_address, menu);
        return true;
    }

    // 返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_new_address:{
                AddAddressActivity.actionStart(ChooseAddressActivity.this);
            }
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 活动跳转
    public static void actionStart(Context context, List<String> data){
        Intent intent = new Intent(context, ChooseAddressActivity.class);
        intent.putExtra("location", data.get(1));
        intent.putExtra("city", data.get(0));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 加载界面
    private void initView(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String positionLocation = intent.getStringExtra("location");
        String city = intent.getStringExtra("city");
        locationNow.setText(positionLocation);
        yourCity.setText(city);
        yourCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yourCityIsClicked){
                    positionSelect.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    yourCityIsClicked = !yourCityIsClicked;
                }else{
                    positionSelect.setVisibility(View.GONE);
                    citySelect.setVisibility(View.VISIBLE);
                    yourCityIsClicked = !yourCityIsClicked;
                }
            }
        });

        relocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.sendEmptyMessage(0);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            handler.sendEmptyMessage(1);
                        }
                    }
                }).start();
            }
        });

        List<AddressList> addressLists = DataSupport.findAll(AddressList.class);
        if(addressLists.size() == 0){
            Log.i("TAG", "findInBmob");
            Bmob.initialize(this, "84aaecd322d3f4afa028222b754f2f98");

            BmobUser currentUser = BmobUser.getCurrentUser();
            String userid = currentUser.getObjectId();
            Log.i("bmob","userid：" + userid);
            BmobQuery<Address> query = new BmobQuery<Address>();
            query.addWhereEqualTo("userObjectId",userid);
            query.findObjects(new FindListener<Address>() {
                @Override
                public void done(List<Address> list, BmobException e) {
                    if (e == null) {
                        Log.i("bmob","limits："+list.size());
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = list;
                        Log.i("TAG", "sendMessage");
                        addressHandler.sendMessage(msg);
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }else{
            Log.i("TAG", "findInLocation, addressLists.size=" + addressLists.size());
            for(AddressList addressList : addressLists){
                addressListChangeModelList.add(new AddressListChangeModel(addressList.getLocation(), addressList.getDoorNum(),
                        addressList.getName() + "(" + (addressList.getGender() ? "先生" : "女士") + ") " +
                                addressList.getTel()));
            }
            Log.i("TAG", "addressListChangeModelList.size=" + addressListChangeModelList.size());
            AddressListChangeAdapter adapter = new AddressListChangeAdapter(R.layout.item_address, addressListChangeModelList);
            Log.i("TAG", "adapter");
            LinearLayoutManager layoutManager = new LinearLayoutManager(ChooseAddressActivity.this);
            Log.i("TAG", "layoutManager");
            recyclerView.setLayoutManager(layoutManager);
            Log.i("TAG", "setLayoutManager");
            recyclerView.setAdapter(adapter);
            Log.i("TAG", "setAdapter");
        }
    }

    //加载城市选择
    private void initCitySelect(){
        mComparator = new PinyinComparator();
        //设置右侧SideBar触摸监听
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {

            @Override
            public void onLetterChange(String letter) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });

        mDateList = filledData(getResources().getStringArray(R.array.data));
        // 根据a-z进行排序源数据
        Collections.sort(mDateList, mComparator);

        //RecyclerView设置manager
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new CitySortAdapter(this, mDateList, this);
        mRecyclerView.setAdapter(mAdapter);
        mDecoration = new TitleItemDecoration(this, mDateList);
        //如果add两个，那么按照先后顺序，依次渲染。
        mRecyclerView.addItemDecoration(mDecoration);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ChooseAddressActivity.this, DividerItemDecoration.VERTICAL));
    }


    /**
     * 为RecyclerView填充数据
     *
     * @param date
     * @return
     */
    private List<CitySortModel> filledData(String[] date) {
        List<CitySortModel> mSortList = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            CitySortModel sortModel = new CitySortModel();
            sortModel.setName(date[i]);
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
            } else {
                sortModel.setLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    // adapter点击回调函数
    @Override
    public void onItemClick(View view, int position) {
        yourCity.setText(mDateList.get(position).getName());

        positionSelect.setVisibility(View.VISIBLE);
        citySelect.setVisibility(View.GONE);
        yourCityIsClicked = !yourCityIsClicked;
    }
}
