package com.geotdb.compile.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.utils.SPUtils;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MapModeDialog extends DialogFragment {
    private RelativeLayout dlg_map_rl1, dlg_map_rl2, dlg_map_rl3;
    private AppCompatRadioButton dlg_map_rb1, dlg_map_rb2, dlg_map_rb3;
    private Context context;
    private int mode;

    public void show(AppCompatActivity context) {
        this.context = context;
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title("定位模式")
                .customView(R.layout.dlg_map_mode, true).positiveText(R.string.agree).callback(agreeCallback).build();

        dlg_map_rl1 = (RelativeLayout) dialog.getCustomView().findViewById(R.id.dlg_map_rl1);
        dlg_map_rl2 = (RelativeLayout) dialog.getCustomView().findViewById(R.id.dlg_map_rl2);
        dlg_map_rl3 = (RelativeLayout) dialog.getCustomView().findViewById(R.id.dlg_map_rl3);

        dlg_map_rb1 = (AppCompatRadioButton) dialog.getCustomView().findViewById(R.id.dlg_map_rb1);
        dlg_map_rb2 = (AppCompatRadioButton) dialog.getCustomView().findViewById(R.id.dlg_map_rb2);
        dlg_map_rb3 = (AppCompatRadioButton) dialog.getCustomView().findViewById(R.id.dlg_map_rb3);

        dlg_map_rl1.setOnClickListener(rl1Listener);
        dlg_map_rl2.setOnClickListener(rl2Listener);
        dlg_map_rl3.setOnClickListener(rl3Listener);

        mode = (int) SPUtils.get(context, "map_mode", 0);

        if (mode != 0) {
            switch (mode) {
                case 1:
                    dlg_map_rl1.callOnClick();
                    break;
                case 2:
                    dlg_map_rl2.callOnClick();
                    break;
                case 3:
                    dlg_map_rl3.callOnClick();
                    break;
            }
        }
        return dialog;
    }

    MaterialDialog.ButtonCallback agreeCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            SPUtils.put(context, "map_mode", mode);
            dialog.dismiss();
        }
    };


    //哈哈，最笨的方法
    View.OnClickListener rl1Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mode = 1;
            dlg_map_rl1.setBackgroundResource(R.color.met_underlineColor);
            dlg_map_rb1.setChecked(true);
            dlg_map_rl2.setBackgroundResource(R.color.base_list_tag_color);
            dlg_map_rb2.setChecked(false);
            dlg_map_rl3.setBackgroundResource(R.color.base_list_tag_color);
            dlg_map_rb3.setChecked(false);
        }
    };
    View.OnClickListener rl2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mode = 2;
            dlg_map_rl2.setBackgroundResource(R.color.met_underlineColor);
            dlg_map_rb2.setChecked(true);
            dlg_map_rl1.setBackgroundResource(R.color.base_list_tag_color);
            dlg_map_rb1.setChecked(false);
            dlg_map_rl3.setBackgroundResource(R.color.base_list_tag_color);
            dlg_map_rb3.setChecked(false);
        }
    };
    View.OnClickListener rl3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mode = 3;
            dlg_map_rl3.setBackgroundResource(R.color.met_underlineColor);
            dlg_map_rb3.setChecked(true);
            dlg_map_rl1.setBackgroundResource(R.color.base_list_tag_color);
            dlg_map_rb1.setChecked(false);
            dlg_map_rl2.setBackgroundResource(R.color.base_list_tag_color);
            dlg_map_rb2.setChecked(false);
        }
    };

}
