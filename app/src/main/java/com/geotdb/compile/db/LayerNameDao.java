package com.geotdb.compile.db;

import android.content.Context;

import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.LayerName;
import com.geotdb.compile.vo.LayerType;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class LayerNameDao {

    private Context context;
    private Dao<LayerName, String> lnDao;
    private Dao<LayerType, String> ltDao;
    private DBHelper helper;

    public LayerNameDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getInstance(context);
            lnDao = helper.getDao(LayerName.class);
            ltDao = helper.getDao(LayerType.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public List<DropItemVo> getDropItemList(String query) {
//        List<DropItemVo> list = new ArrayList<DropItemVo>();
//        try {
//            GenericRawResults<DropItemVo> results = lnDao.queryRaw(query, new RawRowMapper<DropItemVo>() {
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

    public void addItemOnName(LayerName layerName) {
        try {
            lnDao.create(layerName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCountOnName() {
        try {
            return lnDao.queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addItemOnType(LayerType layerType) {
        try {
            ltDao.create(layerType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCountOnType() {
        try {
            return ltDao.queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
