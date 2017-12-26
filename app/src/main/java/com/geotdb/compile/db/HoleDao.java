package com.geotdb.compile.db;

import android.content.Context;

import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class HoleDao {


    private Context context;
    private Dao<Hole, String> holeDao;
    private DBHelper helper;

    public HoleDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getInstance(context);
            holeDao = helper.getDao(Hole.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个勘探点
     *
     * @param holeID
     */
    public Hole queryForId(String holeID) {
        try {
            return holeDao.queryForId(holeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据isRelated字段查询
     */
    public Hole queryForIsRelated(String relateID) {
        try {
            return holeDao.queryBuilder().where().eq("isRelated", relateID).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Hole> getHoleListByCode(String projectID, String code) {
        List<Hole> list = new ArrayList<>();
        try {
            list = holeDao.queryBuilder().where().eq("projectID", projectID).and().
                    like("code", code + "%").or().like("code", "%" + code).or().like("code", "%" + code + "%").query();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Hole> getHoleListByProjectIDUserDelete(String projectID) {
        List<Hole> list = new ArrayList<>();
        try {
            QueryBuilder<Hole, String> qb = holeDao.queryBuilder();
            qb.where().eq("projectID", projectID);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 增加一个勘探点
     *
     * @param hole
     */
    public void add(Hole hole) {
        try {
            holeDao.createOrUpdate(hole);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有未上传的勘探点
     *
     * @param projectID
     * @return
     */
    public List<Hole> getHoleListByNotUpload(String projectID) {
        List<Hole> list = new ArrayList<Hole>();
        try {
            QueryBuilder<Hole, String> qb = holeDao.queryBuilder();
            qb.where().eq("projectID", projectID).and().eq("state", "1").and().eq("isDelete", "0");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 更新勘探点
     *
     * @param hole
     */
    public void update(Hole hole) {
        try {
            holeDao.update(hole);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除勘探点
     *
     * @param hole
     */
    public boolean delete(Hole hole) {
        try {
            holeDao.delete(hole);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据ID删除
     */
    public boolean deleteByID(String id) {
        try {
            holeDao.deleteById(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取某项目的所有勘探点
     *
     * @param projectID
     * @return
     */
    public List<Hole> getHoleListByProjectID(String projectID) {
        List<Hole> list = new ArrayList<>();
        try {
            QueryBuilder<Hole, String> qb = holeDao.queryBuilder();
            qb.where().eq("projectID", projectID).and().eq("isDelete", "0");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查看该点之下是否都已经上传，点、记录、媒体
     */
    public boolean checkIsUpload(String holeID) {
        L.e("TAG", "holeID--->>" + holeID);
        //当前点
        if (!"2".equals(queryForId(holeID).getState())) {
            return false;
        }
        //所有的记录
        List<Record> recordList = new RecordDao(context).getRecordListByHoleID(holeID);
        if (recordList != null) {
            for (Record record : recordList) {
                L.e("TAG", "record--->>" + record.getState());
                if (!"2".equals(record.getState())) {
                    return false;
                }
            }
        }
        //所有的媒体
        List<Media> mediaList = new MediaDao(context).getMediaListByHoleID(holeID);
        if (mediaList != null) {
            for (Media media : mediaList) {
                L.e("TAG", "media--->>" + media.getState());
                if (!"2".equals(media.getState())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据项目ID查询所有点，是否都提交验收
     */
    public List<Hole> getHoleListBeSubmit(String projectID) {
        try {
            return holeDao.queryBuilder().where().eq("projectID", projectID).and().eq("state", "3").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateState(String projectID) {
        try {
            UpdateBuilder updateBuilder = holeDao.updateBuilder();
            updateBuilder.where().eq("projectID", projectID);
            updateBuilder.updateColumnValue("state", "1");
            updateBuilder.updateColumnValue("relateID", "");
            updateBuilder.updateColumnValue("relateCode", "");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkRelated(String relateID, String projectID) {
        try {
            List<Hole> list = holeDao.queryBuilder().where().eq("relateID", relateID).and().eq("projectID", projectID).query();
            if (list == null || list.size() == 0) {
                return false;
            } else {
                for (Hole hole : list) {
                    if ("".equals(hole.getUserID()) || hole.getUserID() == null || Common.getUserIDBySP(context).equals(hole.getUserID())) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}