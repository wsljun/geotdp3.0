package com.geotdb.compile.utils;

import java.io.File;


import android.os.Environment;

/**
 * 用个工具类把整个应用所涉及的地址控制起来.(有这个想法,并没有使用)
 * <p/>
 * 存放静态字符串
 *
 * @author XuFeng
 */
public class Urls {
    // 服务端接口
//    public static final String SERVER_PATH = "http://wyxt.gotoip2.com/";//新疆
//    public static final String SERVER_PATH = "http://www.geotdp.com:8081/";//北京
//    public static final String SERVER_PATH = "http://www.geotdp.com/";//新疆-old
//    public static final String SERVER_PATH = "http://115.47.116.157/";//辽宁
//    public static final String SERVER_PATH = "http://test.geotdp.com/";//测试 老接口 有关联等
//    public static final String SERVER_PATH = "http://new.geotdp.com/";//新

//    public static final String SERVER_PATH = "http://192.168.1.117:80/";//王浩
//    public static final String SERVER_PATH = "http://192.168.1.102:8087/";//my

//    public static final String SERVER_PATH = "http://ln.geotdp.com:8081/";//辽宁勘察
    public static final String SERVER_PATH = "http://xj.geotdp.com/";//新疆
//    public static final String SERVER_PATH = "http://218.249.134.238:8082/";//新平台测试
//    public static final String SERVER_PATH = "http://bj.geotdp.com:8081/";//北京

    // 登录请求
    public static final String LOGIN_POST = SERVER_PATH + "geotdp/compileUser/login";
    //获取版本信息
    public static final String GET_APP_CHECK_VERSION = SERVER_PATH + "geotdp/version/check";
    //下载apk地址
    public static final String Download_APK = SERVER_PATH + "gcdz.apk";
    // 关联项目 获取项目信息请求
    public static final String GET_PROJECT_INFO_BY_KEY_POST = SERVER_PATH + "geotdp/project/getProjectInfoByKey";
    //获取需要关联的勘察点列表
    public static final String GET_RELATE_HOLE = SERVER_PATH + "geotdp/hole/getRelateList";
    //关联勘察点
    public static final String DO_RELATE_HOLE = SERVER_PATH + "geotdp/hole/relate";
    //获取下载数据列表 getHoleListWithRecord
    public static final String GET_RELATE_HOLEWITHRECORD = SERVER_PATH + "geotdp/hole/getHoleListWithRecord";
    //下载服务器的数据
    public static final String DOWNLOAD_RELATE_HOLE = SERVER_PATH + "geotdp/hole/download";
    //上传字典库
    public static final String DICTIONARY_UPLOAD = SERVER_PATH + "geotdp/dictionary/upload";
    //下载字典库
    public static final String DICTIONARY_DOWNLOAD = SERVER_PATH + "geotdp/dictionary/download";
    // 勘探点上传所有
    public static final String UPLOAD_HOLE_NEW = SERVER_PATH + "geotdp/hole/uploadNew";

    //提交验收勘察点
    public static final String GET_HOLE_SUBMIT = SERVER_PATH + "geotdp/hole/submit";
    // 上传项目
    public static final String UPLOAD_PROJECT = SERVER_PATH + "geotdp/project/upload";
    // 上传勘探点
    public static final String UPLOAD_HOLE = SERVER_PATH + "geotdp/hole/upload";
    // 上传记录
    public static final String UPLOAD_RECORD = SERVER_PATH + "geotdp/record/upload";
    // 上传媒体(文件)
    public static final String UPLOAD_MEDIA_File = SERVER_PATH + "geotdp/media/upload";
    // 上传GPS
    public static final String UPLOAD_GPS = SERVER_PATH + "geotdp/gps/upload";
    /**
     * 124.117.212.117/trshswwz/web/sms/sendVerifyCode/手机号码
     * <p> 这个不需要参数,直接把手机号码放在连接里面就好了.</p>
     */
    public static final String VerifyCode_POST = SERVER_PATH + "trshswwz/web/sms/sendVerifyCode/";

    // 应用相关数据的路径
    public static final String APP_OLDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "gcdz";
    public static final String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.geotdb.compile" + File.separator + "files";
    // 存放数据库的路径
    public static final String DATABASE_PATH = APP_PATH + File.separator + "database";
    // 本地orm框架控制的数据库名
    public static final String DATABASE_NAME = "gcdz.db";
    // 本地orm框架控制的数据库完整的地址
    public static final String DATABASE_BASE = DATABASE_PATH + File.separator + DATABASE_NAME;

    // 存放图片的路径
    public static final String PIC_PATH = APP_PATH + File.separator + "pic";
    // 存放VIDEO的路径
    public static final String VIDEO_PATH = APP_PATH + File.separator + "video";
    // 存放XXX图片的路径
    public static final String PIC_HIDDEN_PATH = PIC_PATH + File.separator + "hidden";

    // 存放模版的路径
    public static final String TEMPLATE_PATH = APP_PATH + File.separator + "template";
    // 存放文书模版的路径
    public static final String File_TEMPLATE_PATH = TEMPLATE_PATH + File.separator + "file";


    /**
     * 整理应用的广播过滤字符串
     */
    //dictionary上传完成
    public static final String DICTIONARY_UPLOAD_COMPLETE = "upload.dictionary.complete";
    //dictionary下载完成
    public static final String DICTIONARY_DOWNLOAD_COMPLETE = "download.dictionary.complete";
    //更新时，根据不同情况，发送不同广播的过滤
    public static final String SERVICE_UPDATE_NEXT = "updata.service.next";

    /**
     * 图片路径 没有就创建
     */
    public static String getPicPath() {
        File destDir = new File(PIC_PATH);
        if (!destDir.exists())
            destDir.mkdirs();
        return PIC_PATH;
    }

    //list显示条数
    public static int PAGESIZE = 10;

}