package com.example.task1.myapplication.db;

import cn.bmob.v3.BmobObject;

/**
 * 积分排名
 */
public class Rank extends BmobObject {
    private int score;
    private String userId;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
