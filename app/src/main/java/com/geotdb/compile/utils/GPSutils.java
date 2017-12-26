package com.geotdb.compile.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;

public class GPSutils {

    public interface Gps {

    }

    // 地理位置控制类，用于GPS信息处理
    private static LocationManager lm;

    public static void initGPS(Context context, LocationListener locationListener) {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Activity activity = (Activity) context;

        // 判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(intent, 0);
            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // 监听状态
        lm.addGpsStatusListener(listener);

        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    // 状态监听
    static GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    setLog("第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    // setLog("卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    // System.out.println("搜索到：" + count + "颗卫星");
                    // setLog("搜索到：" + count + "颗卫星");
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    setLog("定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    setLog("定位结束");
                    break;
            }
        }

        ;
    };

    private static void setLog(String txt) {
        // jkgzbgpszt.setText(txt);
    }

    public static String utcToTimeZoneDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone zone = TimeZone.getTimeZone("GMT+8");
        Date dateTemp = new Date(date);
        format.setTimeZone(zone);
        return format.format(dateTemp);
    }

    public static String setNewThumbnail(String oldPath, AMapLocation amapLocation) throws Exception {
        // Gps纬度
        double lat = amapLocation.getLatitude();
        // Gps经度
        double lon = amapLocation.getLongitude();
        // Gps海拔
        double alt = amapLocation.getAltitude();
        try {
            // 获取jpg文件
            ExifInterface exifInterface = new ExifInterface(oldPath);
            Log.e("TAG", lat + "--" + lon + "--" + alt);
            System.out.println("utcToTimeZoneDate(mLocation.getTime()):" + utcToTimeZoneDate(amapLocation.getTime()));
            // 日期时间
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, utcToTimeZoneDate(amapLocation.getTime()));
            //
            // System.out.println("String.valueOf(alt):"+String.valueOf((int)alt));
            // 写入海拔信息
            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, String.valueOf(alt));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, lon > 0 ? "0" : "1");

            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, gpsInfoConvert(lat));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat > 0 ? "N" : "S");
            // 写入经度信息
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, gpsInfoConvert(lon));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon > 0 ? "E" : "W");

            // //写入日期戳
            // exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP,utcToTimeZoneDate(mLocation.getTime()));
            // //写入时间戳
            // exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP,utcToTimeZoneDate(mLocation.getTime()));

            // 这句话很重要，一定要saveAttributes才能使写入的信息生效。
            exifInterface.saveAttributes();
            // 获取纬度信息
            // String latitude =
            // exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            // 获取经度信息
            // String longitude =
            // exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String gpsInfoConvert(double gpsInfo) {
        gpsInfo = Math.abs(gpsInfo);
        String dms = Location.convert(gpsInfo, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        if (secnds.length == 0) {
            seconds = splits[2];
        } else {
            seconds = secnds[0];
        }
        return splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
    }

}
