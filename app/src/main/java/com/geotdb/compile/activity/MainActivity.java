/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geotdb.compile.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.fragment.NotLoginFramgment;
import com.geotdb.compile.service.DownloadService;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.LocalUserDao;
import com.geotdb.compile.fragment.ProjectListFragment;
import com.geotdb.compile.receiver.UpdateApkReceiver;
import com.geotdb.compile.service.UpdataService;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.dialog.LoginDialog;
import com.geotdb.compile.utils.VersionUtils;
import com.geotdb.compile.view.DrawableVerticalButton;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Project;
import com.geotdb.compile.vo.VersionVo;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.qiujuer.genius.ui.widget.Button;

import okhttp3.Call;

/**
 * TODO
 */
public class MainActivity extends BaseAppCompatActivity {
    private static MainActivity mainActivity;
    private DrawerLayout mDrawerLayout;
    private ProjectListFragment projectListFragment;
    private NotLoginFramgment notLoginFramgment;
    private TextView drawer_username;
    private TextView Drawer_realname;
    FloatingActionButton addProject;
    private UpdateApkReceiver updateApkReceiver;
    private LocalUser localUser;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localUser = Common.getLocalUser(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(drawerLis);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        View headerView = navigationView.getHeaderView(0);
        drawer_username = (TextView) headerView.findViewById(R.id.Drawer_username);
        Drawer_realname = (TextView) headerView.findViewById(R.id.Drawer_realname);
        ImageView btnLogin = (ImageView) headerView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(btnLoginListener);
        RelativeLayout project = (RelativeLayout) headerView.findViewById(R.id.drawer_project);
        project.setOnClickListener(projectListener);
        RelativeLayout msg = (RelativeLayout) headerView.findViewById(R.id.drawer_msg);
        msg.setOnClickListener(msgListener);
        RelativeLayout news = (RelativeLayout) headerView.findViewById(R.id.drawer_news);
        news.setOnClickListener(newsListener);
        RelativeLayout tender = (RelativeLayout) headerView.findViewById(R.id.drawer_tender);
        tender.setOnClickListener(tenderListener);
        RelativeLayout setting = (RelativeLayout) headerView.findViewById(R.id.drawer_setting);
        setting.setOnClickListener(settingListener);
        RelativeLayout help = (RelativeLayout) headerView.findViewById(R.id.drawer_help);
        help.setOnClickListener(helpListener);

        Button retroaction = (Button) headerView.findViewById(R.id.drawer_retroaction);
        retroaction.setOnClickListener(retroactionListener);
        Button logout = (Button) headerView.findViewById(R.id.drawer_logout);
        logout.setOnClickListener(logoutListener);
        Button quit = (Button) headerView.findViewById(R.id.drawer_quit);
        quit.setOnClickListener(quitListener);

        fragmentManager = getSupportFragmentManager();

        //接收更新信息的处理
        IntentFilter checkFilter = new IntentFilter(Urls.SERVICE_UPDATE_NEXT);
        updateApkReceiver = new UpdateApkReceiver();
        registerReceiver(updateApkReceiver, checkFilter);
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter("MainActivity.CheckUser.login");
        registerReceiver(loginReceiver, intentFilter);
        new VersionUtils(this).getVersion();

        checkUser();
    }


    //接收到广播，重新检查用户是否存在

    BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkUser();
        }
    };
    // 登录
    View.OnClickListener btnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    };
    //注销
    View.OnClickListener quitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            showLogout();
        }
    };
    //全部项目
    View.OnClickListener projectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            if (localUser != null) {
                if (projectListFragment == null) {
                    setProjectListFragment();
                }
            } else {
                ToastUtil.showToastS(MainActivity.this, "请先登陆");
            }

        }
    };

    //我的信息
    View.OnClickListener msgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            ToastUtil.showToastS(MainActivity.this, "未开通");
        }
    };

    //新闻
    View.OnClickListener newsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            ToastUtil.showToastS(MainActivity.this, "未开通");
        }
    };

    //招投标
    View.OnClickListener tenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            ToastUtil.showToastS(MainActivity.this, "未开通");
        }
    };

    //设置
    View.OnClickListener settingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            goSetting();
        }
    };

    //帮助
    View.OnClickListener helpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            Intent intent = new Intent(MainActivity.this, HelpActivtiy.class);
            intent.setAction(TAG);
            startActivity(intent);
        }
    };

    //跳转登陆界面
    View.OnClickListener retroactionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    };

    //退出
    View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawers();
            finishDialog();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        L.e("TAG", "MainActvity--->>>onResume");
//        checkUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpdataService.class));
        unregisterReceiver(loginReceiver);
        unregisterReceiver(updateApkReceiver);
    }

    //左侧抽屉的监听,打开抽屉时检测用户
    DrawerLayout.DrawerListener drawerLis = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
