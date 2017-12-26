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


import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * 动探日志
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record_power_log")
public class RecordPowerLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
    @DatabaseField
    String code = "";//'记录编号',
    @DatabaseField
    String projectID = "";//'所属项目',
    @DatabaseField
    String holeID = "";//'所属勘探点',
    @DatabaseField
    String powerID = "";//'所属动探记录',
    @DatabaseField
    String type = "";//'原位测试类型',
    @DatabaseField
    String begin = "";           // 起始
    @DatabaseField
    String end = "";         //终止
    @DatabaseField
    String blow = "";         //击数
    @DatabaseField
    String description = "";//'描述备注',
    @DatabaseField
    String createTime = "";//'创建时间',
    @DatabaseField
    String updateTime = "";//'更新时间',
    @DatabaseField
    String uploadTime = "";//'上传时间',
    @DatabaseField
    String recordPerson = "";//'描述员(记录负责人)',
    @DatabaseField
    String operatePerson = "";//'机长(操作负责人)',
    @DatabaseField
    String isDelete = "";//'是否删除',

    boolean isEnabled = true; //是否可编辑

    public RecordPowerLog() {

    }

    public void init(RecordPower recordPower) {
        this.id = Common.getUUID();
        System.out.println("id-------------->:" + this.id);
//        this.projectID = recordPower.getProjectID();
//        this.holeID = recordPower.getHoleID();
//        this.holeID = recordPower.getId();
        this.createTime = DateUtil.date2Str(new Date());       //添加时间
        this.updateTime = DateUtil.date2Str(new Date());       //更新时间
        if ("".equals(this.code)) {
            this.code = "新建记录";
        }
    }

    public RecordPowerLog(String begin, String end) {
        this.begin = begin;
        this.end = end;
    }


    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getBlow() {
        return blow;
    }

    public void setBlow(String blow) {
        this.blow = blow;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHoleID() {
        return holeID;
    }

    public void setHoleID(String holeID) {
        this.holeID = holeID;
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

    public String getPowerID() {
        return powerID;
    }

    public void setPowerID(String powerID) {
        this.powerID = powerID;
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
}
