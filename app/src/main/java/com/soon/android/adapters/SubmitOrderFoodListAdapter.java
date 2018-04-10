package com.soon.android.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;
import com.soon.android.db.StoreShoppingCar;
import com.soon.android.utils.DecimalUtil;

import java.util.List;

/**
 * Created by LYH on 2018/3/7.
 */

public class SubmitOrderFoodListAdapter extends BaseQuickAdapter<StoreShoppingCar, BaseViewHolder> {

    public SubmitOrderFoodListAdapter(int layoutResId, List<StoreShoppingCar> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StoreShoppingCar item) {
        helper.setText(R.id.name, item.getGoodsName())
                .setText(R.id.sum, "x" + item.getSum())
                .setText(R.id.discount, item.getDiscount() + "折")
                .setText(R.id.price, "￥" + DecimalUtil.decimalForTwo(item.getPrice() * item.getDiscount() * 0.1));
    }
}
