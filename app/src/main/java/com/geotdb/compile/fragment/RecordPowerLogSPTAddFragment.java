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

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.geotdb.compile.vo.RecordPower;
import com.geotdb.compile.vo.RecordPowerLog;

import java.util.ArrayList;
import java.util.List;

public class RecordPowerLogSPTAddFragment extends RecordPowerLogBaseFragment {
    public static final String KEY_TYPE = "RecordPowerLogListFragment:Type";

    private MaterialEditTextMeter edtBegin1;
    private MaterialEditTextMeter edtEnd1;
    private MaterialEditTextInt edtBlow1;
    private MaterialEditTextMeter edtBegin2;
    private MaterialEditTextMeter edtEnd2;
    private MaterialEditTextInt edtBlow2;
    private MaterialEditTextMeter edtBegin3;
    private MaterialEditTextMeter edtEnd3;
    private MaterialEditTextInt edtBlow3;
    private MaterialEditTextMeter edtBegin4;
    private MaterialEditTextMeter edtEnd4;
    private MaterialEditTextInt edtBlow4;

    private RecordPower recordPower;
    private List<RecordPowerLog> recordPowerLogList = new ArrayList<RecordPowerLog>();;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(EXTRA_RECORD_POWER)) {
            recordPower = (RecordPower) getArguments().getSerializable(EXTRA_RECORD_POWER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_power_log_spt_add, null);
        edtBegin1 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtBegin1);
        edtEnd1 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtEnd1);
        edtBlow1 = (MaterialEditTextInt) convertView.findViewById(R.id.edtBlow1);
        edtBegin2 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtBegin2);
        edtEnd2 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtEnd2);
        edtBlow2 = (MaterialEditTextInt) convertView.findViewById(R.id.edtBlow2);
        edtBegin3 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtBegin3);
        edtEnd3 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtEnd3);
        edtBlow3 = (MaterialEditTextInt) convertView.findViewById(R.id.edtBlow3);
        edtBegin4 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtBegin4);
        edtEnd4 = (MaterialEditTextMeter) convertView.findViewById(R.id.edtEnd4);
        edtBlow4 = (MaterialEditTextInt) convertView.findViewById(R.id.edtBlow4);

        edtBegin1.addTextChangedListener(beginTextWatcher);
        edtBegin1.setText(0.00);

        return convertView;
    }

    DBHelper dbHelper;

    private List<RecordPowerLog> getRecordPowerLogList(int c) {
        for (int i = 0; i < c; i++) {
            recordPowerLogList.add(new RecordPowerLog());
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

        return recordPowerLogList;
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

    TextWatcher endTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
//            double j = Double.valueOf(holder.vo.getEnd());
//            double i = Double.valueOf(holder.edtEnd.getText().toString());
//            if (i != j) {
//                System.out.println("发生了变化i=" + i + ";j=" + j + ";");
//                if (i < j) {
//                    System.out.println("后续的值清空");
//                    updateEnabled(position);
//                } else {
//                    System.out.println("把还原回来");
//                    holder.edtEnd.setText(holder.vo.getEnd());
//                }
//            }
        }
    };

    public void updatePowerLog(double begin) {
        try {
            double n1 = 0.15;
            double nn = 0.1;
            double b;
            double e;
            b = begin;
            e = begin + n1;
            edtEnd1.setText(e);
            for (int i = 2; i <= 4; i++) {
                b = e;
                e = b + nn;
                switch (i) {
                    case 2:
                        edtBegin2.setText(b);
                        edtEnd2.setText(e);
                        break;
                    case 3:
                        edtBegin3.setText(b);
                        edtEnd3.setText(e);
                        break;
                    case 4:
                        edtBegin4.setText(b);
                        edtEnd4.setText(e);
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void updateEnabled(int position) {
//        try {
//            for (int i = position + 1; i < powerLogList.size(); i++) {
//                powerLogList.get(i).setIsEnabled(false);
//            }
//            itemAdapter.notifyDataSetChanged();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public RecordPower getValue() {
//        recordPower.setBegin(edtBegin1.getText().toString());
//        recordPower.setEnd(edtEnd4.getText().toString());
//        int blow = Integer.valueOf(edtBlow1.getText().toString())+Integer.valueOf(edtBlow2.getText().toString())+Integer.valueOf(edtBlow3.getText().toString())+Integer.valueOf(edtBlow4.getText().toString());
//        recordPower.setBlow(String.valueOf(blow));
        return recordPower;
    }

}
