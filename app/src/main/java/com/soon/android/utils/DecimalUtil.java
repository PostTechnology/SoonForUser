package com.soon.android.utils;

import java.text.DecimalFormat;

/**
 * Created by LYH on 2018/3/7.
 */

public class DecimalUtil {

    public static String decimalForTwo(double price){
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(price);
        return p;
    }
}
