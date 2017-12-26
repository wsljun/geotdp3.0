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
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.view.MaterialEditTextMillimeter;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.List;

/**
 * 回次编辑
 */
public class RecordEditFrequencyFragment extends RecordEditBaseFragment {
    private MaterialBetterSpinner sprType;//选择钻进方法
    private MaterialBetterSpinner sprMode;//选择护壁方法
    private MaterialEditTextMillimeter edtAperture;//输入钻孔孔径

    List<DropItemVo> typeList;
    DropListAdapter typeListAdapter;
    private int sortNoType = 0;
    List<DropItemVo> modeList;
    DropListAdapter modeListAdapter;
    private int sortNoMode= 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_frequency_edit, null);
        try {
            sprType = (MaterialBetterSpinner) convertView.findViewById(R.id.sprType);
            sprMode = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMode);
            edtAperture = (MaterialEditTextMillimeter) convertView.findViewById(R.id.edtAperture);

            typeListAdapter = new DropListAdapter(context, R.layout.drop_item, getLayerTypeList());
            sprType.setAdapter(typeListAdapter);
            sprType.setOnItemClickListener(typeListener);
            //默认填写
            //sprType.setText(typeList.get(0).getName());
            modeListAdapter = new DropListAdapter(context, R.layout.drop_item, getModeList());
            sprMode.setAdapter(modeListAdapter);
            sprMode.setOnItemClickListener(modeListener);
            //设置可自定义和可清空
            sprType.setCustom().setClear();
            sprMode.setCustom().setClear();
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
    MaterialBetterSpinner.OnItemClickListener modeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoMode = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void initValue() {
        try {
            sprType.setText(record.getFrequencyType());
            sprMode.setText(record.getFrequencyMode());
            edtAperture.setText(record.getAperture());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DropItemVo> getLayerTypeList() {
//        typeList = new ArrayList<>();
//        typeList.add(new DropItemVo("1", "回转钻进"));
//        typeList.add(new DropItemVo("2", "冲击钻进"));
//        typeList.add(new DropItemVo("3", "震动钻进"));
//        typeList.add(new DropItemVo("4", "冲洗钻进"));
//        typeList.add(new DropItemVo("5", "洛阳铲"));
//        typeList.add(new DropItemVo("6", "勺型铲"));
//        typeList.add(new DropItemVo("7", "麻花钻"));
//        typeList = layerNameDao.getDropItemList("select rowid as _id,name  from dictionary where sort='钻进方法' order by sortNo");
        typeList = dictionaryDao.getDropItemList(getSqlString("钻进方法"));
        return typeList;
    }

    private List<DropItemVo> getModeList() {
//        modeList = new ArrayList<>();
//        modeList.add(new DropItemVo("2", "套管"));
//        modeList.add(new DropItemVo("3", "泥浆"));
//        modeList = layerNameDao.getDropItemList("select rowid as _id,name  from dictionary where sort='护壁方法' order by sortNo");
        modeList = dictionaryDao.getDropItemList(getSqlString("护壁方法"));
        return modeList;
    }

    @Override
    public Record getRecord() {
        record.setFrequencyType(sprType.getText().toString());
        record.setFrequencyMode(sprMode.getText().toString());
        record.setAperture(edtAperture.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprType.getText().toString() + "--" + sprMode.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_FREQUENCY;
    }

    @Override
    public boolean validator() {
        boolean validator = true;

        if (sortNoType > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1","钻进方法", sprType.getText().toString(),"" + sortNoType,relateID,Record.TYPE_FREQUENCY));
        }
        if (sortNoMode > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1","护壁方法", sprMode.getText().toString(),"" + sortNoMode,relateID,Record.TYPE_FREQUENCY));
        }

//        if (TextUtils.isEmpty(sprType.getText().toString())) {
//            validator = false;
//            sprType.setError("护壁方法不能为空");
//        }
//        if (TextUtils.isEmpty(sprMode.getText().toString())) {
//            validator = false;
//            sprMode.setError("钻进方法不能为空");
//        }
//        if (Integer.parseInt(edtAperture.getText().toString()) == 0) {
//            validator = false;
//            edtAperture.setError("钻孔孔径必须大于0");
//        }
        return validator;
    }
}
