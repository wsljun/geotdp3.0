package com.geotdb.compile.view;

import android.content.Context;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;

/**
 * Created by Administrator on 2016/6/28.
 */
public class WaitDialog {
    static MaterialDialog mDialog;

    //显示检验序列号的对话框R.string.serialNumberTest_progress_dialog R.string.please_wait
    public static void showProgressDialog(Context context, int title, int content, boolean progress, int max) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(context).title(title).content(content).progress(progress, max).progressIndeterminateStyle(false).build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    //销毁检验序列号的对话框
    public static void dismissProgressDialog() {
        mDialog.dismiss();
    }

    //注销提示框
    public static void showOverAllDialog(Context context, String content, MaterialDialog.ButtonCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.content(content);
        builder.positiveText(R.string.agree);
        builder.negativeText(R.string.disagree);
        builder.callback(callback);
        MaterialDialog dialog = builder.build();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }


    //更新
    public static void showOverAllDialog(Context context, boolean must, String content, MaterialDialog.ButtonCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.content(content);
        if (!must) {
            builder.negativeText(R.string.disagree);
        }
        builder.canceledOnTouchOutside(false);
        builder.positiveText(R.string.agree);
        builder.callback(callback);
        MaterialDialog dialog = builder.build();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
}
