package com.soon.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.Address;
import com.soon.android.db.AddressList;
import com.soon.android.utils.LoginUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateAddressActivity extends AppCompatActivity {

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
    EditText location;

    @BindView(R.id.door_number)
    EditText doorNumber;

    @BindView(R.id.add_address_btn)
    Button addAddressBtn;

    private boolean gender = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_address);
        ButterKnife.bind(this);

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

    // 活动跳转
    public static void actionStart(Context context, int position){
        Intent intent = new Intent(context, UpdateAddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    // 加载界面
    private void initView(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", -1);
        final List<AddressList> addressLists = DataSupport.findAll(AddressList.class);
        AddressList addressList = addressLists.get(position);
        final String addressId = addressList.getAddressId();
        name.setText(addressList.getName());
        tel.setText(addressList.getTel());
        location.setText(addressList.getLocation());
        doorNumber.setText(addressList.getDoorNum());
        if(addressList.getGender()){
            gender = true;
            male.setTextColor(Color.BLUE);
            female.setTextColor(Color.BLACK);
        }else{
            gender = false;
            female.setTextColor(Color.BLUE);
            male.setTextColor(Color.BLACK);
        }

        male.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                gender = true;
                male.setTextColor(Color.BLUE);
                female.setTextColor(Color.BLACK);
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                gender = false;
                female.setTextColor(Color.BLUE);
                male.setTextColor(Color.BLACK);
            }
        });

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("") || name.getText().toString() == null){
                    Toast.makeText(UpdateAddressActivity.this, "请填写联系人！", Toast.LENGTH_SHORT).show();
                }else if (tel.getText().toString().equals("") || tel.getText().toString() == null){
                    Toast.makeText(UpdateAddressActivity.this, "请填写电话！", Toast.LENGTH_SHORT).show();
                }else if (location.getText().toString().equals("") || location.getText().toString() == null){
                    Toast.makeText(UpdateAddressActivity.this, "请填写地址！", Toast.LENGTH_SHORT).show();
                }else if (doorNumber.getText().toString().equals("") || doorNumber.getText().toString() == null){
                    Toast.makeText(UpdateAddressActivity.this, "请填写门牌号！", Toast.LENGTH_SHORT).show();
                }else{
                    final Address address = new Address();
                    address.setUserObjectId(LoginUtils.login().get("userid"));
                    address.setName(name.getText().toString());
                    address.setTel(tel.getText().toString());
//                    address.setLocation();
//                    address.setLatitude();
                    address.setLocation(location.getText().toString());
                    address.setGender(gender);
                    address.setDoorNum(doorNumber.getText().toString());
                    address.update(addressId, new UpdateListener(){
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Toast.makeText(UpdateAddressActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                AddressList addressList = new AddressList();
                                addressList.clone(address);
                                addressList.updateAll("addressId = ?", addressId);
                                finish();
                            }else{
                                Toast.makeText(UpdateAddressActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
