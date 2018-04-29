package com.soon.android.utils;

import com.soon.android.bmobBean.Address;
import com.soon.android.db.StoreShoppingCar;

import java.util.List;

/**
 * Created by LYH on 2018/3/8.
 */

public class JsonUtil {

    public static String toArray(List<StoreShoppingCar> storeShoppingCarList){
        StringBuilder s = new StringBuilder();
        s.append("[");
        for (StoreShoppingCar ssc : storeShoppingCarList){
            s.append("{");
            s.append("id:" + ssc.getId() + ",");
            s.append("storeObectId:" + ssc.getStoreObectId() + ",");
            s.append("goodsObectId:" + ssc.getGoodsObectId() + ",");
            s.append("name:" + ssc.getGoodsName() + ",");
            s.append("price:" + DecimalUtil.decimalForTwo(ssc.getPrice() * ssc.getDiscount() * 0.1) + ",");
            s.append("discount:" + ssc.getDiscount() + ",");
            s.append("sum:" + ssc.getSum());
            s.append("},");
        }
        s.deleteCharAt(s.length() - 1);
        s.append("]");
        return s.toString();
    }

    public static String toString(Address address){
        StringBuilder s = new StringBuilder();
        s.append("{");
        s.append("name:" + address.getName() + ",");
        s.append("tel:" + address.getTel() + ",");
        s.append("location:" + address.getLocation() + ",");
        s.append("doorNum:" + address.getDoorNum());
        s.append("}");
        return s.toString();
    }
}
