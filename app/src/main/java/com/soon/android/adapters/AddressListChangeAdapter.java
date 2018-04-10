package com.soon.android.adapters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;
import com.soon.android.adapterDataModels.AddressListChangeModel;

import java.util.List;

/**
 * Created by 84975 on 2018/3/6.
 */

public class AddressListChangeAdapter extends BaseQuickAdapter<AddressListChangeModel, BaseViewHolder> {

    public AddressListChangeAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddressListChangeModel item) {
        helper.setText(R.id.brief_address, item.getBrief_address())
                .setText(R.id.detailed_address, item.getDetailed_address())
                .setText(R.id.brief_user_info, item.getBrief_user_info());
    }
}
