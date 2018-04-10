package com.soon.android.adapterDataModels;

/**
 * Created by LYH on 2018/1/31.
 */

public class SortListItemModel {

    private String sortName;

    public SortListItemModel() {
    }

    public SortListItemModel(String sortName) {
        this.sortName = sortName;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }
}
