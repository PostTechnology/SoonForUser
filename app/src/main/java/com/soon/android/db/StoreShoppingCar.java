package com.soon.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by LYH on 2018/3/6.
 */

public class StoreShoppingCar extends DataSupport {

    private int id;

    private String storeObectId;

    private String goodsObectId;

    private String goodsName;

    private float price;

    private float discount;

    private int sum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreObectId() {
        return storeObectId;
    }

    public void setStoreObectId(String storeObectId) {
        this.storeObectId = storeObectId;
    }

    public String getGoodsObectId() {
        return goodsObectId;
    }

    public void setGoodsObectId(String goodsObectId) {
        this.goodsObectId = goodsObectId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
