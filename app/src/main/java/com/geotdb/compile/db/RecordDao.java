package com.geotdb.compile.db;

import android.content.Context;

import com.geotdb.compile.service.NewUploadService;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class RecordDao {


    private Context mContext;
    private Dao<Record, String> recordDao;
    private DBHelper helper;

    public RecordDao(Context context) {
        this.mContext = context;
        try {
            helper = DBHelper.getInstance(mContext);
            recordDao = helper.getDao(Record.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个记录
     *
     * @param recordID
     */
    public Record queryForId(String recordID) {
        try {
            return recordDao.queryForId(recordID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加一个勘探点
     *
     * @param record
     */
    public void add(Record record) {
        try {
            recordDao.createOrUpdate(record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个项目下所有所有未上传的记录
     *
     * @param projectID
     * @return
     */
    public List<Record> getNotUploadListByProjectID(String projectID) {
        List<Record> list = new ArrayList<Record>();
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder();
            qb.where().eq("projectID", projectID).and().eq("state", "1");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取一个勘探点下所有所有未上传的记录
     *
     * @param holeID
     * @return
     */
    public List<Record> getNotUploadListByHoleID(String holeID) {
        List<Record> list = new ArrayList<Record>();
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder();
            qb.where().eq("holeID", holeID).and().eq("state", "1");
            qb.orderBy("createTime", true);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 更新勘探点
     *
     * @param record
     */
    public void update(Record record) {
        try {
            recordDao.update(record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * 根据分类获取统计
     *
     * @param holeID
     * @return
     */
    public Map<Integer, Integer> getSortCountMap(String holeID) {
        Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder();
            qb.where().eq("holeID", holeID).and().eq("updateID", "").and().ne("state", "0").and().ne("type", Record.TYPE_SCENE_OPERATEPERSON).and().ne("type", Record.TYPE_SCENE_OPERATECODE).and().ne("type", Record.TYPE_SCENE_RECORDPERSON).and().ne("type", Record.TYPE_SCENE_SCENE).and().ne("type", Record.TYPE_SCENE_PRINCIPAL).and().ne("type", Record.TYPE_SCENE_TECHNICIAN).and().ne("type", Record.TYPE_SCENE_VIDEO);
            countMap.put(1, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_FREQUENCY).and().ne("state", "0");
            countMap.put(2, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_LAYER).and().ne("state", "0");
            countMap.put(3, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_WATER).and().ne("state", "0");
            countMap.put(4, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_DPT).and().ne("state", "0");
            countMap.put(5, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_SPT).and().ne("state", "0");
            countMap.put(6, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_GET_EARTH).and().ne("state", "0");
            countMap.put(7, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_GET_WATER).and().ne("state", "0");
            countMap.put(8, qb.query().size());
            qb.reset();
            qb.where().eq("holeID", holeID).and().eq("type", Record.TYPE_SCENE).and().ne("state", "0");
            countMap.put(9, qb.query().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countMap;
    }


    /**
     * 根据holeiID和类别查询record
     */
    public Record getRecordByType(String holeID, String type) {
        try {
            return recordDao.queryBuilder().where().eq("holeID", holeID).and().eq("type", type).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个勘探点某个深度在类别内是否存在
     *
     * @param record
     * @return
     */
    public boolean validatorBeginDepth(Record record, String type, String depth) {
        int i = 0;
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder();
            Where where = qb.where();
            //beginDepth <= depth < endDepth
            where.eq("holeID", record.getHoleID()).and().ne("state", "0").and().le("beginDepth", depth).and().gt("endDepth", depth).and().ne("id", record.getId()).and().eq("updateID", "");

            if (type.equals(Record.TYPE_DPT) || type.equals(Record.TYPE_SPT)) {
                where.and().in("type", Record.TYPE_DPT, Record.TYPE_SPT);
            } else {
                where.and().eq("type", type);
            }
            i = qb.query().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i > 0;
    }


    /**
     * 获取一个勘探点某个深度在类别内是否存在
     *
     * @param record
     * @return
     */
    public boolean validatorEndDepth(Record record, String type, String depth) {
        int i = 0;
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder();
            Where where = qb.where();
            //beginDepth < depth <=endDepth
            where.eq("holeID", record.getHoleID()).and().ne("state", "0").and().lt("beginDepth", depth).and().ge("endDepth", depth).and().ne("id", record.getId()).and().eq("updateID", "");

            if (type.equals(Record.TYPE_DPT) || type.equals(Record.TYPE_SPT)) {
                where.and().in("type", Record.TYPE_DPT, Record.TYPE_SPT);
            } else {
                where.and().eq("type", type);
            }
            System.out.println("where:=========:" + where.getStatement());

            i = qb.query().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i > 0;
    }

    /**
     * 获取一个勘探点某个深度在类别内是否存在
     *
     * @param holeID
     * @return
     */
    public List<Record> getRecordOne(String holeID) {
        List<Record> list = new ArrayList<>();
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder().orderBy("endDepth", true).orderBy("beginDepth", true);
            Where where = qb.where();
            where.eq("holeID", holeID).and().ne("state", "0");
            where.and().in("type", Record.TYPE_LAYER, Record.TYPE_WATER);
            System.out.println("where:=========:" + where.getStatement());

            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取一个勘探点某个深度在类别内是否存在
     *
     * @param holeID
     * @return
     */
    public List<Record> getRecordTwo(String holeID) {
        List<Record> list = new ArrayList<>();
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder().orderBy("endDepth", true).orderBy("beginDepth", true);
            Where where = qb.where();
            where.eq("holeID", holeID).and().ne("state", "0");
            where.and().in("type", Record.TYPE_GET_EARTH, Record.TYPE_GET_WATER, Record.TYPE_DPT, Record.TYPE_SPT);
            System.out.println("where:=========:" + where.getStatement());

            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取某勘探点的所有记录
     *
     * @param holeID
     * @return
     */
    public List<Record> getRecordListByHoleID(String holeID) {
        List<Record> list = new ArrayList<>();
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder();
            qb.where().eq("holeID", holeID);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据项目ID、类别，查询所有记录
     * 机长列表
     */
    public List<Record> getRecordListByProject(String projectID, String type) {
        try {
//            return recordDao.queryBuilder().where().eq("projectID", projectID).and().eq("type", type).query();
            //暂时不需要在项目之下查询
            return recordDao.queryBuilder().where().eq("type", type).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除记录
     *
     * @param record
     */
    public boolean delete(Record record) {
        try {
            recordDao.delete(record);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashMap getCodeMap(String holeID) {
        HashMap hashmap = new HashMap();
        try {
            GenericRawResults<String> results = recordDao.queryRaw("select code from record where code like '%__-___'and holeID='" + holeID + "' and state !='0'", new RawRowMapper<String>() {
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

    public List<Record> getCodeMapNew(String holeID, String type) {
        try {
            QueryBuilder<Record, String> qb = recordDao.queryBuilder().orderBy("updateTime", true);
            return qb.where().eq("holeID", holeID).and().eq("type", type).and().ne("state", "0").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据id查询关联的该记录的历史记录
     */
    public List<Record> getUpdateRecordByID(Record record) {
        try {
            return recordDao.queryBuilder().where().eq("updateID", record.getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateState(String projectID) {
        try {
            UpdateBuilder updateBuilder = recordDao.updateBuilder();
            updateBuilder.where().eq("projectID", projectID);
            updateBuilder.updateColumnValue("state", "1");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}