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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.fragment.RecordEditDPTFragment;
import com.geotdb.compile.fragment.RecordEditBaseFragment;
import com.geotdb.compile.fragment.RecordEditFrequencyFragment;
import com.geotdb.compile.fragment.RecordEditGetEarthFragment;
import com.geotdb.compile.fragment.RecordEditGetWaterFragment;
import com.geotdb.compile.fragment.RecordEditLayerFragment;
import com.geotdb.compile.fragment.RecordEditSPTFragment;
import com.geotdb.compile.fragment.RecordEditSceneFragment;
import com.geotdb.compile.fragment.RecordEditWaterFragment;
import com.geotdb.compile.fragment.RecordOperateCodeFragment;
import com.geotdb.compile.fragment.RecordOperatePersionFragment;
import com.geotdb.compile.fragment.RecordPersonFragment;
import com.geotdb.compile.fragment.RecordPrincipalFragment;
import com.geotdb.compile.fragment.RecordSceneFragment;
import com.geotdb.compile.fragment.RecordTechnicianFragment;
import com.geotdb.compile.fragment.RecordVideoFragment;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.view.MaterialEditTextElevation;
import com.geotdb.compile.vo.Gps;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Record;
import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.qiujuer.genius.ui.widget.Button;

public class RecordEditActivity extends RecordEditBaseActivity {

    public static final int REQUEST_CODE = 3000;

    private MaterialEditTextElevation edtBeginDepth;//输入起始深度
    private MaterialEditTextElevation edtEndDepth;//输入终止深度

    private MaterialEditText edtCode; //编号
    private MaterialEditText edtDescription; //描述
    RecordEditBaseFragment recordEditBaseFragment;
    Toolbar toolbar;
    private android.widget.Button record_edit_dptup;
    private TextView record_edit_note_tv;

    private GpsDao gpsDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setTimeZone(this);//默认时区为北京时区
        setContentView(R.layout.act_record_edit);
        context = this;
        setWindowAnimationStyle();

        Bundle bundle = getIntent().getExtras();
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Record, String> recordDao = dbHelper.getDao(Record.class);
            if (bundle.containsKey(EXTRA_RECORD)) {
                record = (Record) bundle.getSerializable(EXTRA_RECORD);
                record = recordDao.queryForId(record.getId());
                recordType = record.getType();
            } else {
                editMode = false;
                record = new Record(context, hole, recordType);
                record.create(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        initValue();
        // 记录原记录
        if (editMode) {
            recordOld = (Record) record.clone();
            //修改gps信息，与老记录对应
            gpsDao = new GpsDao(context);
            gpsOld = gpsDao.getGpsByRecord(record.getId());
            if(gpsOld!=null){
                gpsOld.setRecordID(recordOld.getId());
            }
        }
    }

    private Record recordOld;
    private Gps gpsOld;

    @Override
    public void initView() {
        L.e("-->>RecordEditActivity--initView");
        super.initView();
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.title_new_record_frequency);
            setSupportActionBar(toolbar);
            final ActionBar ab = getSupportActionBar();
            ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);

