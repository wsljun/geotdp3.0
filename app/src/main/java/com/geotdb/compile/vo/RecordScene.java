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


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 备注
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record_scene")
public class RecordScene extends Record {

    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
    @DatabaseField
    String code = "";//'记录编号',
    @DatabaseField
    String projectID = "";//'所属项目',
    @DatabaseField
    String holeID = "";//'所属勘探点',
    @DatabaseField
    String type = "";//'记录类型1.人物描述2.场景描述 3.天气描述 4.突发情况描述',
    @DatabaseField
    String description = "";//'描述内容',
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

    public RecordScene() {

    }

    @Override
    public void init(Hole hole) {
        super.init(hole);
        this.id =super.id;
        this.code =super.code;
        this.projectID =super.projectID;
        this.holeID =super.holeID;
        this.type =super.type;
        this.createTime =super.createTime;
        this.updateTime =super.updateTime;
        this.uploadTime =super.uploadTime;
        this.recordPerson =super.recordPerson;
        this.operatePerson =super.operatePerson;
        this.isDelete =super.isDelete;
    }


    public RecordScene(String code) {
        this.code = code;
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
}
