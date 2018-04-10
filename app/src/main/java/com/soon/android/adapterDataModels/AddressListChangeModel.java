package com.soon.android.adapterDataModels;

/**
 * Created by 84975 on 2018/3/6.
 */

public class AddressListChangeModel {

    private String brief_address;

    private String detailed_address;

    private String brief_user_info;

    public String getBrief_address() {
        return brief_address;
    }

    public AddressListChangeModel(String brief_address, String detailed_address, String brief_user_info) {
        this.brief_address = brief_address;
        this.detailed_address = detailed_address;
        this.brief_user_info = brief_user_info;
    }

    public void setBrief_address(String brief_address) {
        this.brief_address = brief_address;
    }

    public String getDetailed_address() {
        return detailed_address;
    }

    public void setDetailed_address(String detailed_address) {
        this.detailed_address = detailed_address;
    }

    public String getBrief_user_info() {
        return brief_user_info;
    }

    public void setBrief_user_info(String brief_user_info) {
        this.brief_user_info = brief_user_info;
    }
}
