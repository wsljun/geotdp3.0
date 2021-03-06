package com.geotdb.compile.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.adapter.DropListAdapter;
import com.geotdb.compile.R;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.activity.base.MyApplication;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.MD5Utils;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Project;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.qiujuer.genius.ui.widget.Button;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * @author Aidan Follestad (afollestad)
 */
public class LoginDialog extends DialogFragment {
    Context context;
    private MaterialBetterSpinner edtUserName;
    private MaterialEditText edtUserPwd;
    private Button btnLogin;
    private AppCompatCheckBox remember;
    private AppCompatCheckBox autologin;


    DBHelper dbHelper;
    Dao<LocalUser, String> dao = null;
    DropListAdapter modeListAdapter;

    private List<DropItemVo> nameList;

    public LoginDialog() {

    }

    public void show(AppCompatActivity context) {
        this.context = context;
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

    private String strUserName = "";
    private String strUserPwd = "";
    private LocalUser localUser;

    MaterialDialog mDialog;

    //显示检验序列号的对话框
    private void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(getActivity()).title(R.string.loging_progress_dialog).content(R.string.please_wait).progress(true, 0).progressIndeterminateStyle(horizontal).build();
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
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(R.string.title_user_login).customView(R.layout.dlg_login, true).positiveText(R.string.disagree)
                .btnSelector(R.drawable.md_btn_cancel_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE).neutralColor(Color.WHITE).btnStackedGravity(GravityEnum.CENTER).build();

        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

        remember = (AppCompatCheckBox) dialog.getCustomView().findViewById(R.id.remember);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!remember.isChecked() && autologin.isChecked()) {
                    autologin.setChecked(false);
                }
            }
        });
        autologin = (AppCompatCheckBox) dialog.getCustomView().findViewById(R.id.autologin);
        autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autologin.isChecked() && !remember.isChecked()) {
                    remember.setChecked(true);
                }
            }
        });
        edtUserName = (MaterialBetterSpinner) dialog.getCustomView().findViewById(R.id.edtUserName);
        edtUserPwd = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtUserPwd);
        //实例化数据库操作类
        dbHelper = DBHelper.getInstance(context);
        try {
            dao = dbHelper.getDao(LocalUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.e("TAG", "modeList=" + getModeList().size());
        //用户名下拉列表
        if (getModeList() == null || getModeList().size() == 0) {
            modeListAdapter = null;
        } else {
            L.e("TAG", "getModeList() ！= null");
            modeListAdapter = new DropListAdapter(context, R.layout.drop_item, getModeList());
            edtUserName.setAdapter(modeListAdapter);
            edtUserName.setOnItemClickListener(spinnerListener);
            edtUserName.addTextChangedListener(changeListener);
            initUsername();
        }
        edtUserName.setIsEnabled(true);//可以编辑edit
        //登陆button
        btnLogin = (Button) dialog.getCustomView().findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSerialNumberTest();
            }
        });
        return dialog;
    }

    //修改用户名密码为空
    TextWatcher changeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            edtUserPwd.setText("");
            remember.setChecked(false);
            autologin.setChecked(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    //标示密码框是否改变
    private boolean pwdChange = false;
    TextWatcher pwdListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            pwdChange = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    //edtUserName下拉列表点击监听
    MaterialBetterSpinner.OnItemClickListener spinnerListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            try {
                List<LocalUser> list = dao.queryBuilder().where().eq("email", getModeList().get(i).getName()).query();
                L.e("TAG", "spinnerListener--->>>>" + list.size() + "....i" + i);
                if (list.size() != 0) {
//                    MyApplication.getInstance().setLocalUser(list.get(0));
                    Common.setLocalUser(context, list.get(0).getId());
                    initUsername();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };

    //初始化用户名，密码等信息
    public void initUsername() {
        //null说明没有用户自动登陆
        if (Common.getLocalUser(context) != null) {
            L.e("TAG", "getLocalUser() != null");
            localUser = Common.getLocalUser(context);
        } else {
            try {
                String name = getModeList().get(0).getName();
                localUser = dao.queryBuilder().where().eq("email", name).query().get(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        boolean reb = Boolean.parseBoolean(localUser.getIsRemembar());
        boolean auto = Boolean.parseBoolean(localUser.getIsAutoLogin());
        //检查是否记住密码
        //用户名显示的是Email
        edtUserName.setText(localUser.getEmail());
        remember.setChecked(reb);
        autologin.setChecked(auto);
        if (reb) {
            L.e("TAG", "记住密码");
            //密码显示的是等长度的 ***
            edtUserPwd.setText(getPwdByLength(Integer.parseInt(localUser.getPwdLength())));
        }
        pwdChange = false;
        edtUserPwd.addTextChangedListener(pwdListener);
    }


    //获取密码长度的***
    private StringBuffer getPwdByLength(int length) {
        StringBuffer astLength = new StringBuffer();
        for (int i = 0; i < length; i++) {
            astLength.append("*");
        }
        return astLength;
    }


    //多个用户切换，从数据库获取用户集合
    private List<DropItemVo> getModeList() {
        return MyApplication.getInstance().getNameList();
    }


    private void strSerialNumberTest() {
        strUserName = edtUserName.getText().toString();
        strUserPwd = edtUserPwd.getText().toString();
        if (TextUtils.isEmpty(strUserName)) {
            edtUserName.setError("请输入用户名");
        } else if (TextUtils.isEmpty(strUserPwd)) {
            edtUserPwd.setError("请输入密码");
        } else {
            //不管有没有网络，有没有记住密码或自动登陆，输入了用户名和密码先跟数据库校正，有就登陆
            if (!loginByLocal()) {
                L.e("TAG", "--->>>loginByLocal---false");
                int APNType = Common.getAPNType(getActivity());
                if (APNType > 0) {
                    L.e("TAG", "--->>>loginByNetwork");
                    loginByNetwork();
                } else {
                    //没有网络给予提示
                    ToastUtil.showToastS(getActivity(), "无网络连接");
                }
            }

        }
    }

    //本地登陸
    public boolean loginByLocal() {
        L.e("TAG", "--->>>loginByLocal");
        try {
            List<LocalUser> list = dao.queryBuilder().where().eq("email", strUserName).query();
            if (null != list && list.size() > 0) {
                L.e("TAG", "--->>>pwdChange：" + pwdChange);
                if (pwdChange) {
                    String password = MD5Utils.MD5(strUserPwd);
                    L.e("TAG", "--->>>list.get(0).getPwd()：" + list.get(0).getPwd() + "---password:" + password);
                    if (list.get(0).getPwd().equalsIgnoreCase(password)) {
                        L.e("TAG", "--->>>loginIsSuccess");
                        loginIsSuccess();
                        dismiss();
                        return true;
                    }
                } else {
                    loginIsSuccess();
                    dismiss();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //網絡登陸
    private void loginByNetwork() {
        Map<String, String> map = new HashMap<>();
        map.put("email", strUserName);
        try {
            //获取当前输入的用户名，根据用户名查询数据库，查看是否有用户数据
            List<LocalUser> list = dao.queryBuilder().where().eq("email", strUserName).query();
            L.e("TAG", "list=" + list.size());
            if (list.size() == 0) {
                map.put("password", MD5Utils.MD5(strUserPwd));
            } else {
                localUser = list.get(0);
                //两种可能，一种密码是才输入的，一种是****
                //数据库里面有用户信息，我已经拿到用户信息了，到这里了说明密码框不为空
                if (MD5Utils.MD5(strUserPwd).equals(localUser.getPwd())) {
                    map.put("password", localUser.getPwd());
                    L.e("TAG", "--->>>equals");
                } else {
                    map.put("password", MD5Utils.MD5(strUserPwd));
                    L.e("TAG", "--->>>equals---false");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.e("TAG", "usernmae=" + map.get("email") + "--password=" + map.get("password"));
        L.e("LOGIN_POST---->>" + Urls.LOGIN_POST);
        OkHttpUtils.post().params(map).url(Urls.LOGIN_POST).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "onError:e:" + e.getMessage());
                dismissProgressDialog();
                ToastUtil.showToastS(context, "网络连接错误");
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showProgressDialog(false);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                L.e("TAG", "onResponse:response=" + response);
                if (JsonUtils.isGoodJson(response)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    showStacked(jsonResult.getMessage());
                    //如果登陆成功，保存用户名和密码到数据库,并保存到baen
                    if (jsonResult.getStatus()) {
                        String result = jsonResult.getResult();
                        localUser = gson.fromJson(result.toString(), LocalUser.class);
                        L.e("登陆成功----》》》》" + localUser.getEmail());
                        loginIsSuccess();
                    }
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }

            }
        });
    }

    public void loginIsSuccess() {
        try {
            localUser.setIsRemembar(String.valueOf(remember.isChecked()));
            //用户下拉列表排序用
            localUser.setAddTime(String.valueOf(System.currentTimeMillis()));
            //登陸成功，並且选择自动登陆，遍历数据库将所有的用户isautologin字段改为false
            //如果登陆成功的不需要自动登陆，那么所有的都不要自动登陆
            List<LocalUser> list = dao.queryForAll();
            for (LocalUser l : list) {
                l.setIsAutoLogin("false");
                dao.update(l);
            }
            localUser.setIsAutoLogin(String.valueOf(autologin.isChecked()));
            //记录密码长度
            localUser.setPwdLength(strUserPwd.length() + "");
            //保存或修改到数据库
            dao.createOrUpdate(localUser);
            //将用户名集合、当前用户信息发送给application
            nameList = MyApplication.getInstance().getNameList();
            DropItemVo drop = null;
            int same = 1;
            if (nameList.size() == 0) {
                MyApplication.getInstance().getNameList().add(new DropItemVo("1", localUser.getEmail()));
            } else {
                for (DropItemVo dropItemVo : nameList) {
                    if (dropItemVo.getName().equals(localUser.getEmail())) {
                        same = same * -1;
                    }
                }
                if (same > 0) {
                    //？？？？？用戶名的問題 ，是电话、email、、、、、
                    MyApplication.getInstance().getNameList().add(new DropItemVo(getModeList().size() + 1 + "", localUser.getEmail()));
                }
            }
            //只要登陆成功，就记录该用户
            Common.setLocalUser(context, localUser.getId());
            //这里应该通知activity重写加载fragment
            context.sendBroadcast(new Intent("MainActivity.CheckUser.login"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //普通的对话框提示
    private void showStacked(String s) {
        new MaterialDialog.Builder(context).title("提示").content(s).positiveText("确认").btnStackedGravity(GravityEnum.CENTER).forceStacking(true)
                .show();
        this.dismiss();
    }

    //成功获取到项目信息后的提示
    private void showProjectInfo(final Project project) {
        String s = "项目名称:" + project.getFullName() + "\n" + "负 责 人:" + project.getRealName() + "\n" + "项目编号:" + project.getCode() + "\n" + "勘察单位:" + project.getCompanyName() + "\n" + "建设单位:" + project.getOwner() + "\n" + "项目地点:" + project.getProName() + project.getCityName() + "" + project.getDisName() + project.getAddress() + "\n";

        new MaterialDialog.Builder(context).title("提示").content(s).positiveText("确认").btnStackedGravity(GravityEnum.CENTER).forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                    }
                }).show();
    }
}
