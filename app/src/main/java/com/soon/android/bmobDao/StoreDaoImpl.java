package com.soon.android.bmobDao;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.soon.android.MyApplication;
import com.soon.android.bmobBean.Store;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by LYH on 2018/3/5.
 */

public class StoreDaoImpl implements StoreDao {

    private static final int CANTEEN_LIST_DATA = 0;

    private List<Store> stores = new ArrayList<>();

    private Handler handler = new Handler(){

        public void  handleMessage(Message msg){
            switch (msg.what){
                case CANTEEN_LIST_DATA:
                    stores = (List<Store>) msg.obj;
                    Toast.makeText(MyApplication.getContext(), "aaa" + stores.size(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    //根据经纬度查询店家信息
    public List<Store> queryStoreListByLocation(double longitude, double latitude) {
        if (stores != null){
            stores = null;
        }
        BmobQuery<Store> query = new BmobQuery<Store>();
        //查询在某一范围的店家
        query.addWhereLessThan("longitude", longitude + 0.01);
        query.addWhereLessThan("longitude", latitude + 0.01);
        //执行查询方法
        query.findObjects(new FindListener<Store>() {
            @Override
            public void done(List<Store> object, BmobException e) {
                if(e==null){
                    Message message = new Message();
                    message.what = CANTEEN_LIST_DATA;
                    message.obj = object;
                    handler.sendMessage(message);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        //Toast.makeText(MyApplication.getContext(), "123:" + stores.size(), Toast.LENGTH_SHORT).show();
        return stores;
    }

    private void queryStore(){

    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}
