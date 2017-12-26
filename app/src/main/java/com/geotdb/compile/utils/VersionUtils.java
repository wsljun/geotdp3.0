package com.geotdb.compile.utils;

import android.content.Context;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.service.DownloadService;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.VersionVo;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/8/29.
 */
public class VersionUtils {
    Context context;

    public VersionUtils(Context context) {
        this.context = context;
    }

    public void getVersion() {
        if (Common.getAPNType(context) > 0) {
            getAPKVersion();
        } else {
            ToastUtil.showToastS(context, "当前网络不可用");
        }
    }

    //获取本地apk版本信息，查看是否要更新
    private void getAPKVersion() {
        final int versionCode = Common.getAPPVersionCodeFromAPP(context);
        L.e("url-->>>" + Urls.GET_APP_CHECK_VERSION);
        OkHttpUtils.post().url(Urls.GET_APP_CHECK_VERSION).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "onError:e-->>" + e.getMessage());
                ToastUtil.showToastS(context, e.getMessage());
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
                        String code = versionVo.getCode();
                        try {
                            int c = Integer.parseInt(code);
                            if (versionCode < c) {
                                if (versionVo.getType().equals("1")) {  //0-不用更新  1-可以更新 2必须更新
                                    showOverAllDialog(context, false, content, positionCallback);
                                } else if (versionVo.getType().equals("2")) {
                                    showOverAllDialog(context, true, content, positionCallback);
                                }
                            } else {
                                ToastUtil.showToastS(context, "已经是最新版本");
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ToastUtil.showToastS(context, jsonResult.getMessage());
                    }
                } else {
                    ToastUtil.showToastS(context, "服务端接口发生变化");
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
            context.startService(new Intent(context, DownloadService.class));
            //通知loading已经下载，进行下一步
            Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
            intent.putExtra("case", "down");
            context.sendBroadcast(intent);
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            Intent intent = new Intent(Urls.SERVICE_UPDATE_NEXT);
            intent.putExtra("case", "nodown");
            context.sendBroadcast(intent);
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
        dialog.show();
    }
}
