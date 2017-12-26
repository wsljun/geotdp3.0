package mabeijianxi.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;

import mabeijianxi.camera.model.MediaObject;
import mabeijianxi.camera.model.MediaRecorderConfig;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.Log;
import mabeijianxi.camera.util.StringUtils;
import mabeijianxi.camera.views.ProgressView;

public class MediaRecorderActivity extends Activity implements
        MediaRecorderBase.OnErrorListener, OnClickListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener {

    private static int RECORD_TIME_MAX = 6 * 1000;
    private static int RECORD_TIME_MIN = (int) (1.5f * 1000);
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    private static final int HANDLE_STOP_RECORD = 1;

    private ImageView mTitleNext;
    private CheckBox mCameraSwitch;
    private CheckedTextView mRecordDelete;
    private CheckBox mRecordLed;
    private TextView mRecordController;

    private RelativeLayout mBottomLayout;
    private SurfaceView mSurfaceView;
    private ProgressView mProgressView;

    private MediaRecorderBase mMediaRecorder;
    private MediaObject mMediaObject;

    private volatile boolean mPressedStatus;
    private volatile boolean mReleased;
    public final static String VIDEO_URI = "video_uri";
    public final static String OUTPUT_DIRECTORY = "output_directory";
    public final static String VIDEO_SCREENSHOT = "video_screenshot";
    public final static String OVER_ACTIVITY_NAME = "over_activity_name";
    public final static String MEDIA_RECORDER_MAX_TIME_KEY = "media_recorder_max_time_key";
    public final static String MEDIA_RECORDER_MIN_TIME_KEY = "media_recorder_min_time_key";
    public final static String MEDIA_RECORDER_CONFIG_KEY = "media_recorder_config_key";

    public static void goSmallVideoRecorder(Activity context, String overGOActivityName, MediaRecorderConfig mediaRecorderConfig) {
        context.startActivity(new Intent(context, MediaRecorderActivity.class).putExtra(OVER_ACTIVITY_NAME, overGOActivityName).putExtra(MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        initData();
        loadViews();
    }

    private void initData() {
        Intent intent = getIntent();
        MediaRecorderConfig mediaRecorderConfig = intent.getParcelableExtra(MEDIA_RECORDER_CONFIG_KEY);
        if (mediaRecorderConfig == null) {
            return;
        }
        RECORD_TIME_MAX = mediaRecorderConfig.getRecordTimeMax();
        RECORD_TIME_MIN = mediaRecorderConfig.getRecordTimeMin();
        MediaRecorderBase.MAX_FRAME_RATE = mediaRecorderConfig.getMaxFrameRate();
        MediaRecorderBase.MIN_FRAME_RATE = mediaRecorderConfig.getMinFrameRate();
        MediaRecorderBase.SMALL_VIDEO_HEIGHT = mediaRecorderConfig.getSmallVideoHeight();
        MediaRecorderBase.SMALL_VIDEO_WIDTH = mediaRecorderConfig.getSmallVideoWidth();
        MediaRecorderBase.mVideoBitrate = mediaRecorderConfig.getVideoBitrate();
        MediaRecorderBase.CAPTURE_THUMBNAILS_TIME = mediaRecorderConfig.getCaptureThumbnailsTime();
        MediaRecorderBase.doH264Compress = mediaRecorderConfig.isDoH264Compress();
    }

    private void loadViews() {
        setContentView(R.layout.activity_media_recorder);
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        mCameraSwitch = (CheckBox) findViewById(R.id.record_camera_switcher);
        mTitleNext = (ImageView) findViewById(R.id.title_next);
        mProgressView = (ProgressView) findViewById(R.id.record_progress);
        mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
        mRecordController = (TextView) findViewById(R.id.record_controller);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRecordLed = (CheckBox) findViewById(R.id.record_camera_led);


        mTitleNext.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        mRecordDelete.setOnClickListener(this);
//        mRecordController.setOnTouchListener(mOnVideoControllerTouchListener);
        mRecordController.setOnClickListener(onVideoControllerListener);

        if (MediaRecorderBase.isSupportFrontCamera()) {
            mCameraSwitch.setOnClickListener(this);
        } else {
            mCameraSwitch.setVisibility(View.GONE);
        }
        if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
            mRecordLed.setOnClickListener(this);
        } else {
            mRecordLed.setVisibility(View.GONE);
        }


        mProgressView.setMaxDuration(RECORD_TIME_MAX);
        mProgressView.setMinTime(RECORD_TIME_MIN);
        initSurfaceView();
    }

    private void initSurfaceView() {
        final int w = DeviceUtils.getScreenWidth(this);
        ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = (int) (w / (MediaRecorderBase.SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)));
        int width = w;
        int height = w * 4 / 3;
        //
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
    }

    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        File f = new File(VCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,VCamera.getVideoCachePath() + key);

        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }


    boolean isRecording = false;
    OnClickListener onVideoControllerListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mMediaRecorder == null) {
                Toast.makeText(MediaRecorderActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            } else {
                if (isRecording) {
                    if (mPressedStatus) {
                        isRecording = false;
                        stopRecord();
//                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
//                            mTitleNext.performClick();
//                        }
                        mRecordController.setBackgroundResource(R.drawable.small_video_stop);
                        mRecordController.setTextColor(getResources().getColor(R.color.comera_col_stop));
                        mRecordController.setText(R.string.record_co2);

                    }
                } else {
                    isRecording = true;
//                    if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
//                        mTitleNext.performClick();
//                    }
                    startRecord();
                    mRecordController.setBackgroundResource(R.drawable.small_video_shoot);
                    mRecordController.setTextColor(getResources().getColor(R.color.camera_progress_three));
                    mRecordController.setText(R.string.record_co1);
                }
            }


        }
    };

    private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null) {
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                        Log.e("TAG", "mMediaObject.getDuration() >= RECORD_TIME_MAX");
                        return true;
                    }
                    if (cancelDelete())
                        return true;
                    startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    if (mPressedStatus) {
                        stopRecord();
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            mTitleNext.performClick();
                        }
                    }
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecord();
        UtilityAdapter.freeFilterParser();
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
    }

    private void startRecord() {
        isRecording = true;
        if (mMediaRecorder != null) {

            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }

            if (mMediaRecorder instanceof MediaRecorderSystem) {
                mCameraSwitch.setVisibility(View.GONE);
            }
            mProgressView.setData(mMediaObject);
        }

        mPressedStatus = true;
        mRecordController.animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start();


        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD, RECORD_TIME_MAX - mMediaObject.getDuration());
        }
        mRecordDelete.setVisibility(View.GONE);
        mCameraSwitch.setEnabled(false);
        mRecordLed.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (mRecordDelete != null && mRecordDelete.isChecked()) {
            cancelDelete();
            return;
        }

        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mMediaObject.delete();
                                    finish();
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        finish();
    }

    private void stopRecord() {
        isRecording = false;
        mPressedStatus = false;
        mRecordController.animate().scaleX(1).scaleY(1).setDuration(500).start();

        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }

        mRecordDelete.setVisibility(View.VISIBLE);
        mCameraSwitch.setEnabled(true);
        mRecordLed.setEnabled(true);

        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
            mHandler.removeMessages(HANDLE_STOP_RECORD);
        }

        if (id != R.id.record_delete) {
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mRecordDelete.setChecked(false);
                        if (mProgressView != null)
                            mProgressView.invalidate();
                    }
                }
            }
        }

        if (id == R.id.title_back) {
            onBackPressed();
        } else if (id == R.id.record_camera_switcher) {
            if (mRecordLed.isChecked()) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                mRecordLed.setChecked(false);
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.switchCamera();
            }

            if (mMediaRecorder.isFrontCamera()) {
                mRecordLed.setEnabled(false);
            } else {
                mRecordLed.setEnabled(true);
            }
        } else if (id == R.id.record_camera_led) {
            if (mMediaRecorder != null) {
                if (mMediaRecorder.isFrontCamera()) {
                    return;
                }
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.toggleFlashMode();
            }
        } else if (id == R.id.title_next) {
            Log.e("TAG", "============title_next============" + isRecording);
            if (isRecording) {
                stopRecord();
//                if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
//                    Log.e("TAG", "============title_next__mTitleNext.performClick============");
//                    mTitleNext.performClick();
//                }
            }
            mMediaRecorder.startEncoding();
        } else if (id == R.id.record_delete) {
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mMediaObject.removePart(part, true);
                        mRecordDelete.setChecked(false);
                    } else {
                        part.remove = true;
                        mRecordDelete.setChecked(true);
                    }
                }
                if (mProgressView != null)
                    mProgressView.invalidate();

                checkStatus();
            }
        }
    }


    private boolean cancelDelete() {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
                mRecordDelete.setChecked(false);

                if (mProgressView != null)
                    mProgressView.invalidate();

                return true;
            }
        }
        return false;
    }

    private int checkStatus() {
        int duration = 0;
        if (!isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {
                    mCameraSwitch.setVisibility(View.VISIBLE);
                    mRecordDelete.setVisibility(View.GONE);
                }
                if (mTitleNext.getVisibility() != View.INVISIBLE)
                    mTitleNext.setVisibility(View.INVISIBLE);
            } else {
                if (mTitleNext.getVisibility() != View.VISIBLE) {
                    mTitleNext.setVisibility(View.VISIBLE);
                }
            }
        }
        return duration;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
                        if (mMediaObject != null && mMediaObject.getMedaParts() != null && mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            Log.e("TAG", "============handleMessage >= RECORD_TIME_MAX=============");
                            stopRecord();
                            mTitleNext.performClick();
                            return;
                        }
                        if (mProgressView != null) {
                            mProgressView.invalidate();
                        }
                        if (mPressedStatus) {
                            sendEmptyMessageDelayed(0, 30);
                        }

                    }
                    break;
            }
        }
    };

    @Override
    public void onEncodeStart() {
        Log.e("TAG", "============onEncodeStart=============");
        showProgress("", getString(R.string.record_camera_progress_message));
    }

    @Override
    public void onEncodeProgress(int progress) {
    }

    @Override
    public void onEncodeComplete() {
        hideProgress();
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(getIntent().getStringExtra(OVER_ACTIVITY_NAME)));
            intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, mMediaObject.getOutputDirectory());
            intent.putExtra(MediaRecorderActivity.VIDEO_URI, mMediaObject.getOutputTempTranscodingVideoPath());
            intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, mMediaObject.getOutputVideoThumbPath());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.valueOf(R.string.record_video_transcoding_need));
        }

        finish();
    }

    @Override
    public void onEncodeError() {
        hideProgress();
        Toast.makeText(this, R.string.record_video_transcoding_faild, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {

    }

    public void onFinished() {
        finish();
    }

    protected ProgressDialog mProgressDialog;

    public ProgressDialog showProgress(String title, String message) {
        return showProgress(title, message, -1);
    }

    public ProgressDialog showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        hideProgress();
        mProgressDialog = null;
    }
}
