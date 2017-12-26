package mabeijianxi.camera;

import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;

import mabeijianxi.camera.model.MediaObject.MediaPart;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.Log;
import mabeijianxi.camera.util.StringUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.IOException;

public class FFMpegUtils {

	public static final float AUDIO_VOLUME_HIGH = 1F;
	public static final float AUDIO_VOLUME_MEDIUM = 0.66F;
	public static final float AUDIO_VOLUME_LOW = 0.33F;
	public static final int AUDIO_VOLUME_CLOSE = 0;

	private static final String FFMPEG_COMMAND_LOG_LOGCATE = " -d stdout -loglevel verbose";
	private static final String FFMPEG_COMMAND_VCODEC = " -pix_fmt yuv420p -vcodec libx264 -profile:v baseline -preset ultrafast";

	public static String getLogCommand() {
		if (VCamera.isLog())
			return FFMPEG_COMMAND_LOG_LOGCATE;
		else
			return " -d \"" + VCamera.getVideoCachePath() + VCamera.FFMPEG_LOG_FILENAME_TEMP + "\" -loglevel verbose";
	}

	public static String getVCodecCommand() {
		return FFMPEG_COMMAND_VCODEC;
	}
	public static boolean captureThumbnails(String videoPath, String outputPath, String wh) {
		return captureThumbnails(videoPath, outputPath, wh, "1");
	}
	public static boolean importVideo(MediaPart part, int mWindowWidth, int videoWidth, int videoHeight, int cropX, int cropY, boolean hasAudio) {
		if (part != null && !StringUtils.isEmpty(part.tempPath)) {
			File f = new File(part.tempPath);
			if (f != null && f.exists() && !f.isDirectory()) {
				StringBuffer buffer = new StringBuffer("ffmpeg");

				buffer.append(FFMpegUtils.getLogCommand());

				buffer.append(" -i \"");
				buffer.append(part.tempPath);
				buffer.append("\"");

				int rotation = -1;
				int width = videoWidth, height = videoHeight, cX = cropX, cY = cropY;

				float videoAspectRatio = videoWidth * 1.0F / videoHeight;

				if (DeviceUtils.hasJellyBeanMr1()) {
					MediaMetadataRetriever metadata = new MediaMetadataRetriever();
					metadata.setDataSource(part.tempPath);
					try {
						rotation = Integer.parseInt(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
					} catch (NumberFormatException e) {
						rotation = -1;
					}
				} else {
					rotation = UtilityAdapter.VideoGetMetadataRotate(part.tempPath);
				}

				if (rotation == 90 || rotation == 270) {
					int w = videoWidth;
					width = videoHeight;
					height = w;
				}

				buffer.append(" -vf \"scale=");
				if (width >= height) {
					buffer.append("-1:480");
					float scaleWidth = 480 * videoAspectRatio;
					int viewWidth = (int) (mWindowWidth * videoAspectRatio);
					cX = (int) (scaleWidth * (cropX * 1.0F / viewWidth));
				} else {
					buffer.append("480:-1");
					float scaleHeight = 480 / videoAspectRatio;//857
					int viewHeight = (int) (mWindowWidth / videoAspectRatio);//964
					cY = (int) (scaleHeight * (cropY * 1.0F / viewHeight));//
				}

				buffer.append("[tmp];[tmp]");

				boolean hasRotation = true;
				switch (rotation) {
				case 90:
					buffer.append("transpose=1[transpose];[transpose]");
					break;
				case 270:
					buffer.append("transpose=2[transpose];[transpose]");
					break;
				case 180:
					buffer.append("vflip[vflip];[vflip]hflip[transpose];[transpose]");
					break;
				default:
					hasRotation = false;
					break;
				}

				buffer.append(" crop=480:480:");
				buffer.append(cX);
				buffer.append(":");
				buffer.append(cY);

				buffer.append("\"");

				if (hasRotation) {
					buffer.append(" -metadata:s:v rotate=\"\"");
				}

				buffer.append(" -ss ");
				buffer.append(String.format("%.1f", part.startTime / 1000F));

				buffer.append(" -t ");
				buffer.append(String.format("%.1f", (part.endTime - part.startTime) / 1000F));

				buffer.append(" -an -vcodec rawvideo -f rawvideo -s 480x480 -pix_fmt yuv420p -r 15 \"");
				buffer.append(part.mediaPath);
				buffer.append("\"");

				if (!hasAudio) {
					final byte[] hz = new byte[44100 * 2 * 1 * 1];
					part.prepareAudio();
					try {
						int duration = (int) (part.endTime - part.startTime);
						int forCount = duration / 1000;
						if (forCount > 0) {
							for (int i = 0; i < forCount; i++) {
								part.mCurrentOutputAudio.write(hz);
							}
						}
						if (duration % 1000 != 0) {
							int lastSize = (int) (44100 * 2 * 1 * (duration - forCount * 1000) / 1000F);
							if (lastSize % 2 != 0)
								lastSize++;
							part.mCurrentOutputAudio.write(new byte[lastSize]);
						}
					} catch (IOException e) {
						Log.e("Yixia", "convertImage2Video", e);
					} catch (Exception e) {
						Log.e("Yixia", "convertImage2Video", e);
					}
					part.stop();
				} else {
					buffer.append(" -ss ");
					buffer.append(String.format("%.1f", part.startTime / 1000F));

					buffer.append(" -t ");
					buffer.append(String.format("%.1f", (part.endTime - part.startTime) / 1000F));

					buffer.append(" -vn -acodec pcm_s16le -f s16le -ar 44100 -ac 1 \"");
					buffer.append(part.audioPath);
					buffer.append("\"");
				}

				boolean result = UtilityAdapter.FFmpegRun("", buffer.toString()) == 0;

				if (!result) {
					VCamera.copyFFmpegLog(buffer.toString());
				}
				return result;
			}
		}
		return false;
	}

	public static boolean convertImage2Video(MediaPart part) {
		if (part != null && !StringUtils.isEmpty(part.tempPath)) {
			File f = new File(part.tempPath);
			if (f != null && f.exists() && !f.isDirectory()) {
				int width = 0, height = 0, rotation = -1, cropX = 0, cropY = 0;
				try {
					ExifInterface exif = new ExifInterface(part.tempPath);
					width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
					height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
					rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

				} catch (IOException e) {
					Log.e("Yixia", "convertImage2Video", e);
				}

				StringBuffer scaleBuffer = new StringBuffer();
				if (width > 0 && height > 0) {
					float videoAspectRatio = width * 1.0F / height;

					scaleBuffer.append(" -vf \"scale=");
					if (width > height) {
						scaleBuffer.append("-1:480");
						float scaleWidth = 480 * videoAspectRatio;
						cropX = (int) ((scaleWidth - 480) / 2);
					} else {
						scaleBuffer.append("480:-1");
						float scaleHeight = 480 / videoAspectRatio;
						cropY = (int) ((scaleHeight - 480) / 2);
					}
					scaleBuffer.append("[tmp];[tmp]");
					switch (rotation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						scaleBuffer.append("transpose=1[transpose];[transpose]");
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						scaleBuffer.append("transpose=2[transpose];[transpose]");
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						scaleBuffer.append("vflip[vflip];[vflip]hflip[transpose];[transpose]");
						break;
					}

					scaleBuffer.append(" crop=480:480:");
					scaleBuffer.append(cropX);
					scaleBuffer.append(":");
					scaleBuffer.append(cropY);
					scaleBuffer.append("\"");
				}

				scaleBuffer.append(" -s 480x480");

				String cmd = String.format("ffmpeg %s -y -loop 1 -f image2 -i \"%s\" -vcodec rawvideo -r 15 -t %.1f -f rawvideo %s -pix_fmt yuv420p \"%s\"", FFMpegUtils.getLogCommand(), part.tempPath, part.duration / 1000F, scaleBuffer.toString(), part.mediaPath);
				if (UtilityAdapter.FFmpegRun("", cmd) == 0) {
					final byte[] hz = new byte[44100 * 2 * 1 * (int) (part.duration / 1000F)];
					part.prepareAudio();
					try {
						part.mCurrentOutputAudio.write(hz);
						part.stop();
						return true;
					} catch (IOException e) {
						Log.e("Yixia", "convertImage2Video", e);
					} catch (Exception e) {
						Log.e("Yixia", "convertImage2Video", e);
					}
				} else {
					VCamera.copyFFmpegLog(cmd);
				}
				return true;
			}
		}

		return false;
	}

	public static boolean captureThumbnails(String videoPath, String outputPath, String wh, String ss) {
		//ffmpeg -i /storage/emulated/0/DCIM/04.04.mp4 -s 84x84 -vframes 1 /storage/emulated/0/DCIM/Camera/miaopai/1388843007381.jpg
		//ffmpeg -i eis-sample.mpg -s 40x40 -r 1/5 -vframes 10 %d.jpg
		FileUtils.deleteFile(outputPath);
		if (ss == null)
			ss = "";
		else
			ss = " -ss " + ss;
		String cmd = String.format("ffmpeg -d stdout -loglevel verbose -i \"%s\"%s -s %s -vframes 1 \"%s\"", videoPath, ss, wh, outputPath);
		return UtilityAdapter.FFmpegRun("", cmd) == 0;
	}


}
