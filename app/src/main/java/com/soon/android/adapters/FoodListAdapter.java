package com.soon.android.adapters;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mcxtzhang.lib.AnimShopButton;
import com.mcxtzhang.lib.IOnAddDelListener;
import com.soon.android.R;
import com.soon.android.adapterDataModels.FoodListItemModel;
import com.soon.android.bmobBean.Goods;
import com.soon.android.db.StoreShoppingCar;
import com.soon.android.utils.DecimalUtil;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by LYH on 2018/1/31.
 */

public class FoodListAdapter extends BaseQuickAdapter<Goods, BaseViewHolder> {

    private RelativeLayout bottomRelativeLayout;

    private ImageView shopCar;

    private TextView closeAccount;

    private TextView chooseSumPriceTV;

    private float chooseSumPrice = 0;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    public FoodListAdapter(int layoutResId, List<Goods> data, RelativeLayout bottomRelativeLayout){
        super(layoutResId, data);
        this.bottomRelativeLayout = bottomRelativeLayout;
        shopCar = (ImageView)bottomRelativeLayout.findViewById(R.id.shopping_car);
        closeAccount = (TextView) bottomRelativeLayout.findViewById(R.id.close_account);
        chooseSumPriceTV = (TextView) bottomRelativeLayout.findViewById(R.id.sum_price);
    }

    @Override
    protected void convert(BaseViewHolder helper, Goods item) {
        final Goods goods = item;
        Glide.with(mContext).load(item.getImageFile().getFileUrl()).into((ImageView) helper.getView(R.id.food_img));
        helper.setText(R.id.food_name, item.getName())
                .setText(R.id.discount, item.getDiscount() + "")
                .setText(R.id.sales_volume, "月售 " + item.getSalesVolume() + " 份")
                .setText(R.id.food_price, "￥" + DecimalUtil.decimalForTwo(item.getPrice() * goods.getDiscount() * 0.1))
                .addOnClickListener(R.id.anim_shop_button);
        AnimShopButton animShopButton = (AnimShopButton)helper.getView(R.id.anim_shop_button);
        if (!item.getStatus()){
            animShopButton.setReplenish(true);
        }

        animShopButton.setOnAddDelListener(new IOnAddDelListener() {
            @Override
            public void onAddSuccess(int i) {
                shopCar.setImageResource(R.drawable.ic_shopping_car_color);
                closeAccount.setBackgroundColor(Color.parseColor("#F83F3C"));
                Toast.makeText(mContext, goods.getPrice() + "", Toast.LENGTH_SHORT).show();
                chooseSumPrice += goods.getPrice() * goods.getDiscount() * 0.1;
                if (chooseSumPrice == 0){
                    chooseSumPriceTV.setText("￥0");
                }else{
                    chooseSumPriceTV.setText("￥" + DecimalUtil.decimalForTwo(chooseSumPrice));
                }
                List<StoreShoppingCar> storeShoppingCarList = DataSupport.where("goodsObectId = ?", goods.getObjectId()).find(StoreShoppingCar.class);
                if (storeShoppingCarList!=null && storeShoppingCarList.size()>0){
                    StoreShoppingCar shoppingCar = new StoreShoppingCar();
                    shoppingCar.setSum(storeShoppingCarList.get(0).getSum() + 1);
                    shoppingCar.updateAll("goodsObectId = ?", storeShoppingCarList.get(0).getGoodsObectId());
                    //Toast.makeText(mContext, shoppingCar.getSum(), Toast.LENGTH_SHORT).show();
                }else{
                    //将商品存储至购物车表
                    StoreShoppingCar shoppingCar = new StoreShoppingCar();
                    shoppingCar.setStoreObectId(goods.getStoreObjectId());
                    shoppingCar.setGoodsObectId(goods.getObjectId());
                    shoppingCar.setGoodsName(goods.getName());
                    shoppingCar.setDiscount(goods.getDiscount());
                    shoppingCar.setPrice(goods.getPrice());
                    shoppingCar.setSum(1);
                    shoppingCar.save();
                }

            }

            @Override
            public void onAddFailed(int i, FailType failType) {

            }

            @Override
            public void onDelSuccess(int i) {
                chooseSumPrice -= goods.getPrice() * goods.getDiscount() * 0.1;

                if (chooseSumPrice == 0){
                    shopCar.setImageResource(R.drawable.ic_shopping_car);
                    closeAccount.setBackgroundColor(Color.parseColor("#cdcdcd"));
                    chooseSumPriceTV.setText("￥0");
                }else{
                    chooseSumPriceTV.setText("￥" + DecimalUtil.decimalForTwo(chooseSumPrice));
                }
                List<StoreShoppingCar> storeShoppingCarList = DataSupport.where("goodsObectId = ?", goods.getObjectId()).find(StoreShoppingCar.class);
                if (storeShoppingCarList!=null && storeShoppingCarList.size()>0){
                    if (storeShoppingCarList.get(0).getSum() > 0){
                        StoreShoppingCar shoppingCar = new StoreShoppingCar();
                        shoppingCar.setSum(storeShoppingCarList.get(0).getSum() - 1);
                        shoppingCar.updateAll("goodsObectId = ?", storeShoppingCarList.get(0).getGoodsObectId());
                    }else{
                        DataSupport.deleteAll(StoreShoppingCar.class, "goodsObectId = ?", goods.getObjectId());
                    }
                }


            }

            @Override
            public void onDelFaild(int i, FailType failType) {

            }
        });
    }
}
