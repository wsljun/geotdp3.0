package com.geotdb.compile.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.geotdb.compile.adapter.HoleListAdapter;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.dialog.UploadProgressDialog;
import com.geotdb.compile.vo.Gps;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Project;
import com.geotdb.compile.vo.Record;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 所有的上传操作在这里实现，并链接在一起
 * 在正确的回调下，循环上传子项
 * 进度问题，在调用页面获取总条数，并设置广播接收器，没接到一条广播，进度加一
 * 在这里没完成一条，发送一条广播
 */
public class UploadService extends Service {
    private Map<String, String> map;
    private Context context;
    private DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        map = new HashMap<>();
        context = UploadService.this;
        dbHelper = DBHelper.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Project project = (Project) intent.getSerializableExtra("project");
        final Hole hole = (Hole) intent.getSerializableExtra("hole");
        if (project != null) {
            uploadProject(project);
        }

        if (hole != null) {
            uploadHole(hole);
        }
        return super.onStartCommand(intent, flags, startId);

    }

    //发送广播通知进度条
    private void sendReceiver() {
        Intent intent = new Intent("upload.success");
        context.sendBroadcast(intent);
    }

    public void uploadProject(final Project project) {
        L.e("upload---project---project");
        map.put("project.serialNumber", project.getSerialNumber());
        map.put("project.recordPerson", project.getRecordPerson());
        if ("1".equals(project.getState())) {
            OkHttpUtils.post().addHeader("Connection", "close").url(Urls.UPLOAD_PROJECT).params(map).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    L.e("TAG", "Project--onError-->>" + e.getMessage());
                    ToastUtil.showToastS(context, e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    L.e("TAG", "Project--onResponse-->>" + response);
                    Gson gson = new Gson();
                    try {
                        JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                        if (jsonResult.getStatus()) {
                            try {
                                sendReceiver();
                                Dao<Project, String> dao = dbHelper.getDao(Project.class);
                                Project p = dao.queryForId(project.getId());
                                p.setState("2");
                                dao.update(p);
                                //遍历上传子项
                                uploadForProject(project);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            L.e("TAG", "Project--onResponse-->>上传失败");
                            ToastUtil.showToastS(context, jsonResult.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToastS(context, "上传失败");
                    }
                }
            });
        } else {
            uploadForProject(project);
        }
    }

    public void uploadForProject(Project project) {
        //遍历上传子项
        List<Hole> holeList = new HoleDao(context).getHoleListByNotUpload(project.getId());
        if (holeList.size() > 0) { //如果存在未上传的勘探点
            L.e("upload---project---hole--------上传hole");
            for (Hole hole : holeList) {
                uploadHole(hole);
            }
        } else {
            //遍历上传子项
            List<Record> recordList = new RecordDao(context).getNotUploadListByProjectID(project.getId());
            if (recordList.size() > 0) { //如果存在未上传的记录
                L.e("upload---project---record--------上传record");
                for (Record record : recordList) {
                    uploadreRecord(record);
                }
            } else {
                //遍历上传子项
                List<Media> mediaList = new MediaDao(context).getNotUploadListByProjectID(project.getId());
                if (mediaList.size() > 0) { //如果存在未上传的媒体
                    L.e("upload---project---media--------上传midia");
                    for (Media media : mediaList) {
                        uploadMedia(media);
                    }
                }
            }
        }


    }


    public void uploadHole(final Hole hole) {
        L.e("upload---hole---hole");
        String serialNumber = new ProjectDao(context).queryForId(hole.getProjectID()).getSerialNumber();
        map.clear();
        map = hole.getNameValuePairMap(serialNumber);
        map.put("hole.relateID", hole.getRelateID());
//        map.put("hole.userID", Common.getUserIDBySP(context));
        if (hole.getRelateID() != null && !"".equals(hole.getRelateID())) {
            if (Integer.parseInt(hole.getLocationState()) == 0) {
                if ("1".equals(hole.getState())) {
                    OkHttpUtils.post().addHeader("Connection", "close").url(Urls.UPLOAD_HOLE).params(map).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            L.e("TAG", "Hole--onError-->>" + e.getMessage());
                            ToastUtil.showToastS(context, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            L.e("TAG", "Hole--onResponse-->>" + response);
                            Gson gson = new Gson();
                            try {
                                JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                                if (jsonResult.getStatus()) {
                                    try {
                                        sendReceiver();
                                        Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
                                        Hole h = dao.queryForId(hole.getId());
                                        h.setState("2");
                                        h.setUploaded("1");
                                        dao.update(h);
                                        //遍历上传子项
                                        uploadForHole(hole);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    L.e("TAG", "Hole--onResponse-->>上传失败");
                                    ToastUtil.showToastS(context, jsonResult.getMessage());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.showToastS(context, "勘察点上传失败");
                            }
                        }
                    });

                } else {
                    uploadForHole(hole);
                }
            } else {
                ToastUtil.showToastL(context, "勘察点没有定位");
            }
        } else {
            ToastUtil.showToastL(context, "勘察点没有关联");
        }


    }

    public void uploadForHole(Hole hole) {
        //遍历上传子项
        List<Record> recordList = new RecordDao(context).getNotUploadListByHoleID(hole.getId());
        if (recordList.size() > 0) { //如果存在未上传的记录
            L.e("upload---hole---record-------上传record");
            for (Record record : recordList) {
                uploadreRecord(record);
            }
        } else {
            //遍历上传子项
            List<Media> mediaList = new MediaDao(context).getNotUploadListByHoleID(hole.getId());
            if (mediaList.size() > 0) { //如果存在未上传的媒体
                L.e("upload---hole---media--------上传media");
                for (Media media : mediaList) {
                    uploadMedia(media);
                }
            }
        }

    }

    public void uploadreRecord(final Record record) {
        L.e("upload---record---record");
        String relateID = new HoleDao(context).queryForId(record.getHoleID()).getRelateID();
        //判断勘察点是否关联
        if (relateID.equals("") || relateID == null) {
            ToastUtil.showToastS(context, "该勘察点为关联");
        } else {
            final String serialNumber = new ProjectDao(context).queryForId(record.getProjectID()).getSerialNumber();
            map.clear();
            map = record.getNameValuePairMap(serialNumber);
            //勘察点已经关联，修改参数holeID为获取的服务器的ID（isRelated）
//            map.put("record.holeID", result);
//            map.put("record.localHoleID", record.getHoleID());

            if ("1".equals(record.getState())) {
                OkHttpUtils.post().addHeader("Connection", "close").url(Urls.UPLOAD_RECORD).params(map).build().execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("TAG", "Record--onError-->>" + e.getMessage());
                        ToastUtil.showToastS(context, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.e("TAG", "Record--onResponse-->>" + response);
                        Gson gson = new Gson();
                        try {
                            JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                            if (jsonResult.getStatus()) {
                                sendReceiver();
                                try {
                                    Dao<Record, String> dao = dbHelper.getDao(Record.class);
//                            Record r = dao.queryForId(record.getId());
                                    record.setState("2");
                                    dao.update(record);
                                    //判断是否是原始记录
                                    if (record.getUpdateId().equals("")) {
                                        L.e("TAG", "Record--onResponse-->> 最新记录");
                                        //遍历上传原始记录
                                        uploadUpdateRecord(record);
                                    }
                                    List<Gps> gpsList = new GpsDao(context).getListGpsByRecord(record.getId());
                                    //遍历上传子项
                                    uploadForRecord(record);
                                    if (gpsList != null) {  //如果记录对应的GPS 存在 ,并没有上传失败 ,就算这条记录上传完成.
                                        for (Gps gps : gpsList) {
                                            uploadGps(gps, serialNumber);
                                        }
                                    } else {
                                        L.e("TAG", "Record--onResponse-->>没有gps信息");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                L.e("TAG", "Record--onResponse-->>上传失败");
                                ToastUtil.showToastS(context, jsonResult.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToastS(context, "记录上传失败,勘察点未上传,请先上传勘察点");
                        }

                    }
                });

            }
        }

    }

    //上传所有原始记录
    public void uploadUpdateRecord(Record record) {
        List<Record> updateRecord = new RecordDao(context).getUpdateRecordByID(record);
        if (updateRecord != null) {
            //查询原始记录是否已经上传,上传未上传的
            for (Record r : updateRecord) {
                if (r.getState().equals("1")) {
                    uploadreRecord(r);
                    L.e("uploadreRecord----" + updateRecord.size() + "------time-" + r.getId());
                }
            }
        }
    }


    public void uploadForRecord(Record record) {
        //遍历上传子项
        List<Media> mediaList = new MediaDao(context).getNotUploadListByRecordID(record.getId());
        if (mediaList.size() > 0) { //如果存在未上传的媒体
            L.e("upload---record---media");
            for (Media media : mediaList) {
                uploadMedia(media);
            }
        }
    }

    public void uploadMedia(final Media media) {
        L.e("upload---media---mediaData");
//        String isRelated = new HoleDao(context).queryForId(media.getHoleID()).getRelateID();
        //判断勘察点是否关联
//        if (isRelated == null || isRelated.equals("")) {
//            ToastUtil.showToastS(context, "该勘察点为关联");
//        } else {
        final UploadProgressDialog progressDialog = new UploadProgressDialog();
        map.clear();
        final String serialNumber = new ProjectDao(context).queryForId(media.getProjectID()).getSerialNumber();
        map = media.getMap(serialNumber);
        //修改参数
//            map.put("media.holeID", result);
//            map.put("record.localHoleID", media.getHoleID());
        String strParams = "/" + serialNumber;
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
        OkHttpUtils.post().addHeader("Connection", "close").addFile("file_upload", media.getId() + suffix, new File(localPaht)).url(Urls.UPLOAD_MEDIA_File + strParams).params(map).build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
//                progressDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
//                progressDialog.scheduleTimer();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
//                progressDialog.cancelTimer();
//                progressDialog.dismiss();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "Media--onError-->>" + e.getMessage());
                ToastUtil.showToastS(context, e.getMessage());
//                progressDialog.cancelTimer();
//                progressDialog.dismiss();
            }

            @Override
            public void inProgress(float progress, long t, int id) {
                super.inProgress(progress, t, id);
                L.e("TAG", "Media--inProgress-->>" + progress);
//                progressDialog.setProgressBar(progress, t);
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "Media--onResponse-->>" + response);
                Gson gson = new Gson();
                try {
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        try {
                            Dao<Media, String> dao = dbHelper.getDao(Media.class);
                            Media m = dao.queryForId(media.getId());
                            m.setState("2");
                            m.setUploadTime(DateUtil.date2Str(new Date()));
                            dao.update(m);
                            sendReceiver();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        L.e("TAG", "Media--onResponse-->>上传失败---holeID=" + media.getHoleID());
                        ToastUtil.showToastS(context, jsonResult.getMessage());
                    }

                    Gps gps = new GpsDao(context).getGpsByMedia(media.getId());
                    if (gps != null) {  //如果媒体对应的GPS 存在 ,并没有上传失败 ,就算这条媒体上传完成.
                        uploadGps(gps, serialNumber);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
//        }

    }

    public void uploadGps(final Gps gps, String serialNumber) {
        L.e("upload---uploadGps");
//        String isRelated = new HoleDao(context).queryForId(gps.getHoleID()).getRelateID();
//        //判断勘察点是否关联
//        if (isRelated.equals("") || isRelated == null) {
//            ToastUtil.showToastS(context, "该勘察点为关联");
//        } else {
        map.clear();
        map = gps.getNameValuePairMap(serialNumber);
        //修改参数
//            map.put("gps.holeID", result);
        OkHttpUtils.post().addHeader("Connection", "close").url(Urls.UPLOAD_GPS).params(map).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "Gps--onError-->>" + e.getMessage());
                ToastUtil.showToastS(context, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "Gps--onResponse-->>" + response);
                Gson gson = new Gson();
                try {
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        L.e("TAG", "Gps--onResponse-->> 上传成功");
                    } else {
                        L.e("TAG", "Gps--onResponse-->>上传失败---holeID=" + gps.getHoleID());
                        ToastUtil.showToastS(context, jsonResult.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    }

}
