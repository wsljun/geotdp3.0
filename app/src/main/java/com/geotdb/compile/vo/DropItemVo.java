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


import com.geotdb.compile.db.DBHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉控件用的对象
 *
 * @author XuFeng
 */

public class DropItemVo implements Serializable {

    private static final long serialVersionUID = 1L;
    String id;             //主键
    String name;           //无
    String value;           //无
    String pic;         //小图标

    public DropItemVo() {
    }

    public DropItemVo(String id, String name) {
        this.id = id;
        this.name = name;
        this.value = name;
    }

    public DropItemVo(String id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static DBHelper dbHelper;

//    public static List<DropItemVo> getDropItemList(String query, Context context) {
//        List<DropItemVo> list = new ArrayList<DropItemVo>();
//        if (dbHelper == null) dbHelper = DBHelper.getInstance(context);
//        try {
//            Dao<LayerName, String> dao = dbHelper.getDao(LayerName.class);
//            GenericRawResults<DropItemVo> results = dao.queryRaw(query, new RawRowMapper<DropItemVo>() {
//                @Override
//                public DropItemVo mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
//                    DropItemVo dropItemVo = new DropItemVo();
//                    dropItemVo.setId(resultColumns[0]);
//                    dropItemVo.setName(resultColumns[1]);
//                    dropItemVo.setValue(resultColumns[1]);
//                    return dropItemVo;
//                }
//            });
//
//            Iterator<DropItemVo> iterator = results.iterator();
//            while (iterator.hasNext()) {
//                DropItemVo dropItemVo = iterator.next();
//                list.add(dropItemVo);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    public static List<String> getStrList(List<DropItemVo> dLit) {
        List<String> list = new ArrayList<>();
        for (DropItemVo d : dLit) {
            list.add(d.getName());
        }
        return list;
    }

}
