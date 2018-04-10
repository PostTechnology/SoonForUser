package com.soon.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.soon.android.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 84975 on 2018/1/31.
 */

public class LBSUtils {

    private static LocationClient mLocationClient = null;
    private static MyLocationListener myLocationListener = null;

    public static void requestLocation() {
        if(mLocationClient != null){
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(myLocationListener);
            myLocationListener = null;
            mLocationClient = null;
        }
        mLocationClient = new LocationClient(MyApplication.getContext());
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        initLocation();
        mLocationClient.start();    // 开始定位
    }

    private static void initLocation() {
        LocationClientOption option = new LocationClientOption();
//        option.setScanSpan(5000);   // 设置更新当前位置的时间间隔为5000ms
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);   // 设置定位模式
        option.setIsNeedAddress(true);  // 设置需要获取当前位置的详细信息
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
    }

    public static List<String> getMessage(){
        List<String> msg = new ArrayList<>();
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("locatPosition", Context.MODE_PRIVATE);
        String city = preferences.getString("city","");
        String location = preferences.getString("location","");
        msg.add(city);
        msg.add(location);
        return msg;
    }

    private static class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(location.getDistrict()).append(location.getStreet());

            String locationDescribe = location.getLocationDescribe();   // 获取位置描述信息

            SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("locatPosition", Context.MODE_PRIVATE).edit();
            if(location.getCity() == "" || location.getCity() == null){
                editor.putString("city","请选择");
                editor.putString("location","定位失败");
            }else{
                editor.putString("city",location.getCity());
                editor.putString("location",currentPosition.toString() + "(" + locationDescribe + ")");
            }
            editor.apply();
        }
    }

}
