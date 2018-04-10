package com.soon.android.bmobDao;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.soon.android.bmobBean.Address;
import com.soon.android.bmobBean.Store;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by LYH on 2018/3/7.
 */

public class AddressDaoImpl implements AddressDao {

    private List<Address> addressesListData = new ArrayList<>();

    private Handler handler = new Handler(){

        public void  handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    addressesListData = (List<Address>) msg.obj;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void queryByUserObjectId(String userObjectId) {
        BmobQuery<Address> query = new BmobQuery<Address>();
        query.addWhereEqualTo("userObjectId", userObjectId);
        query.setLimit(50);
        query.findObjects(new FindListener<Address>() {
            @Override
            public void done(List<Address> object, BmobException e) {
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
}
