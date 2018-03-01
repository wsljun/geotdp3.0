/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geotdb.compile.vo;


import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.utils.FileUtil;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.L;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 媒体
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "media")
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";             //主键
    @DatabaseField
    String name = "";           // 媒体名称
    @DatabaseField
    String projectID = "";      //对应的项目',
    @DatabaseField
    String holeID = "";         //对应的钻孔',
    @DatabaseField
    String recordID = "";       //所属记录',
    @DatabaseField
    String gpsID = "";          //对应的定位信息',
    @DatabaseField
    String createTime = "";     //添加时间',
    @DatabaseField
    String createUser = "";     //添加人',
    @DatabaseField
    String uploadTime = "";     //提交时间',
    @DatabaseField
    String uploadUser = "";     //提交人',
    @DatabaseField
    String type = "";           //媒体类型0.图片 .1 视频.2.录音.3签名',
    @DatabaseField
    String state = "";          //媒体状态(0.未保存.1.未上传.2.已上传)',
    @DatabaseField
    String localPath = "";      //本地路径',
    @DatabaseField
    String internetPath = "";   //互联网路径',
    @DatabaseField
    String remark = "";         //备注',
    @DatabaseField
    String isDelete = "";       //是否删除',

    Gps gps;

    public Media() {

    }

    public Media(String jpg) {
        this.localPath = jpg;
    }

    public Media(Context context, Record record, String localPath, AMapLocation aMapLocation) {
        try {
            this.id = Common.getUUID();
            this.projectID = record.getProjectID();
            this.holeID = record.getHoleID();
            this.recordID = record.getId();

            this.createTime = DateUtil.date2Str(new Date(aMapLocation.getTime()));       //添加时间

            this.localPath = localPath;
            this.state = "1";       //
            this.isDelete = "0";       //是否删除',

            HashMap codeMap = getCodeMap(context, record.getProjectID());
            DecimalFormat df = new DecimalFormat("000");
            int codeInt = 1;

            String typeStr = "T";
            String codeStr = typeStr + "-" + df.format(codeInt);
            while (codeMap.containsKey(codeStr)) {
                codeInt += 1;
                codeStr = typeStr + "-" + df.format(codeInt);
            }
//            String holeCode = new HoleDao(context).queryForId(record.getHoleID()).getCode();
//            this.name = holeCode + "_" + record.getType() + "_" + codeStr;
            this.name = codeStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getCodeMap(Context context, String projectID) {
        HashMap hashmap = new HashMap();
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            GenericRawResults<String> results = dao.queryRaw("select name from media where name like 'T-___'and projectID='" + projectID + "' and state !='0'", new RawRowMapper<String>() {
                @Override
                public String mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    String s = resultColumns[0];
                    return s;
                }
            });

            Iterator<String> iterator = results.iterator();
            while (iterator.hasNext()) {
                String string = iterator.next();
                hashmap.put(string, string);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashmap;
    }

    /**
     * 获取媒体
     *
     * @param context
     */
    public Media get(Context context, String id) {
        Media media = null;
        try {
            media = new MediaDao(context).getMediaByID(id);
            media.setGps(new GpsDao(context).getGpsByMedia(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return media;
    }


    /**
     * 删除媒体
     *
     * @param context
     */
    public boolean delete(Context context) {
        try {
            //如果是视频的话，视频保存的是文件夹路径，其中包括了视频和图片，都要刪除
            File aFile = new File(localPath);
            if (aFile.isDirectory()) {
                if (aFile.exists()) {
                    FileUtil.deleteAllFiles(localPath);
                }
            }
            if (!aFile.exists() || aFile.delete()) {
                L.e("照片删除成功");
                Gps gps = new GpsDao(context).getGpsByMedia(getId());
                if (gps == null || gps.delete(context)) {
                    L.e("GPS数据删除成功");
                    if (new MediaDao(context).delete(this)) {
                        L.e("媒体数据删除成功");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //生成属性列表
    public Map<String, String> getMap(String serialNumber) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("media.projectID", serialNumber);

        params.put("media.id", id);
        params.put("media.name", name);
        params.put("media.holeID", holeID);
        params.put("media.recordID", recordID);
        params.put("media.gpsID", gpsID);
        params.put("media.createTime", createTime);
        params.put("media.createUser", createUser);
        params.put("media.uploadUser", uploadUser);
        params.put("media.type", type);
        params.put("media.state", state);
        params.put("media.internetPath", internetPath);
        params.put("media.remark", remark);
        //如果是文件加，只能是video的文件夹
        File file = new File(localPath);
        if (file.isDirectory()) {
            params.put("media.localPath", Common.getVideoByDir(localPath));
            L.e("isDirectory");
        } else {
            L.e("noDirectory");
            params.put("media.localPath", localPath);
        }

        return params;
    }

    public static Map<String, String> getMap(List<Media> list, String serialNumber) {
        Map<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Media media = list.get(i);
            map.put("media[" + i + "].projectID", serialNumber);
            map.put("media[" + i + "].id", media.getId() == null ? "" : media.getId());
            map.put("media[" + i + "].name", media.getName() == null ? "" : media.getName());
            map.put("media[" + i + "].holeID", media.getHoleID() == null ? "" : media.getHoleID());
            map.put("media[" + i + "].recordID", media.getRecordID() == null ? "" : media.getRecordID());
            map.put("media[" + i + "].gpsID", media.getGpsID() == null ? "" : media.getGpsID());
            if(!TextUtils.isEmpty(media.getCreateTime())){
                map.put("media[" + i + "].createTime", media.getCreateTime());
            }
            if(!TextUtils.isEmpty(media.getCreateUser())){
                map.put("media[" + i + "].createUser", media.getCreateUser());
            }
            if(!TextUtils.isEmpty(media.getUploadUser())){
                map.put("media[" + i + "].uploadUser", media.getUploadUser());
            }
            if(!TextUtils.isEmpty(media.getType())){
                map.put("media[" + i + "].type", media.getType());
            }
            if(!TextUtils.isEmpty(media.getState())){
                map.put("media[" + i + "].state", media.getState());
            }
            if(!TextUtils.isEmpty(media.getInternetPath())){
                map.put("media[" + i + "].internetPath", media.getInternetPath());
            }
            if(!TextUtils.isEmpty(media.getRemark())){
                map.put("media[" + i + "].remark", media.getRemark());
            }
            //如果是文件加，只能是video的文件夹
            File file = new File(list.get(i).getLocalPath());
            if (file.isDirectory()) {
                map.put("media[" + i + "].localPath", Common.getVideoByDir(list.get(i).getLocalPath()));
                L.e("isDirectory");
            } else {
                L.e("noDirectory");
                map.put("media[" + i + "].localPath", list.get(i).getLocalPath());
            }
        }
        return map;
    }

    public Media(String name, String localPath) {
        this.name = name;
        this.localPath = localPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getGpsID() {
        return gpsID;
    }

    public void setGpsID(String gpsID) {
        this.gpsID = gpsID;
    }

    public String getHoleID() {
        return holeID;
    }

    public void setHoleID(String holeID) {
        this.holeID = holeID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternetPath() {
        return internetPath;
    }

    public void setInternetPath(String internetPath) {
        this.internetPath = internetPath;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getState() {
        return state;
    }

    public String getStateName() {
        if ("2".equals(state)) {
            return "已上传";
        } else {
            return "未上传";
        }

    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }
}
