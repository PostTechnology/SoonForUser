package com.soon.android.gson;

/**
 * Created by LYH on 2018/1/29.
 */

public class CanteenInfor {

    private String canteenName;

    private float ratingNum;

    private int salesVolume;

    private int dispatchTime;

    private float sendOutPrice;

    private float transportationExpense;

    private float distance;

    public String getCanteenName() {
        return canteenName;
    }

    public void setCanteenName(String canteenName) {
        this.canteenName = canteenName;
    }

    public float getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(float ratingNum) {
        this.ratingNum = ratingNum;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }

    public int getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(int dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public float getSendOutPrice() {
        return sendOutPrice;
    }

    public void setSendOutPrice(float sendOutPrice) {
        this.sendOutPrice = sendOutPrice;
    }

    public float getTransportationExpense() {
        return transportationExpense;
    }

    public void setTransportationExpense(float transportationExpense) {
        this.transportationExpense = transportationExpense;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
