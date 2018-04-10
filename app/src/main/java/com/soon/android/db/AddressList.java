package com.soon.android.db;

import com.soon.android.bmobBean.Address;

import org.litepal.crud.DataSupport;

/**
 * Created by 84975 on 2018/3/9.
 */

public class AddressList extends DataSupport {

    private String addressId;

    private String userObjectId;

    private String name;

    private Double longitude;

    private String location;

    private Double latitude;

    private Boolean gender;

    private String doorNum;

    private Boolean defaultAddress;

    private String tel;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getDoorNum() {
        return doorNum;
    }

    public void setDoorNum(String doorNum) {
        this.doorNum = doorNum;
    }

    public Boolean getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean clone(Address address){
        setAddressId(address.getObjectId());
        setUserObjectId(address.getUserObjectId());
        setName(address.getName());
        setGender(address.getGender());
        setTel(address.getTel());
        setLongitude(address.getLongitude());
        setLatitude(address.getLatitude());
        setLocation(address.getLocation());
        setDoorNum(address.getDoorNum());
        setDefaultAddress(address.getDefaultAddress());
        return true;
    }
}
