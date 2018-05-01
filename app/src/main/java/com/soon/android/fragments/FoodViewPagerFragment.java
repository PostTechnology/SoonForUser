package com.soon.android.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gavin.com.library.StickyDecoration;
import com.gavin.com.library.listener.GroupListener;
import com.soon.android.MyApplication;
import com.soon.android.R;
import com.soon.android.adapters.FoodListAdapter;
import com.soon.android.adapters.SortListAdapter;
import com.soon.android.bmobBean.Goods;
import com.soon.android.bmobBean.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodViewPagerFragment extends Fragment {

    @BindView(R.id.sort_recycler_view)
    RecyclerView sortRecyclerView;

    @BindView(R.id.food_recycler_view)
    RecyclerView foodRecyclerView;

    private List<String> sortData = new ArrayList<>();

    private List<Goods> foodData = new ArrayList<>();

    private static final int SORT = 1;

    private static final int GOODS = 2;

    private StickyDecoration decoration;

    private Handler handler = new Handler(){

        public void  handleMessage(Message msg){
            switch (msg.what){
                case SORT:
                    for (Goods good : (List<Goods>) msg.obj){
                        sortData.add(good.getSort());
                        loadSort(sortData);
                    }
                    break;
                case GOODS:
                    foodData = (List<Goods>) msg.obj;
                    loadGoods(decoration);
                    break;
                default:
                    break;
            }
        }
    };

    private RelativeLayout bottomRelativeLayout;

    public FoodViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_view_pager, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bottomRelativeLayout = getActivity().findViewById(R.id.bottomRelativeLayout);

        //回调
        GroupListener groupListener = new GroupListener() {
            @Override
            public String getGroupName(int position) {
                //根据position获取对应的组名称
                //Toast.makeText(MyApplication.getContext(), "类别" + foodData.get(position).getSort(), Toast.LENGTH_SHORT).show();
                return foodData.get(position).getSort();
            }
        };
        //创建StickyDecoration，实现悬浮栏
        decoration = StickyDecoration.Builder
                .init(groupListener)
                .setGroupBackground(Color.parseColor("#FFFFFF"))
                .setGroupTextColor(Color.BLACK)//字体颜色
                .setGroupTextSize(40)
                .setDivideColor(Color.parseColor("#CCCCCC"))
                .setDivideHeight(2)
                .setTextSideMargin(10)
                //重置span（使用GridLayoutManager时必须调用）
                //.resetSpan(mRecyclerView, (GridLayoutManager) manager)
                .build();

        Store store = (Store)getActivity().getIntent().getSerializableExtra("canteen_data");
        querySort(store.getObjectId());
        queryGoods(store.getObjectId());
    }

    //从Goods表查询对应id店铺的商品类别
    private void querySort(String storeObjectId){
        //只返回Goods表的对应的店铺id的sort这列的值
        String bql ="select distinct sort from Goods where storeObjectId = ? order by +sort";
        new BmobQuery<Goods>().doSQLQuery(bql,new SQLQueryListener<Goods>(){

            @Override
            public void done(BmobQueryResult<Goods> result, BmobException e) {
                if(e ==null){
                    List<Goods> list = (List<Goods>) result.getResults();
                    if(list!=null && list.size()>0){
                        Message message = new Message();
                        message.what = SORT;
                        message.obj = list;
                        handler.sendMessage(message);
                        Log.i("smile", "查询成功：共" + list.size() + "条数据。");
                    }else{
                        Log.i("smile", "查询成功，无数据返回");
                    }
                }else{
                    Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        }, storeObjectId);
    }

    private void loadSort(List<String> sortData){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        sortRecyclerView.setLayoutManager(layoutManager);
        SortListAdapter adapter = new SortListAdapter(R.layout.sort_list_item, sortData);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == 0){
                    foodRecyclerView.scrollToPosition(0);
                    LinearLayoutManager mLayoutManager =
                            (LinearLayoutManager) foodRecyclerView.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(0, 0);
                }else {
                    foodRecyclerView.scrollToPosition(5);
                    LinearLayoutManager mLayoutManager =
                            (LinearLayoutManager) foodRecyclerView.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(5, 0);
                }
            }
        });
        sortRecyclerView.setAdapter(adapter);
    }

    //从Goods表查询对应id店铺的商品
    private void queryGoods(String storeObjectId){
        String bql ="select * from Goods where storeObjectId = ? order by +sort";
        new BmobQuery<Goods>().doSQLQuery(bql,new SQLQueryListener<Goods>(){

            @Override
            public void done(BmobQueryResult<Goods> result, BmobException e) {
                if(e ==null){
                    List<Goods> list = (List<Goods>) result.getResults();
                    if(list!=null && list.size()>0){
                        Message message = new Message();
                        message.what = GOODS;
                        message.obj = list;
                        handler.sendMessage(message);
                        Log.i("smile", "查询成功：共" + list.size() + "条数据。");
                    }else{
                        Log.i("smile", "查询成功，无数据返回");
                    }
                }else{
                    Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        }, storeObjectId);
    }

    private void loadGoods(StickyDecoration decoration){
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        foodRecyclerView.setLayoutManager(layoutManager1);
        foodRecyclerView.addItemDecoration(decoration);
        FoodListAdapter adapter1 = new FoodListAdapter(R.layout.food_list_item, foodData, bottomRelativeLayout);
        adapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MyApplication.getContext(), "onItemChildClick" + position, Toast.LENGTH_SHORT).show();
            }
        });
        foodRecyclerView.setAdapter(adapter1);
        foodRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager mLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mLayoutManager.findFirstVisibleItemPosition() == 5){
                    sortRecyclerView.getChildAt(1).setBackgroundColor(Color.parseColor(	"#ff0099"));
                }
            }
        });
    }
}
