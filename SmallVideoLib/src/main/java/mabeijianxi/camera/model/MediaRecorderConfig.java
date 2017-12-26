package mabeijianxi.camera.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jian on 2016/8/25 17:03
 * mabeijianxi@gmail.com
 */
public final class MediaRecorderConfig implements Parcelable {
    private final int RECORD_TIME_MAX;
    private final int RECORD_TIME_MIN;
    private final int SMALL_VIDEO_HEIGHT;
    private final int SMALL_VIDEO_WIDTH;
    private final int MAX_FRAME_RATE;
    private final int MIN_FRAME_RATE;
    private final int VIDEO_BITRATE;
    private final boolean doH264Compress;
    private final int captureThumbnailsTime;

    private MediaRecorderConfig(Buidler buidler) {
        this.RECORD_TIME_MAX = buidler.RECORD_TIME_MAX;
        this.RECORD_TIME_MIN = buidler.RECORD_TIME_MIN;
        this.MAX_FRAME_RATE = buidler.MAX_FRAME_RATE;
        this.captureThumbnailsTime = buidler.captureThumbnailsTime;
        this.MIN_FRAME_RATE = buidler.MIN_FRAME_RATE;
        this.SMALL_VIDEO_HEIGHT = buidler.SMALL_VIDEO_HEIGHT;
        this.SMALL_VIDEO_WIDTH = buidler.SMALL_VIDEO_WIDTH;
        this.VIDEO_BITRATE = buidler.VIDEO_BITRATE;
        this.doH264Compress = buidler.doH264Compress;
    }

    public int getCaptureThumbnailsTime() {
        return captureThumbnailsTime;
    }

    public boolean isDoH264Compress() {
        return doH264Compress;
    }

    public int getMaxFrameRate() {
        return MAX_FRAME_RATE;
    }

    public int getMinFrameRate() {
        return MIN_FRAME_RATE;
    }

    public int getRecordTimeMax() {
        return RECORD_TIME_MAX;
    }

    public int getRecordTimeMin() {
        return RECORD_TIME_MIN;
    }

    public int getSmallVideoHeight() {
        return SMALL_VIDEO_HEIGHT;
    }

    public int getSmallVideoWidth() {
        return SMALL_VIDEO_WIDTH;
    }

    public int getVideoBitrate() {
        return VIDEO_BITRATE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected MediaRecorderConfig(Parcel in) {
        RECORD_TIME_MAX = in.readInt();
        RECORD_TIME_MIN = in.readInt();
        SMALL_VIDEO_HEIGHT = in.readInt();
        SMALL_VIDEO_WIDTH = in.readInt();
        MAX_FRAME_RATE = in.readInt();
        MIN_FRAME_RATE = in.readInt();
        VIDEO_BITRATE = in.readInt();
        doH264Compress = in.readByte() != 0;
        captureThumbnailsTime = in.readInt();
    }

    public static final Creator<MediaRecorderConfig> CREATOR = new Creator<MediaRecorderConfig>() {
        @Override
        public MediaRecorderConfig createFromParcel(Parcel in) {
            return new MediaRecorderConfig(in);
        }

        @Override
        public MediaRecorderConfig[] newArray(int size) {
            return new MediaRecorderConfig[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RECORD_TIME_MAX);
        dest.writeInt(RECORD_TIME_MIN);
        dest.writeInt(SMALL_VIDEO_HEIGHT);
        dest.writeInt(SMALL_VIDEO_WIDTH);
        dest.writeInt(MAX_FRAME_RATE);
        dest.writeInt(MIN_FRAME_RATE);
        dest.writeInt(VIDEO_BITRATE);
        dest.writeByte((byte) (doH264Compress ? 1 : 0));
        dest.writeInt(captureThumbnailsTime);
    }

    public static class Buidler {
        /**
         * 录制时间
         */
        private int RECORD_TIME_MAX = 6 * 1000;
        private int RECORD_TIME_MIN = (int) (1.5f * 1000);
        private int SMALL_VIDEO_HEIGHT = 360;
        private int SMALL_VIDEO_WIDTH = 480;
        private int MAX_FRAME_RATE = 1;
        private int MIN_FRAME_RATE = 1;
        private int VIDEO_BITRATE = 2048;
        private boolean doH264Compress = true;
        private int captureThumbnailsTime = 1;

        public MediaRecorderConfig build() {
            return new MediaRecorderConfig(this);
        }

        public Buidler captureThumbnailsTime(int captureThumbnailsTime) {
            this.captureThumbnailsTime = captureThumbnailsTime;
            return this;
        }

        public Buidler doH264Compress(boolean doH264Compress) {
            this.doH264Compress = doH264Compress;
            return this;
        }

        public Buidler maxFrameRate(int MAX_FRAME_RATE) {
            this.MAX_FRAME_RATE = MAX_FRAME_RATE;
            return this;
        }

        public Buidler minFrameRate(int MIN_FRAME_RATE) {
            this.MIN_FRAME_RATE = MIN_FRAME_RATE;
            return this;
        }

        public Buidler recordTimeMax(int RECORD_TIME_MAX) {
            this.RECORD_TIME_MAX = RECORD_TIME_MAX;
            return this;
        }

        public Buidler recordTimeMin(int RECORD_TIME_MIN) {
            this.RECORD_TIME_MIN = RECORD_TIME_MIN;
            return this;
        }

        public Buidler smallVideoHeight(int SMALL_VIDEO_HEIGHT) {
            this.SMALL_VIDEO_HEIGHT = SMALL_VIDEO_HEIGHT;
            return this;
        }

        public Buidler smallVideoWidth(int SMALL_VIDEO_WIDTH) {
            this.SMALL_VIDEO_WIDTH = SMALL_VIDEO_WIDTH;
            return this;
        }

        public Buidler videoBitrate(int VIDEO_BITRATE) {
            this.VIDEO_BITRATE = VIDEO_BITRATE;
            return this;
        }

    }

}
