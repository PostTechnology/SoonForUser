package com.soon.android.utils;

import android.content.SharedPreferences;

import com.soon.android.MyApplication;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 84975 on 2018/3/6.
 */

public class LoginUtils {

    // 将用户数据保存至本地
    public static void saveUserData(String userid, String username, String password, Boolean vip, String email){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("userData", MODE_PRIVATE).edit();
        editor.putString("userid", userid);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("vip", vip.toString());
        editor.putString("email", email);
        editor.apply();
    }

    // 获取本地用户数据
    public static Map<String, String> login(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("userData", MODE_PRIVATE);
        Map<String, String> userData = new HashMap<>();
        userData.put("userid", preferences.getString("userid",""));
        userData.put("username", preferences.getString("username",""));
        userData.put("password", preferences.getString("password",""));
        userData.put("vip", preferences.getString("vip",""));
        userData.put("email", preferences.getString("email",""));
        return userData;
    }
}
