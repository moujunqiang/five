package com.example.task1.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task1.myapplication.db.UserBean;

import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserID;
    /**
     * Clear
     */
    private Button mBtUsenameClear;
    private FrameLayout mUsenameLayout;
    private EditText mPassword;
    /**
     * Clear
     */
    private Button mBtPwdClear;
    private FrameLayout mUsecodeLayout;
    /**
     * 注册
     */
    private Button mLogin;
    private RelativeLayout mLoginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mUserID = (EditText) findViewById(R.id.userID);
        mBtUsenameClear = (Button) findViewById(R.id.bt_usename_clear);
        mBtUsenameClear.setOnClickListener(this);
        mUsenameLayout = (FrameLayout) findViewById(R.id.usename_layout);
        mPassword = (EditText) findViewById(R.id.password);
        mBtPwdClear = (Button) findViewById(R.id.bt_pwd_clear);
        mBtPwdClear.setOnClickListener(this);
        mUsecodeLayout = (FrameLayout) findViewById(R.id.usecode_layout);
        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(this);
        mLoginLayout = (RelativeLayout) findViewById(R.id.login_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.bt_usename_clear:
                mUserID.setText("");
                break;
            case R.id.bt_pwd_clear:
                mPassword.setText("");

                break;
            case R.id.login:

                if (TextUtils.isEmpty(mUserID.getText().toString())) {
                    Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mPassword.getText().toString())) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                String bql = "select * from UserBean where userName = ?";//查询玩家1的地铁跑酷的GameScore信息
                new BmobQuery<UserBean>().doSQLQuery(bql, new SQLQueryListener<UserBean>() {

                    @Override
                    public void done(BmobQueryResult<UserBean> result, BmobException e) {
                        if (e == null) {
                            List<UserBean> list = (List<UserBean>) result.getResults();
                            if (list != null && list.size() > 0) {
                                Toast.makeText(RegisterActivity.this, "该用户已存在", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("smile", "查询成功，无数据返回");
                                register();
                            }
                        } else {
                            Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                        }
                    }
                }, mUserID.getText().toString());
                break;
        }
    }

    public void register() {
        UserBean p2 = new UserBean();
        p2.setPwd(mPassword.getText().toString());
        p2.setUserName(mUserID.getText().toString());
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    finish();
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "创建数据失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}