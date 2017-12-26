package com.geotdb.compile.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
//import com.geotdb.compile.fragment.HoleListFragment;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * @author Aidan Follestad (afollestad)
 */
public class HoleInfoDialog extends DialogFragment {
    Context context;

    private MaterialEditText edtCode;
    private MaterialEditText edtType;
    private MaterialEditText edtElevation;
    private MaterialEditText edtDepth;
    private MaterialEditText edtOperatePerson;
    private MaterialEditText edtOperateCode;
    private MaterialEditText edtRadius;
    private MaterialEditText edtMapLongitude;
    private MaterialEditText edtMapLatitude;
    private MaterialEditText edtMapTime;

    private MaterialEditText edtCreateTime;
    private MaterialEditText edtUpdateTime;
    private String holeID;
    private Hole hole;
    private Record jRecord, zRecord;

    private TextView hole_code_tv;
    private ImageButton hole_close_ib;

    public HoleInfoDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dlg_hole_info, false)
                .build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

        edtCode = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCode);
        edtType = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtType);
        edtElevation = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtElevation);
        edtDepth = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtDepth);
        edtOperatePerson = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtOperatePerson);
        edtOperateCode = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtOperateCode);
        edtRadius = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtRadius);
        edtMapLongitude = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtMapLongitude);
        edtMapLatitude = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtMapLatitude);
        edtMapTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtMapTime);
        edtCreateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCreateTime);
        edtUpdateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtUpdateTime);

        hole_code_tv = (TextView) dialog.getCustomView().findViewById(R.id.hole_code_tv);
        hole_code_tv.setText(hole.getCode());
        hole_close_ib = (ImageButton) dialog.getCustomView().findViewById(R.id.hole_close_ib);
        hole_close_ib.setOnClickListener(closeListener);

        edtCode.setText(setMaterialEditText(hole.getCode()));
        edtType.setText(setMaterialEditText(hole.getType()));
        edtElevation.setText(setMaterialEditText(hole.getElevation()));
        edtDepth.setText(setMaterialEditText(hole.getDepth()));

        if (null != jRecord) {
            edtOperatePerson.setText(setMaterialEditText(jRecord.getOperatePerson()));
        }
        if (null != zRecord) {
            edtOperateCode.setText(setMaterialEditText(zRecord.getTestType()));
        }
        edtRadius.setText(setMaterialEditText(hole.getRadius()));
        edtMapLongitude.setText(setMaterialEditText(hole.getMapLongitude()));
        edtMapLatitude.setText(setMaterialEditText(hole.getMapLatitude()));
        edtMapTime.setText(setMaterialEditText(hole.getMapTime()));
        edtCreateTime.setText(setMaterialEditText(hole.getCreateTime()));
        edtUpdateTime.setText(setMaterialEditText(hole.getUpdateTime()));

        return dialog;
    }

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public String setMaterialEditText(String s) {
        return s;
    }


    //复制点，不用了
//    private void showDelDialog() {
//        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        new MaterialDialog.Builder(getActivity())
//                .content("输入复制条数")
//                .inputType(InputType.TYPE_CLASS_NUMBER)
//                .inputMaxLength(16)
//                .positiveText(R.string.agree)
//                .input("10", "1", false, new MaterialDialog.InputCallback() {
//                    @Override
//                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                        int i = Integer.valueOf(input.toString());
//                        if (hole.copy(context, i)) {
//                            ToastUtil.showToastL(context, "复制数据成功");
//                            HoleListFragment holeListFragment = (HoleListFragment) fragmentManager.findFragmentById(R.id.holeListFragment);
//                            holeListFragment.onRefreshList();
//                            dismiss();
//                        } else {
//                            ToastUtil.showToastL(getActivity(), "删除图片失败");
//                        }
//                    }
//                })
//                .negativeText(R.string.disagree)
//                .show();
//    }


    public void show(AppCompatActivity context, String id) {
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
        holeID = id;
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Hole, String> holeDao = dbHelper.getDao(Hole.class);
            hole = holeDao.queryForId(holeID);
            jRecord = new Record().getRecord(context, holeID, Record.TYPE_SCENE_OPERATEPERSON);
            zRecord = new Record().getRecord(context, holeID, Record.TYPE_SCENE_OPERATECODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
