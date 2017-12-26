package com.geotdb.compile.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;

import com.geotdb.compile.utils.Key;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.L;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 本地用户
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "hole")//"井孔规则表")
public class Hole implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";                //勘探点ID
    @DatabaseField
    String code = "";                //勘探点编号
    @DatabaseField
    String projectID = "";        //所属项目
    @DatabaseField
    String attribute = "";        //勘探点性质
    @DatabaseField
    String createTime = "";        //勘探点添加时间
    @DatabaseField
    String updateTime = "";        //勘探点更新时间  新加
    @DatabaseField
    String uploadTime = "";        //数据库录入时间
    @DatabaseField
    String description = "";        //勘探点描述
    @DatabaseField
    String type = "1";            //勘探点类型1.探井2.钻孔
    @DatabaseField
    String depth = "";            //钻孔深度--设计孔深
    @DatabaseField
    String elevation = "";        //孔口高程(标高)
    @DatabaseField
    String shownWaterLevel = "";    //初见水位
    @DatabaseField
    String stillWaterLevel = "";    //静止水位
    @DatabaseField
    String beginTime = "";        //钻孔开始时间
    @DatabaseField
    String endTime = "";            //钻孔结束时间
    @DatabaseField
    String state = "1";            //钻孔状态  0临时 1.记录中、未开始 2.已上传 3.已提交验收
    @DatabaseField
    String inputPerson = "";        //记录员
    @DatabaseField
    String operatePerson = "";    //机长
    @DatabaseField
    String operateCode = "";        //设备编号
    @DatabaseField
    String isDelete = "0";        //是否删除

    @DatabaseField
    String longitude = "";        //勘察点经度
    @DatabaseField
    String latitude = "";         //勘察点纬度
    @DatabaseField
    String radius = "";            //靶值

    @DatabaseField
    String locationState = "1";    //定位状态0.已定位1.未定位

    @DatabaseField
    String projectName = "";        //项目名称
    @DatabaseField
    String recordsCount = "0";    //拥有记录数

    @DatabaseField
    String mapLocation = "";      //在地图大概所在
    @DatabaseField
    String mapZoom = "";          //地图缩放程度
    @DatabaseField
    String mapLatitude = "";      //基准地图中心纬度
    @DatabaseField
    String mapLongitude = "";    //基准地图中心经度
    @DatabaseField
    String mapTime = "";          //基准时间
    @DatabaseField
    String mapPic = "";          //所在地截图

    int uploadedCount;          //已上传
    int notUploadCount;         //未上传

    String currentDepth;            //进尺深度
    @DatabaseField
    String relateID = "";         //储存关联到的ID，
    @DatabaseField
    String relateCode = "";         //关联的code
    @DatabaseField
    String userID;         //下载的hole中带有userID，判断是否是自己的项目

    List<LocalUser> userList; //关联勘察点获取的已关联用户
    List<Record> recordList; //下载hole时，封装数据
    @DatabaseField
    String uploaded = ""; //0没上传过、1已经上传过
    int userCount;//relateHoleDialog 中对list进行排序

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    public Hole() {

    }

    //生成map类型参数
    public Map<String, String> getNameValuePairMap(String serialNumber) {
        Map<String, String> params = new ConcurrentHashMap<>();
        params.put("hole.projectID", serialNumber);
        params.put("hole.relateID", relateID);
        params.put("hole.id", id);
        params.put("hole.code", code);
        params.put("hole.attribute", attribute == null ? "" : attribute);
        params.put("hole.createTime", createTime == null ? "" : createTime);
        params.put("hole.updateTime", updateTime == null ? "" : updateTime);
        params.put("hole.type", type == null ? "" : type);
        params.put("hole.elevation", elevation == null ? "" : elevation);
        params.put("hole.beginTime", beginTime == null ? "" : beginTime);
        params.put("hole.endTime", endTime == null ? "" : endTime);
        params.put("hole.inputPerson", inputPerson == null ? "" : inputPerson);
        params.put("hole.operatePerson", operatePerson == null ? "" : operatePerson);
        params.put("hole.operateCode", operateCode == null ? "" : operateCode);

        params.put("hole.longitude", longitude == null ? "" : longitude);
        params.put("hole.latitude", latitude == null ? "" : latitude);
        params.put("hole.radius", radius == null ? "" : radius);
        params.put("hole.locationState", locationState == null ? "" : locationState);
        params.put("hole.projectName", projectName == null ? "" : projectName);
        params.put("hole.recordsCount", recordsCount == null ? "" : recordsCount);

        params.put("hole.mapLatitude", mapLatitude == null ? "" : mapLatitude);
        params.put("hole.mapLongitude", mapLongitude == null ? "" : mapLongitude);
        params.put("hole.mapTime", mapTime == null ? "" : mapTime);
        DateUtil.traversal(params);
        return params;

    }

    public static Map<String, String> getMap(List<Hole> list, String serialNumber) {
        Map<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put("hole[" + i + "].projectID", serialNumber);
            map.put("hole[" + i + "].id", list.get(i).getId());
            map.put("hole[" + i + "].code", list.get(i).getCode());
            map.put("hole[" + i + "].attribute", list.get(i).getAttribute());
            map.put("hole[" + i + "].createTime", list.get(i).getCreateTime());
            map.put("hole[" + i + "].updateTime", list.get(i).getUpdateTime());
            map.put("hole[" + i + "].description", list.get(i).getDescription());
            map.put("hole[" + i + "].type", list.get(i).getType());
            map.put("hole[" + i + "].elevation", list.get(i).getElevation());
            map.put("hole[" + i + "].beginTime", list.get(i).getBeginTime());
            map.put("hole[" + i + "].endTime", list.get(i).getEndTime());
            map.put("hole[" + i + "].inputPerson", list.get(i).getInputPerson());
            map.put("hole[" + i + "].operatePerson", list.get(i).getOperatePerson());
            map.put("hole[" + i + "].operateCode", list.get(i).getOperateCode());
            map.put("hole[" + i + "].longitude", list.get(i).getLongitude());
            map.put("hole[" + i + "].latitude", list.get(i).getLatitude());
            map.put("hole[" + i + "].radius", list.get(i).getRadius());
            map.put("hole[" + i + "].locationState", list.get(i).getLocationState());
            map.put("hole[" + i + "].projectName", list.get(i).getProjectName());
            map.put("hole[" + i + "].recordsCount", list.get(i).getRecordsCount());
            map.put("hole[" + i + "].mapLatitude", list.get(i).getMapLatitude());
            map.put("hole[" + i + "].mapLongitude", list.get(i).getMapLongitude());
            map.put("hole[" + i + "].mapTime", list.get(i).getMapTime());
        }
        return map;
    }

    //给该加密的数据加密
    public void jiaMi() {
//		try {
//			this.code = Key.jiaMi(this.code);
//			this.projectID = Key.jiaMi(this.projectID);
//			this.type = Key.jiaMi(this.type);
//			this.elevation = Key.jiaMi(this.elevation);
//			this.depth = Key.jiaMi(this.depth);
//			this.shownWaterLevel = Key.jiaMi(this.shownWaterLevel);
//			this.stillWaterLevel = Key.jiaMi(this.stillWaterLevel);
//			this.longitude = Key.jiaMi(this.longitude);
//			this.latitude = Key.jiaMi(this.latitude);
//			this.operatePerson = Key.jiaMi(this.operatePerson);
//			this.inputPerson = Key.jiaMi(this.inputPerson);
//			this.uploadTime = Key.jiaMi(this.uploadTime);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
    }

    //给该解密的数据解密
    public void jieMi() {
//		try {
//			this.code = Key.jieMi(this.code);
//			this.projectID = Key.jieMi(this.projectID);
//			this.type = Key.jieMi(this.type);
//			this.elevation = Key.jieMi(this.elevation);
//			this.depth = Key.jieMi(this.depth);
//			this.shownWaterLevel = Key.jieMi(this.shownWaterLevel);
//			this.stillWaterLevel = Key.jieMi(this.stillWaterLevel);
//			this.longitude = Key.jieMi(this.longitude);
//			this.latitude = Key.jieMi(this.latitude);
//			this.operatePerson = Key.jieMi(this.operatePerson);
//			this.inputPerson = Key.jieMi(this.inputPerson);
//			this.uploadTime = Key.jieMi(this.uploadTime);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
    }

    public Hole(Context context, String projectID) {
        try {
            this.id = Common.getUUID();

            HashMap codeMap = getCodeMap(context, projectID);
            DecimalFormat df = new DecimalFormat("000");
            int codeInt = 1;
            String codeStr0 = "ZK-";
            String codeStr = codeStr0 + df.format(codeInt);

            while (codeMap.containsKey(codeStr)) {
                codeInt += 1;
                codeStr = codeStr0 + df.format(codeInt);
            }
            this.code = codeStr;

            this.projectID = projectID;

            this.type = "钻孔";

            this.state = "0";//新建一个临时点

            this.recordsCount = "0";

            this.createTime = DateUtil.date2Str(new Date());       //添加时间
            this.updateTime = DateUtil.date2Str(new Date());       //更新时间
            this.radius = "15";

            this.mapLocation = "";
            this.mapZoom = "";
            this.mapLatitude = "";
            this.mapLongitude = "";
            this.mapPic = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getCodeMap(Context context, String projectID) {
        HashMap hashmap = new HashMap();
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            GenericRawResults<String> results = dao.queryRaw("select code from hole where code like '__-___'and projectID='" + projectID + "'", new RawRowMapper<String>() {
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
     * 想办法验证将要保存的编号数据库里是否存在
     *
     * @param context
     * @return
     */
    public List<Hole> getHoleByCode(Context context, String newCode, String projectID) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            return dao.queryBuilder().where().eq("code", newCode).and().eq("projectID", projectID).and().eq("relateID","").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除勘探点
     *
     * @param context
     */
    public boolean delete(Context context) {
        try {
            //先删除所有记录.
            List<Record> records = new RecordDao(context).getRecordListByHoleID(id);
            for (Record record : records) {
                record.delete(context);
            }
            L.e("记录删除成功");
            if (new HoleDao(context).delete(this)) {
                L.e("勘探点数据删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 复制勘探点
     *
     * @param context
     */
    public boolean copy(Context context, int i) {
        try {
            for (int j = 0; j < i; j++) {
                Hole hole = new Hole(context, this.projectID);

                float mapLatitude0 = Float.valueOf(mapLatitude);
                float mapLongitude0 = Float.valueOf(mapLongitude);

                hole.mapLatitude = String.valueOf(mapLatitude0 + 0.00008);
                hole.mapLongitude = String.valueOf(mapLongitude0 + 0.00008);

                new HoleDao(context).add(hole);

                //先复制所有记录.
                List<Record> records = new RecordDao(context).getRecordListByHoleID(id);
                for (Record record : records) {
                    record.copy(context, hole);
                }
                System.out.println("记录复制成功");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void init() {
        this.id = Common.getUUID();
        this.createTime = DateUtil.date2Str(new Date());       //添加时间
        this.updateTime = DateUtil.date2Str(new Date());       //更新时间
        System.out.println(this.id);
        if ("".equals(this.code)) {
            this.code = "新建勘探点";
        }
        this.jiaMi();
    }

    public String getJiaMiId() {
        try {
            return Key.jiaMi(this.id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id;
    }


    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
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

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputPerson() {
        return inputPerson;
    }

    public void setInputPerson(String inputPerson) {
        this.inputPerson = inputPerson;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
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

    public String getHoleName() {
        return projectName;
    }

    public void setHoleName(String projectName) {
        this.projectName = projectName;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(String recordsCount) {
        this.recordsCount = recordsCount;
    }

    public int getRecordsCount2Int() {
        return Integer.valueOf(recordsCount);
    }

    public void setRecordsCount2Int(int recordsCount) {
        this.recordsCount = String.valueOf(recordsCount);
    }

    public String getShownWaterLevel() {
        return shownWaterLevel;
    }

    public void setShownWaterLevel(String shownWaterLevel) {
        this.shownWaterLevel = shownWaterLevel;
    }

    public String getState() {
        return state;
    }

    public String getStateName() {
        if (state.equals("2")) {
            return "进行中";
        } else if (state.equals("3")) {
            return "已结束";
        }
        return "未开始";
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStillWaterLevel() {
        return stillWaterLevel;
    }

    public void setStillWaterLevel(String stillWaterLevel) {
        this.stillWaterLevel = stillWaterLevel;
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

    public String getLocationState() {
        return locationState;
    }

    public void setLocationState(String locationState) {
        this.locationState = locationState;
    }

    public String getMapLatitude() {
        return mapLatitude;
    }

    public void setMapLatitude(String mapLatitude) {
        this.mapLatitude = mapLatitude;
    }

    public String getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(String mapLocation) {
        this.mapLocation = mapLocation;
    }

    public String getMapLongitude() {
        return mapLongitude;
    }


    public void setMapLongitude(String mapLongitude) {
        this.mapLongitude = mapLongitude;
    }

    public String getMapPic() {
        return mapPic;
    }

    public void setMapPic(String mapPic) {
        this.mapPic = mapPic;
    }

    public String getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(String mapZoom) {
        this.mapZoom = mapZoom;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getMapTime() {
        return mapTime;
    }

    public void setMapTime(String mapTime) {
        this.mapTime = mapTime;
    }

    public int getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(int uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public int getNotUploadCount() {
        return notUploadCount;
    }

    public void setNotUploadCount(int notUploadCount) {
        this.notUploadCount = notUploadCount;
    }

    public String getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(String currentDepth) {
        this.currentDepth = currentDepth;
    }

    public String getRelateID() {
        return relateID;
    }

    public void setRelateID(String relateID) {
        this.relateID = relateID;
    }

    public String getRelateCode() {
        return relateCode;
    }

    public void setRelateCode(String relateCode) {
        this.relateCode = relateCode;
    }

    public List<LocalUser> getUserList() {
        return userList;
    }

    public void setUserList(List<LocalUser> userList) {
        this.userList = userList;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
