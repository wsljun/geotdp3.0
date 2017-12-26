package com.geotdb.compile.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.utils.ToastUtil;

import java.io.File;

/**
 * Created by Administrator on 2017/2/8.
 */
public class DownloadService extends Service {
    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;
    private long requestId;
    //百度音乐测试地址
    String testLink = "http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initDownManager(Urls.Download_APK);
        L.e("DownloadService---onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化下载器
     **/
    private void initDownManager(String downloadLink) {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(downloadLink));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "gcdz.apk");
//        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "member.apk");
        // 将下载请求放入队列
        requestId = manager.enqueue(down);
        SPUtils.put(this, "requestId", requestId + "");
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    // 接受下载完成后的intent
    public class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (requestId == downId) {
                    L.e("long----" + downId);
                    //自动安装apk
//                    installAPK(manager.getUriForDownloadedFile(downId));
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    File apkFile = queryDownloadedApk(context);
                    if (apkFile != null) {
                        install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(install);
                    }
                } else {
                    ToastUtil.showToastS(context, "下载失败");
                    L.e("下载失败----");
                }
                //停止服务并关闭广播
//                UpdataService.this.stopSelf();
            }
        }

        public File queryDownloadedApk(Context context) {
            File targetApkFile = null;
            DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (requestId != -1) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(requestId);
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                Cursor cur = downloader.query(query);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (!TextUtils.isEmpty(uriString)) {
                            targetApkFile = new File(Uri.parse(uriString).getPath());
                        }
                    }
                    cur.close();
                }
            }
            return targetApkFile;
        }

        /**
         * 安装apk文件
         */
//        private void installAPK(Uri apk) {
//            // 通过Intent安装APK文件
//            Intent intents = new Intent();
//            intents.setAction("android.intent.action.VIEW");
//            intents.addCategory("android.intent.category.DEFAULT");
//            intents.setType("application/vnd.android.package-archive");
//            intents.setData(apk);
//            intents.setDataAndType(apk, "application/vnd.android.package-archive");
//            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
//            startActivity(intents);
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
    }


    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

}
