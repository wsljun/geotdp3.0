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
 * 取样
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record_get")
public class RecordGet implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_EARTH = "取岩土样";
    public static final String TYPE_WATER = "取水样";

    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
    @DatabaseField
    String earthType = "";//'土样质量等级',
    @DatabaseField
    String waterDepth = "";
    @DatabaseField
    String getMode = "";//'取水方式',取样工具和方法'

    public RecordGet() {
        try {
            this.id = Common.getUUID();
            this.earthType = "";
            this.getMode = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecordGet(String type) {
        try {
            this.id = Common.getUUID();
            if (TYPE_WATER.equals(type)) {
                waterDepth = "0";
                this.getMode = "";
            } else {
                this.earthType = "";
                this.getMode = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    //生成属性列表
//    public List<NameValuePair> getNameValuePairList(String serialNumber) {
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("recordGet.id",id));
//        params.add(new BasicNameValuePair("recordGet.earthType",earthType));
//        params.add(new BasicNameValuePair("recordGet.waterDepth",waterDepth));
//        params.add(new BasicNameValuePair("recordGet.getMode",getMode));
//
//        return params;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
