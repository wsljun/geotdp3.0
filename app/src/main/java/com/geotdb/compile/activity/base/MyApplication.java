package com.geotdb.compile.activity.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.vo.DropItemVo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.util.DeviceUtils;
import okhttp3.OkHttpClient;


public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {

    private List<DropItemVo> nameList = new ArrayList<>();//用户名集合，用于切换用户的spinner

    public List<DropItemVo> getNameList() {
        return nameList;
    }

    public void setNameList(List<DropItemVo> nameList) {
        this.nameList = nameList;
    }

    private static MyApplication myApplication;

    /**
     * 单例模式中获取唯一的MyApplication实例
     */
    public static MyApplication getInstance() {
        if (null == myApplication) {
            myApplication = new MyApplication();
        }
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        MultiDex.install(this);
        //短视频初始化设置
        initSmallVideo(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        //记录程序崩溃日志

    }

    /**
     * 设置拍摄视频缓存路径
     */
    public static void initSmallVideo(Context context) {

        File dcim = new File(Urls.VIDEO_PATH);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/picvideo/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/picvideo/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/picvideo/");
        }
        VCamera.setDebugMode(true);
        VCamera.initialize(context);
    }


    /**
     * 程序崩溃调用此方法，记录错误日志，保存到本地
     * * @param thread
     *
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        L.e("---->>>uncaughtException");
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        sb.append("Hoast time is  ");
        sb.append(time + " //t");
        sb.append("Version code is  ");
        sb.append(Build.VERSION.SDK_INT + "//t");
        sb.append("Model is  ");
        sb.append(Build.MODEL + "//t");
        sb.append(ex.toString() + "//t");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        sb.append(sw.toString());

        File file = new File(Urls.APP_PATH + "log.log");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] bytes = sb.toString().getBytes();
            os.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
