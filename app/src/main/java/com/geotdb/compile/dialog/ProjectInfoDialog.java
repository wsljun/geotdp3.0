package com.geotdb.compile.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.maps.model.Text;
import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.vo.Project;
import com.j256.ormlite.dao.Dao;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProjectInfoDialog extends DialogFragment {

    Context context;

    private MaterialEditText edtSerialNumber;
    private MaterialEditText edtFullName;
    private MaterialEditText edtCode;
    private MaterialEditText edtLeader;
    private MaterialEditText edtAddress;
    private MaterialEditText edtWorkCompany;
    private MaterialEditText edtCompanyName;
    private MaterialEditText edtDesignCompanyName;
    private MaterialEditText edtState;
    private MaterialEditText edtRemark;

    private MaterialEditText edtCreateTime;
    private MaterialEditText edtUpdateTime;
    Project project;
    private TextView project_fullname_tv;
    private ImageButton project_close_ib;

    public ProjectInfoDialog() {

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
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dlg_project_info, false)
                .build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

        edtSerialNumber = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtSerialNumber);
        edtFullName = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtFullName);
        edtCode = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCode);
        edtLeader = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtLeader);
        edtAddress = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtAddress);
        edtCreateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCreateTime);
        edtUpdateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtUpdateTime);

        edtWorkCompany = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtWorkCompany);
        edtCompanyName = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCompanyName);
        edtDesignCompanyName = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtDesignCompanyName);
        edtState = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtState);
        edtRemark = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtRemark);
        project_fullname_tv = (TextView) dialog.getCustomView().findViewById(R.id.project_fullname_tv);
        project_close_ib = (ImageButton) dialog.getCustomView().findViewById(R.id.project_close_ib);
        project_close_ib.setOnClickListener(closeListener);

        project_fullname_tv.setText(setMaterialEditText(project.getFullName()));
        edtSerialNumber.setText(setMaterialEditText(project.getSerialNumber()));
        edtFullName.setText(setMaterialEditText(project.getFullName()));
        edtCode.setText(setMaterialEditText(project.getCode()));
        edtLeader.setText(setMaterialEditText(project.getLeader()));
        edtWorkCompany.setText(setMaterialEditText(project.getOwner()));
        edtCompanyName.setText(setMaterialEditText(project.getCompanyName()));
        edtDesignCompanyName.setText(setMaterialEditText(project.getDesignCompanyName()));
        edtState.setText(setMaterialEditText(project.getStateName()));
        edtRemark.setText(setMaterialEditText(project.getRemark()));
        edtAddress.setText(setMaterialEditText(project.getAddress()));
        edtCreateTime.setText(setMaterialEditText(project.getCreateTime()));
        edtUpdateTime.setText(setMaterialEditText(project.getUpdateTime()));

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
//        return !"".equals(s) ? s : "未填写";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void show(AppCompatActivity context, String id) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Project, String> dao = dbHelper.getDao(Project.class);
            project = dao.queryForId(id);
            project.jieProject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

}
