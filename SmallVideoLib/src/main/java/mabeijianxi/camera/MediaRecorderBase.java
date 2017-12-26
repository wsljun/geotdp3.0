package mabeijianxi.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import mabeijianxi.camera.model.MediaObject;
import mabeijianxi.camera.model.MediaObject.MediaPart;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.StringUtils;

public abstract class MediaRecorderBase implements Callback, PreviewCallback, IMediaRecorder {
    public static int SMALL_VIDEO_HEIGHT = 360;
    public static int SMALL_VIDEO_WIDTH = 480;


    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_CAMERA_SET_PREVIEW_DISPLAY = 101;
    public static final int MEDIA_ERROR_CAMERA_PREVIEW = 102;
    public static final int MEDIA_ERROR_CAMERA_AUTO_FOCUS = 103;

    public static final int AUDIO_RECORD_ERROR_UNKNOWN = 0;
    public static final int AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT = 1;
    public static final int AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT = 2;
    public static final int AUDIO_RECORD_ERROR_CREATE_FAILED = 3;

    public static final int VIDEO_BITRATE_NORMAL = 1024;
    public static final int VIDEO_BITRATE_MEDIUM = 1536;
    public static final int VIDEO_BITRATE_HIGH = 2048;

    protected static final int MESSAGE_ENCODE_START = 0;
    protected static final int MESSAGE_ENCODE_PROGRESS = 1;
    protected static final int MESSAGE_ENCODE_COMPLETE = 2;
    protected static final int MESSAGE_ENCODE_ERROR = 3;

    protected static int MAX_FRAME_RATE = 20;
    protected static int MIN_FRAME_RATE = 8;

    protected static int CAPTURE_THUMBNAILS_TIME = 1;

    protected static boolean doH264Compress = true;

    protected Camera camera;
    protected Camera.Parameters mParameters = null;
    protected List<Size> mSupportedPreviewSizes;
    protected SurfaceHolder mSurfaceHolder;

    protected AudioRecorder mAudioRecorder;
    protected EncodeHandler mEncodeHanlder;
    protected MediaObject mMediaObject;

    protected OnEncodeListener mOnEncodeListener;
    protected OnErrorListener mOnErrorListener;
    protected OnPreparedListener mOnPreparedListener;

    protected int mFrameRate = MIN_FRAME_RATE;
    protected int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    protected static int mVideoBitrate = 2048;

    protected int mSupportedPreviewWidth = 0;
    protected boolean mPrepared, mStartPreview, mSurfaceCreated;
    protected volatile boolean mRecording;
    protected volatile long mPreviewFrameCallCount = 0;

    public MediaRecorderBase() {

    }

