package com.geotdb.compile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.geotdb.compile.utils.ToastUtil;

/**
 * Created by Administrator on 2017/3/21.
 */
public class UpdateApkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String aCase = intent.getExtras().getString("case");
        if ("error".equals(aCase)) {
            ToastUtil.showToastS(context, "网络链接失败...");
        } else if ("new".equals(aCase)) {
            ToastUtil.showToastS(context, "已是最新版本...");
        } else if ("pass".equals(aCase) || "nodown".equals(aCase)) {
//            ToastUtil.showToastS(context, "......");
        } else if ("down".equals(aCase)) {
            ToastUtil.showToastS(context, "开始下载...");
        }
    }
}
