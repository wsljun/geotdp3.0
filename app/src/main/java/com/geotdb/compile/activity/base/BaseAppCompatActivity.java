package com.geotdb.compile.activity.base;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;

public class BaseAppCompatActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context context;
    protected Activity activity;

//	protected TextView tvwTitle;
//	protected Button btnBack;
//	protected View vwRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 等待对话框
     */
    MaterialDialog mDialog;

    //显示检验序列号的对话框
    public void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(this).title(R.string.dictionary_wait_dialog).content(R.string.please_wait).progress(true, 0).progressIndeterminateStyle(horizontal).build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    //销毁检验序列号的对话框
    public void dismissProgressDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
