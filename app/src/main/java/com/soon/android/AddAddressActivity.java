package com.soon.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.Address;
import com.soon.android.db.AddressList;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddAddressActivity extends AppCompatActivity {

    @BindView(R.id.add_address_toolbar)
    Toolbar toolbar;

    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.male)
    TextView male;

    @BindView(R.id.female)
    TextView female;

    @BindView(R.id.tel)
    EditText tel;

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.door_number)
    EditText doorNumber;

    @BindView(R.id.add_address_btn)
    Button addAddressBtn;

    private boolean isClick = false;

    private boolean gender = false;

    private String selectAddress = null;

    private Double longitude = 0d;
    private Double latitude = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();
    }

    // 返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    selectAddress = data.getStringExtra("data_return");
                    longitude = data.getDoubleExtra("data_lng",0d);
                    latitude = data.getDoubleExtra("data_lat",0d);
                    Log.d("TAG", "onActivityResult: " + selectAddress);
                    Toast.makeText(AddAddressActivity.this, selectAddress, Toast.LENGTH_SHORT).show();
                }
                break;
            default: break;
        }
    }

    // 活动跳转
    public static void actionStart(Context context){
        Intent intent = new Intent(context, AddAddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 加载界面
    private void initView(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(selectAddress != null){
            location.setText(selectAddress);
        }

        male.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                isClick = true;
                gender = true;
                male.setTextColor(Color.BLUE);
                female.setTextColor(Color.BLACK);
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                isClick = true;
                gender = false;
                female.setTextColor(Color.BLUE);
                male.setTextColor(Color.BLACK);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAddressActivity.this, PoiSelectActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("") || name.getText().toString() == null){
                    Toast.makeText(AddAddressActivity.this, "请填写联系人！", Toast.LENGTH_SHORT).show();
                }else if (!isClick){
                    Toast.makeText(AddAddressActivity.this, "请选择性别！", Toast.LENGTH_SHORT).show();
                }else if (tel.getText().toString().equals("") || tel.getText().toString() == null){
                    Toast.makeText(AddAddressActivity.this, "请填写电话！", Toast.LENGTH_SHORT).show();
                }else if (location.getText().toString().equals("") || location.getText().toString() == null){
                    Toast.makeText(AddAddressActivity.this, "请填写地址！", Toast.LENGTH_SHORT).show();
                }else if (doorNumber.getText().toString().equals("") || doorNumber.getText().toString() == null){
                    Toast.makeText(AddAddressActivity.this, "请填写门牌号！", Toast.LENGTH_SHORT).show();
                }else{
                    final Address address = new Address();
                    BmobUser currentUser = BmobUser.getCurrentUser();
                    address.setUserObjectId(currentUser.getObjectId());
                    address.setName(name.getText().toString());
                    address.setTel(tel.getText().toString());
                    address.setLongitude(longitude);
                    address.setLatitude(latitude);
                    SharedPreferences.Editor editor = getSharedPreferences("locatPosition", Context.MODE_PRIVATE).edit();
                    editor.putFloat("Lng", longitude.floatValue());
                    editor.putFloat("Lat", latitude.floatValue());
                    editor.apply();
                    address.setLocation(location.getText().toString());
                    address.setGender(gender);
                    address.setDoorNum(doorNumber.getText().toString());
                    List<AddressList> addressLists = DataSupport.findAll(AddressList.class);
                    if(addressLists.size() == 0){
                        address.setDefaultAddress(true);
                    }else{
                        address.setDefaultAddress(false);
                    }
                    address.save(new SaveListener(){
                        @Override
                        public void done(Object o, BmobException e) {
                            if(e == null){
                                Toast.makeText(AddAddressActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                AddressList addressList = new AddressList();
                                addressList.clone(address);
                                addressList.save();
                                finish();
                            }else{
                                Toast.makeText(AddAddressActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
