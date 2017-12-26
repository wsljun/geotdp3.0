package com.geotdb.compile.db;

import android.content.Context;

import com.geotdb.compile.vo.Media;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class MediaDao {


    private Context context;
    private Dao<Media, String> mediaDao;
    private DBHelper helper;

    public MediaDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getInstance(context);
            mediaDao = helper.getDao(Media.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据recordID获取media
     */
    public Media getMediaByRecordID(String recordID) {
        try {
            return mediaDao.queryBuilder().orderBy("createTime", false).where().eq("recordID", recordID).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 增加一个媒体
     *
     * @param media
     */
    public void add(Media media) {
        try {
            mediaDao.create(media);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取所有未上传的媒体
     *
     * @param projectID
     * @return
     */
    public List<Media> getNotUploadListByProjectID(String projectID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = mediaDao.queryBuilder();
            qb.where().eq("projectID", projectID).and().eq("state", "1");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取所有未上传的媒体
     *
     * @param holeID
     * @return
     */
    public List<Media> getNotUploadListByHoleID(String holeID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = mediaDao.queryBuilder();
            qb.where().eq("holeID", holeID).and().eq("state", "1");
            qb.orderBy("createTime", true);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取所有未上传的媒体
     *
     * @param recordID
     * @return
     */
    public List<Media> getNotUploadListByRecordID(String recordID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = mediaDao.queryBuilder();
            qb.where().eq("recordID", recordID).and().eq("state", "1").and().eq("isDelete", "0");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某条记录的所有媒体
     *
     * @param recordID
     * @return
     */
    public List<Media> getMediaListByRecordID(String recordID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = mediaDao.queryBuilder();
            qb.where().eq("recordID", recordID).and().eq("isDelete", "0");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取点的所有媒体
     */

    public List<Media> getMediaListByHoleID(String holeID) {
        try {
            return mediaDao.queryBuilder().where().eq("holeID", holeID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得改点的所有媒体数量
     */

    public int getMediaCountByHoleID(String holeID) {
        return getMediaListByHoleID(holeID).size();
    }

    /**
     * 获取某条记录的媒体数量
     * 偷懒了
     */
    public int getMediaCountByrdcordID(String recordID) {
        return getMediaListByRecordID(recordID).size();
    }

    /**
     * 获取某条记录的所有媒体
     *
     * @param recordID
     * @return
     */
    public List<Media> getMediaListByRecordID2(String recordID) {
        List<Media> list = new ArrayList<Media>();
        list.add(new Media());
        list.add(new Media("jpg"));
        try {
            GenericRawResults<Media> results = mediaDao.queryRaw("select id,localPath,state,name from media where recordID ='" + recordID + "' order by createTime desc", new RawRowMapper<Media>() {
                @Override
                public Media mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Media media = new Media();
                    media.setId(resultColumns[0]);
                    media.setLocalPath(resultColumns[1]);
                    media.setState(resultColumns[2]);
                    media.setName(resultColumns[3]);
                    return media;
                }
            });

            Iterator<Media> iterator = results.iterator();
            while (iterator.hasNext()) {
                Media media = iterator.next();
                list.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取所有未上传的媒体
     *
     * @param mediaID
     * @return
     */
    public Media getMediaByID(String mediaID) {
        try {
            return mediaDao.queryForId(mediaID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //得到所有媒体
    public List<Media> getMediaList() {
        try {
            return mediaDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新媒体
     *
     * @param media
     */
    public void update(Media media) {
        try {
            mediaDao.update(media);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除媒体
     *
     * @param media
     */
    public boolean delete(Media media) {
        try {
            mediaDao.delete(media);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateState(String projectID) {
        try {
            UpdateBuilder updateBuilder = mediaDao.updateBuilder();
            updateBuilder.where().eq("projectID", projectID);
            updateBuilder.updateColumnValue("state", "1");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}