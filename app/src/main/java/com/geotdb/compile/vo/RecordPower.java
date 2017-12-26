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
 * 原位
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record_power")
public class RecordPower implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_DPY = "动力触探";
    public static final String TYPE_SPT = "标准贯入";

    public static final String TYPE_1 = "轻型";
    public static final String TYPE_2 = "重型";
    public static final String TYPE_3 = "超重型";

    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
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


    public RecordPower() {
        this.id = Common.getUUID();
        this.powerType = TYPE_1;
        setBegin("0");
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPowerType() {
        return powerType;
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
}
