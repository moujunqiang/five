package com.example.task1.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task1.myapplication.db.Rank;
import com.example.task1.myapplication.db.UserBean;
import com.example.task1.myapplication.game.ComputerAI;
//import com.example.task1.myapplication.game.ComputerAI2;
import com.example.task1.myapplication.game.Coordinate;
import com.example.task1.myapplication.game.Game;
import com.example.task1.myapplication.game.GameConstants;
import com.example.task1.myapplication.game.GameView;
import com.example.task1.myapplication.game.Player;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SingleGameActivity extends Activity implements OnClickListener {
    private static final String TAG = "SingleGameActivity";
    public SingleGameActivity renjiGameAty = this;
    GameView mGameView = null;

    Game mGame;
    Player me;
    Player computer;
    ComputerAI ai;
//    ComputerAI2 ai2;

    // 胜局
    private TextView mBlackWin;
    private TextView mWhiteWin;

    // 当前落子方
    private ImageView mBlackActive;
    private ImageView mWhiteActive;

    // 姓名
    private TextView mBlackName;
    private TextView mWhiteName;
    private TextView showtime;
    private TextView textView;
    // Control Button
    private Button restart;
    private Button rollback;

    private boolean isRollback;
    private int totalCount = 0;
    /**
     * 处理游戏回调信息，刷新界面
     */
    private Handler mComputerHandler;

    /**
     * 处理游戏回调信息，刷新界面
     */
    private Handler mRefreshHandler = new Handler() {

        public void handleMessage(Message msg) {
            Log.d(TAG, "refresh action=" + msg.what);
            switch (msg.what) {
                case GameConstants.GAME_OVER:
                    if (msg.arg1 == Game.BLACK) {
                        showWinDialog("黑方胜！");
                        playCongratsRing();
                        updateRank();

                        me.win();
                    } else if (msg.arg1 == Game.WHITE) {
                        showWinDialog("白方胜！");
                        playFailRing();
                        computer.win();
                    }

                    updateScore(me, computer);
                    break;
                case GameConstants.ACTIVE_CHANGE:
                    //每下一步棋 步数加一  这里是两个人的步数
                    totalCount += 1;
                    updateActive(mGame);
                    break;
                case GameConstants.ADD_CHESS:
                    playPutRing();
                    updateActive(mGame);
                    if (mGame.getActive() == computer.getType()) {
                        mComputerHandler.sendEmptyMessage(0);
                    }
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
        setContentView(R.layout.game_single);
        renjiGameAty = this;
//        int flag=getIntent().getIntExtra("flag",0);
//        Log.d(TAG,"the value of flag is///////");
        initViews();
//        if(flag==2){
        initGame();
        initComputer();
//        }
//        else if(flag==1){
//            initGame2();
//            initComputer2();
//        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews() {
        mGameView = (GameView) findViewById(R.id.game_view);
        mBlackName = (TextView) findViewById(R.id.black_name);
        mBlackWin = (TextView) findViewById(R.id.black_win);
        mBlackActive = (ImageView) findViewById(R.id.black_active);
        mWhiteName = (TextView) findViewById(R.id.white_name);
        mWhiteWin = (TextView) findViewById(R.id.white_win);
        mWhiteActive = (ImageView) findViewById(R.id.white_active);
        restart = (Button) findViewById(R.id.restart);
        rollback = (Button) findViewById(R.id.rollback);
        restart.setOnClickListener(this);
        rollback.setOnClickListener(this);

    }

    private void initGame() {
        me = new Player(getString(R.string.myself), Game.BLACK);
        computer = new Player(getString(R.string.computer), Game.WHITE);
        mGame = new Game(mRefreshHandler, me, computer);
        mGame.setMode(GameConstants.MODE_SINGLE);
        mGameView.setGame(mGame);
        updateActive(mGame);
        updateScore(me, computer);
        ai = new ComputerAI(mGame.getWidth(), mGame.getHeight());
    }
//    private void initGame2(){
//        me = new Player(getString(R.string.myself), Game.BLACK);
//        computer = new Player(getString(R.string.computer), Game.WHITE);
//        mGame = new Game(mRefreshHandler, me, computer);
//        mGame.setMode(GameConstants.MODE_SINGLE);
//        mGameView.setGame(mGame);
//        updateActive(mGame);
//        updateScore(me, computer);
//        ai2 = new ComputerAI2(mGame.getWidth(), mGame.getHeight());
//    }

    private void initComputer() {
        HandlerThread thread = new HandlerThread("computerAi");
        thread.start();
        mComputerHandler = new ComputerHandler(thread.getLooper());
    }
//    private void initComputer2(){
//        HandlerThread thread = new HandlerThread("computerAi");
//        thread.start();
//        mComputerHandler = new ComputerHandler2(thread.getLooper());
//    }

    private void updateActive(Game game) {
        if (game.getActive() == Game.BLACK) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBlackActive.setVisibility(View.VISIBLE);
                    mWhiteActive.setVisibility(View.INVISIBLE);
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBlackActive.setVisibility(View.INVISIBLE);
                    mWhiteActive.setVisibility(View.VISIBLE);
                }
            });

        }
    }

    private void updateScore(Player black, Player white) {
        mBlackWin.setText(black.getWin());
        mWhiteWin.setText(white.getWin());
    }

    @Override
    protected void onDestroy() {
        mComputerHandler.getLooper().quit();
        super.onDestroy();
    }

    private void showWinDialog(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setMessage(message);
        b.setPositiveButton("继续", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGame.reset();
                mGameView.drawGame();
                totalCount = 0;
            }
        });
        b.setNegativeButton("退出", new DialogInterface.OnClickListener() {

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
                updateScore(me, computer);
                mGameView.drawGame();
                break;
            case R.id.rollback:
                if (mGame.getActive() != me.getType()) {
                    isRollback = true;
                } else {
                    rollback();
                }
                break;
            default:
                break;
        }

    }

    private void rollback() {

        boolean rollback = mGame.rollback(2);
        boolean rollback1 = mGame.rollback(2);
        if (!rollback || !rollback1) {
            Toast.makeText(renjiGameAty, "已经不能悔棋了", Toast.LENGTH_SHORT).show();
        }
        updateActive(mGame);
        mGameView.drawGame();
    }


    class ComputerHandler extends Handler {

        public ComputerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            ai.updateValue(mGame.getChessMap());
            Coordinate c = ai.getPosition(mGame.getChessMap());
            mGame.addChess(c, computer);
            mGameView.drawGame();
            if (isRollback) {
                rollback();
                isRollback = false;
            }
        }

    }
//    class ComputerHandler2 extends Handler{
//
//        public ComputerHandler2(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            Coordinate a=mGame.getActions().pollLast();
//            mGame.addChess(a,me);
//            Coordinate c = ai2.normalautomatic(mGame.getChessMap(),a);
//            mGame.addChess(c, computer);
//            mGameView.drawGame();
//            if (isRollback){
//                rollback();
//                isRollback = false;
//            }
//        }
//
//    }

    public AssetManager assetManager;

    public MediaPlayer playCongratsRing() {
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

    public MediaPlayer playFailRing() {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer();
            assetManager = getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd("fail.mp3");
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
