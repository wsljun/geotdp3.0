/*
 * Copyright (C) 2015 The Android Open Source PowerLog
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

package com.geotdb.compile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.view.MaterialEditTextInt;
import com.geotdb.compile.view.MaterialEditTextMeter;
import com.geotdb.compile.vo.RecordPowerLog;
import com.geotdb.compile.vo.RecordPower;
import com.j256.ormlite.dao.Dao;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class RecordPowerLogDPTAddFragment extends RecordPowerLogBaseFragment {
    public MaterialEditTextMeter edtBegin;
    public MaterialEditTextMeter edtEnd;
    public MaterialEditTextInt edtBlow;

//    Button btnAddPowerLog;

    private int type;
    private RecordPower recordPower;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        nextLogListener = (NextLogListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(KEY_TYPE)) {
            type = getArguments().getInt(KEY_TYPE);
        }
        if (getArguments().containsKey(EXTRA_RECORD_POWER)) {
            recordPower = (RecordPower) getArguments().getSerializable(EXTRA_RECORD_POWER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_dpt_edit, null);
        edtBegin = (MaterialEditTextMeter) convertView.findViewById(R.id.edtBegin);
        edtEnd = (MaterialEditTextMeter) convertView.findViewById(R.id.edtEnd);
        edtBlow = (MaterialEditTextInt) convertView.findViewById(R.id.edtBlow);

        edtBegin.addTextChangedListener(beginTextWatcher);
        edtBegin.setText(recordPower.getBegin1());

//        btnAddPowerLog = (Button) convertView.findViewById(R.id.btnAddPowerLog);
//        btnAddPowerLog.setVisibility(View.VISIBLE);
//        btnAddPowerLog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nextLogListener.showNextLog();
//
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                RecordPowerLogDPTListFragment recordPowerLogDPTListFragment = (RecordPowerLogDPTListFragment) fragmentManager.findFragmentById(R.id.recordPowerLogDPTListFragment);
//                recordPowerLogDPTListFragment.onRefreshList();
//            }
//        });

        return convertView;
    }

    DBHelper dbHelper;

    private List<RecordPowerLog> getPowerLogList(int c) {
        List<RecordPowerLog> list = new ArrayList<RecordPowerLog>();
        for (int i = 0; i < c; i++) {
            list.add(new RecordPowerLog());
        }
//        dbHelper = DBHelper.getInstance(getActivity());
//        try {
//            Dao<PowerLog, String> dao = dbHelper.getDao(PowerLog.class);
//            GenericRawResults<PowerLog> results = dao.queryRaw("select id,code,type,state,recordsCount,updateTime,mapLatitude,mapLongitude,mapPic from powerLog order by updateTime desc", new RawRowMapper<PowerLog>() {
//                @Override
//                public PowerLog mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
//                    PowerLog powerLog = new PowerLog();
//                    powerLog.setId(resultColumns[0]);
//                    powerLog.setBegin(resultColumns[1]);
//                    powerLog.setEnd(resultColumns[2]);
//                    powerLog.setBlow(resultColumns[3]);
//                    //powerLog.jieMi();
//                    return powerLog;
//                }
//            });
//
//            Iterator<PowerLog> iterator = results.iterator();
//            while (iterator.hasNext()) {
//                PowerLog powerLog = iterator.next();
//                list.add(powerLog);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return list;
    }

    TextWatcher beginTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                double i = Double.valueOf(s.toString());
                updatePowerLog(i);
            }
        }
    };

    public void updatePowerLog(double begin) {
        try {
            double nn = 0.1;
            switch (type) {
                case 0:     //轻型
                    nn = 0.3;
                    break;
                case 1:     //重型
                    nn = 0.1;
                    break;
                case 2:     //超重型
                    nn = 0.1;
                    break;
            }
            edtEnd.setText(begin + nn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecordPower getValue() {
        RecordPowerLog recordPowerLog = new RecordPowerLog();
        recordPowerLog.init(recordPower);
        recordPowerLog.setBegin(edtBegin.getText().toString());
        recordPowerLog.setEnd(edtEnd.getText().toString());
        recordPowerLog.setBlow(edtBlow.getText().toString());

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        try {
            Dao<RecordPowerLog, String> recordPowerLogDao = dbHelper.getDao(RecordPowerLog.class);
            recordPowerLogDao.create(recordPowerLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recordPower.setBegin1(recordPowerLog.getBegin());
        recordPower.setEnd1(recordPowerLog.getEnd());
        recordPower.setBlow1(recordPowerLog.getBlow());
        return recordPower;
    }


    public interface NextLogListener {
        public void showNextLog();
    }

    public NextLogListener nextLogListener;

}
