package com.geotdb.compile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DateUtil {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 将字符串转换成时间
     *
     * @param str "yyyy-MM-dd HH:mm:ss"
     */
    public static Date str2Date(String str) {
        Date date = null;
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 2012-02-24
        //date = java.sql.Date.valueOf(str);
        return date;
    }

    /**
     * 将时间转换成字符串
     *
     * @param date
     */
    public static String date2Str(Date date) {
        String str = null;
        str = sdf.format(date);
        return str;
    }


    public static void traversal(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> itEntry = it.next();
            String itKey = itEntry.getKey();
            String itValue = itEntry.getValue();
            //注意：可以使用这种遍历方式进行删除元素和修改元素
            itEntry.setValue(itValue == null ? "" : itValue);
//            it.remove();
        }
    }
}