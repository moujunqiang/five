package com.example.task1.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import com.example.task1.myapplication.net.ConnectedService;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.task1.myapplication.net.ConnectConstants.CONNECT_ADD_CHESS;
import static com.example.task1.myapplication.net.ConnectConstants.EQUAL_AGREE;
import static com.example.task1.myapplication.net.ConnectConstants.EQUAL_ASK;
import static com.example.task1.myapplication.net.ConnectConstants.EQUAL_REJECT;
import static com.example.task1.myapplication.net.ConnectConstants.GAME_CONNECTED;
import static com.example.task1.myapplication.net.ConnectConstants.ROLLBACK_AGREE;
import static com.example.task1.myapplication.net.ConnectConstants.ROLLBACK_ASK;
import static com.example.task1.myapplication.net.ConnectConstants.ROLLBACK_REJECT;

public class NetGameActivity extends Activity implements OnClickListener {

    private static final String TAG = "GameActivity";
    GameView mGameView = null;
    Game mGame;
    Player me;
    Player challenger;

    // 胜局
    private TextView mBlackWin;
    private TextView mWhiteWin;

    // 当前落子方
    private ImageView mBlackActive;
    private ImageView mWhiteActive;

    // 姓名
    private TextView mBlackName;
    private TextView mWhiteName;

    // Control Button
    private Button rollback;
    private Button requestEqual;


    // 网络服务
    private ConnectedService mService;
    boolean isServer;

    // 连接等待框
    private ProgressDialog waitDialog;

