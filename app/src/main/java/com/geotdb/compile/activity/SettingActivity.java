package com.geotdb.compile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.offlinemap.OfflineMapActivity;
import com.geotdb.compile.service.DownloadService;
import com.geotdb.compile.service.UpdataService;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.dialog.MapModeDialog;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.utils.VersionUtils;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.VersionVo;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/7/14.
 */
public class SettingActivity extends BaseAppCompatActivity {
    private TextView setting_newcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting_main);
//        IntentFilter intentFilter = new IntentFilter("updata.service.check");
//        registerReceiver(checkReceiver, intentFilter);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setting_newcode = (TextView) findViewById(R.id.setting_newcode);
        setting_newcode.setText(Common.getAPPVersionNameFromAPP(this));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_location:
//                showMapModeDialog();
                ToastUtil.showToastS(this, "暂停使用");
                break;
            case R.id.setting_map:
                startActivity(new Intent(this, OfflineMapActivity.class));
                break;
            case R.id.setting_update:
                new VersionUtils(this).getVersion();
                break;
            case R.id.setting_about:
                ToastUtil.showToastS(this, "暂停使用");
                break;
            case R.id.setting_dictionary:
                startActivity(new Intent(this, DictionaryActvity.class));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //检查版本
    private void checkVersion() {
        Intent intent = new Intent(SettingActivity.this, UpdataService.class);
        intent.setAction("SettingActivity");
        startService(intent);
    }

    private void getVersion() {
        if (Common.getAPNType(this) > 0) {
            getAPKVersion();
        } else {
            ToastUtil.showToastS(this, "当前网络不可用");
        }
    }

    //获取本地apk版本信息，查看是否要更新
    public void getAPKVersion() {
        final int versionCode = Common.getAPPVersionCodeFromAPP(this);
        L.e("url-->>>" + Urls.GET_APP_CHECK_VERSION);
        OkHttpUtils.post().url(Urls.GET_APP_CHECK_VERSION).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "onError:e-->>" + e.getMessage());
                //提示得委婉点
                ToastUtil.showToastS(SettingActivity.this, "未能获取版本信息");
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "onResponse:response-->>" + response);
                if (JsonUtils.isGoodJson(response)) {
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
                                showOverAllDialog(SettingActivity.this, false, content, positionCallback);
                            } else if (versionVo.getType().equals("2")) {
                                showOverAllDialog(SettingActivity.this, true, content, positionCallback);
                            }
                        } else {
                            ToastUtil.showToastS(SettingActivity.this, "已经是最新版本");
                        }

                    } else {
                        ToastUtil.showToastS(SettingActivity.this, "未能获取版本信息");
                    }
                } else {
                    ToastUtil.showToastS(SettingActivity.this, "服务器异常，请联系客服");
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
            startService(new Intent(SettingActivity.this, DownloadService.class));
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
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

//    BroadcastReceiver checkReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            stopService(new Intent(SettingActivity.this, UpdataService.class));
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(checkReceiver);
    }


    //定位模式选择的dialog
    public void showMapModeDialog() {
        new MapModeDialog().show(this);
    }

}
