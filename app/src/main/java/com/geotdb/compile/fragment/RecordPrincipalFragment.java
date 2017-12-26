package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.geotdb.compile.R;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 负责人
 */
public class RecordPrincipalFragment extends RecordEditBaseFragment {
    private MaterialBetterSpinner principal_name;

    private List<Record> recordList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_scene_principal, container, false);
        initView(convertView);
        initValue();
        loadData();
        return convertView;
    }

    private void initView(View v) {
        principal_name = (MaterialBetterSpinner) v.findViewById(R.id.principal_name);
    }

    private void initValue() {
        principal_name.setText(record.getOperatePerson());
    }

    @Override
    public Record getRecord() {
        record.setOperatePerson(principal_name.getText().toString());
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_PRINCIPAL;
    }


    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                recordList = new RecordDao(getActivity()).getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_PRINCIPAL);
                handler.sendMessage(new Message());
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (recordList != null) {
                Iterator<Record> ir = recordList.iterator();
                while (ir.hasNext()) {
                    Record record = ir.next();
                    if ("".equals(record.getOperatePerson())) {
                        ir.remove();
                    }
                }
                for (int i = 0; i < recordList.size(); i++) {
                    for (int j = i + 1; j < recordList.size(); j++) {
                        if (recordList.get(i).getOperatePerson().equals(recordList.get(j).getOperatePerson())) {
                            recordList.remove(j);
                            j--;
                        }
                    }
                }

                final List<DropItemVo> list = new ArrayList<>();
                for (int i = 1; i <= recordList.size(); i++) {
                    DropItemVo dropItemVo = new DropItemVo();
                    dropItemVo.setId(i + "");
                    dropItemVo.setName(recordList.get(i - 1).getOperatePerson());
                    dropItemVo.setValue(recordList.get(i - 1).getOperatePerson());
                    list.add(dropItemVo);
                }
                principal_name.setText(record.getOperatePerson());
                principal_name.setAdapter(context, list, MaterialBetterSpinner.MODE_CUSTOM);
                principal_name.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    }
                });

            }
        }
    };
}
