package com.geotdb.compile.db;

import android.content.Context;

import com.geotdb.compile.vo.LocalUser;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class LocalUserDao {
    private Context context;
    private Dao<LocalUser, String> localUserDao;

    public LocalUserDao(Context context) {
        this.context = context;
        try {
            localUserDao = DBHelper.getInstance(context).getDao(LocalUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LocalUser> getList() {
        try {
            return localUserDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocalUser getUserByID(String id) {
        try {
            return localUserDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(LocalUser localUser){
        try {
            localUserDao.update(localUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
