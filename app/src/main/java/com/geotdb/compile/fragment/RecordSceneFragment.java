package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.geotdb.compile.R;
import com.geotdb.compile.vo.Record;

/**
 * 场景照片
 */
public class RecordSceneFragment extends RecordEditBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_scene_scene, container, false);
        initView(convertView);
        initValue();
        return convertView;
    }

    private void initView(View v) {
    }

    private void initValue() {
    }

    @Override
    public Record getRecord() {
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_SCENE;
    }
}
