package com.geotdb.compile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.LocalUser;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
    public static final String DB_NAME = Urls.DATABASE_BASE;//完整路径
    public static final int DB_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static DBHelper instance;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null)
                    instance = new DBHelper(context);
            }
        }
        return instance;
    }


    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }


    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        L.e("TAG", "DBHelper>>>>>onCreate");
        try {
            //老测试版没有这个表，就创建它
            TableUtils.createTableIfNotExists(connectionSource, LocalUser.class);
            TableUtils.createTableIfNotExists(connectionSource, Dictionary.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String ALTER_RECORD_UPDATEID = "ALTER TABLE `record` ADD COLUMN updateID VARCHAR(45) default '';";
    private String UPDATE_DICTIONARY_LAYERNAME = "update dictionary set sort = dictionary.type  where sort = '土的名称'";
    private String ADD_DICTIONARY_LAYERTYPE = "insert into dictionary (name,sort,sortNo) values ('填土','岩土类型','1'),('冲填土','岩土类型','2'),('黏性土','岩土类型','3'),('粉土','岩土类型','4'),('粉黏互层','岩土类型','5'),('黄土状黏性土','岩土类型','6'),('黄土状粉土','岩土类型','7'),('淤泥','岩土类型','8'),('碎石土','岩土类型','9'),('砂土','岩土类型','10'),('岩石','岩土类型','11')";
    private String ADD_DICTIONARY_LAYER_TT = "insert into dictionary (name,sort,sortNo) values ('卵石','填土_主要成分','1'),('角砾','填土_主要成分','2'),('黏性土','填土_主要成分','3'),('粉土','填土_主要成分','4'),('建筑垃圾','填土_次要成分','1'),('生活垃圾','填土_次要成分','2'),('碎砖','填土_次要成分','3'),('块石','填土_次要成分','4')";
    private String ADD_DICTIONARY_LAYER_CTT = "insert into dictionary (name,sort,sortNo) values ('以泥沙为主','冲填土_状态','1')";
    private String ADD_DICTIONARY_LAYER_YN = "insert into dictionary (name,sort,sortNo) values ('流塑','淤泥_状态','1')";
    private String ADD_DICTIONARY_LAYER_NXT = "insert into dictionary (name,sort,sortNo) values ('硬可塑','黏性土_状态','8'),('软可塑','黏性土_状态','7')";
    private String ALTER_DICTIONARY_RELATEID = "ALTER TABLE `dictionary` ADD COLUMN relateID VARCHAR(45) default '';";
    private String ALTER_DICTIONARY_FORM = "ALTER TABLE `dictionary` ADD COLUMN form VARCHAR(45) default '';";
    private String ALTER_HOLE_RELATEID = "ALTER TABLE `hole` ADD COLUMN relateID VARCHARD(45) default '';";
    private String ALTER_HOLE_RELATECODE = "ALTER TABLE `hole` ADD COLUMN relateCode VARCHARD(45) default '';";
    private String ALTER_HOLE_UPLOADED = "ALTER TABLE `hole` ADD COLUMN uploaded VARCHARD(1) default '0';";
    private String UPDATE_DICTIONARY_TYPE = "update dictionary set type = '0'";
    //修改岩土类型的sortNo，顺序问题
    private String UPDATE_DICTIONARY_YT2 = "update dictionary set sortNo = '2' where name='黏性土'";
    private String UPDATE_DICTIONARY_YT3 = "update dictionary set sortNo = '3' where name='粉土'";
    private String UPDATE_DICTIONARY_YT4 = "update dictionary set sortNo = '4' where name='砂土'";
    private String UPDATE_DICTIONARY_YT5 = "update dictionary set sortNo = '5' where name='碎石土'";
    private String UPDATE_DICTIONARY_YT6 = "update dictionary set sortNo = '6' where name='冲填土'";
    private String UPDATE_DICTIONARY_YT7 = "update dictionary set sortNo = '7' where name='粉黏互层'";
    private String UPDATE_DICTIONARY_YT8 = "update dictionary set sortNo = '8' where name='黄土状黏性土'";
    private String UPDATE_DICTIONARY_YT9 = "update dictionary set sortNo = '9' where name='黄土状粉土'";
    private String UPDATE_DICTIONARY_YT10 = "update dictionary set sortNo = '10' where name='淤泥'";
    private String UPDATE_DICTIONARY_YT11 = "update dictionary set sortNo = '11' where name='岩石'";

    private String ALTER_HOLE_USERID = "ALTER TABLE `hole` ADD COLUMN userID VARCHARD(45) default '';";

    //version==3

    private String ADD_INDEX_HOLE = "create index if not exists index_hole  on hole (projectID,state,isDelete)";
    private String ADD_INDEX_RECORD = "create index if not exists index_record  on record (holeID,projectID,state,isDelete)";
    private String ADD_INDEX_MEDIA = "create index if not exists index_media  on media (recordID,holeID,projectID,state,isDelete)";
    //修改字段长度
    private String UPDATE_PROJECT_DES = "alter table project alter column describe varchar(2000)";

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        L.e("TAG", "DBHelper>>>>>onUpgrade--old=" + oldVersion + "--new=" + newVersion);
        if (oldVersion < 2) {
            //record添加updateID字段
            sqLiteDatabase.execSQL(ALTER_RECORD_UPDATEID);
            //删除没用的表
            sqLiteDatabase.execSQL("DROP TABLE record_get;");
            sqLiteDatabase.execSQL("DROP TABLE record_frequency;");
            sqLiteDatabase.execSQL("DROP TABLE record_layer;");
            sqLiteDatabase.execSQL("DROP TABLE record_power;");
            sqLiteDatabase.execSQL("DROP TABLE record_scene;");
            sqLiteDatabase.execSQL("DROP TABLE record_water;");
            sqLiteDatabase.execSQL("DROP TABLE layer_name;");
            sqLiteDatabase.execSQL("DROP TABLE layer_type;");
            //修改字典库，（layer_name）
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_LAYERNAME);
            //修改字典库，（layer_type）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYERTYPE);
            //修改字典库，（layer_layer_tt）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_TT);
            //修改字典库，（layer_layer_ctt）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_CTT);
            //修改字典库，（layer_layer_nxt）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_NXT);
            //修改字典库，（layer_layer_yn）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_YN);
            //dictionary添加relateID字段，上传下载关联
            sqLiteDatabase.execSQL(ALTER_DICTIONARY_RELATEID);
            //dictionary添加form字段，区分岩土、取水等类型
            sqLiteDatabase.execSQL(ALTER_DICTIONARY_FORM);
            //hole添加isRelated字段，判断是否关联
            sqLiteDatabase.execSQL(ALTER_HOLE_RELATEID);
            //修改岩土类型的sortNo，顺序问题
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT2);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT3);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT4);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT5);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT6);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT7);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT8);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT9);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT10);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT11);
            //hole添加isRelated字段，判断是否关联
            sqLiteDatabase.execSQL(ALTER_HOLE_RELATECODE);
            //hole添加upload字段，判断是否上传过
            sqLiteDatabase.execSQL(ALTER_HOLE_UPLOADED);
            //初始化type字段设置为0
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_TYPE);
            //hole添加userID字段
            sqLiteDatabase.execSQL(ALTER_HOLE_USERID);
        }
        if (oldVersion < 4) {
            sqLiteDatabase.execSQL(ADD_INDEX_HOLE);
            sqLiteDatabase.execSQL(ADD_INDEX_RECORD);
            sqLiteDatabase.execSQL(ADD_INDEX_MEDIA);
        }

    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        L.e("TAG", "DBHelper>>>>>onDowngrade--old=" + oldVersion + "--new=" + newVersion);
    }
}