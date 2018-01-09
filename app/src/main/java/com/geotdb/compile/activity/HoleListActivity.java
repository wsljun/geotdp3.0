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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.adapter.HoleListAdapter;
import com.geotdb.compile.adapter.RelateHoleAdapter;
import com.geotdb.compile.db.DBHelper;
//import com.geotdb.compile.fragment.HoleListFragment;
import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.dialog.HoleInfoDialog;
import com.geotdb.compile.dialog.RelateHoleDialog;
import com.geotdb.compile.service.NewUploadService;
import com.geotdb.compile.service.UploadService;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.view.MaterialEditTextNoEmoji;
import com.geotdb.compile.vo.Gps;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Project;
import com.geotdb.compile.vo.Record;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.qiujuer.genius.ui.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Call;

public class HoleListActivity extends BaseAppCompatActivity {
    public static final String EXTRA_PROJECT = "project";

    public Project project;
    //    private HoleListFragment holeListFragment;
    private FloatingActionButton addProject;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager manager;
    private int page = 1;//页数
    private int count;//总记录数
    private List<Hole> list;
    private HoleListAdapter holeListAdapter;
    private DBHelper dbHelper;

    private ProjectDao projectDao;
    private HoleDao holeDao;
    private RecordDao recordDao;
    private GpsDao gpsDao;
    //    private Hole hole, relateHole;
    private int holePosition;
    private FloatingActionMenu hole_menu;

    private List<Hole> relateList;//获取勘察点列表
    private RelateHoleDialog relateHoleDialog;//获取勘察点列表dialog
    private Hole relateHole;//获取发布勘探点列表，点击获取发布的hole，存放关联的勘察点
    private MaterialEditTextNoEmoji search_edit;

