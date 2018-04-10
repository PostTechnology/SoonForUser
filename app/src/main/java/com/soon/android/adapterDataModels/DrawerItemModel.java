package com.soon.android.adapterDataModels;

/**
 * Created by LYH on 2018/1/22.
 */

public class DrawerItemModel {

    private int imageId;

    private String name;

    public DrawerItemModel(){}

    public DrawerItemModel(int imageId, String name){
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
