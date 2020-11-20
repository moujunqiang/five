package com.example.task1.myapplication;

import android.app.Application;

import cn.bmob.v3.Bmob;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "8723278d4f502f2b279e675df353cb8b");
    }
}
