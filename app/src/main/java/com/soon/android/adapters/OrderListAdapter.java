package com.soon.android.adapters;

import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;
import com.soon.android.bmobBean.Order;
import com.soon.android.utils.DecimalUtil;

import java.util.List;

/**
 * Created by LYH on 2018/3/8.
 */

public class OrderListAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {

    public OrderListAdapter(int layoutResId, List<Order> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        helper.setText(R.id.store_name, item.getStoreName())
        .setText(R.id.created_at, item.getCreatedAt())
                .setText(R.id.order_sum_price, "￥" + DecimalUtil.decimalForTwo(item.getSumPrice()));
        switch (item.getStatus()){
            case 0:
                helper.setText(R.id.order_status, "代付款");
                break;
            case 1:
                helper.setText(R.id.order_status, "已付款");
                break;
            case 2:
                helper.setText(R.id.order_status, "已接单");
                break;
            case 3:
                helper.setText(R.id.order_status, "已完成");
                break;
            case -1:
                helper.setText(R.id.order_status, "已取消");
                break;
            default:
                break;
        }

    }
}
