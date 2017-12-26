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
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


import java.io.Serializable;

/**
 * 地下水
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record_water")
public class RecordWater implements Serializable {
    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
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

//    //生成属性列表
//    public List<NameValuePair> getNameValuePairList(String serialNumber) {
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("recordPower.id",id));
//        params.add(new BasicNameValuePair("recordPower.waterType",waterType));
//        params.add(new BasicNameValuePair("recordPower.shownWaterLevel",shownWaterLevel));
//        params.add(new BasicNameValuePair("recordPower.stillWaterLevel",stillWaterLevel));
//        params.add(new BasicNameValuePair("recordPower.shownTime",shownTime));
//        params.add(new BasicNameValuePair("recordPower.stillTime",stillTime));
//        return params;
//    }

    public RecordWater() {
        this.id = Common.getUUID();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
