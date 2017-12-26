package com.geotdb.compile.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Project;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.qiujuer.genius.ui.widget.Button;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProjectAddDialog extends DialogFragment {

    Context context;

    private FolderSelectCallback mCallback;

    private Button btnTest;

    private MaterialEditText edtSerialNumber;
    private MaterialEditText edtFullName;
    private MaterialEditText edtCode;
    private MaterialEditText edtLeader;

    private String strSerialNumber;
    private String strFullName;
    private String strCode;
    private String strLeader;

    MaterialDialog mDialog;


    private final MaterialDialog.ButtonCallback mButtonCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog materialDialog) {
            strSerialNumber = edtSerialNumber.getText().toString();
            strFullName = edtFullName.getText().toString();
            strCode = edtCode.getText().toString();
            strLeader = edtLeader.getText().toString();

            Project project = new Project();
            project.setSerialNumber(strSerialNumber);
            project.setFullName(strFullName);
            project.setCode(strCode);
            project.setLeader(strLeader);

//            MyApplication myApp = (MyApplication) getActivity().getApplication();
//            SQLiteDatabase sqLite = myApp.getName();
//            sqLite.execSQL(project.getInsertSQL(), project.getObject());

            materialDialog.dismiss();
            mCallback.onFolderSelection(project);
        }

        @Override
        public void onNegative(MaterialDialog materialDialog) {
            materialDialog.dismiss();
        }
    };

    public interface FolderSelectCallback {
        void onFolderSelection(Project project);
    }

    public ProjectAddDialog() {

    }

    private void strSerialNumberTest() {
        strSerialNumber = edtSerialNumber.getText().toString();
        if ("".equals(strSerialNumber)) {
            ToastUtil.showToastL(getActivity(), "请输入序列号");
        } else {
            int APNType = Common.getAPNType(getActivity());
            if (APNType > 0) {
                System.out.println("网络类型为" + APNType);
                relevance(strSerialNumber);
            } else {
                //没有网络给予提示
                ToastUtil.showToastL(getActivity(), "没有网络");
            }
        }

    }

    //显示检验序列号的对话框
    private void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.serialNumberTest_progress_dialog)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .progressIndeterminateStyle(horizontal)
                    .build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    //销毁检验序列号的对话框
    private void dismissProgressDialog() {
        mDialog.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.title_addNewProject)
                .customView(R.layout.dlg_project_add, true)
                .positiveText(R.string.agree)
                .neutralText(R.string.disagree)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .btnSelector(R.drawable.md_btn_cancel_selector_custom, DialogAction.NEUTRAL)
                .positiveColor(Color.WHITE)
                .neutralColor(Color.WHITE)
                .callback(mButtonCallback)
                //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .btnStackedGravity(GravityEnum.CENTER)
                .build();

        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

//        btnTest =(ButtonRectangle) dialog.getCustomView().findViewById(R.id.btnTest);
//        btnTest.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    strSerialNumberTest();
//                }
//            });

        edtSerialNumber = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtSerialNumber);
        edtFullName = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtFullName);
        edtCode = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCode);
        edtLeader = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtLeader);

        return dialog;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (FolderSelectCallback) activity;
    }

    public void show(AppCompatActivity context) {
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

    private static class FolderSorter implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }


    //关联操作
    private void relevance(String strSerialNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("project.serialNumber", strSerialNumber);
        OkHttpUtils.post().params(params).url(Urls.GET_PROJECT_INFO_BY_KEY_POST).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "onError:id=" + id + "--e:" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "onResponse:response=" + response + "--id=" + id);
                Gson gson = new Gson();
                JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                if (jsonResult.getStatus()) {
                    String result2 = jsonResult.getResult();
                    Project mProject = gson.fromJson(result2.toString(), Project.class);
                    showProjectInfo(mProject);
                } else {
                    showStacked(jsonResult.getMessage());
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showProgressDialog(false);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissProgressDialog();
            }
        });
    }

    /**
     * 启动检验项目的线程
     */
//    private void startGetProjectThread() {
//
//            AsyncTask<String, Void, JSONObject> task = new AsyncTask<String, Void, JSONObject>() {
//            @Override
//            protected void onPreExecute() {
//                showProgressDialog(false);
//                super.onPreExecute();
//            }
//
//                @Override
//            protected JSONObject doInBackground(String... params1) {
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("companyProject.serialNumber",strSerialNumber));
//                JSONObject json = ServerProxy.invoke(Urls.GET_PROJECT_INFO_BY_KEY_POST, params);
//                return json;
//            }
//
//
//            @Override
//            protected void onPostExecute(JSONObject json) {
//                dismissProgressDialog();
//                if (json != null) {
//                    try {
//                        JSONObject result = json.getJSONObject("result");
//                        Gson gson = new Gson();
//                        JsonResult jsonResult = gson.fromJson(result.toString(), JsonResult.class);
//                        if(jsonResult.getStatus()){
//                            String result2 = jsonResult.getResult();
//                            Project mProject = gson.fromJson(result2.toString(), Project.class);
//                            showProjectInfo(mProject);
//                        }else{
//                            showStacked(jsonResult.getMessage());
//                        }
//                    } catch (JSONException e) {
//                        System.out.println(e.toString());
//                        e.printStackTrace();
//                    }
//                } else {
//                    showStacked("项目获取出错");
//                }
//            }
//        };
//        task.execute();
//    }
    //普通的对话框提示
    private void showStacked(String s) {
        new MaterialDialog.Builder(context)
                .title("提示")
                .content(s)
                .positiveText("确认")
                .btnStackedGravity(GravityEnum.CENTER)
                .forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    //成功获取到项目信息后的提示
    private void showProjectInfo(final Project project) {
        String s = "项目名称:" + project.getFullName() + "\n"
                + "负 责 人:" + project.getRealName() + "\n"
                + "项目编号:" + project.getCode() + "\n"
                + "勘察单位:" + project.getCompanyName() + "\n"
                + "建设单位:" + project.getOwner() + "\n"
                + "项目地点:" + project.getProName() + project.getCityName() + "" + project.getDisName() + project.getAddress() + "\n";

        new MaterialDialog.Builder(context)
                .title("提示")
                .content(s)
                .positiveText("确认")
                .btnStackedGravity(GravityEnum.CENTER)
                .forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        edtSerialNumber.setText(project.getSerialNumber());
                        edtFullName.setText(project.getFullName());
                        edtCode.setText(project.getCode());
                        edtLeader.setText(project.getRealName());
                    }
                }).show();
    }
}
