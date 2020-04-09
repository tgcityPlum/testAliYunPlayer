package com.tgcity.testaliyunplayer.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tgcity.testaliyunplayer.R;
import com.tgcity.testaliyunplayer.adapter.PlayListAdapter;
import com.tgcity.testaliyunplayer.bean.PlayData;
import com.tgcity.testaliyunplayer.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TGCity
 * @description 视频播放列表
 */
public class PlayListActivity extends Activity {

    private SurfaceView surfaceView;
    private TextView tvTitle;
    private ImageView ivPause;
    private RecyclerView rvPlayList;

    private AliListPlayer aliYunListPlayer;
    /**
     * -1 初始状态
     * 0 播放状态
     * 1 暂停状态
     */
    private int playStatus = -1;
    /**
     * 视频数组
     */
    private List<PlayData> playDataArrayList = new ArrayList<>();
    private PlayListAdapter playListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play_list);

        initViews();

        initPlayList();

        onLoadData();

        initAliYunPlayer();
    }

    /**
     * 初始化界面views
     */
    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);
        tvTitle = findViewById(R.id.tvTitle);
        ivPause = findViewById(R.id.ivPause);
        rvPlayList = findViewById(R.id.rvPlayList);
    }

    /**
     * 初始化视频列表
     */
    private void initPlayList() {
        rvPlayList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        playListAdapter = new PlayListAdapter(playDataArrayList);
        rvPlayList.setAdapter(playListAdapter);

        playListAdapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PlayData data = playDataArrayList.get(position);
                if (aliYunListPlayer != null && data != null) {
                    if (!data.isChoose()) {
                        tvTitle.setText(data.getTitle());
                        aliYunListPlayer.moveTo(data.getId());
                        for (int i = 0; i < playDataArrayList.size(); i++) {
                            if (playDataArrayList.get(i).isChoose()) {
                                playDataArrayList.get(i).setChoose(false);
                                break;
                            }
                        }
                        playDataArrayList.get(position).setChoose(true);
                        playListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * 模拟加载数据
     */
    private void onLoadData() {

        String data = JsonUtils.getJson(getApplicationContext(), "playdata.json");

        if (!TextUtils.isEmpty(data)) {

            List<PlayData> playDataList = new Gson().fromJson(data, new TypeToken<List<PlayData>>() {
            }.getType());

            if (playDataList.size() > 0) {
                playDataList.get(0).setChoose(true);
                tvTitle.setText(playDataList.get(0).getTitle());

                playDataArrayList.clear();
                playDataArrayList.addAll(playDataList);
                playListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加视频播放器
     */
    private void initAliYunPlayer() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                initPlayer(surfaceHolder);
                initPlayerListener();
                onFirstPlay();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                aliYunListPlayer.redraw();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                aliYunListPlayer.setDisplay(null);
            }
        });
    }

    /**
     * 初始化视频播放器
     *
     * @param surfaceHolder SurfaceHolder
     */
    private void initPlayer(SurfaceHolder surfaceHolder) {
        aliYunListPlayer = AliPlayerFactory.createAliListPlayer(getApplicationContext());
        aliYunListPlayer.setDisplay(surfaceHolder);
        aliYunListPlayer.setPreloadCount(11);
    }

    /**
     * 设置视频监听
     */
    private void initPlayerListener() {
        //完成监听
        aliYunListPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                Toast.makeText(getApplicationContext(), "视频播放完成", Toast.LENGTH_SHORT).show();
            }
        });
        //出错事件
        aliYunListPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                Toast.makeText(getApplicationContext(), "视频加载失败", Toast.LENGTH_SHORT).show();
            }
        });
        //准备成功事件
        aliYunListPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                playStatus = 0;
                Toast.makeText(getApplicationContext(), "视频加载成功", Toast.LENGTH_SHORT).show();
            }
        });
        //缓冲
        aliYunListPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
                Log.e("tgcity", "onLoadingBegin");
            }

            @Override
            public void onLoadingProgress(int i, float v) {
                Log.e("tgcity", "onLoadingProgress ->" + i);
            }

            @Override
            public void onLoadingEnd() {
                Log.e("tgcity", "onLoadingEnd");
            }
        });
    }

    /**
     * 绑定视频数据
     */
    private void onFirstPlay() {

        for (PlayData data : playDataArrayList) {
            aliYunListPlayer.addUrl(data.getUrl(), data.getId());
        }

        aliYunListPlayer.moveTo(playDataArrayList.get(0).getId());

        aliYunListPlayer.getConfig().mMaxDelayTime = 100;
        aliYunListPlayer.setAutoPlay(true);
        aliYunListPlayer.prepare();
        aliYunListPlayer.start();
    }

    /**
     * 返回操作
     *
     * @param view View
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * 播放\暂停操作
     *
     * @param view View
     */
    public void onPlay(View view) {
        if (aliYunListPlayer != null) {
            if (playStatus == 0) {
                //切换暂停
                ivPause.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play));
                aliYunListPlayer.pause();
                playStatus = 1;
            } else if (playStatus == 1) {
                //切换播放
                ivPause.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause));
                aliYunListPlayer.start();
                playStatus = 0;
            } else {
                Toast.makeText(getApplicationContext(), "视频正在加载，请稍等", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 上一步操作
     *
     * @param view View
     */
    public void onPrevious(View view) {
        if (aliYunListPlayer != null) {
            //判断是否是第一个
            if (!aliYunListPlayer.getCurrentUid().equals(playDataArrayList.get(0).getId())) {
                aliYunListPlayer.moveToPrev();
                for (int i = 0; i < playDataArrayList.size(); i++) {
                    if (playDataArrayList.get(i).isChoose()) {
                        playDataArrayList.get(i).setChoose(false);
                        playDataArrayList.get(i - 1).setChoose(true);
                        tvTitle.setText(playDataArrayList.get(i - 1).getTitle());
                        playListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        }
    }

    /**
     * 下一步操作
     *
     * @param view View
     */
    public void onNext(View view) {
        if (aliYunListPlayer != null) {
            //判断是否是最后一个
            if (!aliYunListPlayer.getCurrentUid().equals(playDataArrayList.get(playDataArrayList.size() - 1).getId())) {
                aliYunListPlayer.moveToNext();
                for (int i = 0; i < playDataArrayList.size(); i++) {
                    if (playDataArrayList.get(i).isChoose()) {
                        playDataArrayList.get(i).setChoose(false);
                        playDataArrayList.get(i + 1).setChoose(true);
                        tvTitle.setText(playDataArrayList.get(i + 1).getTitle());
                        playListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (aliYunListPlayer != null) {
            aliYunListPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (aliYunListPlayer != null) {
            aliYunListPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (aliYunListPlayer != null) {
            aliYunListPlayer.release();
        }
    }
}
