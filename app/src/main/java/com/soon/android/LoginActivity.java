package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.soon.android.bmobBean.User;
import com.soon.android.customView.FullScreenVideoView;
import com.soon.android.utils.LoginUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
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
                BmobUser user = new User();
                user.setUsername(userAccount);
                user.setPassword(userPassword);
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User o, BmobException e) {
                        if(e == null){
                            LoginUtils.saveUserData(o.getObjectId(), userAccount, userPassword, o.getVIP(), o.getEmail());
                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "success");
                            finish();
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

    public static void actionStart(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
