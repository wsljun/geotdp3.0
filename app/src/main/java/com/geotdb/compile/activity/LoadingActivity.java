package com.geotdb.compile.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.utils.FileUtil;
import com.geotdb.compile.activity.base.MyApplication;
import com.geotdb.compile.db.LocalUserDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.DropItemVo;


import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.vo.LocalUser;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author XuFeng
 * @ 1.检测版本
 * 2.根据序列号加载员工数据
 */
public class LoadingActivity extends BaseAppCompatActivity {
    private TextView tv;
    private WebView loading_webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_loading);
        initView();
    }

    /**
     * 初始化布局控件
     */
    private void initView() {
        tv = (TextView) findViewById(R.id.tvwMessage);
//        new MyCount(1000, 1000).start();
        loading_webView = (WebView) findViewById(R.id.loading_webView);
        loadLocalHtml("file:///android_asset/index.html");
        detectPermission();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public void loadLocalHtml(String url) {
        WebSettings ws = loading_webView.getSettings();
        ws.setJavaScriptEnabled(true);//开启JavaScript支持
        loading_webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //重写此方法，用于捕捉页面上的跳转链接
                if ("http://start/".equals(url)) {
                    //在html代码中的按钮跳转地址需要同此地址一致
//                    Toast.makeText(getApplicationContext(), "开始体验", Toast.LENGTH_SHORT).show();
                    if (isReady) {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    }else{
                        ToastUtil.showToastS(LoadingActivity.this,"还没准备好,请稍等");
                    }

                }
                return true;
            }
        });
        loading_webView.loadUrl(url);
    }


    /**
     * 定义一个倒计时的内部类
     */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            detectPermission();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv.setText("加载中...");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        L.e("TAG", "detectPermission--->>>onRequestPermissionsResult");
    }

    private static final int REQUECT_CODE_STORAGE = 2;

    /**
     * 检测权限
     */
    public void detectPermission() {
        L.e("TAG", "detectPermission--->>>detectPermission");
        MPermissions.requestPermissions(LoadingActivity.this, REQUECT_CODE_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA);
    }

    @PermissionGrant(REQUECT_CODE_STORAGE)
    public void requestSdcardSuccess() {
        L.e("TAG", "detectPermission--->>>requestSdcardSuccess");
        initDB();
        checkUser();
    }


    @PermissionDenied(REQUECT_CODE_STORAGE)
    public void requestSdcardFailed() {
        L.e("TAG", "detectPermission--->>>requestSdcardFailed");
        finish();
    }


    /**
     * 数据库处理
     * 卸载的时候会删除应用目录下的sp文件，以此为依据，判断是否是卸载重新安装，更新不会删除sp文件
     */
    public void initDB() {
        tv.setText("初始化数据库...");
        String name = (String) SPUtils.get(getApplicationContext(), "name", "");
        if (TextUtils.isEmpty(name)) {
            FileUtil.deleteAllFiles(Urls.APP_PATH);
            copyDB();
            SPUtils.put(getApplicationContext(), "name", "sqlite");
        }
        SPUtils.put(getApplicationContext(), "setup", "11111");
        chackOldPath(Urls.APP_OLDPATH);
    }

    /**
     * 复制db到sd卡中
     */
    public void copyDB() {
        try {
            //在指定的文件夹中创建文件
            FileUtil.CreateText();
            InputStream is = getResources().openRawResource(R.raw.gcdz);
            FileOutputStream fos = new FileOutputStream(Urls.DATABASE_BASE);
            byte[] buffer = new byte[8192];
            int count = 0;
            while ((count = is.read(buffer)) >= 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 检查老的路径是否存在，没啥用
     *
     * @param olePath
     * @return
     */
    public boolean chackOldPath(String olePath) {
        File file = new File(olePath);
        if (file.exists()) {
            if (file.listFiles() != null) {
                L.e("oldPath--" + file.getPath());
                return true;
            } else {
                L.e("oldPath----false1");
                return false;
            }
        } else {
            L.e("oldPath----false2");
            return false;
        }
    }

    /**
     * 进入主页前，检查用户是否存在，获取用户集合，获取用户名集合
     */
    private void checkUser() {
        tv.setText("初始化用户信息...");
        List<LocalUser> userList = new ArrayList<>();
        LocalUser localUser = null;
        List<DropItemVo> nameList = null;
        LocalUserDao localUserDao = new LocalUserDao(this);
        //按时间排序查询,现在还没有写
        userList = localUserDao.getList();
        if (userList.size() == 0) {
            localUser = new LocalUser();
        } else {
            localUser = userList.get(0);
            //获取所有usernmae集合，放到application
            nameList = new ArrayList<>();
            LocalUser newUser = null;
            for (int i = 1; i <= userList.size(); i++) {
                nameList.add(new DropItemVo(i + "", userList.get(i - 1).getEmail()));
                //这里 要么按时间排序找到最近的，要么判断出一条我需要的用户信息
                localUser = userList.get(i - 1);
                //只有一个自动登陆的用户，查出来给全局
                if (Boolean.parseBoolean(localUser.getIsAutoLogin())) {
                    newUser = localUser;
                }
            }
            if (newUser != null) {
                Common.setLocalUser(this, newUser.getId());
            } else {
                Common.setLocalUser(this, "");
            }
            MyApplication.getInstance().setNameList(nameList);
        }
        goMainPage();
    }


    private boolean isReady = false;

    /**
     * 跳转主页面
     */
    public void goMainPage() {
        //准备好跳转页面了
        isReady = true;
    }

    //屏蔽返回键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}