    private boolean isRequest;
    private boolean isEqual;
    int totalCount = 0;
    /**
     * 处理游戏回调信息，刷新界面
     */
    private Handler mRefreshHandler = new Handler() {

        public void handleMessage(Message msg) {
            Log.d(TAG, "refresh action=" + msg.what);
            switch (msg.what) {
                //游戏结束
                case GameConstants.GAME_OVER:
                    if (msg.arg1 == me.getType()) {
                        showWinDialog("恭喜你！你赢了！");
                        playCongratsRing();
                        updateRank();
                        me.win();
                    } else if (msg.arg1 == challenger.getType()) {
                        showWinDialog("很遗憾！你输了！");
                        //playFailRing();
                        challenger.win();
                    } else {
                        Log.d(TAG, "type=" + msg.arg1);
                    }
                    updateScore(me, challenger);
                    break;
                //落子
                case GameConstants.ADD_CHESS:
                    playPutRing();
                    int x = msg.arg1;
                    int y = msg.arg2;
                    mService.addChess(x, y);
                    updateActive(mGame);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 处理网络信息，更新界面
     */
    private Handler mRequestHandler = new Handler() {

        public void handleMessage(Message msg) {
            Log.d(TAG, "net action=" + msg.what);
            switch (msg.what) {
                //网络连接中断
                case GAME_CONNECTED:
                    waitDialog.dismiss();
                    break;
                //下棋
                case CONNECT_ADD_CHESS:
                    mGame.addChess(msg.arg1, msg.arg2, challenger);
                    mGameView.drawGame();
                    updateActive(mGame);
                    break;
                //悔棋请求
                case ROLLBACK_ASK:
                    showRollbackDialog();
                    break;
                //悔棋请求接受
                case ROLLBACK_AGREE:
                    Toast.makeText(NetGameActivity.this, "对方同意悔棋", Toast.LENGTH_SHORT).show();
                    rollback();
                    isRequest = false;
                    break;
                //悔棋请求拒绝
                case ROLLBACK_REJECT:
                    isRequest = false;
                    Toast.makeText(NetGameActivity.this, "对方拒绝了你的请求", Toast.LENGTH_LONG).show();
                    break;
                //求和请求
                case EQUAL_ASK:
                    showEqualDialog();
                    break;
                //求和请求接受
                case EQUAL_AGREE:
                    Toast.makeText(NetGameActivity.this, "对方同意求和", Toast.LENGTH_SHORT).show();
                    equal();
                    isEqual = false;
                    break;
                //求和请求拒绝
                case EQUAL_REJECT:
                    isEqual = false;
                    Toast.makeText(NetGameActivity.this, "对方拒绝了你的求和", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_net);
        mGameView = (GameView) findViewById(R.id.game_view);
        //初始化五子棋界面
        initViews();
        //初始化游戏准备工作
        initGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        mGameView = (GameView) findViewById(R.id.game_view);
        mBlackName = (TextView) findViewById(R.id.black_name);
        mBlackWin = (TextView) findViewById(R.id.black_win);
        mBlackActive = (ImageView) findViewById(R.id.black_active);
        mWhiteName = (TextView) findViewById(R.id.white_name);
        mWhiteWin = (TextView) findViewById(R.id.white_win);
        mWhiteActive = (ImageView) findViewById(R.id.white_active);
        rollback = (Button) findViewById(R.id.rollback);
        requestEqual = (Button) findViewById(R.id.requestEqual);
        rollback.setOnClickListener(this);
        requestEqual.setOnClickListener(this);

    }

    //初始化游戏初始信息
    private void initGame() {
        //获取之前ConnectionActivity传过来的意图附带信息
        Bundle b = getIntent().getExtras();
        //当该信息为空，表明网络连接出现了问题
        if (b == null) {
            Toast.makeText(this, "建立网络失败,请重试", Toast.LENGTH_SHORT).show();
            finish();
        }
        //否则建立连接
        showProgressDialog(null, "建立连接中，请稍后");
        isServer = b.getBoolean("isServer");
        String ip = b.getString("ip");
        mService = new ConnectedService(mRequestHandler, ip, isServer);
        //谁发起的请求谁就是黑子并且先下棋
        if (isServer) {
            me = new Player(Game.BLACK);
            challenger = new Player(Game.WHITE);
            mBlackName.setText(R.string.myself);
            mWhiteName.setText(R.string.challenger);
        } else {
            me = new Player(Game.WHITE);
            challenger = new Player(Game.BLACK);
            mWhiteName.setText(R.string.myself);
            mBlackName.setText(R.string.challenger);
        }
        mGame = new Game(mRefreshHandler, me, challenger);
        mGame.setMode(GameConstants.MODE_NET);
        mGameView.setGame(mGame);
        updateActive(mGame);
        updateScore(me, challenger);
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

    private void updateScore(Player me, Player challenger) {
        mBlackWin.setText(me.getWin());
        mWhiteWin.setText(challenger.getWin());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.stop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGame.getActive() != me.getType()) {
            return true;
        }
        if (isRequest) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showWinDialog(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(message);
        b.setPositiveButton("继续", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGame.reset();
                mGameView.drawGame();
            }
        });
        b.setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.stop();
                startActivity(new Intent(NetGameActivity.this, ConnectionActivity.class));
            }
        });
        b.show();
    }

    //为悔棋和求和按钮注册监听器
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rollback:
                mService.rollback();
                isRequest = true;
                break;
            case R.id.requestEqual:
                mService.equal();
                isEqual = true;
                break;

            default:
                break;
        }
    }

    //悔棋功能实现
    private void rollback() {
        if (mGame.getActive() == me.getType()) {
            mGame.rollback(1);
        }
        mGame.rollback(1);
        updateActive(mGame);
        mGameView.drawGame();
    }

    private void rollback2() {
        if (mGame.getActive() != me.getType()) {
            boolean rollback = mGame.rollback(1);
            if (!rollback) {
                Toast.makeText(this, "已经不能悔棋了", Toast.LENGTH_SHORT).show();
            }
        }
        boolean rollback = mGame.rollback(1);
        if (!rollback) {
            Toast.makeText(this, "已经不能悔棋了", Toast.LENGTH_SHORT).show();
        }
        updateActive(mGame);
        mGameView.drawGame();
    }

    //求和功能实现
    private void equal() {
        mGame.reset();
        updateActive(mGame);
        updateScore(me, challenger);
        mGameView.drawGame();
    }

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

    // 显示等待框
    private void showProgressDialog(String title, String message) {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        if (!TextUtils.isEmpty(title)) {
            waitDialog.setTitle(title);
        }
        waitDialog.setMessage(message);
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(true);
        waitDialog.show();
    }

    //处理悔棋请求
    private void showRollbackDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("是否同意对方悔棋");
        b.setCancelable(false);
        b.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.agreeRollback();
                rollback2();
            }
        });
        b.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.rejectRollback();
            }
        });
        b.show();
    }

    //处理求和请求
    private void showEqualDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("是否同意对方求和");
        b.setCancelable(false);
        b.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.agreeEqual();
                equal();
            }
        });
        b.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.rejectEqual();
            }
        });
        b.show();
    }

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