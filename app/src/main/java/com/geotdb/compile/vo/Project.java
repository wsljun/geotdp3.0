package com.geotdb.compile.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.geotdb.compile.utils.Key;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 项目表
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";              //主键
    @DatabaseField
    String code = "";             //代码
    @DatabaseField
    String companyID = "";        //(勘察单位)
    @DatabaseField
    String designCompanyID = "";  //设计单位
    @DatabaseField
    String workCompany = "";      //作业单位(施工单位)(建设单位)
    @DatabaseField
    String designationID = "";    //审图机构
    @DatabaseField
    String owner = "";            //业主
    @DatabaseField
    String leader = "";           //负责人
    @DatabaseField
    String fullName = "";         //全称
    @DatabaseField
    String referred = "";         //简称
    @DatabaseField
    String type = "0";            //类型
    @DatabaseField
    String recordPerson = "";     //记录负责人(描述员)  UserID
    @DatabaseField
    String operatePerson = "";    //操作负责人(机长)
    @DatabaseField
    String province = "";         //省份
    @DatabaseField
    String city = "";             //城市
    @DatabaseField
    String district = "";         //区县
    @DatabaseField
    String address = "";          //地址
    @DatabaseField
    String level = "";            //级别
    @DatabaseField
    String describe = "";         //项目描述
    @DatabaseField
    String createTime = "";       //添加时间
    @DatabaseField
    String updateTime = "";       //更新时间
    @DatabaseField
    String uploadTime = "";       //提交时间
    @DatabaseField
    String beginTime = "";        //勘查开始时间(第一个空,在客户端添加的时间)
    @DatabaseField
    String endTime = "";          //勘查结束时间(最晚的更新时间)
    @DatabaseField
    String state = "1";           //状态1.未开始2.进行中3.已结束
    @DatabaseField
    String stage = "1";           //阶段1.预查2.普查3.详查4.勘查
    @DatabaseField
    String annex = "";            //指导资料
    @DatabaseField
    String participants = "";     //参与者
    @DatabaseField
    String serialNumber = "";     //序列号
    @DatabaseField
    String isCheck = "0";         //是否审核
    @DatabaseField
    String isDelete = "0";        //是否删除
    @DatabaseField
    String remark = "";           //备注
    @DatabaseField
    String companyName = "";      //工程设计单位(勘察单位)
    @DatabaseField
    String designCompanyName = "";      //工程设计单位(勘察单位)
    @DatabaseField
    String proName = "";          //省
    @DatabaseField
    String cityName = "";         //工区市
    @DatabaseField
    String disName = "";         //区县
    @DatabaseField
    String leaderName = "";      //负责人姓名
    @DatabaseField
    String mapLocation = "";      //在地图大概所在
    @DatabaseField
    String mapZoom = "";          //地图缩放程度
    @DatabaseField
    String mapLatitude = "";      //地图中心纬度
    @DatabaseField
    String mapLongitude = "";    //地图中心经度
    @DatabaseField
    String mapPic = "";          //所在地截图
    @DatabaseField
    String holeCount = "0";      //孔数

    String realName = "";         //负责人姓名

    int uploadedCount;          //已上传
    int notUploadCount;         //未上传

    public Project() {

    }

    public Project(int i) {
    }

    public Project(Cursor cursor) {
        try {
            this.serialNumber = cursor.getString(cursor.getColumnIndex("serialNumber"));
            this.id = cursor.getString(cursor.getColumnIndex("id"));
            this.code = Key.jieMi(cursor.getString(cursor.getColumnIndex("code")));
            this.fullName = Key.jieMi(cursor.getString(cursor.getColumnIndex("fullName")));
            this.leader = Key.jieMi(cursor.getString(cursor.getColumnIndex("leader")));

            this.companyName = cursor.getString(cursor.getColumnIndex("companyName"));
            this.owner = cursor.getString(cursor.getColumnIndex("owner"));
            this.proName = cursor.getString(cursor.getColumnIndex("proName"));
            this.cityName = cursor.getString(cursor.getColumnIndex("cityName"));
            this.disName = cursor.getString(cursor.getColumnIndex("disName"));
            this.address = cursor.getString(cursor.getColumnIndex("address"));

            this.state = cursor.getString(cursor.getColumnIndex("state"));
            this.holeCount = cursor.getString(cursor.getColumnIndex("holeCount"));
            this.updateTime = cursor.getString(cursor.getColumnIndex("updateTime"));

            this.mapLocation = cursor.getString(cursor.getColumnIndex("mapLocation"));
            this.mapZoom = cursor.getString(cursor.getColumnIndex("mapZoom"));
            this.mapLatitude = cursor.getString(cursor.getColumnIndex("mapLatitude"));
            this.mapLongitude = cursor.getString(cursor.getColumnIndex("mapLongitude"));
            this.mapPic = cursor.getString(cursor.getColumnIndex("mapPic"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Project(Context context) {
        try {
            this.serialNumber = "";
            this.id = Common.getUUID();

            ProjectDao projectDao = new ProjectDao(context);
            HashMap codeMap = projectDao.getCodeMap();
            DecimalFormat df = new DecimalFormat("000");
            int codeInt = 1;
            String codeStr0 = "XM-";
            String codeStr = codeStr0 + df.format(codeInt);

            HashMap fullNameMap = projectDao.getFullNameMap();
            String fullNameStr1 = "号项目";
            String fullNameStr = df.format(codeInt) + fullNameStr1;

            while (codeMap.containsKey(codeStr) || fullNameMap.containsKey(fullNameStr)) {
                codeInt += 1;
                codeStr = codeStr0 + df.format(codeInt);
                fullNameStr = df.format(codeInt) + fullNameStr1;
            }
            this.code = codeStr;
            this.fullName = fullNameStr;

            this.leader = "";

            this.companyName = "";
            this.owner = "";
            this.proName = "";
            this.cityName = "";
            this.disName = "";
            this.address = "";

            this.state = "1";
            this.holeCount = "0";

            this.createTime = DateUtil.date2Str(new Date());       //添加时间
            this.updateTime = DateUtil.date2Str(new Date());       //更新时间

            this.mapLocation = "";
            this.mapZoom = "";
            this.mapLatitude = "";
            this.mapLongitude = "";
            this.mapPic = "";
            //到添加项目这里，必定是登录过了
            this.recordPerson = Common.getUserIDBySP(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //给该加密的数据加密
    public void jiaProject() {
//        try {
//            this.code = Key.jiaMi(this.code);
//            this.fullName = Key.jiaMi(this.fullName);
//            this.leader = Key.jiaMi(this.leader);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    //给该解密的数据解密
    public void jieProject() {
//        try {
//            this.code = Key.jieMi(this.code);
//            this.fullName = Key.jieMi(this.fullName);
//            this.leader = Key.jieMi(this.leader);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }


    public String getInsertSQL() {
        return "insert into project(id,createTime,updateTime,state,holeCount,serialNumber,code,fullName,leader,companyName,owner,proName,cityName,disName,address,mapLocation,mapZoom,mapLatitude,mapLongitude,mapPic) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    public Object[] getObject() {
        this.id = Common.getUUID();
        this.createTime = DateUtil.date2Str(new Date());       //添加时间
        this.updateTime = DateUtil.date2Str(new Date());       //更新时间

        System.out.println(this.id);
        this.jiaProject();
        System.out.println("这里呢" + this.mapPic);
        return new Object[]{this.id, this.createTime, this.updateTime, this.state, this.holeCount, this.serialNumber, this.code, this.fullName, this.leader, this.companyName, this.owner, this.proName, this.cityName, this.disName, this.address, this.mapLocation, this.mapZoom, this.mapLatitude, this.mapLongitude, this.mapPic};
    }

    public void init() {
        this.id = Common.getUUID();
        this.createTime = DateUtil.date2Str(new Date());       //添加时间
        this.updateTime = DateUtil.date2Str(new Date());       //更新时间
        System.out.println(this.id);
        if ("".equals(this.leader)) {
            this.leader = "未填写";
        }
        if ("".equals(this.fullName)) {
            this.fullName = "新建勘察项目";
        }
        this.jiaProject();
        System.out.println("这里呢" + this.mapPic);
    }


    public Project(String fullName, String leader) {
        this.fullName = fullName;
        this.leader = leader;
    }

    public String getId() {
        return id;
    }

    public String getJiaMiId() {
        try {
            return Key.jiaMi(this.id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id;
    }


    /**
     * 删除项目
     *
     * @param context
     */
    public boolean delete(Context context) {
        try {
            //先删除所有勘探点.
            List<Hole> holes = new HoleDao(context).getHoleListByProjectID(id);
            for (Hole hole : holes) {
                hole.delete(context);
            }
            System.out.println("勘探点删除成功");
            if (new ProjectDao(context).delete(this)) {
                System.out.println("项目数据删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getWorkCompany() {
        return workCompany;
    }

    public void setWorkCompany(String workCompany) {
        this.workCompany = workCompany;
    }

    public String getDesignationID() {
        return designationID;
    }

    public void setDesignationID(String designationID) {
        this.designationID = designationID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getReferred() {
        return referred;
    }

    public void setReferred(String referred) {
        this.referred = referred;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecordPerson() {
        return recordPerson;
    }

    public void setRecordPerson(String recordPerson) {
        this.recordPerson = recordPerson;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getAnnex() {
        return annex;
    }

    public void setAnnex(String annex) {
        this.annex = annex;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getHoleCount() {
        return holeCount;
    }

    public void setHoleCount(String holeCount) {
        this.holeCount = holeCount;
    }

    public int getHoleCount2Int() {
        return Integer.valueOf(holeCount);
    }

    public void setHoleCount2Int(int holeCount) {
        this.holeCount = String.valueOf(holeCount);
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getMapLatitude() {
        return mapLatitude;
    }

    public void setMapLatitude(String mapLatitude) {
        this.mapLatitude = mapLatitude;
    }

    public double getMapLatitude2Double() {
        return Double.valueOf(mapLatitude);
    }

    public double getMapLongitude2Double() {
        return Double.valueOf(mapLongitude);
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

    public String getMapPicToUrl() {
        return Urls.getPicPath() + "/" + mapPic + ".png";
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

    public String getDesignCompanyID() {
        return designCompanyID;
    }

    public void setDesignCompanyID(String designCompanyID) {
        this.designCompanyID = designCompanyID;
    }

    public String getDesignCompanyName() {
        return designCompanyName;
    }

    public void setDesignCompanyName(String designCompanyName) {
        this.designCompanyName = designCompanyName;
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
}
