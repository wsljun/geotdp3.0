package mabeijianxi.camera;

import mabeijianxi.camera.model.MediaObject.MediaPart;
public interface IMediaRecorder {

	public MediaPart startRecord();
	
	public void stopRecord();
	
	public void onAudioError(int what, String message);
	public void receiveAudioData(byte[] sampleBuffer, int len);
}
