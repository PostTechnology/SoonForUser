package com.soon.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.U_PersonalData;
import com.soon.android.utils.CameraAlbumUtil;
import com.soon.android.utils.ProgressDialogUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PersonalCenterActivity extends AppCompatActivity {

    @BindView(R.id.add_personal_data_toolbar)
    Toolbar toolbar;

    @BindView(R.id.personal_img)
    ImageView PersonalImg;

    @BindView(R.id.nickname)
    EditText nicknameText;

    @BindView(R.id.male)
    TextView male;

    @BindView(R.id.female)
    TextView female;

    @BindView(R.id.teacher)
    TextView teacher;

    @BindView(R.id.student)
    TextView student;

    @BindView(R.id.other)
    TextView other;

    public static final int TAKE_PHOTO = 3;//拍照的标识

    public static final int CHOOSE_PHOTO = 4;//从相册中选择的标识

    private ProgressDialog progressDialog;// 加载框

    private boolean isUpImg = false;

    private boolean isJobClick = false;

    private String nickname = "";

    private boolean gender = false;

    private String job = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        nicknameText.setText(preferences.getString("nickname", ""));
        String filename = preferences.getString("icon","");
        job = preferences.getString("job", "");
        gender = preferences.getBoolean("sex", false);
        if(!filename.equals("")){
            String imageUrl = Environment.getExternalStorageDirectory() + "/" + filename;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
            PersonalImg.setImageBitmap(bitmap);
        }
        switch (job) {
            case "教师":
                isJobClick = true;
                teacher.setTextColor(Color.BLUE);
                student.setTextColor(Color.BLACK);
                other.setTextColor(Color.BLACK);
                break;
            case "学生":
                isJobClick = true;
                student.setTextColor(Color.BLUE);
                teacher.setTextColor(Color.BLACK);
                other.setTextColor(Color.BLACK);
                break;
            case "其他":
                isJobClick = true;
                other.setTextColor(Color.BLUE);
                student.setTextColor(Color.BLACK);
                teacher.setTextColor(Color.BLACK);
                break;
            default:
                break;
        }
        if (gender) {
            male.setTextColor(Color.BLUE);
            female.setTextColor(Color.BLACK);
        }else{
            female.setTextColor(Color.BLUE);
            male.setTextColor(Color.BLACK);
        }
    }

    @OnClick({R.id.personal_img, R.id.male, R.id.female, R.id.teacher, R.id.student, R.id.other, R.id.add_personal_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_img:
                showDialog();
                break;
            case R.id.male:
                gender = true;
                male.setTextColor(Color.BLUE);
                female.setTextColor(Color.BLACK);
                break;
            case R.id.female:
                gender = false;
                female.setTextColor(Color.BLUE);
                male.setTextColor(Color.BLACK);
                break;
            case R.id.teacher:
                isJobClick = true;
                job = "教师";
                teacher.setTextColor(Color.BLUE);
                student.setTextColor(Color.BLACK);
                other.setTextColor(Color.BLACK);
                break;
            case R.id.student:
                isJobClick = true;
                job = "学生";
                student.setTextColor(Color.BLUE);
                teacher.setTextColor(Color.BLACK);
                other.setTextColor(Color.BLACK);
                break;
            case R.id.other:
                isJobClick = true;
                job = "其他";
                other.setTextColor(Color.BLUE);
                student.setTextColor(Color.BLACK);
                teacher.setTextColor(Color.BLACK);
                break;
            case R.id.add_personal_data:
                nickname = nicknameText.getText().toString();
                if (nickname.equals("") || nickname == null) {
                    Toast.makeText(PersonalCenterActivity.this, "请填写昵称！", Toast.LENGTH_SHORT).show();
                } else if (!isJobClick) {
                    Toast.makeText(PersonalCenterActivity.this, "请选择职业！", Toast.LENGTH_SHORT).show();
                } else {
                    updatePersonalData(nickname);
                    finish();
                }
                break;
        }
    }

    //显示列表对话框
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择上传方式")
                .setItems(new String[]{"从相册中选择", "拍照上传"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isUpImg = true;
                        switch (i) {
                            case 0://从相册中选择
                                CameraAlbumUtil.requestPermissions(PersonalCenterActivity.this, CHOOSE_PHOTO);
                                break;
                            case 1://拍照上传
                                CameraAlbumUtil.takePhote(PersonalCenterActivity.this, TAKE_PHOTO);
                                break;
                            default:
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraAlbumUtil.openAlbum(PersonalCenterActivity.this, CHOOSE_PHOTO);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void updatePersonalData(final String nickname) {
        if (isUpImg) {
            String picPath = CameraAlbumUtil.imagePath;
            Toast.makeText(this, picPath, Toast.LENGTH_SHORT).show();

            final BmobFile bmobFile = new BmobFile(new File(picPath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        Log.d("TAG", "上传文件成功:" + bmobFile.getFileUrl());
                        progressDialog = ProgressDialogUtil.getProgressDialog(PersonalCenterActivity.this, "提交信息", "Waiting...", false);
                        progressDialog.show();
                        SharedPreferences preferences = getSharedPreferences("userdata", Context.MODE_PRIVATE);
                        String objectId = preferences.getString("objectId", "");
                        final U_PersonalData personalData = new U_PersonalData();
                        personalData.setNickname(nickname);
                        personalData.setJob(job);
                        personalData.setSex(gender);
                        personalData.setIcon(bmobFile);
                        personalData.update(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                                            editor.putString("nickname", personalData.getNickname());
                                            editor.putString("sex", personalData.getJob());
                                            editor.putString("icon", bmobFile != null ? bmobFile.getFilename() : "");
                                            try {
                                                editor.putBoolean("sex", personalData.getSex());
                                            } finally {
                                                editor.apply();
                                            }
                                            Toast.makeText(PersonalCenterActivity.this, "更新成功!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                }
                            }
                        });
                    } else {
                        Log.d("TAG", "上传文件失败：" + e.getMessage());
                    }
                }

                @Override
                public void onProgress(Integer value) {
                    super.onProgress(value);
                    Log.d("TAG", "onProgress: " + value);
                }
            });
        } else {
            progressDialog = ProgressDialogUtil.getProgressDialog(PersonalCenterActivity.this, "提交信息", "Waiting...", false);
            progressDialog.show();
            SharedPreferences preferences = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String objectId = preferences.getString("objectId", "");
            final U_PersonalData personalData = new U_PersonalData();
            personalData.setNickname(nickname);
            personalData.setJob(job);
            personalData.setSex(gender);
            personalData.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                                editor.putString("nickname", personalData.getNickname());
                                editor.putString("sex", personalData.getJob());
                                try {
                                    editor.putBoolean("sex", personalData.getSex());
                                } finally {
                                    editor.apply();
                                }
                                Toast.makeText(PersonalCenterActivity.this, "更新成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }
    }

    // 返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 活动跳转
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