//            checkUser();
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    //进入主页，检查用户是否存在，获取用户信息，根据用户是否存在 显示不用fragment
    private void checkUser() {
        setFloatingActionButton();
        if (Common.getLocalUser(this) != null) {
            L.e("TAG", "onDrawerOpened--->>>application里有用戶");
            localUser = Common.getLocalUser(this);
            if (!TextUtils.isEmpty(localUser.getRealName())) {
                drawer_username.setText(localUser.getEmail());
                Drawer_realname.setText(localUser.getRealName());
            }
            addProject.setVisibility(View.VISIBLE);
            setProjectListFragment();
        } else {
            L.e("TAG", "onDrawerOpened--->>>application沒有用戶");
            drawer_username.setText("账号未登陆");
            Drawer_realname.setText("");
            addProject.setVisibility(View.GONE);
            setNotLoginFragment();
        }

    }

    /**
     * 设置悬浮添加按钮
     */
    private void setFloatingActionButton() {
        addProject = (FloatingActionButton) findViewById(R.id.addProject);
        addProject.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addProject.show(true);
                addProject.setShowAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_from_bottom));
                addProject.setHideAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_to_bottom));
            }
        }, 300);
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(new Intent("MainActivity.CheckUser.login"));
                createProject();
                onRefreshList();
            }
        });
    }

    public void showFloat() {
        addProject.show(true);
    }

    public void hideFloat() {
        addProject.hide(true);
    }


    public Project createProject() {
        Project project = new Project(this);
        ProjectDao projectDao = new ProjectDao(this);
        projectDao.add(project);
        return project;
    }


    /**
     * 添加projectListFragmnet
     */
    private void setProjectListFragment() {
        L.e("------------------------->>>>setProjectListFragment");
        //向detailFragment传入参数
        projectListFragment = new ProjectListFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.projectListFrameLayout, projectListFragment, "projectListFragment");
        fragmentTransaction.commitAllowingStateLoss();
    }


    /**
     * 添加notLoginFragmnet
     */
    private void setNotLoginFragment() {
        L.e("------------------------->>>>setNotLoginFragment");
        //向detailFragment传入参数
        notLoginFramgment = new NotLoginFramgment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.projectListFrameLayout, notLoginFramgment, "notLoginFramgment");
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.act_search:
                if (localUser == null) {
                    ToastUtil.showToastS(MainActivity.this, "未登录用户，请登录");
                } else {
                    ToastUtil.showToastS(MainActivity.this, "这里是搜索功能,还未开发");
                }

                return true;
            case R.id.act_add:
                if (localUser == null) {
                    ToastUtil.showToastS(MainActivity.this, "未登录用户，请登录");
                } else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ProjectEditActivity.class);
                    startActivityForResult(intent, ProjectEditActivity.REQUEST_CODE);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.drawer_quit:
                        showLogout();
                        return true;
                    case R.id.drawer_logout:
                        finishDialog();
                        return true;
                    case R.id.drawer_setting:
                        goSetting();
                        return true;
                    case R.id.drawer_help:
                        return true;
                    case R.id.drawer_retroaction:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        return true;
                    default:
                        menuItem.setChecked(true);
                        return true;
                }
            }
        });
    }


    /**
     * 跳转设置界面
     */
    private void goSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    /**
     * 注销提示框
     */
    private void showLogout() {
        mDrawerLayout.closeDrawers();
        if (Common.getLocalUser(this) != null) {
            new MaterialDialog.Builder(this).content("退出当前用户登陆").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    //注销用户应该取消当前用户的自动登陆
                    LocalUser nowUser = Common.getLocalUser(MainActivity.this);
                    LocalUserDao localUserDao = new LocalUserDao(MainActivity.this);
                    nowUser.setIsAutoLogin("false");
                    localUserDao.updateUser(nowUser);
                    Common.setLocalUser(MainActivity.this, "");
                    sendBroadcast(new Intent("MainActivity.CheckUser.login"));
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }).show();
        } else {
            ToastUtil.showToastS(this, "当前没有用户登陆");
        }

    }


    public void onRefreshList() {
        L.e("TAG", "MainActvity--->>>onRefreshList");
        if (projectListFragment == null) {
            setProjectListFragment();
        }
        projectListFragment.onRefreshList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                mDrawerLayout.closeDrawers();
            } else {
                finishDialog();
            }
        }
        return true;
    }

    //退出dialog
    private void finishDialog() {
        new MaterialDialog.Builder(this).content("是否退出应用").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                finish();
            }
        }).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ProjectEditActivity.REQUEST_CODE) {
            onRefreshList();
        }
    }
}
