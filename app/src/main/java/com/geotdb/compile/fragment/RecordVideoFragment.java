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
 * Created by Administrator on 2016/10/25.
 */
public class RecordVideoFragment extends RecordEditBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_scene_video, container, false);
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
        return Record.TYPE_SCENE_VIDEO;
    }

}
