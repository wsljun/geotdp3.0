package com.geotdb.compile.fragment;

import android.os.Bundle;

import com.geotdb.compile.fragment.base.BaseFragment;
import com.geotdb.compile.db.DictionaryDao;
import com.geotdb.compile.vo.Record;

/**
 * 地层分类片段 的基础类.
 */
public class LayerDescBaseFragment extends BaseFragment {
    public static final String EXTRA_RECORD_LAYER = "recordLayer";
    Record record;
    DictionaryDao dictionaryDao;
    public Record getRecord() {
        return null;
    }
    public boolean layerValidator() {
        return true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(EXTRA_RECORD_LAYER)) {
            record = (Record) getArguments().getSerializable(EXTRA_RECORD_LAYER);
        }
        dictionaryDao = new DictionaryDao(getActivity());
    }

}