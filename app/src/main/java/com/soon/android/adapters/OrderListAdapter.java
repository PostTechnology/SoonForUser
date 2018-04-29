package com.soon.android.adapters;

import android.view.View;
import android.widget.Button;
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

        Button buyAgain = (Button) helper.getView(R.id.buy_again);
        Button evaluate = (Button) helper.getView(R.id.evaluate);
        Button confirmReceipt = (Button) helper.getView(R.id.confirm_receipt);
        Button pay = (Button) helper.getView(R.id.pay);
        Button cancelOrder = (Button) helper.getView(R.id.cancel_order);
        switch (item.getStatus()){
            case 0:
                helper.setText(R.id.order_status, "待付款");
                buyAgain.setVisibility(View.INVISIBLE);
                evaluate.setVisibility(View.INVISIBLE);
                confirmReceipt.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.VISIBLE);
                cancelOrder.setVisibility(View.VISIBLE);
                break;
            case 1:
                helper.setText(R.id.order_status, "已付款");
                buyAgain.setVisibility(View.INVISIBLE);
                evaluate.setVisibility(View.INVISIBLE);
                confirmReceipt.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.INVISIBLE);
                cancelOrder.setVisibility(View.VISIBLE);
                break;
            case 2:
                helper.setText(R.id.order_status, "已接单");
                buyAgain.setVisibility(View.INVISIBLE);
                evaluate.setVisibility(View.INVISIBLE);
                confirmReceipt.setVisibility(View.VISIBLE);
                pay.setVisibility(View.INVISIBLE);
                cancelOrder.setVisibility(View.VISIBLE);
                break;
            case 3:
                helper.setText(R.id.order_status, "已完成");
                buyAgain.setVisibility(View.VISIBLE);
                if ((item.getComment() == null || item.getComment().equals("")) || item.getRating() == null){
                    evaluate.setVisibility(View.VISIBLE);
                }else{
                    evaluate.setVisibility(View.INVISIBLE);
                }
                confirmReceipt.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.INVISIBLE);
                cancelOrder.setVisibility(View.INVISIBLE);
                break;
            case -1:
                helper.setText(R.id.order_status, "已取消");
                buyAgain.setVisibility(View.INVISIBLE);
                evaluate.setVisibility(View.INVISIBLE);
                confirmReceipt.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.INVISIBLE);
                cancelOrder.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

        helper.addOnClickListener(R.id.buy_again)
                .addOnClickListener(R.id.evaluate)
                .addOnClickListener(R.id.confirm_receipt)
                .addOnClickListener(R.id.pay)
                .addOnClickListener(R.id.cancel_order);
    }
}
