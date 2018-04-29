package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.U_PersonalData;
import com.soon.android.bmobBean.User;
import com.soon.android.customView.FullScreenVideoView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.video_background)
    FullScreenVideoView videoView;

    @BindView(R.id.account)
    EditText account;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.register)
    TextView register;

    @BindView(R.id.clause)
    TextView clause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        // 初始化Bmob
        Bmob.initialize(this, "84aaecd322d3f4afa028222b754f2f98");
    }

    private void initView(){
        // 初始化视频背景
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING：充满屏幕显示，保持比例，如果屏幕比例不对，则进行裁剪
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        });
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.start;
        videoView.setVideoURI(Uri.parse((uri)));
        // 监听播完了重播
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        videoView.start();

        // 初始化登录按钮
        loginButton.getBackground().setAlpha(200);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用Bmob进行用户登录操作
                final String userAccount = account.getText().toString();
                final String userPassword = password.getText().toString();
                final BmobUser user = new User();
                user.setUsername(userAccount);
                user.setPassword(userPassword);
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User o, BmobException e) {
                        if(e == null){
                            BmobQuery<U_PersonalData> query = new BmobQuery<>();
                            query.addWhereEqualTo("userObjectId", user.getObjectId());
                            query.findObjects(new FindListener<U_PersonalData>() {
                                @Override
                                public void done(List<U_PersonalData> list, BmobException e) {
                                    if(e == null){
                                        if(list.size() > 0){
                                            SharedPreferences.Editor editor = getSharedPreferences("userdata",MODE_PRIVATE).edit();
                                            U_PersonalData personalData = list.get(0);
                                            BmobFile bmobFile = personalData.getIcon();
                                            editor.putString("objectId", personalData.getObjectId());
                                            editor.putString("nickname", personalData.getNickname());
                                            editor.putString("sex", personalData.getJob());
                                            editor.putString("icon", bmobFile != null ? bmobFile.getFilename() : "");
                                            try{
                                                editor.putBoolean("sex",personalData.getSex());
                                            }finally {
                                                editor.apply();
                                                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                                bmobFile = personalData.getIcon();
                                                if(bmobFile != null){
                                                    downloadFile(bmobFile);
                                                }
                                                LoginActivity.this.finish();
                                            }
                                        }
                                    }else{
                                        Log.i("bmob","登录失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                            Log.d("TAG", "success");
                        }else{
                            Toast.makeText(LoginActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "error: " + e.getMessage());
                        }
                    }
                });
            }
        });

        // 初始其他控件
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.actionStart(LoginActivity.this);
            }
        });

        String clauseText = "登录即代表阅读并同意<font color='#F83F3C'>服务条款</font>";
        clause.setText(Html.fromHtml(clauseText));
    }

    // 将头像缓存到本地
    private void downloadFile(BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                Toast.makeText(LoginActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    Toast.makeText(LoginActivity.this, "下载成功,保存路径:"+savePath, Toast.LENGTH_SHORT).show();
                    Log.i("Bmob", "done: "+savePath);
                }else{
                    Toast.makeText(LoginActivity.this, "下载失败："+e.getErrorCode()+","+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
