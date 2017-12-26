package com.yixia.videoeditor.adapter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class UtilityAdapter {
	static {
		System.loadLibrary("utility");
	}

	/** 初始化底层库 */
	public static native void FFmpegInit(Object context, String settings);

	/** 获取当前转码时间 */
	public static native int FFmpegVideoGetTransTime(int flag);

	public static native boolean VitamioStartRecord(String yuv, String pcm);

	/** 停止播放器数据的录制 */
	public static native int VitamioStopRecord(int flag);

	/** 获取视频回调指针 */
	public static native int GetVitamioVideoCallbackPointer(int flag);

	/** 获取音频回调指针 */
	public static native int GetVitamioAudioCallbackPointer(int flag);

	/** 获取视频旋转信息 */
	public static native int VideoGetMetadataRotate(String filename);

	public static native int FFmpegRun(String tag, String cmd);

	/** 结束异步执行的ffmpeg */
	public static native void FFmpegKill(String tag);

	public static native boolean FFmpegIsRunning(String tag);

	/** 获取视频信息，相当于调用ffprobe */
	public static native String FFmpegVideoGetInfo(String filename);

	public static native int RenderViewInit(int width, int height);

	public static final int FLIPTYPE_NORMAL = 0x0;
	public static final int FLIPTYPE_HORIZONTAL = 0x1;
	public static final int FLIPTYPE_VERTICAL = 0x2;

	public static native void RenderInputSettings(int inw, int inh, int org, int flip);

	public static final int OUTPUTFORMAT_YUV = 0x1;
	public static final int OUTPUTFORMAT_RGBA = 0x2;
	public static final int OUTPUTFORMAT_MASK_ZIP = 0x4;
	public static final int OUTPUTFORMAT_MASK_NEED_LASTSNAP = 0x8;
	public static final int OUTPUTFORMAT_MASK_HARDWARE_ACC = 0x10;
	public static final int OUTPUTFORMAT_MASK_MP4 = 0x20;

	public static native void RenderOutputSettings(int outw, int outh, int outfps, int format);

	//设置特效
	public static final int FILTERTYPE_FILTER = 0;
	public static final int FILTERTYPE_FRAME = 1;

	public static native void RenderSetFilter(int type, String filter);

	//进行显示
	public static native void RenderStep();

	public static native void RenderDataYuv(byte[] yuv);

	public static native void RenderDataPcm(byte[] pcm);

	public static native int[] RenderGetDataArgb(float alpha);

	public static native boolean RenderOpenOutputFile(String video, String audio);

	public static native void RenderCloseOutputFile();

	public static native boolean RenderIsOutputJobFinish();

	/** 暂停录制 */
	public static native void RenderPause(boolean pause);

	/** 暂停特效 */
	public static void FilterParserPause(boolean pause) {
		if (mAudioTrack != null) {
			if (pause) {
				mAudioTrack.pause();
			} else {
				mAudioTrack.play();
			}
		}
		RenderPause(pause);
	}

	public static native boolean FilterParserInit(String strings, Object surface);

	//查询目前特效处理信息
	public static final int FILTERINFO_PROCESSEDFRAME = 0; //
	public static final int FILTERINFO_CACHEDFRAME = 1; //
	public static final int FILTERINFO_STARTPLAY = 2; //
	public static final int FILTERINFO_PAUSEPLAY = 3; ///<暂停播放
	public static final int FILTERINFO_PROGRESS = 4; ///<当前处理进度
	public static final int FILTERINFO_TOTALMS = 5; //
	public static final int FILTERINFO_CAUSEGC = 6; //

	public static native int FilterParserInfo(int mode);

	/** 停止特效处理 */
	public static native void FilterParserFree();

	public static final int PARSERACTION_INIT = 0; //
	public static final int PARSERACTION_UPDATE = 1; //
	public static final int PARSERACTION_START = 2; //
	public static final int PARSERACTION_STOP = 3; ///<设置停止捕捉
	public static final int PARSERACTION_FREE = 4; ///<释放占用，这时没完成的进度也会被取消
	public static final int PARSERACTION_PROGRESS = 5; //

	/**
	 * 特效处理
	 * 
	 * @param settings
	 * @param actiontype
	 * @return
	 */
	public static native int FilterParserAction(String settings, int actiontype);

	public static native boolean SaveData(String filename, int[] data, int flag);

	private static volatile boolean gInitialized;

	public static boolean	isInitialized(){
		return gInitialized;
	}
	
	public static void initFilterParser() {
		if (!gInitialized) {
			gInitialized = true;
			FilterParserAction("", PARSERACTION_INIT);
		}
	}

	public static void freeFilterParser() {
		gInitialized = false;
		FilterParserAction("", PARSERACTION_FREE);
	}

	public static native int SoundEffect(String inPath, String outPath, float tempoChange, float pitch, int pitchSemitone);

	protected static AudioTrack mAudioTrack;

	@SuppressWarnings("deprecation")
  public static boolean ndkAudioInit() {
		int desiredFrames = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);// * 8;
		//desiredFrames = 101376
		if (mAudioTrack == null) {
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, desiredFrames, AudioTrack.MODE_STREAM);

			// Instantiating AudioTrack can "succeed" without an exception and the track may still be invalid
			// Ref: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioTrack.java
			// Ref: http://developer.android.com/reference/android/media/AudioTrack.html#getState()

			if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
				mAudioTrack = null;
				Log.w("ndkAudio", "Init failed!");
				return false;
			}
			mAudioTrack.play();
		}
		return true;
	}

	/** 底层音频输出 */
	public static void ndkAudioWrite(short[] buffer, int cnt) {
		int limitcount = 100;
		int result;
		for (int i = 0; i < cnt;) {
			limitcount--;
			if (limitcount <= 0) {
				Log.e("ndkAudio", "avoid dead loop");
				break;
			}
			result = mAudioTrack.write(buffer, i, cnt - i);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
				}
			} else {
				Log.w("ndkAudio", "write failed!");
				return;
			}
		}
	}

	public static void ndkAudioQuit() {
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}

	//key
	public static final int NOTIFYKEY_PLAYSTATE = 1;
	public static final int NOTIFYVALUE_BUFFEREMPTY = 0;
	public static final int NOTIFYVALUE_BUFFERFULL = 1;
	public static final int NOTIFYVALUE_PLAYFINISH = 2;
	public static final int NOTIFYVALUE_HAVEERROR = 3;

	/** 底层回调 */
	public static int ndkNotify(int key, int value) {
		if (mOnNativeListener != null) {
			mOnNativeListener.ndkNotify(key, value);
		} else {
			Log.e("ndkNotify", "ndkNotify key:" + key + ", value: " + value);
		}
		return 0;
	}

	/** 注册监听回调 */
	public static void registerNativeListener(OnNativeListener l) {
		mOnNativeListener = l;
	}

	private static OnNativeListener mOnNativeListener;

	/** 底层通知 */
	public static interface OnNativeListener {
		public void ndkNotify(int key, int value);
	}
}
