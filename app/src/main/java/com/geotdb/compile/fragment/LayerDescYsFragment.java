package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.List;

/**
 * 地层分类(岩石) 的片段.
 */
public class LayerDescYsFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprJycd;       //坚硬程度
    private MaterialBetterSpinner sprWzcd;      //完整程度
    private MaterialBetterSpinner sprJbzldj;        //基本质量等级
    private MaterialBetterSpinner sprFhcd;      //风化程度
    private MaterialBetterSpinner sprKwx;       //可挖行
    private MaterialBetterSpinner sprJglx;       //结构类型

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprJycdList;
    private List<DropItemVo> sprWzcdList;
    private List<DropItemVo> sprJbzldjList;
    private List<DropItemVo> sprFhcdList;
    private List<DropItemVo> sprKwxList;
    private List<DropItemVo> sprJglxList;

    private int sortNoYs = 0;
    private int sortNOKwx = 0;

    private static final String KEY_CONTENT = "TestFragment:st";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_ys, null);

        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprJycd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJycd);
        sprWzcd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprWzcd);
        sprJbzldj = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJbzldj);
        sprFhcd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprFhcd);
        sprKwx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKwx);
        sprJglx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJglx);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("岩石_颜色"));
        sprJycdList = dictionaryDao.getDropItemList(getSqlString("岩石_坚硬程度"));
        sprWzcdList = dictionaryDao.getDropItemList(getSqlString("岩石_完整程度"));
        sprJbzldjList = dictionaryDao.getDropItemList(getSqlString("岩石_基本质量等级"));
        sprFhcdList = dictionaryDao.getDropItemList(getSqlString("岩石_风化程度"));
        sprKwxList = dictionaryDao.getDropItemList(getSqlString("岩石_可挖性"));
        sprJglxList = dictionaryDao.getDropItemList(getSqlString("岩石_结构类型"));

        sprYs.setAdapter(context, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprJycd.setAdapter(context, sprJycdList, MaterialBetterSpinner.MODE_CLEAR);
        sprWzcd.setAdapter(context, sprWzcdList, MaterialBetterSpinner.MODE_CLEAR);
        sprJbzldj.setAdapter(context, sprJbzldjList, MaterialBetterSpinner.MODE_CLEAR);
        sprFhcd.setAdapter(context, sprFhcdList, MaterialBetterSpinner.MODE_CLEAR);
        sprKwx.setAdapter(context, sprKwxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKwx.setOnItemClickListener(kwxListener);
        sprJglx.setAdapter(context, sprJglxList, MaterialBetterSpinner.MODE_CLEAR);
        initValue();
        return convertView;
    }


    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener kwxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNOKwx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void initValue() {
        sprYs.setText(record.getYs());
        sprJycd.setText(record.getJycd());
        sprWzcd.setText(record.getWzcd());
        sprJbzldj.setText(record.getJbzldj());
        sprFhcd.setText(record.getFhcd());
        sprKwx.setText(record.getKwx());
        sprJglx.setText(record.getJglx());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setJycd(sprJycd.getText().toString());
        record.setWzcd(sprWzcd.getText().toString());
        record.setJbzldj(sprJbzldj.getText().toString());
        record.setFhcd(sprFhcd.getText().toString());
        record.setKwx(sprKwx.getText().toString());
        record.setJglx(sprJglx.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "岩石_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (sortNOKwx > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "岩石_可挖性", sprKwx.getText().toString(), "" + sortNOKwx, relateID, Record.TYPE_LAYER));
        }
        return true;
    }
}

