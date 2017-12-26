package com.geotdb.compile.fragment;

import android.os.Bundle;

import com.geotdb.compile.fragment.base.BaseFragment;
import com.geotdb.compile.db.DictionaryDao;
import com.geotdb.compile.vo.Record;

/**
 * 记录编辑 的基础类.
 */
public class RecordEditBaseFragment extends BaseFragment {
    public static final String KEY_TYPE = "RecordPowerLogListFragment:Type";
    public static final String EXTRA_RECORD = "record";
    public static final String EXTRA_EDIT_MODE = "editMode";

    boolean editMode;
    Record record;
//    LayerNameDao layerNameDao;
    DictionaryDao dictionaryDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(EXTRA_EDIT_MODE)) {
            editMode = getArguments().getBoolean(EXTRA_EDIT_MODE);
        }

        if (getArguments().containsKey(EXTRA_RECORD)) {
            record = (Record) getArguments().getSerializable(EXTRA_RECORD);
        }
//        layerNameDao = new LayerNameDao(getActivity());
        dictionaryDao = new DictionaryDao(context);
    }


    public Record getRecord() {
        return null;
    }

    public boolean validator() {
        return true;
    }

    public String getTitle() {
        return "";
    }

    public String getTypeName() {
        return null;
    }

    public String getBegin() {
        return null;
    }

    public String getEnd() {
        return null;
    }

}