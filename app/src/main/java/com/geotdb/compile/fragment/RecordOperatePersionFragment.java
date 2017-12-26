package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.geotdb.compile.R;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 机长
 */
public class RecordOperatePersionFragment extends RecordEditBaseFragment {


    private MaterialBetterSpinner operateperson_name;
    private MaterialEditText operateperson_code;

    private List<Record> recordList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_scene_operateperson, container, false);
        initView(convertView);
        loadData();
        return convertView;
    }

    private void initView(View v) {
        operateperson_name = (MaterialBetterSpinner) v.findViewById(R.id.operateperson_name);
        operateperson_code = (MaterialEditText) v.findViewById(R.id.operateperson_code);
    }


    @Override
    public Record getRecord() {
        record.setOperatePerson(operateperson_name.getText().toString());
        record.setTestType(operateperson_code.getText().toString());
        return record;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        if (TextUtils.isEmpty(operateperson_name.getText().toString())) {
            operateperson_name.setError("姓名不能为空");
            validator = false;
        }
        if (TextUtils.isEmpty(operateperson_code.getText().toString())) {
            operateperson_code.setError("编号不能为空");
            validator = false;
        }
        return validator;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_OPERATEPERSON;
    }

    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                recordList = new RecordDao(getActivity()).getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_OPERATEPERSON);
                handler.sendMessage(new Message());
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (recordList != null) {
                //新建的记录机长信息是空的，删除掉
                Iterator<Record> ir = recordList.iterator();
                    while (ir.hasNext()) {
                        Record record = ir.next();
                        if ("".equals(record.getOperatePerson())) {
                            ir.remove();
                        }
                }
                L.e("recordList.size()--->>>" + recordList.size());
                //去掉机长和机长编号相同的数据
                for (int i = 0; i < recordList.size(); i++) {
                    for (int j = i + 1; j < recordList.size(); j++) {
                        if (recordList.get(i).getOperatePerson().equals(recordList.get(j).getOperatePerson()) && recordList.get(i).getTestType().equals(recordList.get(j).getTestType())) {
                            recordList.remove(j);
                            j--;
                        }
                    }
                }
                L.e("recordList.size()--->>>" + recordList.size());


                final List<DropItemVo> list = new ArrayList<>();
                for (int i = 1; i <= recordList.size(); i++) {
                    DropItemVo dropItemVo = new DropItemVo();
                    dropItemVo.setId(i + "");
                    dropItemVo.setName("姓名:"+recordList.get(i - 1).getOperatePerson() + "  编号:" + recordList.get(i - 1).getTestType());
                    dropItemVo.setValue(recordList.get(i - 1).getOperatePerson() + "  " + recordList.get(i - 1).getTestType());
                    list.add(dropItemVo);
                }
                operateperson_name.setText(record.getOperatePerson());
                operateperson_code.setText(record.getTestType());
                operateperson_name.setAdapter(context, list, MaterialBetterSpinner.MODE_CUSTOM);
                operateperson_name.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i + 1 < list.size()) {
                            operateperson_code.setText(recordList.get(i).getTestType());
                        }
                    }
                });
            }
        }
    };

}
