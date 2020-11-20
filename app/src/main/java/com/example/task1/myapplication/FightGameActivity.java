package com.example.task1.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task1.myapplication.db.Rank;
import com.example.task1.myapplication.game.Game;
import com.example.task1.myapplication.game.GameConstants;
import com.example.task1.myapplication.game.GameView;
import com.example.task1.myapplication.game.Player;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class FightGameActivity extends Activity implements OnClickListener {

    private static final String TAG = "FightGameActivity";

    GameView mGameView = null;

    Game mGame;
    Player black;
    Player white;

    private TextView mBlackWin;
    private TextView mWhiteWin;

    private ImageView mBlackActive;
    private ImageView mWhiteActive;

    // Control Button
    private Button restart;
    private Button rollback;
    int totalCount = 0;

    private Handler mRefreshHandler = new Handler() {

        public void handleMessage(Message msg) {
            Log.d(TAG, "refresh action=" + msg.what);
            switch (msg.what) {
                case GameConstants.GAME_OVER:
                    if (msg.arg1 == Game.BLACK) {
                        showWinDialog("黑方胜");
                        playRing();
                        updateRank();
                        black.win();
                    } else if (msg.arg1 == Game.WHITE) {
                        showWinDialog("白方胜");
                        playRing();
                        white.win();
                    }
                    updateScore(black, white);
                    break;
                case GameConstants.ADD_CHESS:
                    updateActive(mGame);
                    playPutRing();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 更新rand 积分
     */
    public void updateRank() {
        final String username = getSharedPreferences("user", MODE_PRIVATE).getString("username", "");
        String bql = "select * from Rank where userId = ? ";//查询玩家1的地铁跑酷的GameScore信息
        new BmobQuery<Rank>().doSQLQuery(bql, new SQLQueryListener<Rank>() {

            @Override
            public void done(BmobQueryResult<Rank> result, BmobException e) {
                if (e == null) {
                    List<Rank> list = (List<Rank>) result.getResults();
                    if (list != null && list.size() > 0) {//如果有记录 就更新
                        Rank rank = list.get(0);
                        int score = 0;
                        if (totalCount <= 20) {
                            score = 10;
                        } else if (20 < totalCount && totalCount <= 40) {
                            score = 5;
                        } else {
                            score = 1;
                        }

                        rank.setScore(rank.getScore() + score);
                        rank.update(list.get(0).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                            }
                        });

                    } else {
                        //如果没有记录 就调用插入方法，插入新的数据
                        Rank category = new Rank();
                        category.setScore(1);
                        category.setUserId(username);
                        category.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {

                            }
                        });
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, username);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fight);
        initViews();
        initGame();
    }

    private void initViews() {
        mGameView = (GameView) findViewById(R.id.game_view);
        mBlackWin = (TextView) findViewById(R.id.black_win);
        mBlackActive = (ImageView) findViewById(R.id.black_active);
        mWhiteWin = (TextView) findViewById(R.id.white_win);
        mWhiteActive = (ImageView) findViewById(R.id.white_active);
        restart = (Button) findViewById(R.id.restart);
        rollback = (Button) findViewById(R.id.rollback);
        restart.setOnClickListener(this);
        rollback.setOnClickListener(this);
    }

    private void initGame() {
        black = new Player(Game.BLACK);
        white = new Player(Game.WHITE);
        mGame = new Game(mRefreshHandler, black, white);
        mGame.setMode(GameConstants.MODE_FIGHT);
        mGameView.setGame(mGame);
        updateActive(mGame);
        updateScore(black, white);
    }

    private void updateActive(Game game) {
        if (game.getActive() == Game.BLACK) {
            mBlackActive.setVisibility(View.VISIBLE);
            mWhiteActive.setVisibility(View.INVISIBLE);
        } else {
            mBlackActive.setVisibility(View.INVISIBLE);
            mWhiteActive.setVisibility(View.VISIBLE);
        }
    }

    private void updateScore(Player black, Player white) {
        mBlackWin.setText(black.getWin());
        mWhiteWin.setText(white.getWin());
    }

    private void showWinDialog(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setMessage(message);
        b.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGame.reset();
                mGameView.drawGame();
            }
        });
        b.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        b.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart:
                mGame.reset();
                updateActive(mGame);
                updateScore(black, white);
                mGameView.drawGame();
                break;
            case R.id.rollback:
                boolean rollback = mGame.rollback(1);
                if (!rollback) {
                    Toast.makeText(this, "已经不能悔棋了", Toast.LENGTH_SHORT).show();
                }
                updateActive(mGame);
                mGameView.drawGame();
                break;
            default:
                break;
        }

    }

    public AssetManager assetManager;

    public MediaPlayer playRing() {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer();
            assetManager = getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd("congrats.mp3");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getStartOffset());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    public MediaPlayer playPutRing() {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer();
            assetManager = getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd("put.mp3");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getStartOffset());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

}
