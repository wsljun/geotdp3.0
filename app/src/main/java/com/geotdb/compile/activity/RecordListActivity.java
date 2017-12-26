/*
 * Copyright (C) 2015 The Android Open Source Hole
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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.fragment.RecordListFragment;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Record;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO implements PopupMenu.OnMenuItemClickListener
 */
public class RecordListActivity extends BaseAppCompatActivity {
    public static final String EXTRA_HOLE_ID = "holeID";
    private Hole hole;
    private HoleDao holeDao;
    private RecordListFragment recordListFragment;
    private FloatingActionMenu menu1;
    private FloatingActionButton addProject;
    private MaterialBetterSpinner sprSort;
    private MaterialBetterSpinner sprSequence;
    private int intSort;
    private List<DropItemVo> listSort;
    private List<DropItemVo> listSequence;
    private String holeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_hole_main);
        initView();
        setRecordListFragment();
        setFloatingActionButton();
        setFloatingAddButton();
    }

    private void initView() {
        //先获取hole数据，给title赋值
        Bundle bundle = this.getIntent().getExtras();
        holeID = bundle.getString(EXTRA_HOLE_ID);
        holeDao = new HoleDao(this);
        hole = holeDao.queryForId(holeID);
        hole.jieMi();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(hole.getCode());
        toolbar.setSubtitle("记录列表");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        sprSort = (MaterialBetterSpinner) findViewById(R.id.sprSort);
        sprSort.setAdapter(context, getLayerTypeList());
        sprSort.setText(listSort.get(0).getValue());
        sprSort.setTag(listSort.get(0).getId());

        sprSequence = (MaterialBetterSpinner) findViewById(R.id.sprSequence);
        sprSequence.setAdapter(context, getModeList());
        sprSequence.setText(listSequence.get(0).getValue());
        sprSequence.setTag(listSequence.get(0).getId());
        sprSort.setOnItemClickListener(sortListener);
        sprSequence.setOnItemClickListener(sequenceListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefreshList();
//        sprSort.setAdapter(context, listSort);
//        sprSort.setText(listSort.get(intSort).getValue());
//        sprSort.setTag(listSort.get(intSort).getId());

    }


    /**
     * sort点击事件
     */
    MaterialBetterSpinner.OnItemClickListener sortListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            DropItemVo d = listSort.get(i);
            intSort = i;
            sprSort.setTag(d.getId());
            sprSort.setText(d.getValue());
            onRefreshList();
        }
    };
    /**
     * sequence点击事件
     */
    MaterialBetterSpinner.OnItemClickListener sequenceListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            DropItemVo d = listSequence.get(i);
            sprSequence.setTag(d.getId());
            sprSequence.setText(d.getValue());
            onRefreshList();
        }
    };

    public String getSort() {
        return sprSort.getTag().toString();
    }

    public String getSequence() {
        return sprSequence.getTag().toString();
    }


    private List<DropItemVo> getLayerTypeList() {
        Map<Integer, Integer> countMap = new RecordDao(context).getSortCountMap(hole.getId());
        listSort = new ArrayList<DropItemVo>();
        listSort.add(new DropItemVo("", "全部记录(" + countMap.get(1) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_FREQUENCY, "回次记录(" + countMap.get(2) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_LAYER, "岩土记录(" + countMap.get(3) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_WATER, "水位记录(" + countMap.get(4) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_DPT, "动探记录(" + countMap.get(5) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_SPT, "标贯记录(" + countMap.get(6) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_GET_EARTH, "取土记录(" + countMap.get(7) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_GET_WATER, "取水记录(" + countMap.get(8) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_SCENE, "备注记录(" + countMap.get(9) + ")"));
        return listSort;
    }

    private List<DropItemVo> getModeList() {
        listSequence = new ArrayList<DropItemVo>();
        listSequence.add(new DropItemVo("1", "最新修改"));
        listSequence.add(new DropItemVo("2", "最早修改"));
        listSequence.add(new DropItemVo("3", "由浅到深"));
        listSequence.add(new DropItemVo("4", "由深到浅"));
        return listSequence;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);

        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
//        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_record_add);
        popupMenu.show();
    }

