package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.adapter.DropListAdapter;
import com.geotdb.compile.R;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.view.MaterialEditTextElevation;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 取样(取水) 的片段.
 */
public class RecordEditGetWaterFragment extends RecordEditBaseFragment {
    private MaterialEditTextElevation edtWaterDepth;    //取水位置
    private MaterialBetterSpinner sprMode;        //取水方法

    List<DropItemVo> modeList;
    DropListAdapter modeListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_get_water_edit, null);
        try {
            edtWaterDepth = (MaterialEditTextElevation) convertView.findViewById(R.id.edtWaterDepth);

            sprMode = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMode);
            modeListAdapter = new DropListAdapter(context, R.layout.drop_item, getModeList());
            sprMode.setAdapter(modeListAdapter);

            //设置工具方法为可选可编辑
            sprMode.setIsEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initValue();
        return convertView;
    }


    private void initValue() {
        try {
            edtWaterDepth.setText(record.getWaterDepth());
            sprMode.setText(record.getGetMode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DropItemVo> getModeList() {
        modeList = new ArrayList<DropItemVo>();
        modeList.add(new DropItemVo("1", "无"));
        modeList.add(new DropItemVo("2", "加入大理石粉"));
        return modeList;
    }


    @Override
    public Record getRecord() {
        record.setWaterDepth(edtWaterDepth.getText().toString());
        record.setGetMode(sprMode.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprMode.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_GET_WATER;
    }

    @Override
    public String getBegin() {
        return edtWaterDepth.getText().toString();
    }

    @Override
    public String getEnd() {
        return edtWaterDepth.getText().toString();
    }


    private void dcmsst_makedcms() {                        //生成说明
//        String  zfc,zfc1;
//        zfc="";zfc1="";
//
//        //将表中的 《颜色》《状态》《包含物》《夹层》数据转到数组中
//        zfc1=dcms_nt_ys.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"颜色:"+zfc1;
//        zfc1=dcms_nt_zt.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   状态:"+zfc1;
//        zfc1=dcms_nt_bhw.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   包含物:"+zfc1;
//        zfc1=dcms_nt_jc.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   夹层:"+zfc1;
//        dcms_nt_dcms.setText(zfc);
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        if ("".equals(edtWaterDepth.getText().toString()) || Double.parseDouble(edtWaterDepth.getText().toString()) <= 0) {
            edtWaterDepth.setError("取水深度必须大于零");
            validator = false;
        }
//        if (TextUtils.isEmpty(sprMode.getText().toString())) {
//            sprMode.setError("请选择取水方式");
//            validator = false;
//        }
        return validator;
    }
}