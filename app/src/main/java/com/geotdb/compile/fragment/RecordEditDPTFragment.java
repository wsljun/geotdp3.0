package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.geotdb.compile.adapter.DropListAdapter;
import com.geotdb.compile.R;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.view.MaterialEditTextElevation;
import com.geotdb.compile.view.MaterialEditTextInt;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;
import com.geotdb.compile.vo.RecordPower;

import java.util.ArrayList;
import java.util.List;

/**
 * 动探
 */
public class RecordEditDPTFragment extends RecordEditBaseFragment {
    private MaterialEditTextElevation edtDrillLength;
    private MaterialBetterSpinner sprPowerType;

    public MaterialEditTextElevation edtBegin;
    public MaterialEditTextElevation edtEnd;
    public MaterialEditTextInt edtBlow;


    List<DropItemVo> powerTypeList;
    DropListAdapter powerTypeListAdapter;

    String powerType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_dpt_edit, null);
        try {
            edtDrillLength = (MaterialEditTextElevation) convertView.findViewById(R.id.edtDrillLength);

            edtBegin = (MaterialEditTextElevation) convertView.findViewById(R.id.edtBegin);
            edtEnd = (MaterialEditTextElevation) convertView.findViewById(R.id.edtEnd);
            edtBlow = (MaterialEditTextInt) convertView.findViewById(R.id.edtBlow);

            edtBegin.addTextChangedListener(beginTextWatcher);

            sprPowerType = (MaterialBetterSpinner) convertView.findViewById(R.id.sprPowerType);
            powerTypeListAdapter = new DropListAdapter(context, R.layout.drop_item, getPowerTypeList());
            sprPowerType.setAdapter(powerTypeListAdapter);
            sprPowerType.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i) {
                        case 0:     // 轻型
                            powerType = RecordPower.TYPE_1;
                            break;
                        case 1:     //重型
                            powerType = RecordPower.TYPE_2;
                            break;
                        case 2:     //超重型
                            powerType = RecordPower.TYPE_3;
                            break;
                    }
                    updatePowerLog(Double.valueOf(edtBegin.getText().toString()));

                }
            });
            initValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void initValue() {
        try {
            edtDrillLength.setText(record.getDrillLength());
            sprPowerType.setText(record.getPowerType());
            edtBegin.setText(record.getBegin1());
            edtEnd.setText(record.getEnd1());
            edtBlow.setText(record.getBlow1());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<DropItemVo> getPowerTypeList() {
//        powerTypeList = DropItemVo.getDropItemList("select rowid as _id,name  from dictionary where sort='贯探类型' order by sortNo", context);
        powerTypeList = new ArrayList<>();
        powerTypeList.add(new DropItemVo("1", RecordPower.TYPE_1));
        powerTypeList.add(new DropItemVo("2", RecordPower.TYPE_2));
        powerTypeList.add(new DropItemVo("3", RecordPower.TYPE_3));
        return powerTypeList;
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
            double nn = 0.3;
            if (RecordPower.TYPE_1.equals(powerType)) {
                nn = 0.3;
            } else if (RecordPower.TYPE_2.equals(powerType)) {
                nn = 0.1;
            } else if (RecordPower.TYPE_3.equals(powerType)) {
                nn = 0.1;
            }
            edtEnd.setText(begin + nn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Record getRecord() {
        record.setDrillLength(edtDrillLength.getText().toString());
        record.setPowerType(sprPowerType.getText().toString());
        record.setBegin1(edtBegin.getText().toString());
        record.setEnd1(edtEnd.getText().toString());
        record.setBlow1(edtBlow.getText().toString());
        return record;
    }


    @Override
    public String getTitle() {
        String title = sprPowerType.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_DPT;
    }

    @Override
    public String getBegin() {
        return edtBegin.getText().toString();
    }

    @Override
    public String getEnd() {
        return edtEnd.getText().toString();
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        RecordDao recordDao = new RecordDao(context);
        if (recordDao.validatorBeginDepth(record, Record.TYPE_DPT, edtBegin.getText().toString())) {
            edtBegin.setError("与其他记录重叠");
            validator = false;
        }

        if (recordDao.validatorEndDepth(record, Record.TYPE_DPT, edtEnd.getText().toString())) {
            edtEnd.setError("与其他记录重叠");
            validator = false;
        }

        if (TextUtils.isEmpty(edtDrillLength.getText().toString()) || Double.valueOf(edtBlow.getText().toString()) == 0) {
            edtBlow.setError("动探击数不能为零");
            validator = false;
        }


        if (TextUtils.isEmpty(edtDrillLength.getText().toString()) || Double.valueOf(edtDrillLength.getText().toString()) == 0) {
            edtDrillLength.setError("钻杆长度不能为零");
            validator = false;
        }
        return validator;
    }
}
