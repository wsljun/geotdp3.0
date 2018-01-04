package com.geotdb.compile.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.vo.Gps;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Record;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;

public class NewUploadService extends Service {
    private Context context;
    private HoleDao holeDao;
    private RecordDao recordDao;
    private MediaDao mediaDao;
    private GpsDao gpsDao;

    @Override
    public void onCreate() {
        super.onCreate();
        context = NewUploadService.this;
        holeDao = new HoleDao(this);
        recordDao = new RecordDao(this);
        mediaDao = new MediaDao(this);
        gpsDao = new GpsDao(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.e("---->>onStartCommand");
        final Hole hole = (Hole) intent.getSerializableExtra("hole");
        final String serialNumber = (String) intent.getSerializableExtra("serialNumber");
        new Thread(new Runnable() {
            @Override
            public void run() {
                L.e("---->>启动上传线程");
                loadAllByHole(hole, serialNumber);
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //发送广播通知进度条
    private void sendReceiver(int size) {
        Intent intent = new Intent("upload.success");
        intent.putExtra("size", size);
        context.sendBroadcast(intent);
    }

    int size = 0;

    //一次性上传所有
    public void loadAllByHole(final Hole hole, final String serialNumber) {
        PostFormBuilder builder = OkHttpUtils.post();
        String strParams = "/" + serialNumber;
        final Map<String, String> map = new ConcurrentHashMap<>();
        map.putAll(hole.getNameValuePairMap(serialNumber));
        size += 1;
        //获取record
        final List<Record> recordList = recordDao.getNotUploadListByHoleID(hole.getId());
        List<Gps> resultGpsList = new LinkedList<>();
        if (recordList != null && recordList.size() > 0) {
            map.putAll(Record.getMap(recordList, serialNumber));
            //获取gps
            for (Record record : recordList) {
                List<Gps> gpsList = gpsDao.getListGpsByRecord(record.getId());
                if (gpsList != null && gpsList.size() > 0) {
                    resultGpsList.addAll(gpsList);
                }
            }
            size += recordList.size();
        }
        //获取media
        final List<Media> mediaList = mediaDao.getNotUploadListByHoleID(hole.getId());
        List<Media> realMediaList = new ArrayList<>();
        if (mediaList != null && mediaList.size() > 0) {
            for (Media media : mediaList) {
                File file = new File(media.getLocalPath());
                String suffix;
                String localPaht;
                if (file.isDirectory()) {
                    localPaht = Common.getVideoByDir(media.getLocalPath());
                    suffix = ".mp4";
                } else {
                    localPaht = media.getLocalPath();
                    suffix = ".jpg";
                }
                //判断媒体文件是否存在
                File f = new File(localPaht);
                if (f.exists()) {
                    //添加媒体参数
                    realMediaList.add(media);
                    //添加对应媒体gps
                    Gps gps = gpsDao.getGpsByMedia(media.getId());
                    resultGpsList.add(gps);
                    //添加媒体文件
                    builder.addFile("file_upload", media.getId() + suffix, new File(localPaht));
                } else {
                    media.delete(this);
                }
            }
            if (realMediaList.size() > 0) {
                map.putAll(Media.getMap(realMediaList, serialNumber));
                strParams += "-file";
            } else {
                strParams += "-noFile";
            }
        }
        if (resultGpsList.size() > 0) {
            map.putAll(Gps.getMap(resultGpsList, serialNumber));
            L.e("resultGpsList:" + resultGpsList.size());
        }
        if (map.size() > 0) {
            L.e("--->>>url:" + Urls.UPLOAD_HOLE_NEW + strParams);
            builder.addHeader("Connection", "close").url(Urls.UPLOAD_HOLE_NEW + strParams).params(map);
            builder.build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    L.e("TAG", "newMedia--onError-->>" + e.getMessage());
                    ToastUtil.showToastS(context, e.getMessage());
                }

                @Override
                public void inProgress(float progress, long t, int id) {
                    super.inProgress(progress, t, id);
                    int size = (int) Math.abs(progress * 100);
                    sendReceiver(size);
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                }

                @Override
                public void onResponse(String response, int id) {
                    L.e("TAG", "newMedia--onResponse-->>" + response);
                    if (JsonUtils.isGoodJson(response)) {
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                        if (jsonResult.getStatus()) {
                            //修改状态
                            hole.setState("2");
                            holeDao.update(hole);
                            if (recordList != null && recordList.size() > 0) {
                                for (Record record : recordList) {
                                    record.setState("2");
                                    recordDao.update(record);
                                }
                            }
                            if (mediaList != null && mediaList.size() > 0) {
                                for (Media media : mediaList) {
                                    media.setState("2");
                                    mediaDao.update(media);
                                }
                            }
                        } else {
                            ToastUtil.showToastS(context, jsonResult.getMessage());
                        }
                    } else {
                        ToastUtil.showToastS(context, "服务器异常，请联系客服");
                    }


                }
            });

        }
    }

}
