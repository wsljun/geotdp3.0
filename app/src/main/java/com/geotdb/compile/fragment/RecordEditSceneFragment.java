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
 * 备注
 */
public class RecordEditSceneFragment extends RecordEditBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_remark_edit, null);
        initValue();
        return convertView;
    }


    private void initValue() {
    }


    @Override
    public Record getRecord() {
        return record;
    }


    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE;
    }

}
