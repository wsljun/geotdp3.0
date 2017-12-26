package com.geotdb.compile.db;

import android.content.Context;

import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Project;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class ProjectDao {


    private Context context;
    private Dao<Project, String> projectDao;
    private DBHelper helper;

    public ProjectDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getInstance(context);
            projectDao = helper.getDao(Project.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个项目
     *
     * @param project
     */
    public void add(Project project) {
        try {
            projectDao.create(project);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改一个项目
     */
    public void update(Project project) {
        try {
            projectDao.update(project);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个项目
     *
     * @param projectID
     */
    public Project queryForId(String projectID) {
        try {
            return projectDao.queryForId(projectID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取项目序列号
     */
    public String queryStrForId(String projectID) {
        try {
            return projectDao.queryForId(projectID).getSerialNumber();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 删除项目
     *
     * @param project
     */
    public boolean delete(Project project) {
        try {
            projectDao.delete(project);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkProjectBySerialNumber(String serialNumber, LocalUser localUser) {
        try {
            if (null != projectDao.queryBuilder().where().eq("serialNumber", serialNumber).and().eq("recordPerson", localUser.getId()).queryForFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashMap getCodeMap() {
        HashMap hashmap = new HashMap();
        try {
            GenericRawResults<String> results = projectDao.queryRaw("select code from project where code like '__-___'", new RawRowMapper<String>() {
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


    public HashMap getFullNameMap() {
        HashMap hashmap = new HashMap();
        try {
            GenericRawResults<String> results = projectDao.queryRaw("select fullName from project where fullName like '___号项目'", new RawRowMapper<String>() {
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

    public List<Project> getAll(String userID) {
        try {
            return projectDao.queryBuilder().where().eq("recordPerson", "").or().eq("recordPerson", userID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateState(Project p) {
        try {
//            UpdateBuilder updateBuilder = projectDao.updateBuilder();
//            updateBuilder.where().eq("id", projectID);
//            updateBuilder.updateColumnValue("state", "1");
//            updateBuilder.update();
            projectDao.update(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}