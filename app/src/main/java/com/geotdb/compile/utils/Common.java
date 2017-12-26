package com.geotdb.compile.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.db.LocalUserDao;
import com.geotdb.compile.service.DownloadService;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.VersionVo;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

@SuppressLint("DefaultLocale")
public abstract class Common {

    public static Boolean isDebug = false;

    private static JSONObject user;

    public static JSONObject getUser() {
        return user;
    }

    public static void setUser(JSONObject user) {
        Common.user = user;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDatedFName(String fname) {
        StringBuffer result = new StringBuffer();

        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String dateSfx = "_" + df.format(new Date());

        int idx = fname.lastIndexOf('.');
        if (idx != -1) {
            result.append(fname.substring(0, idx));
            result.append(dateSfx);
            result.append(fname.substring(idx));
        } else {
            result.append(fname);
            result.append(dateSfx);
        }

        return result.toString();
    }

    /**
     * 最简单的跳转
     *
     * @param activity
     * @param cls
     */
    public static void redirect(Activity activity, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        activity.startActivity(intent);
    }

    /**
     * 去拍照
     *
     * @param activity
     */
    public static void goCamera(Activity activity) {
        try {
            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
            activity.startActivityForResult(i, Activity.DEFAULT_KEYS_DIALER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕方向
     */
    public static int ScreenOrient(Activity activity) {
        int orient = activity.getRequestedOrientation();
        if (orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            // 宽>高为横屏,反正为竖屏
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        return orient;
    }

    /**
     * 用来实现屏幕背景的自动切换
     */
    public static void AutoBackground(Activity activity, View view, int Background_v, int Background_h) {
        int orient = ScreenOrient(activity);
        if (orient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { // 纵向
            view.setBackgroundResource(Background_v);
        } else { // 横向
            view.setBackgroundResource(Background_h);
        }
    }

    /**
     * 获取网络上的图片
     *
     * @param url
     * @return
     */
    public static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //更新图片内容
    private static WeakReference<ImageView> imageViewReference; // 使用WeakReference解决内存问题

    public static void download(String url, ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) { // 实际的下载线程，内部其实是concurrent线程，所以不会阻塞
                return returnBitMap(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) { // 下载完后执行的
                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap); // 下载完设置imageview为刚才下载的bitmap对象
                    }
                }
            }
        }.execute(url);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取UUID
     */
    public static String getDeviceUUID() {
        UUID uuid = UUID.randomUUID();
        String result = uuid.toString().replaceAll("\\-", "");
        return result;
    }

    /**
     * @param context
     * @return
     * @author sky 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络
     */
    public static int getAPNType(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    public static String getUUID() {
        UUID uid = UUID.randomUUID();
        String str = uid.toString().replace("-", "");
        // str.toUpperCase(); 全部字母转换成大写
        return str;
    }

    /*
    * 在SD卡上创建目录；
    */
    public static File createDir(String dirpath) {
        File dir = new File(dirpath);
        dir.mkdir();
        return dir;
    }

    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    /**
     * 调用系统相机时，指定uri
     * 根据文件路径创建Uri
     */
    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * 创建文件存储位置
     */
    public static File getOutputMediaFile() {
        File mediaStorageDir = null;
        try {
            // 存放路径
            mediaStorageDir = createDir(Urls.APP_OLDPATH);
            L.e("TAG", "Successfully created mediaStorageDir: " + mediaStorageDir);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("TAG", "Error in Creating mediaStorageDir: " + mediaStorageDir);
        }
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                L.e("TAG", "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }
        String strFile0 = getUUID() + ".jpg";
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + strFile0);
        return mediaFile;
    }

    /**
     * 获取apk的版本号 currentVersionCode
     *
     * @param ctx
     * @return
     */
    public static int getAPPVersionCodeFromAPP(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }
        return currentVersionCode;
    }

    /**
     * 获取apk的版本号 currentVersionName
     *
     * @param ctx
     * @return
     */
    public static String getAPPVersionNameFromAPP(Context ctx) {
        String currentVersionName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionName = info.versionName; // 版本名
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }
        return currentVersionName;
    }

    //根据路径获取文件名

    public static String getFileNameByPath(String path) {
        String fileName = new File(path).getName();
        return fileName;
    }

    /**
     * @param l1 经度1
     * @param b1 纬度1
     * @param l2 经度2
     * @param b2 纬度2
     * @return
     */
    public static String GetDistance(double l1, double b1, double l2, double b2) {
        double n, t, t2, m, m2, ng2;
        double c, x0, p, e12, c0, c1, c2, c3, pai;
        double x1, y1, x2, y2;

        double y0 = 500000;
        pai = 3.1415926535898;
        p = 206264.8062471;
        c = 6399698.90178271;
        e12 = 6.7385254146835E-03;
        c0 = 6367558.49687;
        c1 = 32005.7801;
        c2 = 133.9213;
        c3 = 0.7032;

        b1 = b1 * pai / 180;
        x0 = c0 * b1 - Math.cos(b1) * (c1 * Math.sin(b1) + c2 * Math.pow(Math.sin(b1), 3) + c3 * Math.pow(Math.sin(b1), 5));//子午线弧长
        double i = (l1 - l2) * 3600; //经差"
        t = Math.tan(b1);
        t2 = t * t;
        ng2 = e12 * Math.pow((Math.cos(b1)), 2);
        n = c / Math.sqrt(1 + ng2);  //卯酉圈曲率半径
        m = i * Math.cos(b1) / p;
        m2 = m * m;
        x1 = x0 + n * t * ((0.5 + ((5 - t2 + 9 * ng2 + 4 * ng2 * ng2) / 24 + (61 - 58 * t2 + t2 * t2) * m2 / 720) * m2) * m2);
        y1 = n * m * (1 + m2 * ((1 - t2 + ng2) / 6 + m2 * (5 - 18 * t2 + t2 * t2 + 14 * ng2 - 58 * ng2 * t2) / 120));
        y1 = y1 + y0;

        b2 = b2 * pai / 180;
        x2 = c0 * b2 - Math.cos(b2) * (c1 * Math.sin(b2) + c2 * Math.pow(Math.sin(b2), 3) + c3 * Math.pow(Math.sin(b2), 5));//子午线弧长
        y2 = 500000;

        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        double value = Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
        String s = String.valueOf(dcmFmt.format(value));
        return s;
    }


    //獲取當前登陸的用戶
    public static LocalUser getLocalUser(Context context) {
        String userID = getUserIDBySP(context);
        if (!TextUtils.isEmpty(userID)) {
            LocalUserDao localUserDao = new LocalUserDao(context);
            return localUserDao.getUserByID(userID);
        }
        return null;
    }

    //登陸成功的用戶保存他的ID
    public static void setLocalUser(Context context, String userID) {
        SPUtils.put(context, "userID", userID);
    }

    public static String getUserIDBySP(Context context) {
        return (String) SPUtils.get(context, "userID", "");
    }

    //检查gps是否开启
    public static boolean gPSIsOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    //获取当前时区是否是GMT+08:00
    public static void setTimeZone(Context context) {
        if (!isChinaTime()) {
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setTimeZone("Asia/Shanghai");// Asia/Taipei//GMT+08:00
        }
    }

    public static boolean isChinaTime() {
        TimeZone tz = TimeZone.getDefault();
        if (tz.getID().equals("Asia/Shanghai")) {
            return true;
        }
        return false;
    }

    //找文件夹下的图片
    public static String getPicByDir(String dirPath) {
        int start = dirPath.lastIndexOf("/");
        if (start != -1) {
            return dirPath + "/" + dirPath.substring(start + 1, dirPath.length()) + ".jpg";
        } else {
            return null;
        }
    }

    //找文件夹下的视频
    public static String getVideoByDir(String dirPath) {
        int start = dirPath.lastIndexOf("/");
        if (start != -1) {
            return dirPath + "/" + dirPath.substring(start + 1, dirPath.length()) + ".mp4";
        } else {
            return null;
        }
    }




}