//    @Override
//    public boolean onMenuItemClick(final MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addFrequency:
//                goRecordEditActivity(Record.TYPE_FREQUENCY);
//                return true;
//            case R.id.addLayer:
//                goRecordEditActivity(Record.TYPE_LAYER);
//                return true;
//            case R.id.addGetEarth:
//                goRecordEditActivity(Record.TYPE_GET_EARTH);
//                return true;
//            case R.id.addGetWater:
//                goRecordEditActivity(Record.TYPE_GET_WATER);
//                return true;
//            case R.id.addDPT:
//                goRecordEditActivity(Record.TYPE_DPT);
//                return true;
//            case R.id.addSPT:
//                goRecordEditActivity(Record.TYPE_SPT);
//                return true;
//            case R.id.addWater:
//                goRecordEditActivity(Record.TYPE_WATER);
//                return true;
//            case R.id.addScene:
//                goRecordEditActivity(Record.TYPE_SCENE);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 设置悬浮添加按钮
     */
    private void setFloatingActionButton() {
        menu1 = (FloatingActionMenu) findViewById(R.id.menu1);
        menu1.setClosedOnTouchOutside(true);

        FloatingActionButton fabFrequency = (FloatingActionButton) findViewById(R.id.fabFrequency);
        FloatingActionButton fabGetEarth = (FloatingActionButton) findViewById(R.id.fabGetEarth);
        FloatingActionButton fabGetWater = (FloatingActionButton) findViewById(R.id.fabGetWater);
        FloatingActionButton fabWater = (FloatingActionButton) findViewById(R.id.fabWater);
        FloatingActionButton fabDPT = (FloatingActionButton) findViewById(R.id.fabDPT);
        FloatingActionButton fabSPT = (FloatingActionButton) findViewById(R.id.fabSPT);
        FloatingActionButton fabScene = (FloatingActionButton) findViewById(R.id.fabScene);
        FloatingActionButton fabLayer = (FloatingActionButton) findViewById(R.id.fabLayer);

        fabFrequency.setOnClickListener(clickListener);
        fabGetEarth.setOnClickListener(clickListener);
        fabGetWater.setOnClickListener(clickListener);
        fabDPT.setOnClickListener(clickListener);
        fabSPT.setOnClickListener(clickListener);
        fabWater.setOnClickListener(clickListener);
        fabScene.setOnClickListener(clickListener);
        fabLayer.setOnClickListener(clickListener);
        //探井不需要回次
        if ("探井".equals(hole.getType())) {
            fabFrequency.setEnabled(false);
        }
    }

    /**
     * 设置悬浮添加按钮 点击事件
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            L.e("TAG", "onClick");
            v.setEnabled(false);
            switch (v.getId()) {
                case R.id.fabFrequency:
                    goRecordEditActivity(Record.TYPE_FREQUENCY);
                    break;
                case R.id.fabLayer:
                    goRecordEditActivity(Record.TYPE_LAYER);
                    break;
                case R.id.fabGetEarth:
                    goRecordEditActivity(Record.TYPE_GET_EARTH);
                    break;
                case R.id.fabGetWater:
                    goRecordEditActivity(Record.TYPE_GET_WATER);
                    break;
                case R.id.fabDPT:
                    goRecordEditActivity(Record.TYPE_DPT);
                    break;
                case R.id.fabSPT:
                    goRecordEditActivity(Record.TYPE_SPT);
                    break;
                case R.id.fabWater:
                    goRecordEditActivity(Record.TYPE_WATER);
                    break;
                case R.id.fabScene:
                    goRecordEditActivity(Record.TYPE_SCENE);
                    break;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.setEnabled(true);
                }
            }, 3000);
            menu1.close(true);
        }
    };


    //设置悬浮添加按钮
    private void setFloatingAddButton() {
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
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra(PreviewActivity.EXTRA_HOLE, hole);
                context.startActivity(intent);
//                Snackbar.make(view, "这里打算实现切换编辑/预览模式(将记录以类似传统报告的形式预览)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    public void showFloat() {
        addProject.show(true);
    }

    public void hideFloat() {
        addProject.hide(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.act_search:
                ToastUtil.showToastL(RecordListActivity.this, "这里是搜索功能,还未开发");
                return true;
            case R.id.act_help:
                Intent intent = new Intent(this, HelpActivtiy.class);
                intent.setAction(TAG);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRecordListFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(RecordListFragment.KEY_HOLE_ID, hole.getId());
            recordListFragment = new RecordListFragment();
            recordListFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.recordListFrameLayout, recordListFragment, "type");
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void goRecordEditActivity(String type) {
        Intent intent = new Intent();
        intent.setClass(RecordListActivity.this, RecordEditActivity.class);
        intent.putExtra(RecordEditActivity.EXTRA_HOLE, hole);
        intent.putExtra(RecordEditActivity.EXTRA_RECORD_TYPE, type);
        startActivityForResult(intent, RecordEditActivity.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            onRefreshList();
        }
    }

    public void onRefreshList() {
        if (recordListFragment == null) {
            setRecordListFragment();
        }
        recordListFragment.onRefreshList();
    }

}
