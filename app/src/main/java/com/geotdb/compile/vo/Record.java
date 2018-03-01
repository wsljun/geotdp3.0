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
import com.geotdb.compile.utils.Key;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 记录
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record")
public class Record implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_FREQUENCY = "回次";
    public static final String TYPE_LAYER = "岩土";
    public static final String TYPE_GET_EARTH = "取土";
    public static final String TYPE_GET_WATER = "取水";
    public static final String TYPE_DPT = "动探";
    public static final String TYPE_SPT = "标贯";
    public static final String TYPE_WATER = "水位";
    public static final String TYPE_SCENE = "备注";

    public static final String TYPE_SCENE_OPERATEPERSON = "机长";
    public static final String TYPE_SCENE_OPERATECODE = "钻机";
    public static final String TYPE_SCENE_RECORDPERSON = "描述员";
    public static final String TYPE_SCENE_SCENE = "场景";
    public static final String TYPE_SCENE_PRINCIPAL = "负责人";
    public static final String TYPE_SCENE_TECHNICIAN = "工程师";
    public static final String TYPE_SCENE_VIDEO = "提钻录像";


    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
    @DatabaseField
    String code = "";//'记录编号',
    @DatabaseField
    String projectID = "";//'所属项目',
    @DatabaseField
    String holeID = "";//'所属勘探点',
    @DatabaseField
    String type = "";//'钻进方法',
    @DatabaseField
    String title = "";//'标题',
    @DatabaseField
    String beginDepth = "";//'起始深度',
    @DatabaseField
    String endDepth = "";//'终止深度',
    @DatabaseField
    String description = "";//'描述备注',
    @DatabaseField
    String createTime = "";//'创建时间',
    @DatabaseField
    String updateTime = "";//'更新时间',
    @DatabaseField
    String uploadTime = "";//'上传时间',
    @DatabaseField
    String recordPerson = "";//'描述员
    @DatabaseField
    String operatePerson = "";//'机长-机长名字、负责人-负责人名字、工程师-工程师名字,
    @DatabaseField
    String versionNumber = "";//版本数',
    @DatabaseField
    String state = "";//状态 0.未添加,1.未上传,2.已上传',
    @DatabaseField
    String isDelete = "";//是否删除',


    @DatabaseField
    String frequencyType = "";//'钻进方法',
    @DatabaseField
    String frequencyMode = "";//'护壁方法',
    @DatabaseField
    String aperture = "";//'钻孔孔径(mm)',


    @DatabaseField
    String earthType = "";//'土样质量等级',
    @DatabaseField
    String waterDepth = "";
    @DatabaseField
    String testType = "";  //实验类型   在机长记录中是机长证件号、在钻机中是钻机编号
    @DatabaseField
    String getMode = "";//'取水方式',取样工具和方法'


    public static final String TYPE_TT = "填土";
    public static final String TYPE_CTT = "冲填土";
    public static final String TYPE_NXT = "黏性土";
    public static final String TYPE_FT = "粉土";
    public static final String TYPE_FNHC = "粉黏互层";
    public static final String TYPE_HTZNXT = "黄土状黏性土";
    public static final String TYPE_HTZFT = "黄土状粉土";
    public static final String TYPE_YN = "淤泥";
    public static final String TYPE_SST = "碎石土";
    public static final String TYPE_ST = "砂土";
    public static final String TYPE_YS = "岩石";

    @DatabaseField
    String layerType = "";//'岩土类型',
    @DatabaseField
    String layerName = "";//'岩土定名',
    @DatabaseField
    String weathering = "";//'地层风化状况',
    @DatabaseField
    String era = "";//'地层年代',
    @DatabaseField
    String causes = "";//'地层成因',
    @DatabaseField
    String wzcf = "";//'物质成份',
    @DatabaseField
    String ys = "";//'颜色',
    @DatabaseField
    String klzc = "";//'颗粒组成',
    @DatabaseField
    String klxz = "";//'颗粒形状',
    @DatabaseField
    String klpl = "";//'颗粒排列',
    @DatabaseField
    String kljp = "";//'颗粒级配',
    @DatabaseField
    String sd = "";//'湿度',
    @DatabaseField
    String msd = "";//'密实度',
    @DatabaseField
    String jyx = "";//'均匀性',
    @DatabaseField
    String zt = "";//'状态',
    @DatabaseField
    String bhw = "";//'包含物',
    @DatabaseField
    String ftfchd = "";//'粉土分层厚度',
    @DatabaseField
    String fzntfchd = "";//'粉质黏土分层厚度',
    @DatabaseField
    String jc = "";//'夹层',
    @DatabaseField
    String kx = "";//'孔隙',
    @DatabaseField
    String czjl = "";//'垂直节理',
    @DatabaseField
    String ybljx = "";//'一般粒径小',
    @DatabaseField
    String ybljd = "";//'一般粒径大',
    @DatabaseField
    String jdljx = "";//'较大粒径小',
    @DatabaseField
    String jdljd = "";//'较大粒径大',
    @DatabaseField
    String zdlj = "";//'最大粒径',
    @DatabaseField
    String mycf = "";//'母岩成份',
    @DatabaseField
    String fhcd = "";//'风化程度',
    @DatabaseField
    String tcw = "";//'充填物',
    @DatabaseField
    String kwzc = "";//'矿物组成',
    @DatabaseField
    String zycf = "";//'主要成分',
    @DatabaseField
    String cycf = "";//'次要成份',
    @DatabaseField
    String djnd = "";//'堆积年代',
    @DatabaseField
    String hsl = "";//'含水量',
    @DatabaseField
    String jycd = "";//'坚硬程度',
    @DatabaseField
    String wzcd = "";//'完整程度',
    @DatabaseField
    String jbzldj = "";//'基本质量等级',
    @DatabaseField
    String kwx = "";//'可挖性',
    @DatabaseField
    String jglx = "";//'结构类型',


