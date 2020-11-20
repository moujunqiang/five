package com.example.task1.myapplication;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task1.myapplication.db.UserBean;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

public class Main2Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playbgmRing();
        //将布局好的登录界面activity_main2赋给Main2Activity
        setContentView(R.layout.activity_main2);
        Button logIn = (Button) findViewById(R.id.login);
        logIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取用户名输入框这个控件
                EditText editUsername = (EditText) findViewById(R.id.userID);
                //将用户向输入框输入的用户名提取出来保存于nameInLogin
                final String nameInLogin = editUsername.getText().toString();
                //定义一个页面跳转的意图
                final Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                //并向游戏页面传递用户名nameInLogin
                intent.putExtra("editUsername", nameInLogin);
                //获取密码输入框这个控件
                EditText editPassword = (EditText) findViewById(R.id.password);
                //将用户向输入框输入的密码提取出来保存于passwordInLogin
                String passwordInLogin = editPassword.getText().toString();
                //并向游戏页面传递密码passwordInLogin
                intent.putExtra("editPassword", passwordInLogin);
                //如果用户输入的用户名和密码与string.xml中保存的用户名和密码一致，则登录成功，跳转页面
                String bql = "select * from UserBean where userName = ? and pwd=?";//查询玩家1的地铁跑酷的GameScore信息
                new BmobQuery<UserBean>().doSQLQuery(bql, new SQLQueryListener<UserBean>() {

                    @Override
                    public void done(BmobQueryResult<UserBean> result, BmobException e) {
                        if (e == null) {
                            List<UserBean> list = (List<UserBean>) result.getResults();
                            if (list != null && list.size() > 0) {
                                startActivity(intent);//跳转到游戏页面
                                getSharedPreferences("user", MODE_PRIVATE).edit().putString("username", nameInLogin).commit();

                            } else {
                                Toast.makeText(Main2Activity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                        }
                    }
                }, nameInLogin, passwordInLogin);


            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, RegisterActivity.class));
            }
        });
    }

    public AssetManager assetManager;

    public MediaPlayer playbgmRing() {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer();
            assetManager = getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd("backgroundMusic.mp3");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getStartOffset());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }


}
