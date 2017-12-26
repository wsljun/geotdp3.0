package com.geotdb.compile.view.camera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private Handler handler = new Handler();
    private Context context;
    private SurfaceView camera_surfaceView;
    private SurfaceHolder surfaceholder;
    private View camera_focus_index;
    //    private ImageView camera_flash_view;
    private ImageView camera_flip_view;
    private ImageView camera_action;
    private ImageView camera_image;
    private ImageView camera_svae;
    private ImageView camera_delete;

    private Camera camera;
    private Camera.Parameters parameters = null;
    private CameraParameter cameraParameter;
    private Camera.Size previewSize;
    private Camera.Size pictureSize;
    private List<Camera.Size> picSize;
    private List<Camera.Size> preSize;
    private int mCurrentCameraId = 0; // 1是前置 0是后置
    private String imagePath = "";

    public float surfaceWH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;

        initView();
        initData();
    }

    private void initView() {
        camera_surfaceView = (SurfaceView) findViewById(R.id.camera_surfaceView);
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = manager.getDefaultDisplay();
//        ViewGroup.LayoutParams lp = camera_surfaceView.getLayoutParams();
//        getWindowManager().getDefaultDisplay();
//        lp.width = display.getWidth();
//        lp.height = display.getHeight();
//        camera_surfaceView.setLayoutParams(lp);

        camera_focus_index = (View) findViewById(R.id.camera_focus_index);
//        camera_flash_view = (ImageView) findViewById(R.id.camera_flash_view);
        camera_flip_view = (ImageView) findViewById(R.id.camera_flip_view);
        camera_flip_view.setOnClickListener(flipListener);
        camera_action = (ImageView) findViewById(R.id.camera_action);
        camera_image = (ImageView) findViewById(R.id.camera_image);
        camera_svae = (ImageView) findViewById(R.id.camera_svae);
        camera_svae.setOnClickListener(saveListener);
        camera_delete = (ImageView) findViewById(R.id.camera_delete);
        camera_delete.setOnClickListener(deleteListener);
    }

    View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            camera_action.setEnabled(true);
            hideImage();
            mFinish();
        }
    };

    private void mFinish() {
        Intent intent = new Intent();
        intent.putExtra("imagePath", imagePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            camera_action.setEnabled(true);
            File image = new File(imagePath);
            if (image.exists()) {
                image.delete();
                Toast.makeText(CameraActivity.this, "已删除", Toast.LENGTH_SHORT).show();
            }
            hideImage();
            imagePath = "";
        }
    };

    View.OnClickListener flipListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switchCamera();
        }
    };

    private void initData() {
        surfaceholder = camera_surfaceView.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.setKeepScreenOn(true);// 屏幕常亮
        surfaceholder.addCallback(surfaceCallback);
        camera_surfaceView.setOnTouchListener(this);
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                surfaceWH = (float) camera_surfaceView.getHeight() / (float) camera_surfaceView.getWidth();
                L.e("surfaceWH" + surfaceWH + "--getWidth=" + camera_surfaceView.getWidth() + "--getHeight=" + camera_surfaceView.getHeight());
                camera = Camera.open(); // 打开摄像头
                camera.setPreviewDisplay(surfaceHolder); // 设置用于显示拍照影像的SurfaceHolder对象
                initParameter();
                camera.startPreview(); // 开始预览
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //实现自动对焦
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        initParameter();//实现相机的参数初始化
                        camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                    }
                }

            });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    };

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release(); // 释放照相机
            camera = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (camera != null) {
            camera.release(); // 释放照相机
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            mFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initParameter() {
        parameters = camera.getParameters(); // 获取各项参数
        cameraParameter = CameraParameter.getInstance();
        picSize = parameters.getSupportedPictureSizes();
        preSize = parameters.getSupportedPreviewSizes();
        pictureSize = cameraParameter.getPictureSize(picSize, 320, surfaceWH);
        previewSize = cameraParameter.getPreviewSize(preSize, 320, surfaceWH);

        parameters.setPreviewSize(previewSize.width, previewSize.height); // 设置预览大小
        parameters.setPictureSize(pictureSize.width, pictureSize.height); // 设置保存的图片尺寸
        parameters.setJpegQuality(80); // 设置照片质量
        parameters.set("orientation", "portrait");
        if (mCurrentCameraId == 0) {
            parameters.set("rotation", 90);
        } else {
            parameters.set("rotation", 270);
        }

        L.e("------getCorrectOrientation=" + getCorrectOrientation());

        camera.setDisplayOrientation(getCorrectOrientation());
        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_flip_view:
                switchCamera();
                break;
            case R.id.camera_action:
                takePhoto();
                break;
        }
    }


    //切换摄像头
    private void switchCamera() {
        mCurrentCameraId = (mCurrentCameraId + 1) % Camera.getNumberOfCameras();
        L.e("mCurrentCameraId=" + mCurrentCameraId);
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        try {
            camera = Camera.open(mCurrentCameraId);
            camera.setPreviewDisplay(surfaceholder);
            initParameter();
        } catch (Exception e) {
            L.e(e.getMessage().toString());
            Toast.makeText(context, "未发现相机", Toast.LENGTH_LONG).show();
        }
    }

    //闪光灯设置
    private void turnLight(Camera camera) {

    }

    //拍照
    private void takePhoto() {
        if (camera != null) {
            camera.takePicture(shutterCallback, null, pictureCallback);

        }
    }


    //点快门直接调用
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            showProgressDialog(true);
        }
    };
    //拍照获取那一帧图片字节数组
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            try {
                camera_action.setEnabled(false);
                new SaveThread(bytes).start();
                initParameter();
                camera.startPreview(); // 开始预览
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class SaveThread extends Thread {
        byte[] data;

        public SaveThread(byte[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                imagePath = saveBitmap(data);
                saveHandler.sendMessage(new Message());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    Handler saveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //处理完图片关闭dialog
            dismissProgressDialog();
            //显示图片
            showImage();
        }
    };

    private void showImage() {
        camera_image.setImageURI(Uri.parse(imagePath));
        camera_image.setVisibility(View.VISIBLE);
        camera_delete.setVisibility(View.VISIBLE);
        camera_svae.setVisibility(View.VISIBLE);
    }

    private void hideImage() {
        camera_image.setVisibility(View.GONE);
        camera_delete.setVisibility(View.GONE);
        camera_svae.setVisibility(View.GONE);
    }


    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public String saveBitmap(byte[] data) throws IOException {
//        Date date = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
//        String filename = format.format(date) + ".jpg";
//        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/finger/");
//        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
//            fileFolder.mkdir();
//        }
//        File jpgFile = new File(fileFolder, filename);
        File jpgFile = Common.getOutputMediaFile();
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
        getimage(jpgFile.getAbsolutePath());//压缩图片
        return jpgFile.getAbsolutePath();
    }


    //图片按比例大小压缩方法（根据路径获取图片并压缩）：
    public Bitmap getimage(String srcPath) {
        Log.e("TAG", "getimage");
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
//        float hh = 800f;//这里设置高度为800f
//        float ww = 640f;//这里设置宽度为480f
        float hh = 320f;
        float ww = 240f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return compressImage(bitmap);
    }

    //质量压缩方法：
    private Bitmap compressImage(Bitmap image) {
        Log.e("TAG", "compressImage");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 80;
        while (baos.toByteArray().length / 1024 > 50 && options != 10) {
            options -= 10;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);

        if (mCurrentCameraId != 0) {
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1); // 镜像水平翻转
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                //对焦
                camera.cancelAutoFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(camera_focus_index.getLayoutParams());
        layout.setMargins((int) motionEvent.getX() - 60, (int) motionEvent.getY() - 60, 0, 0);
        camera_focus_index.setLayoutParams(layout);
        camera_focus_index.setVisibility(View.VISIBLE);

        ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(800);
        camera_focus_index.startAnimation(sa);
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                camera_focus_index.setVisibility(View.INVISIBLE);
            }
        }, 800);
        return false;
    }


    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度，防止预览变形
    private int getCorrectOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        L.e("orientationResult=================" + result + "");
        return result;
    }


    MaterialDialog mDialog;

    private void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(context).title("提示").content("处理中...").progress(true, 0).progressIndeterminateStyle(horizontal).build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    private void dismissProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


}