//    public static final String TYPE_DPY = "动力触探";
//    public static final String TYPE_SPT = "标准贯入";

    public static final String TYPE_1 = "轻型";
    public static final String TYPE_2 = "重型";
    public static final String TYPE_3 = "超重型";

    public static final String TYPE_1_CODE = "N10";
    public static final String TYPE_2_CODE = "N63.5";
    public static final String TYPE_3_CODE = "N120";

    @DatabaseField
    String powerType = "";//'动探类型',
    @DatabaseField
    String drillLength = "";//'钻杆长度',
    @DatabaseField
    String begin1 = "0";           // 起始
    @DatabaseField
    String end1 = "0";         //终止
    @DatabaseField
    String blow1 = "0";         //击数
    @DatabaseField
    String begin2 = "0";           // 起始
    @DatabaseField
    String end2 = "0";         //终止
    @DatabaseField
    String blow2 = "0";         //击数
    @DatabaseField
    String begin3 = "0";           // 起始
    @DatabaseField
    String end3 = "0";         //终止
    @DatabaseField
    String blow3 = "0";         //击数
    @DatabaseField
    String begin4 = "0";           // 起始
    @DatabaseField
    String end4 = "0";         //终止
    @DatabaseField
    String blow4 = "0";         //击数


    @DatabaseField
    String waterType = "";//'地下水类型',
    @DatabaseField
    String shownWaterLevel = "";//'初见水位',
    @DatabaseField
    String stillWaterLevel = "";//'稳定水位',
    @DatabaseField
    String shownTime = "";//'初见时间',
    @DatabaseField
    String stillTime = "";//'稳定时间',
    @DatabaseField
    String updateID = "";//被修改的记录id

    String mediaCount = "0";    //拥有媒体数
    int uploadedCount;          //已上传
    int notUploadCount;         //未上传

    List<Gps> gpsList;//下载hole时，封装数据

    public List<Gps> getGpsList() {
        return gpsList;
    }

    public void setGpsList(List<Gps> gpsList) {
        this.gpsList = gpsList;
    }

    public Record() {

    }

    public Record(String code) {
        this.code = code;
    }

    public Record(Context context, Hole hole, String recordType) {
        try {
            this.id = Common.getUUID();
            RecordDao recordDao = new RecordDao(context);
            DecimalFormat df = new DecimalFormat("000");
            List<Record> recordList = recordDao.getCodeMapNew(hole.getId(), recordType);
            String codeStr = "";

            if (recordList != null && recordList.size() > 0) {
                String str = recordList.get(recordList.size() - 1).getCode().trim();
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(str);
                if (m.find()) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < m.group().length(); i++) {
                        sb.append("0");
                    }
                    DecimalFormat decimalFormat = new DecimalFormat(sb.toString());
                    int num = Integer.parseInt(m.group());
                    num += 1;
                    String subStr = str.substring(0, str.lastIndexOf(m.group()));
                    codeStr = subStr + decimalFormat.format(num);
                    L.e("---->>>数字：:" + num);
                    L.e("---->>>d.format(mg)：:" + decimalFormat.format(num));
                    L.e("---->>>codeStr:" + codeStr);
                } else {
                    int codeInt = 1;
                    codeStr = str + df.format(codeInt);
                    L.e("---->>>str:" + str);
                    L.e("---->>>codeStr:" + codeStr);
                }
            } else {
                HashMap codeMap = recordDao.getCodeMap(hole.getId());
                int codeInt = 1;
                String typeStr = getTypeCode(recordType);
                codeStr = typeStr + "-" + df.format(codeInt);
                while (codeMap.containsKey(codeStr)) {
                    codeInt += 1;
                    codeStr = typeStr + "-" + df.format(codeInt);
                }
            }

            this.code = codeStr;

            this.beginDepth = "0";
            this.endDepth = "0";

            this.isDelete = "1";//是否删除',

            if (recordType.equals(TYPE_GET_EARTH)) {
                this.earthType = "";
                this.getMode = "";
            } else if (recordType.equals(TYPE_GET_WATER)) {
                waterDepth = "0";
                this.getMode = "";
            } else if (recordType.equals(TYPE_SPT)) {
                this.powerType = TYPE_1;
                setBeginBySPT("0.00");
            } else if (recordType.equals(TYPE_WATER)) {

            } else if (recordType.equals(TYPE_SCENE)) {

            }

            //这三种类型的数据的初始内容,继承上一条的数据
            if (recordType.equals(Record.TYPE_FREQUENCY) || recordType.equals(Record.TYPE_LAYER) || recordType.equals(Record.TYPE_DPT)) {
                Record previousRecord = getRecord(context, hole.getId(), recordType);
                if (previousRecord != null) {
                    this.beginDepth = previousRecord.getEndDepth();
                    this.description = previousRecord.getDescription();
                    setValue(previousRecord);
                } else {
                    if (recordType.equals(TYPE_FREQUENCY)) {
                        this.frequencyType = "";
                        this.frequencyMode = "";
                        this.aperture = "0";
                    } else if (recordType.equals(TYPE_LAYER)) {
                        this.layerType = "";
                        this.layerName = "";
                        this.era = "";
                        this.causes = "";
                    } else if (recordType.equals(TYPE_DPT)) {
                        this.powerType = TYPE_1;
                        setBegin("0");
                    }
                }
                this.endDepth = String.valueOf(Double.valueOf(this.beginDepth) + 0.5);
            }

            this.projectID = hole.getProjectID();
            this.holeID = hole.getId();

            this.createTime = DateUtil.date2Str(new Date());       //添加时间
            this.updateTime = DateUtil.date2Str(new Date());       //更新时间

            this.type = recordType;
            this.state = "0";

            this.updateID = "";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setValue(Record record) {
        this.frequencyType = record.getFrequencyType();
        this.frequencyMode = record.getFrequencyMode();
        this.aperture = record.getAperture();

        this.layerType = record.getLayerType();
        this.layerName = record.getLayerName();
        this.weathering = record.getWeathering();
        this.era = record.getEra();
        this.causes = record.getCauses();
        this.wzcf = record.getWzcf();
        this.ys = record.getYs();
        this.klzc = record.getKlzc();
        this.klxz = record.getKlxz();
        this.klpl = record.getKlpl();
        this.kljp = record.getKljp();
        this.sd = record.getSd();
        this.msd = record.getMsd();
        this.jyx = record.getJyx();
        this.zt = record.getZt();
        this.bhw = record.getBhw();
        this.ftfchd = record.getFtfchd();
        this.fzntfchd = record.getFzntfchd();
        this.jc = record.getJc();
        this.kx = record.getKx();
        this.czjl = record.getCzjl();
        this.ybljx = record.getYbljx();
        this.ybljd = record.getYbljd();
        this.jdljx = record.getJdljx();
        this.jdljd = record.getJdljd();
        this.zdlj = record.getZdlj();
        this.mycf = record.getMycf();
        this.fhcd = record.getFhcd();
        this.tcw = record.getTcw();
        this.kwzc = record.getKwzc();
        this.zycf = record.getZycf();
        this.cycf = record.getCycf();
        this.djnd = record.getDjnd();
        this.hsl = record.getHsl();
        this.jycd = record.getJycd();
        this.wzcd = record.getWzcd();
        this.jbzldj = record.getJbzldj();
        this.kwx = record.getKwx();
        this.jglx = record.getJglx();

        this.powerType = record.getPowerType();
        this.drillLength = record.getDrillLength();
        setBegin(record.getEnd1());
    }


    public void setBegin(String begin1) {
        this.begin1 = begin1;
        double nn = 0.3;
        if (TYPE_1.equals(powerType)) {
            nn = 0.3;
        } else if (TYPE_2.equals(powerType)) {
            nn = 0.1;
        } else if (TYPE_3.equals(powerType)) {
            nn = 0.1;
        }
        this.end1 = String.valueOf(Double.valueOf(begin1) + nn);
    }


    public void setBeginBySPT(String begin) {
        try {
            BigDecimal n1 = new BigDecimal("0.15");
            BigDecimal nn = new BigDecimal("0.1");
            BigDecimal b = new BigDecimal(begin);
            BigDecimal e = b.add(n1);
            setEnd1(e.toString());
            for (int i = 2; i <= 4; i++) {
                b = e;
                e = b.add(nn);
                switch (i) {
                    case 2:
                        setBegin2(b.toString());
                        setEnd2(e.toString());
                        break;
                    case 3:
                        setBegin3(b.toString());
                        setEnd3(e.toString());
                        break;
                    case 4:
                        setBegin4(b.toString());
                        setEnd4(e.toString());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setSPTBegin2(String begin) {
        try {
            BigDecimal nn = new BigDecimal("0.1");
            BigDecimal b = new BigDecimal(begin);
            BigDecimal e = b.add(nn);

            setBegin2(b.toString());
            setEnd2(e.toString());
            for (int i = 3; i <= 4; i++) {
                b = e;
                e = b.add(nn);
                switch (i) {
                    case 3:
                        setBegin3(b.toString());
                        setEnd3(e.toString());
                        break;
                    case 4:
                        setBegin4(b.toString());
                        setEnd4(e.toString());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSPTBegin3(String begin) {
        try {
            BigDecimal nn = new BigDecimal("0.1");
            BigDecimal b = new BigDecimal(begin);
            BigDecimal e = b.add(nn);
            setBegin3(b.toString());
            setEnd3(e.toString());
            b = e;
            e = b.add(nn);
            setBegin4(b.toString());
            setEnd4(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSPTBegin4(String begin) {
        try {
            BigDecimal nn = new BigDecimal("0.1");
            BigDecimal b = new BigDecimal(begin);
            BigDecimal e = b.add(nn);
            setBegin4(b.toString());
            setEnd4(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTypeCode(String recordType) {
        if (recordType.equals(Record.TYPE_FREQUENCY)) {
            return "HC";
        } else if (recordType.equals(Record.TYPE_LAYER)) {
            return "YT";
        } else if (recordType.equals(Record.TYPE_GET_EARTH)) {
            return "QT";
        } else if (recordType.equals(Record.TYPE_GET_WATER)) {
            return "QS";
        } else if (recordType.equals(Record.TYPE_DPT)) {
            return "DPT";
        } else if (recordType.equals(Record.TYPE_SPT)) {
            return "SPT";
        } else if (recordType.equals(Record.TYPE_WATER)) {
            return "SW";
        } else if (recordType.equals(Record.TYPE_SCENE)) {
            return "BZ";
        } else if (recordType.equals(Record.TYPE_SCENE_OPERATEPERSON)) {
            return "OP";
        } else if (recordType.equals(Record.TYPE_SCENE_OPERATECODE)) {
            return "OC";
        } else if (recordType.equals(Record.TYPE_SCENE_RECORDPERSON)) {
            return "RP";
        } else if (recordType.equals(Record.TYPE_SCENE_SCENE)) {
            return "SS";
        } else if (recordType.equals(Record.TYPE_SCENE_PRINCIPAL)) {
            return "SP";
        } else if (recordType.equals(Record.TYPE_SCENE_TECHNICIAN)) {
            return "ST";
        } else if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {
            return "SV";
        } else {
            return "HC";

        }
    }

    public Record getRecord(Context context, String holeID, String recordType) {
        Record record = null;
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Record, String> dao = dbHelper.getDao(Record.class);
            GenericRawResults<Record> results = dao.queryRaw("select id,code,type,updateTime,beginDepth,endDepth,title from record where holeID='" + holeID + "' and updateID='' and state !='0' and type='" + recordType + "' order by endDepth desc limit 0,1", new RawRowMapper<Record>() {
                @Override
                public Record mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Record record = new Record();
                    record.setId(resultColumns[0]);
                    record.setCode(resultColumns[1]);
                    record.setType(resultColumns[2]);
                    record.setUpdateTime(resultColumns[3]);
                    record.setBeginDepth(resultColumns[4]);
                    record.setEndDepth(resultColumns[5]);
                    record.setTitle(resultColumns[6]);
//                    record.jieMi();
                    return record;
                }
            });
            record = results.getFirstResult();

            record = dao.queryForId(record.getId());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public List<Record> getRecordByType(Context context, String holeID, String recordType) {
        List<Record> list = null;
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Record, String> dao = dbHelper.getDao(Record.class);
            list = dao.queryBuilder().where().eq("holeID", holeID).and().eq("type", recordType).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 钻孔编辑页面，可以一次查出来，在赋值对应的记录，待修改
    public List<Record> getRecordByTypeAll(Context context, String holeID, String[] recordType) {
        List<Record> list = null;
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Record, String> dao = dbHelper.getDao(Record.class);
            list = dao.queryBuilder().where().eq("holeID", holeID).and().eq("type", recordType[0]).and().eq("type", recordType[1]).and().eq("type", recordType[2]).and().eq("type", recordType[3]).and().eq("type", recordType[4]).and().eq("type", recordType[5]).and().eq("type", recordType[6]).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 创建记录
     *
     * @param context
     */
    public void create(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Record, String> recordDao = dbHelper.getDao(Record.class);
            recordDao.create(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新记录
     *
     * @param context
     */
    public void update(Context context, AMapLocation amapLocation) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Record, String> recordDao = dbHelper.getDao(Record.class);
            this.updateTime = DateUtil.date2Str(new Date());       //更新时间
            recordDao.update(this);

            Gps gps = new Gps(this, amapLocation, getTypeCode(this.type));
            Dao<Gps, String> gpsDao = dbHelper.getDao(Gps.class);
            gpsDao.create(gps);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制勘探点
     *
     * @param context
     */
    public boolean copy(Context context, Hole hole) {
        try {
            //删除对应的GPS.
            GpsDao gpsDao = new GpsDao(context);
            Gps gps = gpsDao.getGpsByRecord(getId());

            this.id = Common.getUUID();
            this.holeID = hole.getId();
            new RecordDao(context).add(this);

            gps.setId(Common.getUUID());
            gps.setRecordID(this.getId());
            gps.setHoleID(this.getHoleID());
            gpsDao.add(gps);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除记录
     *
     * @param context
     */
    public boolean delete(Context context) {
        try {
            //先删除所有的照片.
            List<Media> medias = new MediaDao(context).getMediaListByRecordID(id);
            for (Media media : medias) {
                media.delete(context);
            }
            System.out.println("照片删除成功");
            //删除对应的GPS.
            Gps gps = new GpsDao(context).getGpsByRecord(getId());
            if (gps == null || gps.delete(context)) {
                System.out.println("GPS数据删除成功");
                //再删记录本身.
                if (new RecordDao(context).delete(this)) {
                    System.out.println("记录数据删除成功");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    public boolean uploadRecord(Context context) {
//        try {
//            String serialNumber = new ProjectDao(context).queryForId(getProjectID()).getSerialNumber();
//            //启动上传记录
//            List<NameValuePair> params = getNameValuePairList(serialNumber);
//            JSONObject json = ServerProxy.invoke(Urls.UPLOAD_RECORD, params);
//            if (json != null) {
//                try {
//                    JSONObject result = json.getJSONObject("result");
//                    Gson gson = new Gson();
//                    JsonResult jsonResult = gson.fromJson(result.toString(), JsonResult.class);
//                    if (jsonResult.getStatus()) {
//                        //如果记录上传成功
//                        Gps gps = new GpsDao(context).getGpsByRecord(getId());
//                        if (gps != null) {  //如果记录对应的GPS 存在 ,并没有上传失败 ,就算这条记录上传完成.
//                            if (!gps.uploadGps(serialNumber)) {
//                                return false;
//                            }
//                        }
//                        DBHelper dbHelper = DBHelper.getInstance(context);
//                        Dao<Record, String> recordDao = dbHelper.getDao(Record.class);
//                        setState("2");
//                        recordDao.update(this);
//                        return true;
//                    } else {
//                        System.out.println(jsonResult.getMessage());
//                    }
//                } catch (JSONException e) {
//                    System.out.println(e.toString());
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("项目获取出错");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    public Map<String, String> getNameValuePairMap(String serialNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("record.projectID", serialNumber);

        params.put("record.updateID", updateID);
        params.put("record.id", id);
        params.put("record.code", code);
        params.put("record.holeID", holeID);
        params.put("record.type", type);
        params.put("record.title", title);
        params.put("record.beginDepth", beginDepth);
        params.put("record.endDepth", endDepth);
        params.put("record.createTime", createTime);
        params.put("record.description", description);
        params.put("record.updateTime", updateTime);
        params.put("record.recordPerson", recordPerson);
        params.put("record.operatePerson", operatePerson);

        params.put("record.frequencyType", frequencyType);
        params.put("record.frequencyMode", frequencyMode);
        params.put("record.aperture", aperture);

        params.put("record.earthType", earthType);
        params.put("record.testType", testType);
        params.put("record.waterDepth", waterDepth);
        params.put("record.getMode", getMode);

        params.put("record.layerType", layerType);
        params.put("record.layerName", layerName);
        params.put("record.weathering", weathering);
        params.put("record.era", era);
        params.put("record.causes", causes);
        params.put("record.wzcf", wzcf);
        params.put("record.ys", ys);
        params.put("record.klzc", klzc);
        params.put("record.klxz", klxz);
        params.put("record.klpl", klpl);
        params.put("record.kljp", kljp);
        params.put("record.sd", sd);
        params.put("record.msd", msd);
        params.put("record.jyx", jyx);
        params.put("record.zt", zt);
        params.put("record.bhw", bhw);
        params.put("record.ftfchd", ftfchd);
        params.put("record.fzntfchd", fzntfchd);
        params.put("record.jc", jc);
        params.put("record.kx", kx);
        params.put("record.czjl", czjl);
        params.put("record.ybljx", ybljx);
        params.put("record.ybljd", ybljd);
        params.put("record.jdljx", jdljx);
        params.put("record.jdljd", jdljd);
        params.put("record.zdlj", zdlj);
        params.put("record.mycf", mycf);
        params.put("record.fhcd", fhcd);
        params.put("record.tcw", tcw);
        params.put("record.kwzc", kwzc);
        params.put("record.zycf", zycf);
        params.put("record.cycf", cycf);
        params.put("record.djnd", djnd);
        params.put("record.hsl", hsl);
        params.put("record.jycd", jycd);
        params.put("record.wzcd", wzcd);
        params.put("record.jbzldj", jbzldj);
        params.put("record.kwx", kwx);
        params.put("record.jglx", jglx);

        params.put("record.powerType", powerType);
        params.put("record.drillLength", drillLength);
        params.put("record.begin1", begin1);
        params.put("record.end1", end1);
        params.put("record.blow1", blow1);
        params.put("record.begin2", begin2);
        params.put("record.end2", end2);
        params.put("record.blow2", blow2);
        params.put("record.begin3", begin3);
        params.put("record.end3", end3);
        params.put("record.blow3", blow3);
        params.put("record.begin4", begin4);
        params.put("record.end4", end4);
        params.put("record.blow4", blow4);

        params.put("record.waterType", waterType);
        params.put("record.shownWaterLevel", shownWaterLevel);
        params.put("record.stillWaterLevel", stillWaterLevel);
        params.put("record.shownTime", shownTime);
        params.put("record.stillTime", stillTime);

        DateUtil.traversal(params);
        return params;
    }

    public static Map<String, String> getMap(List<Record> list, String serialNumber) {
        Map<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Record record = list.get(i);
            map.put("record[" + i + "].projectID", serialNumber);
            map.put("record[" + i + "].id", record.getId());
            map.put("record[" + i + "].code", record.getCode());
            map.put("record[" + i + "].holeID", record.getHoleID());
            if (!TextUtils.isEmpty(record.getUpdateId())) {
                map.put("record[" + i + "].updateID", record.getUpdateId());
            }
            if (!TextUtils.isEmpty(record.getType())) {
                map.put("record[" + i + "].type", record.getType());
            }
            if (!TextUtils.isEmpty(record.getTitle())) {
                map.put("record[" + i + "].title", record.getTitle());
            }
            if (!TextUtils.isEmpty(record.getBeginDepth())) {
                map.put("record[" + i + "].beginDepth", record.getBeginDepth());
            }
            if (!TextUtils.isEmpty(record.getEndDepth())) {
                map.put("record[" + i + "].endDepth", record.getEndDepth());
            }
            if (!TextUtils.isEmpty(record.getCreateTime())) {
                map.put("record[" + i + "].createTime", record.getCreateTime());
            }
            if (!TextUtils.isEmpty(record.getDescription())) {
                map.put("record[" + i + "].description", record.getDescription());
            }
            if (!TextUtils.isEmpty(record.getUpdateTime())) {
                map.put("record[" + i + "].updateTime", record.getUpdateTime());
            }
            if (!TextUtils.isEmpty(record.getRecordPerson())) {
                map.put("record[" + i + "].recordPerson", record.getRecordPerson());
            }
            if (!TextUtils.isEmpty(record.getOperatePerson())) {
                map.put("record[" + i + "].operatePerson", record.getOperatePerson());
            }
            if (!TextUtils.isEmpty(record.getFrequencyType())) {
                map.put("record[" + i + "].frequencyType", record.getFrequencyType());
            }
            if (!TextUtils.isEmpty(record.getFrequencyMode())) {
                map.put("record[" + i + "].frequencyMode", record.getFrequencyMode());
            }
            if (!TextUtils.isEmpty(record.getAperture())) {
                map.put("record[" + i + "].aperture", record.getAperture());
            }
            if (!TextUtils.isEmpty(record.getEarthType())) {
                map.put("record[" + i + "].earthType", record.getEarthType());
            }
            if (!TextUtils.isEmpty(record.getTestType())) {
                map.put("record[" + i + "].testType", record.getTestType());
            }
            if (!TextUtils.isEmpty(record.getWaterDepth())) {
                map.put("record[" + i + "].waterDepth", record.getWaterDepth());
            }
            if (!TextUtils.isEmpty(record.getGetMode())) {
                map.put("record[" + i + "].getMode", record.getGetMode());
            }
            if (!TextUtils.isEmpty(record.getLayerType())) {
                map.put("record[" + i + "].layerType", record.getLayerType());
            }
            if (!TextUtils.isEmpty(record.getLayerName())) {
                map.put("record[" + i + "].layerName", record.getLayerName());
            }
            if (!TextUtils.isEmpty(record.getWeathering())) {
                map.put("record[" + i + "].weathering", record.getWeathering());
            }
            if (!TextUtils.isEmpty(record.getEra())) {
                map.put("record[" + i + "].era", record.getEra());
            }
            if (!TextUtils.isEmpty(record.getCauses())) {
                map.put("record[" + i + "].causes", record.getCauses());
            }
            if (!TextUtils.isEmpty(record.getWzcf())) {
                map.put("record[" + i + "].wzcf", record.getWzcf());
            }
            if (!TextUtils.isEmpty(record.getYs())) {
                map.put("record[" + i + "].ys", record.getYs());
            }
            if (!TextUtils.isEmpty(record.getKlzc())) {
                map.put("record[" + i + "].klzc", record.getKlzc());
            }
            if (!TextUtils.isEmpty(record.getKlxz())) {
                map.put("record[" + i + "].klxz", record.getKlxz());
            }
            if (!TextUtils.isEmpty(record.getKlpl())) {
                map.put("record[" + i + "].klpl", record.getKlpl());
            }
            if (!TextUtils.isEmpty(record.getKljp())) {
                map.put("record[" + i + "].kljp", record.getKljp());
            }
            if (!TextUtils.isEmpty(record.getSd())) {
                map.put("record[" + i + "].sd", record.getSd());
            }
            if (!TextUtils.isEmpty(record.getMsd())) {
                map.put("record[" + i + "].msd", record.getMsd());
            }
            if (!TextUtils.isEmpty(record.getJyx())) {
                map.put("record[" + i + "].jyx", record.getJyx());
            }
            if (!TextUtils.isEmpty(record.getZt())) {
                map.put("record[" + i + "].zt", record.getZt());
            }
            if (!TextUtils.isEmpty(record.getBhw())) {
                map.put("record[" + i + "].bhw", record.getBhw());
            }
            if (!TextUtils.isEmpty(record.getFtfchd())) {
                map.put("record[" + i + "].ftfchd", record.getFtfchd());
            }
            if (!TextUtils.isEmpty(record.getFzntfchd())) {
                map.put("record[" + i + "].fzntfchd", record.getFzntfchd());
            }
            if (!TextUtils.isEmpty(record.getJc())) {
                map.put("record[" + i + "].jc", record.getJc());
            }
            if (!TextUtils.isEmpty(record.getKx())) {
                map.put("record[" + i + "].kx", record.getKx());
            }
            if (!TextUtils.isEmpty(record.getCzjl())) {
                map.put("record[" + i + "].czjl", record.getCzjl());
            }
            if (!TextUtils.isEmpty(record.getYbljx())) {
                map.put("record[" + i + "].ybljx", record.getYbljx());
            }
            if (!TextUtils.isEmpty(record.getYbljd())) {
                map.put("record[" + i + "].ybljd", record.getYbljd());
            }
            if (!TextUtils.isEmpty(record.getJdljx())) {
                map.put("record[" + i + "].jdljx", record.getJdljx());
            }
            if (!TextUtils.isEmpty(record.getJdljd())) {
                map.put("record[" + i + "].jdljd", record.getJdljd());
            }
            if (!TextUtils.isEmpty(record.getZdlj())) {
                map.put("record[" + i + "].zdlj", record.getZdlj());
            }
            if (!TextUtils.isEmpty(record.getMycf())) {
                map.put("record[" + i + "].mycf", record.getMycf());
            }
            if (!TextUtils.isEmpty(record.getFhcd())) {
                map.put("record[" + i + "].fhcd", record.getFhcd());
            }
            if (!TextUtils.isEmpty(record.getTcw())) {
                map.put("record[" + i + "].tcw", record.getTcw());
            }
            if (!TextUtils.isEmpty(record.getKwzc())) {
                map.put("record[" + i + "].kwzc", record.getKwzc());
            }
            if (!TextUtils.isEmpty(record.getZycf())) {
                map.put("record[" + i + "].zycf", record.getZycf());
            }
            if (!TextUtils.isEmpty(record.getCycf())) {
                map.put("record[" + i + "].cycf", record.getCycf());
            }
            if (!TextUtils.isEmpty(record.getDjnd())) {
                map.put("record[" + i + "].djnd", record.getDjnd());
            }
            if (!TextUtils.isEmpty(record.getHsl())) {
                map.put("record[" + i + "].hsl", record.getHsl());
            }
            if (!TextUtils.isEmpty(record.getJycd())) {
                map.put("record[" + i + "].jycd", record.getJycd());
            }
            if (!TextUtils.isEmpty(record.getWzcd())) {
                map.put("record[" + i + "].wzcd", record.getWzcd());
            }
            if (!TextUtils.isEmpty(record.getJbzldj())) {
                map.put("record[" + i + "].jbzldj", record.getJbzldj());
            }
            if (!TextUtils.isEmpty(record.getKwx())) {
                map.put("record[" + i + "].kwx", record.getKwx());
            }
            if (!TextUtils.isEmpty(record.getJglx())) {
                map.put("record[" + i + "].jglx", record.getJglx());
            }
            if (!TextUtils.isEmpty(record.getPowerType())) {
                map.put("record[" + i + "].powerType", record.getPowerType());
            }
            if (!TextUtils.isEmpty(record.getDrillLength())) {
                map.put("record[" + i + "].drillLength", record.getDrillLength());
            }
            if (!TextUtils.isEmpty(record.getBegin1())) {
                map.put("record[" + i + "].begin1", record.getBegin1());
            }
            if (!TextUtils.isEmpty(record.getEnd1())) {
                map.put("record[" + i + "].end1", record.getEnd1());
            }
            if (!TextUtils.isEmpty(record.getBlow1())) {
                map.put("record[" + i + "].blow1", record.getBlow1());
            }
            if (!TextUtils.isEmpty(record.getBegin2())) {
                map.put("record[" + i + "].begin2", record.getBegin2());
            }
            if (!TextUtils.isEmpty(record.getEnd2())) {
                map.put("record[" + i + "].end2", record.getEnd2());
            }
            if (!TextUtils.isEmpty(record.getBlow2())) {
                map.put("record[" + i + "].blow2", record.getBlow2());
            }
            if (!TextUtils.isEmpty(record.getBegin3())) {
                map.put("record[" + i + "].begin3", record.getBegin3());
            }
            if (!TextUtils.isEmpty(record.getEnd3())) {
                map.put("record[" + i + "].end3", record.getEnd3());
            }
            if (!TextUtils.isEmpty(record.getBlow3())) {
                map.put("record[" + i + "].blow3", record.getBlow3());
            }
            if (!TextUtils.isEmpty(record.getBegin4())) {
                map.put("record[" + i + "].begin4", record.getBegin4());
            }
            if (!TextUtils.isEmpty(record.getEnd4())) {
                map.put("record[" + i + "].end4", record.getEnd4());
            }
            if (!TextUtils.isEmpty(record.getBlow4())) {
                map.put("record[" + i + "].blow4", record.getBlow4());
            }
            if (!TextUtils.isEmpty(record.getWaterType())) {
                map.put("record[" + i + "].waterType", record.getWaterType());
            }
            if (!TextUtils.isEmpty(record.getShownWaterLevel())) {
                map.put("record[" + i + "].shownWaterLevel", record.getShownWaterLevel());
            }
            if (!TextUtils.isEmpty(record.getStillWaterLevel())) {
                map.put("record[" + i + "].stillWaterLevel", record.getStillWaterLevel());
            }
            if (!TextUtils.isEmpty(record.getShownTime())) {
                map.put("record[" + i + "].shownTime", record.getShownTime());
            }
            if (!TextUtils.isEmpty(record.getStillTime())) {
                map.put("record[" + i + "].stillTime", record.getStillTime());
            }

        }
        return map;
    }

    public void mapPut() {

    }

    public void init(Hole hole) {
        this.id = Common.getUUID();
        System.out.println("id-------------->:" + this.id);
        this.projectID = hole.getProjectID();
        this.holeID = hole.getId();
        this.createTime = DateUtil.date2Str(new Date());       //添加时间
        this.updateTime = DateUtil.date2Str(new Date());       //更新时间
        if ("".equals(this.code)) {
            this.code = "新建记录";
        }
        this.isDelete = "0";
    }


    //给该加密的数据加密
    public void jiaMi() {
        try {
            this.code = Key.jiaMi(this.code);
            this.type = Key.jiaMi(this.type);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public String getContentType() {
        String contentType = " ";
        if (type.equals(TYPE_LAYER)) {
            contentType = layerName;
        } else {
            contentType = type;
        }
        return contentType;
    }

    public String getContent() {
        String content = " ";
        String recordType = type;

        if (recordType.equals(TYPE_FREQUENCY)) {
            if (isNull(frequencyType))
                content += "<font color='black'>钻进方法:</font>" + frequencyType + ";&#12288;";
            if (isNull(frequencyMode))
                content += "<font color='black'>护壁方法:</font>" + frequencyMode + ";&#12288;";
            if (isNull(aperture)) content += "<font color='black'>钻孔孔径:</font>" + aperture + "mm;";

        } else if (recordType.equals(TYPE_LAYER)) {
            if (isNull(layerType))
                content += "<font color='black'>岩土类型:</font>" + layerType + ";&#12288;";
            if (isNull(layerName))
                content += "<font color='black'>岩土定名:</font>" + layerName + ";&#12288;";
            if (isNull(era)) content += "<font color='black'>地层年代:</font>" + era + ";&#12288;";
            if (isNull(causes))
                content += "<font color='black'>地层成因:</font>" + causes + ";&#12288;";
            if (layerType.equals(TYPE_TT)) {
                if (isNull(zycf))
                    content += "<font color='black'>主要成份:</font>" + zycf + ";&#12288;";
                if (isNull(cycf))
                    content += "<font color='black'>次要成份:</font>" + cycf + ";&#12288;";
                if (isNull(djnd))
                    content += "<font color='black'>堆积年代:</font>" + djnd + ";&#12288;";
                if (isNull(msd)) content += "<font color='black'>密实度:</font>" + msd + ";&#12288;";
                if (isNull(jyx)) content += "<font color='black'>均匀性:</font>" + jyx + ";&#12288;";
            } else if (layerType.equals(TYPE_CTT)) {
                if (isNull(wzcf))
                    content += "<font color='black'>物质成份:</font>" + wzcf + ";&#12288;";
                if (isNull(djnd))
                    content += "<font color='black'>堆积年代:</font>" + djnd + ";&#12288;";
                if (isNull(msd)) content += "<font color='black'>密实度:</font>" + msd + ";&#12288;";
                if (isNull(jyx)) content += "<font color='black'>均匀性:</font>" + jyx + ";&#12288;";
            } else if (layerType.equals(TYPE_NXT)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(zt)) content += "<font color='black'>状态:</font>" + zt + ";&#12288;";
                if (isNull(bhw)) content += "<font color='black'>包含物:</font>" + bhw + ";&#12288;";
                if (isNull(jc)) content += "<font color='black'>夹层:</font>" + jc + ";&#12288;";
            } else if (layerType.equals(TYPE_FT)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(bhw)) content += "<font color='black'>包含物:</font>" + bhw + ";&#12288;";
                if (isNull(jc)) content += "<font color='black'>夹层:</font>" + jc + ";&#12288;";
                if (isNull(sd)) content += "<font color='black'>湿度:</font>" + sd + ";&#12288;";
                if (isNull(msd)) content += "<font color='black'>密实度:</font>" + msd + ";&#12288;";
            } else if (layerType.equals(TYPE_FNHC)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(bhw)) content += "<font color='black'>包含物:</font>" + bhw + ";&#12288;";
                if (isNull(zt)) content += "<font color='black'>状态:</font>" + zt + ";&#12288;";
                if (isNull(ftfchd))
                    content += "<font color='black'>粉土分层厚度:</font>" + ftfchd + ";&#12288;";
                if (isNull(fzntfchd))
                    content += "<font color='black'>粉质黏土分层厚度:</font>" + fzntfchd + ";&#12288;";
            } else if (layerType.equals(TYPE_HTZNXT)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(zt)) content += "<font color='black'>状态:</font>" + zt + ";&#12288;";
                if (isNull(kx)) content += "<font color='black'>孔隙:</font>" + kx + ";&#12288;";
                if (isNull(czjl))
                    content += "<font color='black'>垂直节理:</font>" + czjl + ";&#12288;";
                if (isNull(bhw)) content += "<font color='black'>包含物:</font>" + bhw + ";&#12288;";
            } else if (layerType.equals(TYPE_HTZFT)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(msd)) content += "<font color='black'>密实度:</font>" + msd + ";&#12288;";
                if (isNull(klzc))
                    content += "<font color='black'>颗粒组成:</font>" + klzc + ";&#12288;";
                if (isNull(kx)) content += "<font color='black'>孔隙:</font>" + kx + ";&#12288;";
                if (isNull(czjl))
                    content += "<font color='black'>垂直节理:</font>" + czjl + ";&#12288;";
                if (isNull(bhw)) content += "<font color='black'>包含物:</font>" + bhw + ";&#12288;";
            } else if (layerType.equals(TYPE_YN)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(bhw)) content += "<font color='black'>包含物:</font>" + bhw + ";&#12288;";
                if (isNull(hsl)) content += "<font color='black'>含水量:</font>" + hsl + ";&#12288;";
                if (isNull(zt)) content += "<font color='black'>状态:</font>" + zt + ";&#12288;";
            } else if (layerType.equals(TYPE_SST)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(klxz))
                    content += "<font color='black'>颗粒形状:</font>" + klxz + ";&#12288;";
                if (isNull(klpl))
                    content += "<font color='black'>颗粒排列:</font>" + klpl + ";&#12288;";
                if (isNull(ybljx))
                    content += "<font color='black'>一般粒径小:</font>" + ybljx + ";&#12288;";
                if (isNull(ybljd))
                    content += "<font color='black'>一般粒径大:</font>" + ybljd + ";&#12288;";
                if (isNull(jdljx))
                    content += "<font color='black'>较大粒径小:</font>" + jdljx + ";&#12288;";
                if (isNull(jdljd))
                    content += "<font color='black'>较大粒径大:</font>" + jdljd + ";&#12288;";
                if (isNull(zdlj))
                    content += "<font color='black'>最大粒径:</font>" + zdlj + ";&#12288;";
                if (isNull(mycf))
                    content += "<font color='black'>岩母成份:</font>" + mycf + ";&#12288;";
                if (isNull(fhcd))
                    content += "<font color='black'>风化程度:</font>" + fhcd + ";&#12288;";
                if (isNull(kljp))
                    content += "<font color='black'>颗粒级配:</font>" + kljp + ";&#12288;";

                if (isNull(msd)) content += "<font color='black'>密实度:</font>" + msd + ";&#12288;";
                if (isNull(tcw)) content += "<font color='black'>充填物:</font>" + tcw + ";&#12288;";
                if (isNull(sd)) content += "<font color='black'>湿度:</font>" + sd + ";&#12288;";
                if (isNull(jc)) content += "<font color='black'>夹层:</font>" + jc + ";&#12288;";
            } else if (layerType.equals(TYPE_ST)) {
                if (isNull(kwzc))
                    content += "<font color='black'>矿物组成:</font>" + kwzc + ";&#12288;";
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(kljp))
                    content += "<font color='black'>颗粒级配:</font>" + kljp + ";&#12288;";
                if (isNull(klxz))
                    content += "<font color='black'>颗粒形状:</font>" + klxz + ";&#12288;";
                if (isNull(sd)) content += "<font color='black'>湿度:</font>" + sd + ";&#12288;";
                if (isNull(msd)) content += "<font color='black'>密实度:</font>" + msd + ";&#12288;";
            } else if (layerType.equals(TYPE_YS)) {
                if (isNull(ys)) content += "<font color='black'>颜色:</font>" + ys + ";&#12288;";
                if (isNull(jycd))
                    content += "<font color='black'>坚硬程度:</font>" + jycd + ";&#12288;";
                if (isNull(wzcd))
                    content += "<font color='black'>完整程度:</font>" + wzcd + ";&#12288;";
                if (isNull(jbzldj))
                    content += "<font color='black'>基本质量等级:</font>" + jbzldj + ";&#12288;";
                if (isNull(fhcd))
                    content += "<font color='black'>风化程度:</font>" + fhcd + ";&#12288;";
                if (isNull(kwx)) content += "<font color='black'>可挖行:</font>" + kwx + ";&#12288;";
                if (isNull(jglx))
                    content += "<font color='black'>结构类型:</font>" + jglx + ";&#12288;";
            }
        } else if (recordType.equals(TYPE_GET_EARTH)) {
            if (isNull(earthType))
                content += "<font color='black'>土样质量等级:</font>" + earthType + ";";
            if (isNull(getMode))
                content += "<br/><font color='black'>取样工具和方法:</font>" + getMode + ";&#12288;";
        } else if (recordType.equals(TYPE_GET_WATER)) {
            if (isNull(getMode))
                content += "<font color='black'>取水方式:</font>" + getMode + ";&#12288;";
        } else if (recordType.equals(TYPE_DPT)) {
            if (isNull(drillLength))
                content += "<font color='black'>钻杆长度:</font>" + drillLength + "m;&#12288;";
            if (isNull(powerType))
                content += "<font color='black'>" + getPowerTypeCode() + "=</font>";
            if (isNull(blow1)) content += "<font color='black'>" + blow1 + ";</font>";
        } else if (recordType.equals(TYPE_SPT)) {
            if (isNull(drillLength))
                content += "<font color='black'>钻杆长度:</font>" + drillLength + "m;";
            if (isNull(begin1) && isNull(end1) && isNull(blow1))
                content += "<br/><font color='black'> 预:</font>" + begin1 + "m~" + end1 + "m=" + blow1 + "";
            if (isNull(begin2) && isNull(end2) && isNull(blow2))
                content += "<br/><font color='black'>&nbsp;&nbsp;1:</font>" + begin2 + "m~" + end2 + "m=" + blow2 + "";
            if (isNull(begin3) && isNull(end3) && isNull(blow3))
                content += "<br/><font color='black'>&nbsp;&nbsp;2:</font>" + begin3 + "m~" + end3 + "m=" + blow3 + "";
            if (isNull(begin4) && isNull(end4) && isNull(blow4))
                content += "<br/><font color='black'>&nbsp;&nbsp;3:</font>" + begin4 + "m~" + end4 + "m=" + blow4 + "";
        } else if (recordType.equals(TYPE_WATER)) {
            if (isNull(waterType))
                content += "<font color='black'>地下水类型:</font>" + waterType + ";&#12288;";
            if (isNull(shownWaterLevel))
                content += "<font color='black'>初见水位:</font>" + shownWaterLevel + "m;     ";
            if (isNull(shownTime))
                content += "<font color='black'>初见时间:</font>" + shownTime + ";&#12288;";
            if (isNull(stillWaterLevel))
                content += "<font color='black'>稳定水位:</font>" + stillWaterLevel + "m;     ";
            if (isNull(stillTime))
                content += "<font color='black'>稳定时间:</font>" + stillTime + ";&#12288;";
        } else if (recordType.equals(TYPE_SCENE)) {
            content = "BZ";
        } else {
            content = "HC";
        }
        if (isNull(description))
            content += "<br/><font color='black'>其他描述:</font>" + description + ";";
        this.setTitle(content);
        return content;
    }

    public boolean isNull(String s) {
        if (s != null && !"".equals(s)) {
            return true;
        } else {
            return false;
        }
    }

    public String getBeginDepth() {
        return beginDepth;
    }

    public void setBeginDepth(String beginDepth) {
        this.beginDepth = beginDepth;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndDepth() {
        return endDepth;
    }

    public void setEndDepth(String endDepth) {
        this.endDepth = endDepth;
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

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }


    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getRecordPerson() {
        return recordPerson;
    }

    public void setRecordPerson(String recordPerson) {
        this.recordPerson = recordPerson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getFrequencyMode() {
        return frequencyMode;
    }

    public void setFrequencyMode(String mode) {
        this.frequencyMode = mode;
    }

    public String getEarthType() {
        return earthType;
    }

    public void setEarthType(String earthType) {
        this.earthType = earthType;
    }

    public String getGetMode() {
        return getMode;
    }

    public void setGetMode(String mode) {
        this.getMode = mode;
    }

    public String getWaterDepth() {
        return waterDepth;
    }

    public void setWaterDepth(String waterDepth) {
        this.waterDepth = waterDepth;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getWeathering() {
        return weathering;
    }

    public void setWeathering(String weathering) {
        this.weathering = weathering;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getCauses() {
        return causes;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public String getWzcf() {
        return wzcf;
    }

    public void setWzcf(String wzcf) {
        this.wzcf = wzcf;
    }

    public String getYs() {
        return ys;
    }

    public void setYs(String ys) {
        this.ys = ys;
    }

    public String getKlzc() {
        return klzc;
    }

    public void setKlzc(String klzc) {
        this.klzc = klzc;
    }

    public String getKlxz() {
        return klxz;
    }

    public void setKlxz(String klxz) {
        this.klxz = klxz;
    }

    public String getKlpl() {
        return klpl;
    }

    public void setKlpl(String klpl) {
        this.klpl = klpl;
    }

    public String getKljp() {
        return kljp;
    }

    public void setKljp(String kljp) {
        this.kljp = kljp;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getMsd() {
        return msd;
    }

    public void setMsd(String msd) {
        this.msd = msd;
    }

    public String getJyx() {
        return jyx;
    }

    public void setJyx(String jyx) {
        this.jyx = jyx;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getBhw() {
        return bhw;
    }

    public void setBhw(String bhw) {
        this.bhw = bhw;
    }

    public String getFtfchd() {
        return ftfchd;
    }

    public void setFtfchd(String ftfchd) {
        this.ftfchd = ftfchd;
    }

    public String getFzntfchd() {
        return fzntfchd;
    }

    public void setFzntfchd(String fzntfchd) {
        this.fzntfchd = fzntfchd;
    }

    public String getJc() {
        return jc;
    }

    public void setJc(String jc) {
        this.jc = jc;
    }

    public String getKx() {
        return kx;
    }

    public void setKx(String kx) {
        this.kx = kx;
    }

    public String getCzjl() {
        return czjl;
    }

    public void setCzjl(String czjl) {
        this.czjl = czjl;
    }

    public String getYbljx() {
        return ybljx;
    }

    public void setYbljx(String ybljx) {
        this.ybljx = ybljx;
    }

    public String getYbljd() {
        return ybljd;
    }

    public void setYbljd(String ybljd) {
        this.ybljd = ybljd;
    }

    public String getJdljx() {
        return jdljx;
    }

    public void setJdljx(String jdljx) {
        this.jdljx = jdljx;
    }

    public String getJdljd() {
        return jdljd;
    }

    public void setJdljd(String jdljd) {
        this.jdljd = jdljd;
    }

    public String getZdlj() {
        return zdlj;
    }

    public void setZdlj(String zdlj) {
        this.zdlj = zdlj;
    }

    public String getMycf() {
        return mycf;
    }

    public void setMycf(String mycf) {
        this.mycf = mycf;
    }

    public String getFhcd() {
        return fhcd;
    }

    public void setFhcd(String fhcd) {
        this.fhcd = fhcd;
    }

    public String getTcw() {
        return tcw;
    }

    public void setTcw(String tcw) {
        this.tcw = tcw;
    }

    public String getKwzc() {
        return kwzc;
    }

    public void setKwzc(String kwzc) {
        this.kwzc = kwzc;
    }

    public String getZycf() {
        return zycf;
    }

    public void setZycf(String zycf) {
        this.zycf = zycf;
    }

    public String getCycf() {
        return cycf;
    }

    public void setCycf(String cycf) {
        this.cycf = cycf;
    }

    public String getDjnd() {
        return djnd;
    }

    public void setDjnd(String djnd) {
        this.djnd = djnd;
    }

    public String getHsl() {
        return hsl;
    }

    public void setHsl(String hsl) {
        this.hsl = hsl;
    }

    public String getJycd() {
        return jycd;
    }

    public void setJycd(String jycd) {
        this.jycd = jycd;
    }

    public String getWzcd() {
        return wzcd;
    }

    public void setWzcd(String wzcd) {
        this.wzcd = wzcd;
    }

    public String getJbzldj() {
        return jbzldj;
    }

    public void setJbzldj(String jbzldj) {
        this.jbzldj = jbzldj;
    }

    public String getKwx() {
        return kwx;
    }

    public void setKwx(String kwx) {
        this.kwx = kwx;
    }

    public String getJglx() {
        return jglx;
    }

    public void setJglx(String jglx) {
        this.jglx = jglx;
    }


    public String getPowerType() {
        return powerType;
    }

    public String getPowerTypeCode() {
        if (TYPE_1.equals(powerType)) {
            return TYPE_1_CODE;
        } else if (TYPE_2.equals(powerType)) {
            return TYPE_2_CODE;
        } else if (TYPE_3.equals(powerType)) {
            return TYPE_3_CODE;
        }
        return "";
    }

    public void setPowerType(String powerType) {
        this.powerType = powerType;
    }

    public String getDrillLength() {
        return drillLength;
    }

    public void setDrillLength(String drillLength) {
        this.drillLength = drillLength;
    }

    public String getBegin1() {
        return begin1;
    }

    public void setBegin1(String begin1) {
        this.begin1 = begin1;
    }

    public String getEnd1() {
        return end1;
    }

    public void setEnd1(String end1) {
        this.end1 = end1;
    }

    public String getBlow1() {
        return blow1;
    }

    public void setBlow1(String blow1) {
        this.blow1 = blow1;
    }

    public String getBegin2() {
        return begin2;
    }

    public void setBegin2(String begin2) {
        this.begin2 = begin2;
    }

    public String getEnd2() {
        return end2;
    }

    public void setEnd2(String end2) {
        this.end2 = end2;
    }

    public String getBlow2() {
        return blow2;
    }

    public void setBlow2(String blow2) {
        this.blow2 = blow2;
    }

    public String getBegin3() {
        return begin3;
    }

    public void setBegin3(String begin3) {
        this.begin3 = begin3;
    }

    public String getEnd3() {
        return end3;
    }

    public void setEnd3(String end3) {
        this.end3 = end3;
    }

    public String getBlow3() {
        return blow3;
    }

    public void setBlow3(String blow3) {
        this.blow3 = blow3;
    }

    public String getBegin4() {
        return begin4;
    }

    public void setBegin4(String begin4) {
        this.begin4 = begin4;
    }

    public String getEnd4() {
        return end4;
    }

    public void setEnd4(String end4) {
        this.end4 = end4;
    }

    public String getBlow4() {
        return blow4;
    }

    public void setBlow4(String blow4) {
        this.blow4 = blow4;
    }


    public String getShownWaterLevel() {
        return shownWaterLevel;
    }

    public void setShownWaterLevel(String shownWaterLevel) {
        this.shownWaterLevel = shownWaterLevel;
    }

    public String getStillWaterLevel() {
        return stillWaterLevel;
    }

    public void setStillWaterLevel(String stillWaterLevel) {
        this.stillWaterLevel = stillWaterLevel;
    }

    public String getShownTime() {
        return shownTime;
    }

    public void setShownTime(String shownTime) {
        this.shownTime = shownTime;
    }

    public String getStillTime() {
        return stillTime;
    }

    public void setStillTime(String stillTime) {
        this.stillTime = stillTime;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }


    public int getNotUploadCount() {
        return notUploadCount;
    }

    public void setNotUploadCount(int notUploadCount) {
        this.notUploadCount = notUploadCount;
    }

    public int getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(int uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public String getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(String mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getUpdateId() {
        return updateID;
    }

    public void setUpdateId(String updateId) {
        this.updateID = updateId;
    }

    @Override
    public Object clone() {
        Record stu = null;
        try {
            stu = (Record) super.clone();
            stu.id = Common.getUUID();
            stu.updateID = this.getId();
            stu.state = "1";//到这里，都是说明将要保存这条记录，并打算上传，状态设置为1

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }
}
