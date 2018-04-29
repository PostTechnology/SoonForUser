package com.soon.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.soon.android.bmobBean.Order;
import com.soon.android.bmobBean.Store;
import com.soon.android.utils.ProgressDialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class EvaluateActivity extends AppCompatActivity {

    private static final String TAG = "EvaluateActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.canteen_img)
    ImageView canteenImg;
    @BindView(R.id.canteen_name)
    TextView canteenName;
    @BindView(R.id.rating_bar)
    MaterialRatingBar ratingBar;
    @BindView(R.id.comment)
    EditText comment;

    private ProgressDialog progressDialog;

    private String orderId;//本订单的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        ButterKnife.bind(this);

        // 初始化toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("评价");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = ProgressDialogUtil.getProgressDialog(EvaluateActivity.this, null, "Waiting...", false);

        Store store = (Store) getIntent().getSerializableExtra("canteen_data");
        canteenName.setText(store.getName());
        orderId = getIntent().getSerializableExtra("orderId").toString();
        Glide.with(MyApplication.getContext()).load(store.getImage().getFileUrl()).into(canteenImg);
    }

    public static void actionStart(Context context, Store store, String orderId) {
        Intent intent = new Intent(context, EvaluateActivity.class);
        intent.putExtra("canteen_data", store);
        intent.putExtra("orderId", orderId);
        context.startActivity(intent);
    }

    public static void actionStartForResultByFragment(Fragment fragment, Store store, String orderId, int requestCode){
        Intent intent = new Intent(fragment.getActivity(), EvaluateActivity.class);
        intent.putExtra("canteen_data", store);
        intent.putExtra("orderId", orderId);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.release:
                progressDialog.show();
                Order order = new Order();
                order.setRating(ratingBar.getRating());
                order.setComment(comment.getText().toString());
                order.update(orderId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        Log.d(TAG, "评价成功");
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_evaluate, menu);
        return true;
    }
}
