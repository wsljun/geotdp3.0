package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.geotdb.compile.adapter.DropListAdapter;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.RecordEditActivity;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.view.MaterialEditTextElevation;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.qiujuer.genius.ui.widget.Button;

import java.util.List;

/**
 * 水位编辑
 */
public class RecordEditWaterFragment extends RecordEditBaseFragment {

    private MaterialBetterSpinner sprType;

    private Button btnAddWaterShow;
    private Button btnAddWaterStable;

    private MaterialEditTextElevation edtWaterShow;
    private MaterialEditText edtWaterShowTime;

    private MaterialEditTextElevation edtWaterStable;
    private MaterialEditText edtWaterStableTime;

    List<DropItemVo> typeList;
    DropListAdapter typeListAdapter;
    private int sortNoType= 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_water_edit, null);
        try {
            sprType = (com.geotdb.compile.view.MaterialBetterSpinner) convertView.findViewById(R.id.waterType);

            typeList = dictionaryDao.getDropItemList(getSqlString("地下水类型"));

            typeListAdapter = new DropListAdapter(context, R.layout.drop_item, typeList);
            sprType.setAdapter(typeListAdapter);
            //设置可自定义和可清空
            sprType.setCustom();
            sprType.setOnItemClickListener(typeListener);

            btnAddWaterShow = (Button) convertView.findViewById(R.id.btnAddWaterShow);
            btnAddWaterStable = (Button) convertView.findViewById(R.id.btnAddWaterStable);

            edtWaterShow = (MaterialEditTextElevation) convertView.findViewById(R.id.edtWaterShow);
            edtWaterShowTime = (MaterialEditText) convertView.findViewById(R.id.edtWaterShowTime);

            final RecordEditActivity recordEditActivity = (RecordEditActivity) getActivity();
            btnAddWaterShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recordEditActivity.updateLocation();
                    edtWaterShowTime.setText(recordEditActivity.getLocationTime());
                }
            });

            edtWaterStable = (MaterialEditTextElevation) convertView.findViewById(R.id.edtWaterStable);
            edtWaterStableTime = (MaterialEditText) convertView.findViewById(R.id.edtWaterStableTime);

            btnAddWaterStable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recordEditActivity.updateLocation();
                    edtWaterStableTime.setText(recordEditActivity.getLocationTime());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        initValue();
        return convertView;
    }


    MaterialBetterSpinner.OnItemClickListener typeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoType = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void initValue() {
        try {
            sprType.setText(record.getWaterType());
            edtWaterShow.setText(record.getShownWaterLevel());
            edtWaterShowTime.setText(record.getShownTime());
            edtWaterStable.setText(record.getStillWaterLevel());
            edtWaterStableTime.setText(record.getStillTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Record getRecord() {
        record.setWaterType(sprType.getText().toString());
        record.setShownWaterLevel(edtWaterShow.getText().toString());
        record.setShownTime(edtWaterShowTime.getText().toString());
        record.setStillWaterLevel(edtWaterStable.getText().toString());
        record.setStillTime(edtWaterStableTime.getText().toString());
        return record;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
//        if (TextUtils.isEmpty(sprType.getText().toString())) {
//            validator = false;
//            sprType.setError("选择地下水类型");
//        }

        double waterShow = 0.0;
        double waterStable = 0.0;
        String show = edtWaterShow.getText().toString();
        String stable = edtWaterStable.getText().toString();
        if (("".equals(show) || Double.valueOf(show) == 0) && ("".equals(stable) || Double.valueOf(stable) == 0)) {
            validator = false;
            edtWaterShow.setError("至少输入一种水位");
            edtWaterStable.setError("至少输入一种水位");
        } else {
            String sh = edtWaterShow.getText().toString();
            if (sh.equals("")) {
                sh = "0";
            }
            String st = edtWaterStable.getText().toString();
            if (st.equals("")) {
                st = "0";
            }
            waterShow = Double.valueOf(sh);
            waterStable = Double.valueOf(st);
            if (waterShow > 0 && "".equals(edtWaterShowTime.getText().toString())) {
                validator = false;
                edtWaterShowTime.setError("请更新初见水位时间");
            }
            if (waterStable > 0 && "".equals(edtWaterStableTime.getText().toString())) {
                validator = false;
                edtWaterStableTime.setError("请更新稳定水位时间");
            }
            //有时间的，水位为零
            if (!"".equals(edtWaterShowTime.getText().toString()) && waterShow == 0) {
                validator = false;
                edtWaterShow.setError("请输入深度");
            }
            if (!"".equals(edtWaterStableTime.getText().toString()) && waterStable == 0) {
                validator = false;
                edtWaterStable.setError("请输入深度");
            }
        }

        if (sortNoType > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1","地下水类型", sprType.getText().toString(),"" + sortNoType,relateID,Record.TYPE_WATER));
        }
        return validator;
    }

    @Override
    public String getTitle() {
        String title = sprType.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_WATER;
    }

    @Override
    public String getBegin() {
        return edtWaterShow.getText().toString();
    }

    @Override
    public String getEnd() {
        return edtWaterStable.getText().toString();
    }

}
