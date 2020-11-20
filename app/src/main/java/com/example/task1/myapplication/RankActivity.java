package com.example.task1.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.task1.myapplication.db.Rank;
import com.example.task1.myapplication.db.UserBean;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

public class RankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ListView mListView = (ListView) findViewById(R.id.list);

        String bql = "select * from Rank  ORDER BY score DESC";//查询玩家1的地铁跑酷的GameScore信息
        new BmobQuery<Rank>().doSQLQuery(bql, new SQLQueryListener<Rank>() {

            @Override
            public void done(BmobQueryResult<Rank> result, BmobException e) {
                if (e == null) {
                    final List<Rank> list = (List<Rank>) result.getResults();
                    if (list != null && list.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RankAdapter mAdapter = new RankAdapter(RankActivity.this, list);
                                mListView.setAdapter(mAdapter);
                            }
                        });


                    } else {

                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });
    }
}