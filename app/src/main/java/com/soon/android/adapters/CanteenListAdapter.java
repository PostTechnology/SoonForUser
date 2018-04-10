package com.soon.android.adapters;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mcxtzhang.lib.AnimShopButton;
import com.soon.android.R;
import com.soon.android.bmobBean.Store;

import java.util.List;

/**
 * Created by LYH on 2018/1/26.
 */

public class CanteenListAdapter extends BaseQuickAdapter<Store, BaseViewHolder> {

    public CanteenListAdapter(int layoutResId, List<Store> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Store item) {
        Glide.with(mContext).load(item.getImage().getFileUrl()).into((ImageView) helper.getView(R.id.canteen_img));
        helper .setText(R.id.canteen_name, item.getName())
                .setRating(R.id.rating_bar, item.getRating())
                .setText(R.id.rating_num, String.valueOf(item.getRating()))
                .setText(R.id.sales_volume, "月售" + String.valueOf(item.getSalesVolume()) + "单")
                //.setText(R.id.dispatch_time, String.valueOf(item.getDispatchTime()) + "分钟")
                .setText(R.id.send_out_price, "起送￥" + String.valueOf(item.getOfferPrice()))
                .setText(R.id.transportation_expense, "配送费" + String.valueOf(item.getDeliveryCost()) + "元");
                //.setText(R.id.distance, String.valueOf(item.getDistance()) + "km");
    }
}
