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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * @author Aidan Follestad (afollestad)
 */
public class RecordInfoDialog extends DialogFragment {
    Context context;

    private MaterialEditText edtCode;
    private MaterialEditText edtType;
    private MaterialEditText edtDepth;
    private MaterialEditText edtContent;

    private MaterialEditText edtCreateTime;
    private MaterialEditText edtUpdateTime;
    private MaterialEditText edtMediaNumber;
    Record record;
    MediaDao mediaDao;
    private TextView record_code_tv;
    private ImageButton record_close_ib;

    public RecordInfoDialog() {

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
        mediaDao = new MediaDao(context);
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dlg_record_info, true)
                .btnSelector(R.drawable.md_btn_cancel_selector_custom, DialogAction.POSITIVE).build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

        edtCode = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCode);
        edtType = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtType);
        edtDepth = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtDepth);
        edtContent = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtContent);
        edtCreateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCreateTime);
        edtUpdateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtUpdateTime);
        edtMediaNumber = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtMediaNumber);

        record_code_tv = (TextView) dialog.getCustomView().findViewById(R.id.record_code_tv);
        record_code_tv.setText(record.getCode());
        record_close_ib = (ImageButton) dialog.getCustomView().findViewById(R.id.record_close_ib);
        record_close_ib.setOnClickListener(closeListener);

        edtCode.setText(setMaterialEditText(record.getCode()));
        edtType.setText(setMaterialEditText(record.getType()));
        //取水和水位的显示不太一样
        if (record.getType().equals(Record.TYPE_WATER)) {
            String begin = Double.parseDouble(record.getBeginDepth()) > 0 ? record.getBeginDepth() : "-";
            String end = Double.parseDouble(record.getEndDepth()) > 0 ? record.getEndDepth() : "-";
            edtDepth.setText(setMaterialEditText(begin + "/" + end));
        } else if (record.getType().equals(Record.TYPE_GET_WATER)) {
            edtDepth.setText(setMaterialEditText(record.getBeginDepth()));
        } else {
            edtDepth.setText(setMaterialEditText(record.getBeginDepth() + "~" + record.getEndDepth()));
        }
        edtContent.setText(setMaterialEditText(Html.fromHtml(record.getContent()).toString()));
        edtCreateTime.setText(setMaterialEditText(record.getCreateTime()));
        edtUpdateTime.setText(setMaterialEditText(record.getUpdateTime()));
        edtMediaNumber.setText(setMaterialEditText(mediaDao.getMediaCountByrdcordID(record.getId()) + ""));
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
            Dao<Record, String> dao = dbHelper.getDao(Record.class);
            record = dao.queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

}
