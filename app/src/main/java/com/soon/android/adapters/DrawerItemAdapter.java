package com.soon.android.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;
import com.soon.android.adapterDataModels.DrawerItemModel;

import java.util.List;

/**
 * Created by LYH on 2018/1/22.
 */

public class DrawerItemAdapter extends BaseQuickAdapter<DrawerItemModel, BaseViewHolder> {

    public DrawerItemAdapter(int layoutResId, List<DrawerItemModel> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DrawerItemModel item) {
        helper.setImageResource(R.id.icon, item.getImageId());
        helper.setText(R.id.title,item.getName());
    }
}
