package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.geotdb.compile.R;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Record;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * 描述员
 */
public class RecordPersonFragment extends RecordEditBaseFragment {
    private MaterialEditText person_name;
    private LocalUser localUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_scene_recordperson, container, false);
        localUser = Common.getLocalUser(getActivity());
        initView(convertView);
        initValue();
        return convertView;
    }

    private void initView(View v) {
        person_name = (MaterialEditText) v.findViewById(R.id.person_name);
    }

    private void initValue() {
        person_name.setText(localUser.getRealName());
    }

    @Override
    public Record getRecord() {
        record.setRecordPerson(person_name.getText().toString());
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_RECORDPERSON;
    }
}
