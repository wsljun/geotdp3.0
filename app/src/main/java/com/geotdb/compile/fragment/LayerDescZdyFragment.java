package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.vo.Record;

/**
 * 地层分类(自定义) 的片段.
 */
public class LayerDescZdyFragment extends LayerDescBaseFragment {


    private static final String KEY_CONTENT = "TestFragment:zdy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_zdy, null);

        initValue();
        return convertView;
    }

    private void initValue() {
    }

    @Override
    public Record getRecord() {
        return record;
    }
}