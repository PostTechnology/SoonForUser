package com.soon.android.bmobDao;

import com.soon.android.bmobBean.Store;

import java.util.List;

/**
 * Created by LYH on 2018/3/5.
 */

public interface StoreDao {

    List<Store> queryStoreListByLocation(double longitude, double latitude);
}
