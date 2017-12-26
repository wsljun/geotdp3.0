package com.geotdb.compile.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;

public class AssetCopyUtil {
    /**
     * 从资产目录拷贝文件
     *
     * @param context
     *            上下文
     * @param filename
     *            资产目录文件的名称
     * @param destfilepath
     *            目标文件的路径
     * @return
     */
    public static File copy(Context context, String filename, String destfilepath, ProgressDialog pd) {
        try {
            InputStream is = context.getAssets().open(filename);
            int max = is.available();
            pd.setMax(max);
            File file = new File(destfilepath);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            int total = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                pd.setProgress(total);
            }
            fos.flush();
            fos.close();
            is.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