    @SuppressWarnings("deprecation")
    public void setSurfaceHolder(SurfaceHolder sh) {
        if (sh != null) {
            sh.addCallback(this);
            if (!DeviceUtils.hasHoneycomb()) {
                sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }
    }

    public void setOnEncodeListener(OnEncodeListener l) {
        this.mOnEncodeListener = l;
        mEncodeHanlder = new EncodeHandler(this);
    }

    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public boolean isFrontCamera() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isSupportFrontCamera() {
        if (!DeviceUtils.hasGingerbread()) {
            return false;
        }
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        if (2 == numberOfCameras) {
            return true;
        }
        return false;
    }

    public void switchCamera(int cameraFacingFront) {
        switch (cameraFacingFront) {
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                mCameraId = cameraFacingFront;
                stopPreview();
                startPreview();
                break;
        }
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    public boolean autoFocus(AutoFocusCallback cb) {
        if (camera != null) {
            try {
                camera.cancelAutoFocus();

                if (mParameters != null) {
                    String mode = getAutoFocusMode();
                    if (StringUtils.isNotEmpty(mode)) {
                        mParameters.setFocusMode(mode);
                        camera.setParameters(mParameters);
                    }
                }
                camera.autoFocus(cb);
                return true;
            } catch (Exception e) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_AUTO_FOCUS, 0);
                }
                if (e != null)
                    Log.e("Yixia", "autoFocus", e);
            }
        }
        return false;
    }

    private String getAutoFocusMode() {
        if (mParameters != null) {
            List<String> focusModes = mParameters.getSupportedFocusModes();
            if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            } else if (isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            } else if (isSupported(focusModes, "auto")) {
                return "auto";
            }
        }
        return null;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean manualFocus(AutoFocusCallback cb, List<Area> focusAreas) {
        if (camera != null && focusAreas != null && mParameters != null && DeviceUtils.hasICS()) {
            try {
                camera.cancelAutoFocus();
                if (mParameters.getMaxNumFocusAreas() > 0) {
                    mParameters.setFocusAreas(focusAreas);
                }

                if (mParameters.getMaxNumMeteringAreas() > 0)
                    mParameters.setMeteringAreas(focusAreas);

                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                camera.setParameters(mParameters);
                camera.autoFocus(cb);
                return true;
            } catch (Exception e) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_AUTO_FOCUS, 0);
                }
                if (e != null)
                    Log.e("Yixia", "autoFocus", e);
            }
        }
        return false;
    }

    public boolean toggleFlashMode() {
        if (mParameters != null) {
            try {
                final String mode = mParameters.getFlashMode();
                if (TextUtils.isEmpty(mode) || Camera.Parameters.FLASH_MODE_OFF.equals(mode))
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                else
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                return true;
            } catch (Exception e) {
                Log.e("Yixia", "toggleFlashMode", e);
            }
        }
        return false;
    }

    private boolean setFlashMode(String value) {
        if (mParameters != null && camera != null) {
            try {
                if (Camera.Parameters.FLASH_MODE_TORCH.equals(value) || Camera.Parameters.FLASH_MODE_OFF.equals(value)) {
                    mParameters.setFlashMode(value);
                    camera.setParameters(mParameters);
                }
                return true;
            } catch (Exception e) {
                Log.e("Yixia", "setFlashMode", e);
            }
        }
        return false;
    }

    public void setVideoBitRate(int bitRate) {
        if (bitRate > 0)
            mVideoBitrate = bitRate;
    }

    public void prepare() {
        mPrepared = true;
        if (mSurfaceCreated)
            startPreview();
    }

    public MediaObject setOutputDirectory(String key, String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null) {
                if (f.exists()) {
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }

                if (f.mkdirs()) {
                    mMediaObject = new MediaObject(key, path, mVideoBitrate);
                }
            }
        }
        return mMediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mMediaObject = mediaObject;
    }

    public void stopRecord() {
        mRecording = false;

        if (mMediaObject != null) {
            MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.recording) {
                part.recording = false;
                part.endTime = System.currentTimeMillis();
                part.duration = (int) (part.endTime - part.startTime);
                part.cutStartTime = 0;
                part.cutEndTime = part.duration;
            }
        }
    }

    private void stopAllRecord() {
        mRecording = false;
        if (mMediaObject != null && mMediaObject.getMedaParts() != null) {
            for (MediaPart part : mMediaObject.getMedaParts()) {
                if (part != null && part.recording) {
                    part.recording = false;
                    part.endTime = System.currentTimeMillis();
                    part.duration = (int) (part.endTime - part.startTime);
                    part.cutStartTime = 0;
                    part.cutEndTime = part.duration;
                    File videoFile = new File(part.mediaPath);
                    if (videoFile != null && videoFile.length() < 1) {
                        mMediaObject.removePart(part, true);
                    }
                }
            }
        }
    }

    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
    }

    @SuppressWarnings("deprecation")
    protected void prepareCameraParaments() {
        if (mParameters == null)
            return;

        List<Integer> rates = mParameters.getSupportedPreviewFrameRates();
        if (rates != null) {
            if (rates.contains(MAX_FRAME_RATE)) {
                mFrameRate = MAX_FRAME_RATE;
            } else {
                Collections.sort(rates);
                for (int i = rates.size() - 1; i >= 0; i--) {
                    if (rates.get(i) <= MAX_FRAME_RATE) {
                        mFrameRate = rates.get(i);
                        break;
                    }
                }
            }
        }

        mParameters.setPreviewFrameRate(mFrameRate);
        boolean findWidth = false;
        for (int i = 0; i < mSupportedPreviewSizes.size(); i++) {
            Size size = mSupportedPreviewSizes.get(i);
            if (size.height == SMALL_VIDEO_WIDTH) {
                mSupportedPreviewWidth = size.width;
                if (mSupportedPreviewWidth == 720) {
                    mSupportedPreviewWidth = 640;
                }
                findWidth = true;
            }
        }
        if (findWidth) {
            mParameters.setPreviewSize(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH);
        } else {
            new IllegalArgumentException(String.valueOf(R.string.record_video_transcoding_no));
        }

        mParameters.setPreviewFormat(ImageFormat.NV21);

        String mode = getAutoFocusMode();
        if (StringUtils.isNotEmpty(mode)) {
            mParameters.setFocusMode(mode);
        }


        if (isSupported(mParameters.getSupportedWhiteBalance(), "auto"))
            mParameters.setWhiteBalance("auto");

        if ("true".equals(mParameters.get("video-stabilization-supported")))
            mParameters.set("video-stabilization", "true");

        if (!DeviceUtils.isDevice("GT-N7100", "GT-I9308", "GT-I9300")) {
            mParameters.set("cam_mode", 1);
            mParameters.set("cam-mode", 1);
        }
    }

    public void startPreview() {
        if (mStartPreview || mSurfaceHolder == null || !mPrepared)
            return;
        else
            mStartPreview = true;

        try {

            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                camera = Camera.open();
            else
                camera = Camera.open(mCameraId);

            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_SET_PREVIEW_DISPLAY, 0);
                }
                Log.e("Yixia", "setPreviewDisplay fail " + e.getMessage());
            }

            mParameters = camera.getParameters();
            mSupportedPreviewSizes = mParameters.getSupportedPreviewSizes();
            prepareCameraParaments();
            camera.setParameters(mParameters);
            setPreviewCallback();
            camera.startPreview();

            onStartPreviewSuccess();
            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnErrorListener != null) {
                mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_PREVIEW, 0);
            }
            Log.e("Yixia", "startPreview fail :" + e.getMessage());
        }
    }

    protected void onStartPreviewSuccess() {

    }

    protected void setPreviewCallback() {
        Camera.Size size = mParameters.getPreviewSize();
        if (size != null) {
            PixelFormat pf = new PixelFormat();
            PixelFormat.getPixelFormatInfo(mParameters.getPreviewFormat(), pf);
            int buffSize = size.width * size.height * pf.bitsPerPixel / 8;
            try {
                camera.addCallbackBuffer(new byte[buffSize]);
                camera.addCallbackBuffer(new byte[buffSize]);
                camera.addCallbackBuffer(new byte[buffSize]);
                camera.setPreviewCallbackWithBuffer(this);
            } catch (OutOfMemoryError e) {
                Log.e("Yixia", "startPreview...setPreviewCallback...", e);
            }
            Log.e("Yixia", "startPreview...setPreviewCallbackWithBuffer...width:" + size.width + " height:" + size.height);
        } else {
            camera.setPreviewCallback(this);
        }
    }

    public void stopPreview() {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                // camera.lock();
                camera.release();
            } catch (Exception e) {
                Log.e("Yixia", "stopPreview...");
            }
            camera = null;
        }
        mStartPreview = false;
    }

    public void release() {
        stopAllRecord();
        stopPreview();
        if (mAudioRecorder != null) {
            mAudioRecorder.interrupt();
            mAudioRecorder = null;
        }

        mSurfaceHolder = null;
        mPrepared = false;
        mSurfaceCreated = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        this.mSurfaceCreated = true;
        if (mPrepared && !mStartPreview)
            startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        mSurfaceCreated = false;
    }

    @Override
    public void onAudioError(int what, String message) {
        if (mOnErrorListener != null)
            mOnErrorListener.onAudioError(what, message);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mPreviewFrameCallCount++;
        camera.addCallbackBuffer(data);
    }

    public void testPreviewFrameCallCount() {
        new CountDownTimer(1 * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                android.util.Log.e("[Vitamio Recorder]", "testFrameRate..." + mPreviewFrameCallCount);
                mPreviewFrameCallCount = 0;
            }

            @Override
            public void onFinish() {

            }

        }.start();
    }

    @Override
    public void receiveAudioData(byte[] sampleBuffer, int len) {

    }

    public interface OnPreparedListener {
        void onPrepared();
    }

    public interface OnErrorListener {
        void onVideoError(int what, int extra);

        void onAudioError(int what, String message);
    }

    public interface OnEncodeListener {
        void onEncodeStart();

        void onEncodeProgress(int progress);

        void onEncodeComplete();

        void onEncodeError();
    }

    public void startEncoding() {
        if (mMediaObject == null || mEncodeHanlder == null)
            return;

        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_PROGRESS);
        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_COMPLETE);
        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_START);
        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_ERROR);
        mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_START);
    }

    protected void concatVideoParts() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                String cmd = String.format("ffmpeg %s -i \"%s\" -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart \"%s\"", FFMpegUtils.getLogCommand(), mMediaObject.getConcatYUV(), mMediaObject.getOutputTempVideoPath());
                boolean mergeFlag = UtilityAdapter.FFmpegRun("", cmd) == 0;
                if (doH264Compress) {
                    String cmd_transcoding = "ffmpeg -i " + mMediaObject.getOutputTempVideoPath() + " -c:v libx264 -crf 28 -preset:v veryfast -c:a libfdk_aac -vbr 4 " + mMediaObject.getOutputTempTranscodingVideoPath();

                    boolean transcodingFlag = UtilityAdapter.FFmpegRun("", cmd_transcoding) == 0;

                    boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(), SMALL_VIDEO_WIDTH + "x" + SMALL_VIDEO_HEIGHT, String.valueOf(CAPTURE_THUMBNAILS_TIME));

                    FileUtils.deleteCacheFile(mMediaObject.getOutputDirectory());

                    return mergeFlag && captureFlag && transcodingFlag;
                } else {
                    boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempVideoPath(), mMediaObject.getOutputVideoThumbPath(), SMALL_VIDEO_WIDTH + "x" + SMALL_VIDEO_HEIGHT, String.valueOf(CAPTURE_THUMBNAILS_TIME));

                    FileUtils.deleteCacheFile2TS(mMediaObject.getOutputDirectory());

                    return captureFlag && mergeFlag;

                }
            }


            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {

                    mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_COMPLETE);
                } else {
                    mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_ERROR);
                }
            }
        }.execute();
    }

    public static class EncodeHandler extends Handler {

        private WeakReference<MediaRecorderBase> mMediaRecorderBase;

        public EncodeHandler(MediaRecorderBase l) {
            mMediaRecorderBase = new WeakReference<MediaRecorderBase>(l);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaRecorderBase mrb = mMediaRecorderBase.get();
            if (mrb == null || mrb.mOnEncodeListener == null)
                return;
            OnEncodeListener listener = mrb.mOnEncodeListener;
            switch (msg.what) {
                case MESSAGE_ENCODE_START:
                    listener.onEncodeStart();
                    sendEmptyMessage(MESSAGE_ENCODE_PROGRESS);
                    break;
                case MESSAGE_ENCODE_PROGRESS:
                    final int progress = UtilityAdapter.FilterParserAction("", UtilityAdapter.PARSERACTION_PROGRESS);
                    if (progress == 100) {
                        listener.onEncodeProgress(progress);
                        mrb.concatVideoParts();
                    } else if (progress == -1) {
                        sendEmptyMessage(MESSAGE_ENCODE_ERROR);
                    } else {
                        listener.onEncodeProgress(progress);
                        sendEmptyMessageDelayed(MESSAGE_ENCODE_PROGRESS, 200);
                    }
                    break;
                case MESSAGE_ENCODE_COMPLETE:
                    listener.onEncodeComplete();
                    break;
                case MESSAGE_ENCODE_ERROR:
                    listener.onEncodeError();
                    break;
            }
        }
    }

    ;
}
