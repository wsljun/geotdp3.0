package com.geotdb.compile.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.VersionVo;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class UpdataService extends Service {
    String action = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent && null != intent.getAction()) {
            action = intent.getAction();
        }
        if (Common.getAPNType(this) > 0) {
            getAPKVersion();
        } else {
            ToastUtil.showToastS(this, "当前网络不可用");
            //系统设置下检查版本时，发送广播说明操作完成关闭dialog
//            sendBroadcast(new Intent("updata.service.check"));
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //获取本地apk版本信息，查看是否要更新
    public void getAPKVersion() {
        final int versionCode = Common.getAPPVersionCodeFromAPP(this);
//        Map<String, String> map = new HashMap<>();
//        map.put("versionCode", versionCode + "");
        //系统设置下检查版本时，发送广播说明操作完成关闭dialog
//        sendBroadcast(new Intent("updata.service.check"));
        L.e("url-->>>" + Urls.GET_APP_CHECK_VERSION);
        OkHttpUtils.post().url(Urls.GET_APP_CHECK_VERSION).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "onError:e-->>" + e.getMessage());
                //提示得委婉点
                ToastUtil.showToastS(getApplicationContext(), "未能获取版本信息");
                Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
                intent.putExtra("case", "error");
                sendBroadcast(intent);
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "onResponse:response-->>" + response);
                if (!response.equals("")) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        //获取成功
                        VersionVo versionVo = gson.fromJson(jsonResult.getResult().toString(), VersionVo.class);
                        L.e(jsonResult.getResult().toString());
                        String content = "版本:" + versionVo.getName() + "\n更新内容:" + versionVo.getDescription() + "\n大小:" + versionVo.getSize();
                        downloadLink = versionVo.getDownloadLink();
                        if (versionCode < Integer.parseInt(versionVo.getCode())) {
                            if (versionVo.getType().equals("1")) {  //0-不用更新  1-可以更新 2必须更新
                                showOverAllDialog(UpdataService.this, false, content, positionCallback);
                            } else if (versionVo.getType().equals("2")) {
                                showOverAllDialog(UpdataService.this, true, content, positionCallback);
                            } else {//type = 0;
                                Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
                                intent.putExtra("case", "pass");
                                sendBroadcast(intent);
                            }
                        } else {
                            if (!action.equals("MainActivity")) {
                                ToastUtil.showToastS(UpdataService.this, "已经是最新版本");
                            }
                            Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
                            intent.putExtra("case", "new");
                            sendBroadcast(intent);
                        }

                    } else {
                        ToastUtil.showToastS(UpdataService.this, "未能获取版本信息");
                    }
                } else {
                    ToastUtil.showToastS(UpdataService.this, "服务端接口发生变化");
                }

            }
        });
    }


    String downloadLink;
    //提示框确定按键的监听
    MaterialDialog.ButtonCallback positionCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            //启动负责下载的service
            startService(new Intent(UpdataService.this, DownloadService.class));
            //通知loading已经下载，进行下一步
            Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
            intent.putExtra("case", "down");
            sendBroadcast(intent);
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
            intent.putExtra("case", "nodown");
            sendBroadcast(intent);
        }
    };

    //更新
    public void showOverAllDialog(Context context, boolean must, String content, MaterialDialog.ButtonCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.content(content);
        if (!must) {
            builder.negativeText(R.string.disagree);
        }
        builder.canceledOnTouchOutside(false);
        builder.positiveText(R.string.agree);
        builder.callback(callback);
        MaterialDialog dialog = builder.build();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

}