            edtCode = (MaterialEditText) this.findViewById(R.id.edtCode);
            edtBeginDepth = (MaterialEditTextElevation) this.findViewById(R.id.edtBeginDepth);
            edtEndDepth = (MaterialEditTextElevation) this.findViewById(R.id.edtEndDepth);
            edtDescription = (MaterialEditText) this.findViewById(R.id.edtDescription);
            record_edit_note_tv = (TextView) this.findViewById(R.id.record_edit_note_tv);
            record_edit_dptup = (Button) findViewById(R.id.record_edit_dptup);
            record_edit_dptup.setOnClickListener(dptUpListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener dptUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validator()) {
                if (recordEditBaseFragment.validator()) {
                    save();
                    editMode = false;
                    record = new Record(context, hole, Record.TYPE_DPT);
                    record.create(context);
                    initView();
                    initValue();
                }
            }
        }
    };

    public boolean validator() {
        boolean validator = true;
        if (null == getAMapLocation()) {
            validator = false;
            ToastUtil.showToastS(context, "无法获取定位信息");
        }
        if (!Common.gPSIsOPen(RecordEditActivity.this)) {
            validator = false;
            ToastUtil.showToastS(context, "GPS未开启，请开启以提高精度");
        }
        if ("".equals(edtBeginDepth.getText().toString())) {
            edtBeginDepth.setText(edtBeginDepth.getHint());
        }
        if ("".equals(edtEndDepth.getText().toString())) {
            edtEndDepth.setText(edtEndDepth.getHint());
        }
        try {
            //取消必选项，只有取土不需要终止深度
            if (recordType.equals(Record.TYPE_FREQUENCY) || recordType.equals(Record.TYPE_LAYER) || recordType.equals(Record.TYPE_GET_EARTH)) {
                if (Double.valueOf(edtBeginDepth.getText().toString()) >= Double.valueOf(edtEndDepth.getText().toString())) {
                    edtEndDepth.setError("终止深度必须大于起始深度");
                    validator = false;
                }
            }
            //起始值和终止值都不能重叠
            if (recordType.equals(Record.TYPE_FREQUENCY) || recordType.equals(Record.TYPE_LAYER)) {
                RecordDao recordDao = new RecordDao(context);
                if (recordDao.validatorBeginDepth(record, recordType, edtBeginDepth.getText().toString())) {
                    edtBeginDepth.setError("与其他记录重叠");
                    validator = false;
                }

                if (recordDao.validatorEndDepth(record, recordType, edtEndDepth.getText().toString())) {
                    edtEndDepth.setError("与其他记录重叠");
                    validator = false;
                }
            }

            if ("".equals(edtCode.getText().toString())) {
                edtCode.setError("记录编号不能为空");
                validator = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            validator = false;
        }
        return validator;
    }

    private void initValue() {
        try {
            if (null != record.getCode()) {
                edtCode.setText(record.getCode());
            }
            edtBeginDepth.setText(record.getBeginDepth());
            edtEndDepth.setText(record.getEndDepth());
            edtDescription.setText(record.getDescription());

            if (recordType.equals(Record.TYPE_FREQUENCY)) {
                setRecordEditBaseFragment(new RecordEditFrequencyFragment());
            } else if (recordType.equals(Record.TYPE_LAYER)) {
                setRecordEditBaseFragment(new RecordEditLayerFragment());
            } else if (recordType.equals(Record.TYPE_GET_EARTH)) {
                setRecordEditBaseFragment(new RecordEditGetEarthFragment());
            } else if (recordType.equals(Record.TYPE_GET_WATER)) {
                setRecordEditBaseFragment(new RecordEditGetWaterFragment());
            } else if (recordType.equals(Record.TYPE_DPT)) {
                record_edit_dptup.setVisibility(View.VISIBLE);
                setRecordEditBaseFragment(new RecordEditDPTFragment());
            } else if (recordType.equals(Record.TYPE_SPT)) {
                setRecordEditBaseFragment(new RecordEditSPTFragment());
            } else if (recordType.equals(Record.TYPE_WATER)) {
                setRecordEditBaseFragment(new RecordEditWaterFragment());
            } else if (recordType.equals(Record.TYPE_SCENE)) {
                setRecordEditBaseFragment(new RecordEditSceneFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_OPERATEPERSON)) {//机长
                setRecordEditBaseFragment(new RecordOperatePersionFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_OPERATECODE)) {//编号
                setRecordEditBaseFragment(new RecordOperateCodeFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_RECORDPERSON)) {//描述员
                setRecordEditBaseFragment(new RecordPersonFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_SCENE)) {//场景
                setRecordEditBaseFragment(new RecordSceneFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_PRINCIPAL)) {//负责人
                setRecordEditBaseFragment(new RecordPrincipalFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_TECHNICIAN)) {//工程师
                setRecordEditBaseFragment(new RecordTechnicianFragment());
            } else if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {//短视频
                setRecordEditBaseFragment(new RecordVideoFragment());
            }

            //短视频 负责人 工程师 机长、钻机、场景、描述
            if (recordType.equals(Record.TYPE_SCENE_VIDEO) || recordType.equals(Record.TYPE_SCENE_PRINCIPAL) || recordType.equals(Record.TYPE_SCENE_TECHNICIAN) || recordType.equals(Record.TYPE_SCENE_OPERATEPERSON) || recordType.equals(Record.TYPE_SCENE_OPERATECODE) || recordType.equals(Record.TYPE_SCENE_RECORDPERSON) || recordType.equals(Record.TYPE_SCENE_SCENE)) {
                edtBeginDepth.setVisibility(View.GONE);
                edtEndDepth.setVisibility(View.GONE);
                edtCode.setVisibility(View.GONE);
                //修改注释内容
                if (recordType.equals(Record.TYPE_SCENE_OPERATEPERSON)) {
                    record_edit_note_tv.setText(R.string.record_operatepersion);
                } else if (recordType.equals(Record.TYPE_SCENE_OPERATECODE)) {
                    record_edit_note_tv.setText(R.string.record_operatecode);
                } else if (recordType.equals(Record.TYPE_SCENE_RECORDPERSON)) {
                    record_edit_note_tv.setText(R.string.record_recordpersion);
                } else if (recordType.equals(Record.TYPE_SCENE_SCENE)) {
                    record_edit_note_tv.setText(R.string.record_scenescene);
                } else if (recordType.equals(Record.TYPE_SCENE_TECHNICIAN)) {
                    record_edit_note_tv.setText(R.string.record_technician);
                } else if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {
                    record_edit_note_tv.setText(R.string.record_video);
                } else {
                    record_edit_note_tv.setText(R.string.record_principal);
                }

            }

            // 取水、动探、标灌、水位
            if (recordType.equals(Record.TYPE_GET_WATER) || recordType.equals(Record.TYPE_DPT) || recordType.equals(Record.TYPE_SPT) || recordType.equals(Record.TYPE_WATER)) {
                edtBeginDepth.setVisibility(View.GONE);
                edtEndDepth.setVisibility(View.GONE);
            }
            //备注
            if (recordType.equals(Record.TYPE_SCENE)) {
                edtBeginDepth.setVisibility(View.GONE);
                edtEndDepth.setVisibility(View.GONE);
                record_edit_note_tv.setText(R.string.record_scene);
            }
            //非回次
            if (!recordType.equals(Record.TYPE_FREQUENCY)) {
                edtDescription.setHint(R.string.hint_record_layer_description);
                edtDescription.setFloatingLabelText("其他描述");
            }
            //岩土
            if (recordType.equals(Record.TYPE_LAYER)) {
                record_edit_note_tv.setText(R.string.record_layer);
            }


            //接收描述的信息
            IntentFilter filter = new IntentFilter("receiver.clear.edtDescription");
            registerReceiver(clearReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用广播接收来清楚描述信息
    BroadcastReceiver clearReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            edtDescription.setText("");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(clearReceiver);
    }

    private void setRecordEditBaseFragment(RecordEditBaseFragment recordEditBaseFragment) {
        try {
            this.recordEditBaseFragment = recordEditBaseFragment;
            //岩土是描述，其他都是编辑，title不一樣
            String t = "";
            if (this.recordEditBaseFragment.getTypeName().equals(record.TYPE_LAYER)) {
                t = "描述";
            } else {
                t = "记录";
            }
            toolbar.setTitle(this.recordEditBaseFragment.getTypeName() + t + "编辑");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putSerializable(RecordEditBaseFragment.EXTRA_RECORD, record);
            bundle.putBoolean(RecordEditBaseFragment.EXTRA_EDIT_MODE, editMode);
            //向detailFragment传入参数
            recordEditBaseFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fltDetail, recordEditBaseFragment, "type" + recordType);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.act_save:
                holeDao = new HoleDao(this);
                String holeID = record.getHoleID();
                Hole hole = holeDao.queryForId(holeID);
                String userID = hole.getUserID();
                if (userID == null || userID.equals("") || userID.equals(Common.getUserIDBySP(this))) {
                    if (validator()) {
                        if (recordEditBaseFragment.validator()) {
                            save();
                            finish();
                        }
                    }
                } else {
                    ToastUtil.showToastS(this, "不能编辑其他人项目");
                }
                return true;
            case android.R.id.home:
                clearRecord();
                finish();
                return true;
            case R.id.act_help:
                Intent intent = new Intent(this, HelpActivtiy.class);
                intent.setAction(record.getType());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {
        try {
            record = recordEditBaseFragment.getRecord();

            if (recordType.equals(Record.TYPE_GET_WATER) || recordType.equals(Record.TYPE_DPT) || recordType.equals(Record.TYPE_SPT) || recordType.equals(Record.TYPE_WATER)) {
                record.setBeginDepth(recordEditBaseFragment.getBegin());
                record.setEndDepth(recordEditBaseFragment.getEnd());
            } else {
                record.setBeginDepth(edtBeginDepth.getText().toString());
                record.setEndDepth(edtEndDepth.getText().toString());
            }

            record.setCode(edtCode.getText().toString());
            record.setDescription(edtDescription.getText().toString());

            record.setType(recordEditBaseFragment.getTypeName());
            record.setTitle(recordEditBaseFragment.getTitle());

            record.setIsDelete("0");
            record.setState("1");
            //添加描述员
            record.setRecordPerson(Common.getLocalUser(this).getRealName());
            record.update(context, getAMapLocation());

        } catch (Exception e) {
            e.printStackTrace();
        }
        saveMediaList();
        updateHoleState();
        Intent intent = new Intent();
        //短视频 负责人 工程师 机长、钻机、场景、描述 返回id-->>  holeedit 获取他的媒体
        if (record.getType().equals(Record.TYPE_SCENE_VIDEO) || record.getType().equals(Record.TYPE_SCENE_PRINCIPAL) || record.getType().equals(Record.TYPE_SCENE_TECHNICIAN) || record.getType().equals(Record.TYPE_SCENE_OPERATEPERSON) || record.getType().equals(Record.TYPE_SCENE_OPERATECODE) || record.getType().equals(Record.TYPE_SCENE_RECORDPERSON) || record.getType().equals(Record.TYPE_SCENE_SCENE)) {
            intent.putExtra("record", record);
        }
        if (editMode) {
            //备份老的记录
            new RecordDao(this).add(recordOld);
            //老的gps信息重新关联
            gpsDao.update(gpsOld);
        }
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回键添加dialog提示
            finishDialog();
        }
        return true;
    }


    /**
     * 新建点，未保存，删除其子记录
     */
    public void clearRecord() {
        if ("0".equals(record.getState())) {
            record.delete(context);
        }
    }

    //退出dialog
    private void finishDialog() {
        new MaterialDialog.Builder(this).content("是否退出应用").positiveText(R.string.record_finish_save).negativeText(R.string.record_finish_giveup).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                holeDao = new HoleDao(RecordEditActivity.this);
                String holeID = record.getHoleID();
                Hole hole = holeDao.queryForId(holeID);
                String userID = hole.getUserID();
                if (userID == null || userID.equals("") || userID.equals(Common.getUserIDBySP(RecordEditActivity.this))) {
                    if (validator()) {
                        if (recordEditBaseFragment.validator()) {
                            save();
                            finish();
                        }
                    }
                } else {
                    ToastUtil.showToastS(RecordEditActivity.this, "不能编辑其他人项目");
                }
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                clearRecord();
                finish();
            }
        }).show();
    }
}
