package com.geotdb.compile.utils;

import android.content.Context;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;

public class DialogUtil {

    //普通的对话框提示
    private void showDialog(Context context,String message) {
        new MaterialDialog.Builder(context)
                .title("提示")
                .content(message)
                .positiveText("确认")
                .btnStackedGravity(GravityEnum.CENTER)
                .forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    /**
     * 显示一个小提示框(CharSequence)
     *
     * @param context
     * @param message
     */
    public static void showDialog(final Context context, final CharSequence message) {
        new MaterialDialog.Builder(context)
                .title("提示")
                .content(message)
                .positiveText("确认")
                .btnStackedGravity(GravityEnum.CENTER)
                .forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    private MaterialDialog createProgressDialog(Context context,boolean horizontal) {
        MaterialDialog  mDialog = new MaterialDialog.Builder(context)
                    .title(R.string.serialNumberTest_progress_dialog)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .progressIndeterminateStyle(horizontal)
                    .build();
            mDialog.setCanceledOnTouchOutside(false);
       return mDialog;
    }

}