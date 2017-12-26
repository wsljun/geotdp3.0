package com.geotdb.compile.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.database.Cursor;

public class Key {

    /**
     * @param yzf 字符串
     * @param lx  0为解密 .1为加密
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static String jiaMi(String yzf) throws UnsupportedEncodingException {
        String jmzfc, zfc1;    //jmzfc=经过加解密后的值
        int i, j, k, zflen, zflen1;
        jmzfc = "";
        yzf = yzf.trim();
        //将传入的字符串转换为char数组
        if (!yzf.equals("")) {           //为加密过程

            //将传入的字符串转换为char数组
            if (yzf.length() % 2 == 1) yzf = yzf + (char) 27;
            char[] zfc = yzf.toCharArray();
            zflen = zfc.length;
            char[] mzfc = new char[zflen / 2 * 3];        //将char 数组放大3/2倍，备用
            Arrays.fill(mzfc, ' ');                    //赋初值为空格
            //--没隔2位加一个数字
            j = 0;
            for (i = 0; i < zflen; i++) {
                zfc[i] = (char) (zfc[i] + (char) 20);
                zfc[i + 1] = (char) (zfc[i + 1] + (char) 20);

                mzfc[j] = zfc[i];
                mzfc[j + 1] = zfc[i + 1];
                mzfc[j + 2] = (char) (zfc[i] + (i + 1) % 3);            //最终将i+1 变为 i
                i = i + 1;
                j = j + 3;
            }
            zflen1 = j;
            k = zflen1;            //加工后的字串长

            j = (int) k / 2;
            for (i = 0; i < j; i++) {
                k = k - 1;
                char ls1 = (char) (mzfc[i] + (i + 1) % 10);
                char ls2 = (char) (mzfc[k] + (k + 1) % 10);
                mzfc[i] = ls1;
                mzfc[k] = ls2;
            }
            jmzfc = String.valueOf(mzfc);
        }

        return jmzfc.trim();
    }


    /**
     * @param yzf 字符串
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static String jieMi(String yzf) throws UnsupportedEncodingException {
        String jmzfc, zfc1;    //jmzfc=经过加解密后的值
        int i, j, k, zflen, zflen1;
        jmzfc = "";
        yzf = yzf.trim();
        if (!yzf.equals("")) {          //为解密过程
            char[] mzfc = yzf.toCharArray();        //将传入的字符串转换为char数组
            zflen = mzfc.length;
            char[] zfc = new char[zflen];            //还原数组
            Arrays.fill(zfc, ' ');                    //赋初值为空格

            j = zflen;
            for (i = 1; i <= (int) zflen / 2; i++) {
                mzfc[i - 1] = (char) (mzfc[i - 1] - i % 10);
                mzfc[j - 1] = (char) (mzfc[j - 1] - j % 10);
                j = j - 1;
            }

            j = 1;
            for (i = 1; i <= zflen; i++) {
                zfc[j] = (char) (mzfc[i - 1] - (char) 20);
                zfc[j + 1] = (char) (mzfc[i] - (char) 20);
                i = i + 2;
                j = j + 2;
            }
            if (zfc[j - 1] == 27) zfc[j - 1] = 32;
            jmzfc = String.valueOf(zfc);
        }

        return jmzfc.trim();
    }


    public String cursorJM(Cursor cursor, String s) {
        String ss = "";
        try {
            ss = jieMi(cursor.getString(cursor.getColumnIndex(s)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ss;

    }
}