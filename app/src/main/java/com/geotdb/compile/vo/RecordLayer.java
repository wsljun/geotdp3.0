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

import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.utils.Common;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 岩土
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "record_layer")
public class RecordLayer implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_TT = "填土";
    public static final String TYPE_CTT = "冲填土";
    public static final String TYPE_NXT = "黏性土";
    public static final String TYPE_FT = "粉土";
    public static final String TYPE_FNHC = "粉黏互层";
    public static final String TYPE_HTZNXT = "黄土状黏性土";
    public static final String TYPE_HTZFT = "黄土状粉土";
    public static final String TYPE_YN = "淤泥";
    public static final String TYPE_SST = "碎石土";
    public static final String TYPE_ST= "砂土";
    public static final String TYPE_YS = "岩石";
    public static final String TYPE_ZDY = "自定义";

    @DatabaseField(columnName = "id", id = true)
    String id = "";//'记录ID',
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

    //生成属性列表
//    public List<NameValuePair> getNameValuePairList(String serialNumber) {
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("recordLayer.id",id));
//        params.add(new BasicNameValuePair("recordLayer.layerType",layerType));
//        params.add(new BasicNameValuePair("recordLayer.layerName",layerName));
//        params.add(new BasicNameValuePair("recordLayer.weathering",weathering));
//        params.add(new BasicNameValuePair("recordLayer.era",era));
//        params.add(new BasicNameValuePair("recordLayer.causes",causes));
//        params.add(new BasicNameValuePair("recordLayer.wzcf",wzcf));
//        params.add(new BasicNameValuePair("recordLayer.ys",ys));
//        params.add(new BasicNameValuePair("recordLayer.klzc",klzc));
//        params.add(new BasicNameValuePair("recordLayer.klxz",klxz));
//        params.add(new BasicNameValuePair("recordLayer.klpl",klpl));
//        params.add(new BasicNameValuePair("recordLayer.kljp",kljp));
//        params.add(new BasicNameValuePair("recordLayer.sd",sd));
//        params.add(new BasicNameValuePair("recordLayer.msd",msd));
//        params.add(new BasicNameValuePair("recordLayer.jyx",jyx));
//        params.add(new BasicNameValuePair("recordLayer.zt",zt));
//        params.add(new BasicNameValuePair("recordLayer.bhw",bhw));
//        params.add(new BasicNameValuePair("recordLayer.ftfchd",ftfchd));
//        params.add(new BasicNameValuePair("recordLayer.fzntfchd",fzntfchd));
//        params.add(new BasicNameValuePair("recordLayer.jc",jc));
//        params.add(new BasicNameValuePair("recordLayer.kx",kx));
//        params.add(new BasicNameValuePair("recordLayer.czjl",czjl));
//        params.add(new BasicNameValuePair("recordLayer.ybljx",ybljx));
//        params.add(new BasicNameValuePair("recordLayer.ybljd",ybljd));
//        params.add(new BasicNameValuePair("recordLayer.jdljx",jdljx));
//        params.add(new BasicNameValuePair("recordLayer.jdljd",jdljd));
//        params.add(new BasicNameValuePair("recordLayer.zdlj",zdlj));
//        params.add(new BasicNameValuePair("recordLayer.mycf",mycf));
//        params.add(new BasicNameValuePair("recordLayer.fhcd",fhcd));
//        params.add(new BasicNameValuePair("recordLayer.tcw",tcw));
//        params.add(new BasicNameValuePair("recordLayer.kwzc",kwzc));
//        params.add(new BasicNameValuePair("recordLayer.zycf",zycf));
//        params.add(new BasicNameValuePair("recordLayer.cycf",cycf));
//        params.add(new BasicNameValuePair("recordLayer.djnd",djnd));
//        params.add(new BasicNameValuePair("recordLayer.hsl",hsl));
//        params.add(new BasicNameValuePair("recordLayer.jycd",jycd));
//        params.add(new BasicNameValuePair("recordLayer.wzcd",wzcd));
//        params.add(new BasicNameValuePair("recordLayer.jbzldj",jbzldj));
//        params.add(new BasicNameValuePair("recordLayer.kwx",kwx));
//        params.add(new BasicNameValuePair("recordLayer.jglx",jglx));
//        return params;
//    }

    public RecordLayer() {
        try {
            this.id = Common.getUUID();
            this.layerType = "填土";
            this.layerName = "耕土(表土)";
            this.era = "";
            this.causes = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecordLayer(Context context, Hole hole) {
        try {
            this.id = Common.getUUID();

            HashMap codeMap = getCodeMap(context, hole.getId());
            DecimalFormat df = new DecimalFormat("000");
            int codeInt = 1;
            String codeStr0 = "YT-";
            String codeStr = codeStr0 + df.format(codeInt);

            while (codeMap.containsKey(codeStr)) {
                codeInt += 1;
                codeStr = codeStr0 + df.format(codeInt);
            }

            this.layerType = "填土";
            this.layerName = "耕土(表土)";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getCodeMap(Context context, String holeID) {
        HashMap hashmap = new HashMap();
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Project, String> dao = dbHelper.getDao(Project.class);
            GenericRawResults<String> results = dao.queryRaw("select code from record_layer where code like '__-___' and holeID = '" + holeID + "'", new RawRowMapper<String>() {
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

    public double getBeginDepth(Context context, String holeID) {
        double beginDepth = 0;
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Project, String> dao = dbHelper.getDao(Project.class);
            GenericRawResults<String> results = dao.queryRaw("select max(endDepth) as endDepth from record_layer where holeID = '" + holeID + "'", new RawRowMapper<String>() {
                @Override
                public String mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    String s = resultColumns[0];
                    return s;
                }
            });

            Iterator<String> iterator = results.iterator();
            while (iterator.hasNext()) {
                String string = iterator.next();
                beginDepth = Double.valueOf(string);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return beginDepth;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
