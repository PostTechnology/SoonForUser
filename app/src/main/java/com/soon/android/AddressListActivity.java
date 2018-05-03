package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.soon.android.adapterDataModels.AddressListChangeModel;
import com.soon.android.adapters.AddressListChangeAdapter;
import com.soon.android.bmobBean.Address;
import com.soon.android.db.AddressList;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

public class AddressListActivity extends AppCompatActivity {

    @BindView(R.id.address_list_toolbar)
    Toolbar toolbar;

    @BindView(R.id.address_list)
    RecyclerView recyclerView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    final List<Address> addressList = (List<Address>) msg.obj;
                    for(Address address :  addressList){
                        AddressList data = new AddressList();
                        data.clone(address);
                        data.save();
                        addressListChangeModelList.add(new AddressListChangeModel(address.getLocation(),address.getDoorNum(),
                                address.getName() + "(" + (address.getGender() ? "先生" : "女士") + ") " + address.getTel()));
                    }

                    AddressListChangeAdapter adapter = new AddressListChangeAdapter(R.layout.item_address, addressListChangeModelList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(AddressListActivity.this);
                    adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                            Toast.makeText(AddressListActivity.this, "position: "+ position, Toast.LENGTH_SHORT).show();
                            UpdateAddressActivity.actionStart(AddressListActivity.this, position);
                            return false;
                        }
                    });
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            SharedPreferences.Editor editor = getSharedPreferences("locatPosition", Context.MODE_PRIVATE).edit();
                            editor.putFloat("Lng", addressList.get(position).getLongitude().floatValue());
                            editor.putFloat("Lat", addressList.get(position).getLatitude().floatValue());
                            editor.putString("location", addressList.get(position).getLocation());
                            editor.apply();
                            List<BmobObject> objectList = new ArrayList<BmobObject>();
                            for(int i = 0; i < addressList.size(); i++){
                                Address address = addressList.get(i);
                                address.setDefaultAddress(false);
                                if(position == i){
                                    address.setDefaultAddress(true);
                                }
                                AddressList data = new AddressList();
                                data.clone(address);
                                data.updateAll("addressId = ?",i+"");
                                objectList.add(address);
                            }
                            new BmobBatch().updateBatch(objectList).doBatch(new QueryListListener<BatchResult>() {
                                @Override
                                public void done(List<BatchResult> o, BmobException e) {
                                    if(e==null){
                                        for(int i=0;i<o.size();i++){
                                            BatchResult result = o.get(i);
                                            BmobException ex =result.getError();
                                            if(ex==null){
                                                Log.i("changeAddress", "done: 第"+i+"个数据批量更新成功："+result.getUpdatedAt());
                                            }else{
                                                Log.i("changeAddress", "done: 第"+i+"个数据批量更新失败："+result.getUpdatedAt());
                                            }
                                        }
                                    }else{
                                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
//                            Toast.makeText(AddressListActivity.this, "修改默认地址成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    private List<AddressListChangeModel> addressListChangeModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
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
                AddAddressActivity.actionStart(AddressListActivity.this);
            }
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 活动跳转
    public static void actionStart(Context context){
        Intent intent = new Intent(context, AddressListActivity.class);
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

        addressListChangeModelList.clear();

        final List<AddressList> addressLists = DataSupport.findAll(AddressList.class);
        Bmob.initialize(this, "84aaecd322d3f4afa028222b754f2f98");
        BmobUser currentUser = BmobUser.getCurrentUser();
        final String userid = currentUser.getObjectId();
        if(addressLists.size() == 0){
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
                        handler.sendMessage(msg);
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }else{
            for(AddressList addressList : addressLists){
                addressListChangeModelList.add(new AddressListChangeModel(addressList.getLocation(), addressList.getDoorNum(),
                        addressList.getName() + "(" + (addressList.getGender() ? "先生" : "女士") + ") " +
                                addressList.getTel()));
            }
            AddressListChangeAdapter adapter = new AddressListChangeAdapter(R.layout.item_address, addressListChangeModelList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(AddressListActivity.this);
//            adapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
//                @Override
//                public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
//                    UpdateAddressActivity.actionStart(AddressListActivity.this, position);
//                    return false;
//                }
//            });
            adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    Toast.makeText(AddressListActivity.this, "position: "+ position, Toast.LENGTH_SHORT).show();
                    UpdateAddressActivity.actionStart(AddressListActivity.this, position);
                    return false;
                }
            });
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                    SharedPreferences.Editor editor = getSharedPreferences("locatPosition", Context.MODE_PRIVATE).edit();
                    editor.putFloat("Lng", addressLists.get(position).getLongitude().floatValue());
                    editor.putFloat("Lat", addressLists.get(position).getLatitude().floatValue());
                    editor.putString("location", addressLists.get(position).getLocation());
                    editor.apply();
                    Log.i("bmob","userid：" + userid);
                    BmobQuery<Address> query = new BmobQuery<Address>();
                    query.addWhereEqualTo("userObjectId",userid);
                    query.findObjects(new FindListener<Address>() {
                        @Override
                        public void done(List<Address> list, BmobException e) {
                            if (e == null) {
                                List<BmobObject> objectList = new ArrayList<BmobObject>();
                                for(int i = 0; i < list.size(); i++){
                                    Address address = list.get(i);
                                    address.setDefaultAddress(false);
                                    if(position == i){
                                        address.setDefaultAddress(true);
                                    }
                                    AddressList data = new AddressList();
                                    data.clone(address);
                                    data.updateAll("addressId = ?",i+"");
                                    objectList.add(address);
                                }
                                new BmobBatch().updateBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

                                    @Override
                                    public void done(List<BatchResult> o, BmobException e) {
                                        if(e==null){
                                            for(int i=0;i<o.size();i++){
                                                BatchResult result = o.get(i);
                                                BmobException ex =result.getError();
                                                if(ex==null){
                                                    Log.i("changeAddress", "done: 第"+i+"个数据批量更新成功："+result.getUpdatedAt());
                                                }else{
                                                    Log.i("changeAddress", "done: 第"+i+"个数据批量更新失败："+result.getUpdatedAt());
                                                }
                                            }
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
//                    Toast.makeText(AddressListActivity.this, "修改默认地址成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
//            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    Toast.makeText(AddressListActivity.this, "position: "+ position, Toast.LENGTH_SHORT).show();
//                    UpdateAddressActivity.actionStart(AddressListActivity.this, position);
//                }
//            });
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }
}
