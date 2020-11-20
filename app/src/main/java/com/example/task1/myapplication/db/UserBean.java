package com.example.task1.myapplication.db;

import cn.bmob.v3.BmobObject;

public class UserBean  extends BmobObject {
    private String userName;
    private String pwd;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
