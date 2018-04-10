package com.soon.android.adapterDataModels;

/**
 * Created by LYH on 2018/1/31.
 */

public class FoodListItemModel {

    private String foodImgRes;

    private String foodName;

    private int salesVolume;

    private String foodInfor;

    private float foodPrice;

    private String sort;

    public FoodListItemModel() {
    }

    public FoodListItemModel(String foodImgRes, String foodName, int salesVolume, String foodInfor, float foodPrice, String sort) {
        this.foodImgRes = foodImgRes;
        this.foodName = foodName;
        this.salesVolume = salesVolume;
        this.foodInfor = foodInfor;
        this.foodPrice = foodPrice;
        this.sort = sort;
    }

    public String getFoodImgRes() {
        return foodImgRes;
    }

    public void setFoodImgRes(String foodImgRes) {
        this.foodImgRes = foodImgRes;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }

    public String getFoodInfor() {
        return foodInfor;
    }

    public void setFoodInfor(String foodInfor) {
        this.foodInfor = foodInfor;
    }

    public float getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(float foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
