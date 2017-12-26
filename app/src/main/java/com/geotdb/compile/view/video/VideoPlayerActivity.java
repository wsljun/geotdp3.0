package com.geotdb.compile.view.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.geotdb.compile.R;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.vo.Media;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.MediaRecorderBase;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.StringUtils;
import mabeijianxi.camera.views.SurfaceVideoView;


/**
 * 通用单独播放界面
 *
 * @author tangjun
 */
public class VideoPlayerActivity extends AppCompatActivity implements
        SurfaceVideoView.OnPlayStateListener, OnErrorListener,
        OnPreparedListener, OnClickListener, OnCompletionListener,
        OnInfoListener {

    /**
     * 播放控件
     */
    private SurfaceVideoView mVideoView;
    /**
     * 暂停按钮
     */
    private View mPlayerStatus;
    private View mLoading;

    /**
     * 播放路径
     */
    private String mPath;
    /**
     * 是否需要回复播放
     */
    private boolean mNeedResume;


    private AlertDialog dialog;
    /**
     * 视频文件夹地址
     * 文件夹下有视频和截图
     */
    private String directoryPath;

    private String mediaId;
    private Media media;
    private MediaDao mediaDao;

    /**
     * 进度条
     */
    private SeekBar seekbar;
    /**
     * 定时器
     */
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突


    private boolean isLook = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_video_player);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.video_toolbar);
        toolbar.setTitle("提钻视频");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        //录制完成直接看
        Intent intent = getIntent();
        mPath = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
//        mPath = "/storage/emulated/0/com.geotdb.compile/files/video/picvideo/1478593065617/1478593065617.mp4";
        directoryPath = intent.getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY);
        mediaId = intent.getStringExtra("mediaId");

        L.e("mPath= " + mPath);
        L.e("directoryPath= " + directoryPath);
        L.e("mediaId= " + mediaId);


        if (StringUtils.isEmpty(mPath)) {
            finish();
            return;
        }
        if (StringUtils.isEmpty(directoryPath)) {
            finish();
            return;
        }

        if (!StringUtils.isEmpty(mediaId)) {
            mediaDao = new MediaDao(this);
            media = mediaDao.getMediaByID(mediaId);
            isLook = true;
        }


        mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
        seekbar = (SeekBar) findViewById(R.id.video_seekbar);

        int screenWidth = getScreenWidth(this);
        int videoHight = (int) (screenWidth / (MediaRecorderBase.SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)));
        mVideoView.getLayoutParams().height = videoHight;
        mVideoView.requestLayout();

        mPlayerStatus = findViewById(R.id.play_status);
        mLoading = findViewById(R.id.loading);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
//		mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this);

        mVideoView.setVideoPath(mPath);

        seekbar.setOnSeekBarChangeListener(new MySeekbar());

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isChanging == true) {
                    return;
                } else {
                    seekbar.setProgress(mVideoView.getCurrentPosition());
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 10);

    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    //进度条处理
    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            mVideoView.pause();
            isChanging = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mVideoView.seekTo(seekBar.getProgress());
            isChanging = false;
            L.e("seekBar=" + seekBar.getProgress());
            L.e("getDuration=" + mVideoView.getDuration());
            L.e("getCurrentPosition=" + mVideoView.getCurrentPosition());
        }

    }

    public int getScreenWidth(Activity context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        return W;
    }

    private Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_add_record, menu);
        if (isLook) {
            hiddenEditMenu();
        }
        return true;
    }

    private void hiddenEditMenu() {
        if (null != mMenu) {
            for (int i = 0; i < mMenu.size(); i++) {
                mMenu.getItem(i).setVisible(false);
                mMenu.getItem(i).setEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hesitate();
                return true;
            case R.id.act_save:
                saveData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        if (StringUtils.isEmpty(mediaId)) {
            SPUtils.put(this, "directoryPath", directoryPath);
        } else {
            SPUtils.put(this, "directoryPath", "");
        }
        finish();
    }

    private void hesitate() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SPUtils.put(VideoPlayerActivity.this, "directoryPath", "delete");
                                    FileUtils.deleteDir(getIntent().getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY));
                                    if (!StringUtils.isEmpty(mediaId)) {
                                        media.delete(VideoPlayerActivity.this);
                                        L.e("onClick  mediaId != null");
                                    }
                                    try {
                                        MediaStore.Images.Media.insertImage(getContentResolver(), mPath, "title", "description");
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mPath))));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    finish();
                                }
                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                    .setCancelable(false)
                    .show();
        } else {
            dialog.show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mNeedResume) {
            mNeedResume = false;
            if (mVideoView.isRelease())
                mVideoView.reOpen();
            else
                mVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mNeedResume = true;
                mVideoView.pause();
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        stopTimer();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
        mVideoView.start();
//        new Handler().postDelayed(new Runnable() {
//
//            @SuppressWarnings("deprecation")
//            @Override
//            public void run() {
//                if (DeviceUtils.hasJellyBean()) {
//                    mVideoView.setBackground(null);
//                } else {
//                    mVideoView.setBackgroundDrawable(null);
//                }
//            }
//        }, 300);
        seekbar.setMax(mVideoView.getDuration());//设置进度条
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {// 跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                mVideoView.dispatchKeyEvent(this, event);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (!isFinishing()) {
            // 播放失败
        }
        finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videoview:
                Log.e("TAG", "onClick--videoview");
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        if (!isFinishing())
//            mVideoView.reOpen();
        mVideoView.pause();
        mPlayerStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isLook) {
            SPUtils.put(this, "directoryPath", "");
            finish();
        } else {
            hesitate();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                // 音频和视频数据不正确
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!isFinishing())
                    mVideoView.pause();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing())
                    mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (DeviceUtils.hasJellyBean()) {
                    mVideoView.setBackground(null);
                } else {
                    mVideoView.setBackgroundDrawable(null);
                }
                break;
        }
        return false;
    }

}