    private Hole doRelateHole;//点击勘探点列表获得当前的hole

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_project_main);
        Bundle bundle = this.getIntent().getExtras();
        project = (Project) bundle.getSerializable(EXTRA_PROJECT);
        DBHelper dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Project, String> dao = dbHelper.getDao(Project.class);
            project = dao.queryForId(project.getId());
            project.jieProject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        initData();
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(project.getFullName());
        toolbar.setSubtitle("勘探点列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setFloatingActionButton();
        setAddMenuButton();
        recyclerView = (RecyclerView) findViewById(R.id.hole_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.hole_swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.map_stroke));
        manager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(manager);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        recyclerView.setOnScrollListener(scrollListener);

        search_edit = (MaterialEditTextNoEmoji) findViewById(R.id.search_edit);
    }


    private void initData() {
        projectDao = new ProjectDao(this);
        holeDao = new HoleDao(this);
        recordDao = new RecordDao(this);
        gpsDao = new GpsDao(this);

        list = new ArrayList<>();
        relateList = new ArrayList<>();
        relateHoleDialog = new RelateHoleDialog();
        relateHoleDialog.OnRelateOrGet(onRelateOrGetListener);
        relateHoleDialog.OnListItemListener(relateListListener);

        relateHole = new Hole();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefreshList();
    }

    /**
     * 各种按钮的监听
     */
    HoleListAdapter.OnItemListener onItemListener = new HoleListAdapter.OnItemListener() {
        @Override
        public void infoClick(int position) {
            goNext(list.get(position));
        }

        @Override
        public void detailClick(int position) {
            detailDialog(list.get(position));
        }

        @Override
        public void holeListClick(int position) {
            goNext(list.get(position));
        }

        @Override
        public void reportClick(int position) {
            goPreview(list.get(position));
        }

        @Override
        public void editClick(int position) {
            goEdit(list.get(position));
        }

        @Override
        public void uploadClick(int position) {
            holePosition = position;
            doUpload();
        }

        @Override
        public void deleteClick(int position) {
            deleteDialog(list.get(position));
        }
    };


    /**
     * 跳转下级页面
     *
     * @param hole
     */
    private void goNext(final Hole hole) {
        if (Integer.parseInt(hole.getLocationState()) == 0) {
            Intent intent = new Intent(this, RecordListActivity.class);
            intent.putExtra(RecordListActivity.EXTRA_HOLE_ID, hole.getId());
            context.startActivity(intent);
        } else {
            new MaterialDialog.Builder(this).content("勘探点未定位,是否编辑勘察点，进行定位操作").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    goEdit(hole);
                }
            }).show();
        }
    }


    /**
     * 跳转编辑页面
     */
    private void goEdit(Hole hole) {
        Intent intent = new Intent();
        intent.setClass(context, HoleEditActivity.class);
        intent.putExtra(HoleEditActivity.EXTRA_HOLE, hole);
        activity.startActivityForResult(intent, HoleEditActivity.REQUEST_CODE);
    }

    /**
     * 删除勘察点dialog
     */
    private void deleteDialog(final Hole hole) {
        int con = hole.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete2;
        new MaterialDialog.Builder(context).content(con).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                showProgressDialog(false);
                final Message msg = new Message();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (hole.delete(context)) {
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else {
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        }).show();
    }


    /**
     * 勘察点详情dialog
     */
    private void detailDialog(Hole hole) {
        new HoleInfoDialog().show(this, hole.getId());
    }

    /**
     * 跳转预览界面
     */
    private void goPreview(Hole hole) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_HOLE, hole);
        context.startActivity(intent);
    }

    /**
     * 上传勘察点、判断序列号，是否定位，勘察点关联
     */
    private void doUpload() {
        final Hole hole = holeDao.queryForId(list.get(holePosition).getId());
        String serialNumber = project.getSerialNumber();
        if ("".equals(serialNumber) || serialNumber == null) {
            ToastUtil.showToastS(this, "该项目没有序列号");
        } else if (Integer.parseInt(hole.getLocationState()) != 0) {
            new MaterialDialog.Builder(this).content("勘探点未定位,是否编辑勘察点，进行定位操作").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    goEdit(hole);
                }
            }).show();
        } else if (hole.getRelateID().equals("")) {
            new MaterialDialog.Builder(this).content("勘探点未关联,是否获取勘探点列表，进行关联操作").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    doRelateHole = hole;
                    getHoleList(RelateHoleAdapter.HAVE_NOALL, Urls.GET_RELATE_HOLE);
                }
            }).show();
        } else {
            startUploadService((HoleListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(holePosition));
        }

    }

    /**
     * okHttp上传
     */
    private void startUploadService(final HoleListAdapter.ViewHolder holder) {
        L.e("---->>startUploadService");
        final MaterialProgressBar materialProgressBar = holder.mpr;
        final Button btnUpload = holder.btnUpload;
        IntentFilter intentFilter = new IntentFilter("upload.success");
        //上传项目 重新查询一下,列表是关联查询不是完整hole数据
        Hole hole = holeDao.queryForId(holder.vo.getId());
        Intent intent = new Intent(context, NewUploadService.class);
        intent.putExtra("hole", hole);
        intent.putExtra("serialNumber", project.getSerialNumber());
        context.startService(intent);
        context.registerReceiver(new BroadcastReceiver() {
            int uploadedCount = holder.vo.getUploadedCount();          //已上传
            int notUploadCount = holder.vo.getNotUploadCount();        //未上传
            int count = uploadedCount + notUploadCount;   // 总条数

            @Override
            public void onReceive(Context context, Intent intent) {
                int size = intent.getIntExtra("size", 0);
                btnUpload.setText("上传(" + size + "%)");
                btnUpload.setEnabled(size != 100);
                materialProgressBar.setProgress(size);
                if (size == 100) {
                    holder.tvwState.setText("已上传");
                    context.unregisterReceiver(this);
//                    onRefreshList();
                }
            }
        }, intentFilter);
    }

    /**
     * recyclerView滑动的监听
     */
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (count > Urls.PAGESIZE) {
                if (list.size() != count) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                        onLoadMore();
                    }
                } else {
                    int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                        ToastUtil.showToastS(HoleListActivity.this, "全部加载");
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (Math.abs(dy) > 4) {
                if (dy > 0) {
                    hideFloat();
                } else {
                    showFloat();
                }
            }
        }
    };

    /**
     * SwipeRefreshLayout监听
     */
    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            onRefreshList();
        }
    };


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRefresh) {
                page = 1;
                list.clear();
            } else {
                page++;
            }
            list.addAll(getList(project.getId(), page));
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isRefresh) {
                        holeListAdapter = new HoleListAdapter(HoleListActivity.this, list);
                        holeListAdapter.setOnItemListener(onItemListener);
                        recyclerView.setAdapter(holeListAdapter);
                        holeListAdapter.onCollapse();
                    } else {
                        holeListAdapter.notifyDataSetChanged();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case 2:
                    ToastUtil.showToastL(context, "删除勘探点成功");
                    dismissProgressDialog();
                    onRefreshList();
                    break;
                case 3:
                    ToastUtil.showToastL(context, "删除勘探点失败");
                    dismissProgressDialog();
                    break;

            }

        }
    };

    /**
     * 等待对话框
     */
    MaterialDialog mDialog;

    //显示检验序列号的对话框
    public void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(context).title(R.string.dictionary_wait_dialog).content(R.string.please_wait).progress(true, 0).progressIndeterminateStyle(horizontal).build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    //销毁检验序列号的对话框
    public void dismissProgressDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    boolean isRefresh = true;

    //这是刷新的方法
    public void onRefreshList() {
        L.e("HoleListActivity----onRefreshList");
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        list.clear();
        new Thread(runnable).start();
    }


    //新增加载更多方法加载更多
    public void onLoadMore() {
        L.e("HoleListActivity----onLoadMore");
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = false;
        new Thread(runnable).start();
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
                addProject.setShowAnimation(AnimationUtils.loadAnimation(context, R.anim.show_from_bottom));
                addProject.setHideAnimation(AnimationUtils.loadAnimation(context, R.anim.hide_to_bottom));
            }
        }, 300);
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
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


    /**
     * 设置悬浮添加按钮
     */
    private void setAddMenuButton() {
        hole_menu = (FloatingActionMenu) findViewById(R.id.hole_menu);
        hole_menu.setClosedOnTouchOutside(true);
        FloatingActionButton hole_menu_local = (FloatingActionButton) findViewById(R.id.hole_menu_local);
        FloatingActionButton hole_menu_relate = (FloatingActionButton) findViewById(R.id.hole_menu_relate);
        FloatingActionButton hole_menu_get = (FloatingActionButton) findViewById(R.id.hole_menu_get);
        hole_menu_local.setOnClickListener(clickListener);
        hole_menu_relate.setOnClickListener(clickListener);
        hole_menu_get.setOnClickListener(clickListener);
    }

    /**
     * 悬浮添加按钮点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            v.setEnabled(false);
            switch (v.getId()) {
                case R.id.hole_menu_local:
                    localCreate();
                    break;
                case R.id.hole_menu_relate:
                    getHoleList(RelateHoleAdapter.HAVE_SOME, Urls.GET_RELATE_HOLE);
                    break;
                case R.id.hole_menu_get:
                    getHoleList(RelateHoleAdapter.HAVE_ALL, Urls.GET_RELATE_HOLEWITHRECORD);
                    break;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.setEnabled(true);
                }
            }, 3000);
            hole_menu.close(true);
        }
    };

    private void getHoleList(int have, String url) {
        String serialNumber = project.getSerialNumber();
        if (!"".equals(serialNumber) && serialNumber != null) {
            getHoleListForIntrnet(serialNumber, have, url);
        } else {
            ToastUtil.showToastS(HoleListActivity.this, "项目未关联");
        }
    }

    /**
     * 获取勘察点列表
     */
    private void getHoleListForIntrnet(String serialNumber, final int have, String url) {
        showProgressDialog(false);
        Map<String, String> params = new HashMap<>();
        params.put("serialNumber", serialNumber);
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                relateList = null;
                L.e("getHoleListForIntrnet--onError-->>" + e.getMessage());
                ToastUtil.showToastS(context, "获取勘察点失败");
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("getHoleListForIntrnet--response-->>" + response);
                dismissProgressDialog();
                if (JsonUtils.isGoodJson(response)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        relateList = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Hole>>() {
                        }.getType());
                        if (relateList != null && relateList.size() > 0) {
                            if (!relateHoleDialog.isAdded()) {
                                relateHoleDialog.show(HoleListActivity.this, relateList, have);
                            }
                        } else {
                            ToastUtil.showToastS(context, "服务端未创建勘察点，无法关联");
                        }
                    } else {
                        ToastUtil.showToastS(context, jsonResult.getMessage());
                    }
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }


            }
        });
    }


    RelateHoleDialog.OnRelateOrGet onRelateOrGetListener = new RelateHoleDialog.OnRelateOrGet() {
        @Override
        public void onRelate(List<Hole> checkList) {
            L.e("--------onRelate-------------");
            doRelate(checkList);
        }

        @Override
        public void onGet(List<LocalUser> localUserList) {
            L.e("--------onGet-------------");
            doGet(localUserList);
        }
    };
    /**
     * 获取勘察点关联列表后的点击事件
     */
    RelateHoleDialog.OnListItemListener relateListListener = new RelateHoleDialog.OnListItemListener() {
        @Override
        public void onItemClick(int position) {
            //点击勘察点列表得到relateHole
            if (relateHoleDialog.holeList == null) {
                relateHole = relateList.get(position);
            } else {
                relateHole = relateHoleDialog.holeList.get(position);
            }
            relateHoleDialog.dismiss();
            //遍历数据库，查找是否关联
            if (holeDao.checkRelated(relateHole.getId(), project.getId())) {
                ToastUtil.showToastS(context, "该发布点本地已经存在关联");
            } else {
                doRelateForIntrnet(relateHole.getId());
            }
        }
    };

    /**
     * doRelate操作
     */
    private void doRelateForIntrnet(String relateId) {
        showProgressDialog(false);
        Map<String, String> params = new HashMap<>();
        params.put("userID", Common.getUserIDBySP(this));
        params.put("relateID", relateId);
        params.put("holeID", doRelateHole.getId());
        OkHttpUtils.post().url(Urls.DO_RELATE_HOLE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastS(context, "关联勘察点失败");
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("response----------" + response);
                Gson gson = new Gson();
                dismissProgressDialog();
                if (JsonUtils.isGoodJson(response)) {
                    Hole h = gson.fromJson(response.toString(), Hole.class);
                    doRelateHole.setRelateCode(h.getCode());
                    doRelateHole.setRelateID(h.getId());
                    doRelateHole.setUpdateTime(DateUtil.date2Str(new Date()));
                    if (h.getDepth() != null) {
                        doRelateHole.setDepth(h.getDepth());
                    }
                    if (h.getElevation() != null) {
                        doRelateHole.setElevation(h.getElevation());
                    }
                    if (h.getDescription() != null) {
                        doRelateHole.setDescription(h.getDescription());
                    }
                    holeDao.update(doRelateHole);
                    onRefreshList();
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }
            }
        });
    }

    /**
     * 多次关联创建
     */
    private void doRelate(List<Hole> checkList) {
        if (checkList != null && checkList.size() > 0) {
            for (Hole relateHole : checkList) {
                //遍历数据库，查找是否关联
                if (holeDao.checkRelated(relateHole.getId(), project.getId())) {
                    ToastUtil.showToastS(context, "该发布点本地已经存在关联");
                } else {
                    showProgressDialog(false);
                    //每次都新建一个新的勘察点
                    final Hole newHole = new Hole(context, project.getId());
                    newHole.setRelateCode(relateHole.getCode());
                    newHole.setRelateID(relateHole.getId());
                    newHole.setDepth(relateHole.getDepth());
                    newHole.setDescription(relateHole.getDescription());
                    newHole.setElevation(relateHole.getElevation());
                    newHole.setState("1");
                    holeDao.add(newHole);
                    //三个参数项
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", Common.getUserIDBySP(this));
                    params.put("relateID", relateHole.getId());
                    params.put("holeID", newHole.getId());
                    OkHttpUtils.post().url(Urls.DO_RELATE_HOLE).params(params).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtil.showToastS(context, "关联勘察点失败");
                            holeDao.delete(newHole);
                            dismissProgressDialog();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            ToastUtil.showToastS(context, "勘察点关联成功");
                            dismissProgressDialog();
                            if (JsonUtils.isGoodJson(response)) {
                                project.setHoleCount2Int(project.getHoleCount2Int() + 1);
                                projectDao.update(project);
                                onRefreshList();
                            } else {
                                ToastUtil.showToastS(context, "服务器异常，请联系客服");
                            }
                        }
                    });
                }

            }
        }
    }

    /**
     * 多次获取数据
     */

    public void doGet(List<LocalUser> localUserList) {
        showProgressDialog(false);
        if (localUserList != null && localUserList.size() > 0) {
            for (LocalUser localUser : localUserList) {
                Map<String, String> params = new HashMap<>();
                params.put("holeID", localUser.getId());
                OkHttpUtils.post().url(Urls.DOWNLOAD_RELATE_HOLE).params(params).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showToastS(context, "获取数据失败");
                        dismissProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.e("----" + response);
                        if (JsonUtils.isGoodJson(response)) {
                            Gson gson = new Gson();
                            //创建新的id,修改holeID
//                            String newHoleID = Common.getUUID();
                            Hole hole = gson.fromJson(response, Hole.class);
//                            hole.setId(newHoleID);
                            hole.setProjectID(project.getId());
                            //下载的勘探点都是未关联、已经定位的
//                            hole.setRelateID("");
//                            hole.setRelateCode("");
                            hole.setLocationState("1");
                            hole.setIsDelete("0");
                            holeDao.add(hole);
                            List<Record> recordList = hole.getRecordList();
                            if (recordList != null && recordList.size() > 0) {
                                for (Record record : recordList) {
                                    //创建新的id,修改recordID
//                                    String newRecordID = Common.getUUID();
//                                    record.setId(newRecordID);
//                                    record.setHoleID(newHoleID);
                                    record.setProjectID(project.getId());
                                    record.setState("1");
                                    record.setIsDelete("0");
                                    record.setUpdateId("");
                                    recordDao.add(record);
                                    List<Gps> gpsList = record.getGpsList();
                                    if (gpsList != null && gpsList.size() > 0) {
                                        for (Gps gps : gpsList) {
                                            //创建新的id,修改gpsID
//                                            String newGpsID = Common.getUUID();
//                                            gps.setHoleID(newHoleID);
//                                            gps.setRecordID(newRecordID);
//                                            gps.setId(newGpsID);
                                            gps.setProjectID(project.getId());
                                            gpsDao.add(gps);
                                        }
                                    }

                                }
                            } else {
                                ToastUtil.showToastS(context, "没有以上传的数据");
                            }
                            dismissProgressDialog();
                            onRefreshList();
                        } else {
                            ToastUtil.showToastS(context, "服务器异常，请联系客服");
                        }
                        if (JsonUtils.isGoodJson(response)) {

                        } else {
                            ToastUtil.showToastS(context, "服务器异常，请联系客服");
                        }
                    }
                });
            }
        }
    }

    /**
     * 新建勘察点 改變project數量
     */
    public Hole create() {
        Hole hole = new Hole(context, project.getId());
        holeDao.add(hole);
        project.setHoleCount2Int(project.getHoleCount2Int() + 1);
        project.setUpdateTime(DateUtil.date2Str(new Date()));
        projectDao.update(project);
        return hole;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_hole, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.act_search:
                search();
                return true;
            case R.id.act_add:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search() {
        if (search_edit.getVisibility() == View.VISIBLE) {
            String code = search_edit.getText().toString();
            if (TextUtils.isEmpty(code)) {
                onRefreshList();
            } else {
                list.clear();
                list.addAll(getListLike(project.getId(), code));
//                list.addAll(holeDao.getHoleListByCode(project.getId(), code));
                holeListAdapter.notifyDataSetChanged();
            }
            search_edit.setVisibility(View.GONE);
        } else if (search_edit.getVisibility() == View.GONE) {
            search_edit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 本地创建
     */
    private void localCreate() {
        Intent intent = new Intent();
        intent.setClass(context, HoleEditActivity.class);
        intent.putExtra(HoleEditActivity.EXTRA_PROJECT, project);
        startActivityForResult(intent, HoleEditActivity.REQUEST_CODE);
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == HoleEditActivity.REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                onRefreshList();
//            }
//        }
//    }

    private List<Hole> getList(String projectID, int page) {
        List<Hole> list = new ArrayList<Hole>();
        dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            //钻孔的记录数量要 排除机长等基本信息和原始记录信息、机长等信息type不同，原始记录update不为空
            String recordCount = "select count(id) from record r where state <> '0' and r.holeID=h.id and r.type<>'机长' and r.type<>'钻机'and r.type<>'描述员'and r.type<>'场景'and r.type<>'负责人'and r.type<>'工程师'and r.type<>'提钻录像' and r.updateID=''";

            String currentDepth = "select max(r1.endDepth) from record r1 where state <> '0' and updateID='' and r1.holeID=h.id";

            String holeUploadedCount = "select count(id) as uploadedCount from hole h2 where state='2' and h2.id=h.id";
            String recordUploadedCount = "select count(id) as uploadedCount from record r2 where state='2'and r2.holeID=h.id";
            String mediaUploadedCount = "select count(id) as uploadedCount from media m where state='2'and m.holeID=h.id";

            String uploadedCount = "select sum(uploadedCount) from (" + holeUploadedCount + " union all " + recordUploadedCount + " union all " + mediaUploadedCount + ")";

            String holeNotUploadCount = "select count(id) as notUploadCount from hole h2 where state='1' and h2.id=h.id ";
            String recordNotUploadCount = "select count(id) as notUploadCount from record r2 where state='1'and r2.holeID=h.id ";
            String mediaNotUploadCount = "select count(id) as notUploadCount from media m where state='1'and m.holeID=h.id";

            String notUploadCount = "select sum(notUploadCount) from (" + holeNotUploadCount + " union all " + recordNotUploadCount + " union all " + mediaNotUploadCount + ")";
            //用于分页加载的sql 这里的index-1 第二页的时候是跳过一个size  --
            String pageSql = String.format("limit (" + Urls.PAGESIZE + ") offset " + Urls.PAGESIZE * (page - 1));//size:每页显示条数，index页码
            String sql = "select h.id,h.code,h.type,h.state,(" + recordCount + ")as recordsCount,h.updateTime,h.mapLatitude,h.mapLongitude,h.mapPic,(" + uploadedCount + ") as uploadedCount,(" + notUploadCount + ") as notUploadCount,h.projectID ,(" + currentDepth + ")  as currentDepth,h.locationState,h.relateID,h.userID,h.relateCode from hole h where h.projectID='" + projectID + "' order by h.state asc,h.updateTime desc,h.relateID desc " + pageSql;
            L.e("TAG", "sql---->>>" + sql);
            count = holeDao.getHoleListByProjectIDUserDelete(projectID).size();
            GenericRawResults<Hole> results = dao.queryRaw(sql, new RawRowMapper<Hole>() {
                @Override
                public Hole mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Hole hole = new Hole();
                    hole.setId(resultColumns[0]);
                    hole.setCode(resultColumns[1]);
                    hole.setType(resultColumns[2]);
                    hole.setState(resultColumns[3]);
                    hole.setRecordsCount(resultColumns[4]);
                    hole.setUpdateTime(resultColumns[5]);
                    hole.setMapLatitude(resultColumns[6]);
                    hole.setMapLongitude(resultColumns[7]);
                    hole.setMapPic(resultColumns[8]);
                    hole.setUploadedCount(Integer.valueOf(resultColumns[9]));
                    hole.setNotUploadCount(Integer.valueOf(resultColumns[10]));
                    hole.setProjectID(resultColumns[11]);
                    hole.setCurrentDepth(resultColumns[12]);
                    hole.setLocationState(resultColumns[13]);
                    hole.setRelateID(resultColumns[14]);
                    hole.setUserID(resultColumns[15]);
                    hole.setRelateCode(resultColumns[16]);
                    hole.jieMi();
                    return hole;
                }
            });

            Iterator<Hole> iterator = results.iterator();
            while (iterator.hasNext()) {
                Hole hole = iterator.next();
                list.add(hole);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        L.e("TAG", "count:" + count + "---page:" + page + "---size:" + list.size());
        return list;
    }

    private List<Hole> getListLike(String projectID, String code) {
        List<Hole> list = new ArrayList<Hole>();
        dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            //钻孔的记录数量要 排除机长等基本信息和原始记录信息、机长等信息type不同，原始记录update不为空
            String recordCount = "select count(id) from record r where state <> '0' and r.holeID=h.id and r.type<>'机长' and r.type<>'钻机'and r.type<>'描述员'and r.type<>'场景'and r.type<>'负责人'and r.type<>'工程师'and r.type<>'提钻录像' and r.updateID=''";

            String currentDepth = "select max(r1.endDepth) from record r1 where state <> '0' and r1.holeID=h.id";

            String holeUploadedCount = "select count(id) as uploadedCount from hole h2 where state='2' and h2.id=h.id";
            String recordUploadedCount = "select count(id) as uploadedCount from record r2 where state='2'and r2.holeID=h.id";
            String mediaUploadedCount = "select count(id) as uploadedCount from media m where state='2'and m.holeID=h.id";

            String uploadedCount = "select sum(uploadedCount) from (" + holeUploadedCount + " union all " + recordUploadedCount + " union all " + mediaUploadedCount + ")";

            String holeNotUploadCount = "select count(id) as notUploadCount from hole h2 where state='1' and h2.id=h.id ";
            String recordNotUploadCount = "select count(id) as notUploadCount from record r2 where state='1'and r2.holeID=h.id ";
            String mediaNotUploadCount = "select count(id) as notUploadCount from media m where state='1'and m.holeID=h.id";

            String notUploadCount = "select sum(notUploadCount) from (" + holeNotUploadCount + " union all " + recordNotUploadCount + " union all " + mediaNotUploadCount + ")";
            //用于分页加载的sql 这里的index-1 第二页的时候是跳过一个size  --
//            String pageSql = String.format("limit (" + Urls.PAGESIZE + ") offset " + Urls.PAGESIZE * (page - 1));//size:每页显示条数，index页码
            String sql = "select h.id,h.code,h.type,h.state,(" + recordCount + ")as recordsCount,h.updateTime,h.mapLatitude,h.mapLongitude,h.mapPic,(" + uploadedCount + ") as uploadedCount,(" + notUploadCount + ") as notUploadCount,h.projectID ,(" + currentDepth + ")  as currentDepth,h.locationState,h.relateID,h.userID from hole h where" + " code LIKE'%" + code + "%' and h.projectID='" + projectID + "' order by h.updateTime desc ";
            L.e("TAG", "sql---->>>" + sql);
            count = holeDao.getHoleListByProjectIDUserDelete(projectID).size();
            GenericRawResults<Hole> results = dao.queryRaw(sql, new RawRowMapper<Hole>() {
                @Override
                public Hole mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Hole hole = new Hole();
                    hole.setId(resultColumns[0]);
                    hole.setCode(resultColumns[1]);
                    hole.setType(resultColumns[2]);
                    hole.setState(resultColumns[3]);
                    hole.setRecordsCount(resultColumns[4]);
                    hole.setUpdateTime(resultColumns[5]);
                    hole.setMapLatitude(resultColumns[6]);
                    hole.setMapLongitude(resultColumns[7]);
                    hole.setMapPic(resultColumns[8]);
                    hole.setUploadedCount(Integer.valueOf(resultColumns[9]));
                    hole.setNotUploadCount(Integer.valueOf(resultColumns[10]));
                    hole.setProjectID(resultColumns[11]);
                    hole.setCurrentDepth(resultColumns[12]);
                    hole.setLocationState(resultColumns[13]);
                    hole.setRelateID(resultColumns[14]);
                    hole.setUserID(resultColumns[15]);
                    hole.jieMi();
                    return hole;
                }
            });

            Iterator<Hole> iterator = results.iterator();
            while (iterator.hasNext()) {
                Hole hole = iterator.next();
                list.add(hole);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        L.e("TAG", "count:" + count + "---page:" + page + "---size:" + list.size());
        return list;
    }


}
