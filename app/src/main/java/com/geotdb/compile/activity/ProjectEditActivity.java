package com.geotdb.compile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Project;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.qiujuer.genius.ui.widget.Button;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectEditActivity extends BaseAppCompatActivity {
    private Context context;

    public static final String EXTRA_PROJECT = "project";
    public static final int REQUEST_CODE = 1000;

    private MaterialEditText edtSerialNumber;
    private MaterialEditText edtFullName;
    private MaterialEditText edtCode;
    private MaterialEditText edtLeader;

    private MaterialEditText edtAddress;
    private MaterialEditText edtCompanyName;
    private MaterialEditText edtOwner;
    private MaterialEditText edtDescribe;

    private String strSerialNumber;
    private String strFullName;
    private String strCode;
    private String strLeader;

    private String strLocation;

    private Button btnTest;

    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    public Project project;
    private boolean editMode = true; //编辑模式/新建模式
    private String oldStrSerialNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_project_edit);
        context = this;
        setWindowAnimationStyle();
        if (getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            project = (Project) bundle.getSerializable(EXTRA_PROJECT);
            DBHelper dbHelper = DBHelper.getInstance(this);
            try {
                Dao<Project, String> dao = dbHelper.getDao(Project.class);
                project = dao.queryForId(project.getId());
                project.jieProject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            project = new Project(context);
            editMode = false;
        }

        init();
        initValue();
    }

    private void init() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_addNewProject);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        btnTest = (Button) this.findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSerialNumberTest();
            }
        });

        edtSerialNumber = (MaterialEditText) this.findViewById(R.id.edtSerialNumber);
        edtFullName = (MaterialEditText) this.findViewById(R.id.edtFullName);
        edtCode = (MaterialEditText) this.findViewById(R.id.edtCode);
        edtLeader = (MaterialEditText) this.findViewById(R.id.edtLeader);
        edtAddress = (MaterialEditText) this.findViewById(R.id.edtAddress);

        edtCompanyName = (MaterialEditText) this.findViewById(R.id.edtCompanyName);
        edtOwner = (MaterialEditText) this.findViewById(R.id.edtOwner);
        edtDescribe = (MaterialEditText) this.findViewById(R.id.edtDescribe);
        edtSerialNumber.clearFocus();
    }

    private void initValue() {
        oldStrSerialNumber = project.getSerialNumber();
        edtSerialNumber.setText(project.getSerialNumber());
        edtFullName.setText(project.getFullName());
        edtCode.setText(project.getCode());
        edtLeader.setText(project.getLeader());
        edtAddress.setText(project.getAddress());
        edtCompanyName.setText(project.getCompanyName());
        edtOwner.setText(project.getOwner());
        edtDescribe.setText(project.getDescribe());
    }

    /**
     * 为了解决退出动画无效问题
     */
    private void setWindowAnimationStyle() {
//        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
//        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
//        activityStyle.recycle();
//        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
//        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
//        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);//这货报错也能运行？？？
//        activityStyle.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.act_save:
                addProject();
                return true;
            case R.id.act_help:
                ToastUtil.showToastS(this, "未添加");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addProject() {
        strSerialNumber = edtSerialNumber.getText().toString();
        strFullName = edtFullName.getText().toString();
        strCode = edtCode.getText().toString();
        strLeader = edtLeader.getText().toString();
        strLocation = edtAddress.getText().toString();

        saveProject();
    }

    public void saveProject() {
        project.setSerialNumber(oldStrSerialNumber);
        project.setFullName(strFullName);
        project.setCode(strCode);
        project.setLeader(strLeader);
        project.setAddress(strLocation);

        project.setCompanyName(edtCompanyName.getText().toString());
        project.setOwner(edtOwner.getText().toString());
        project.setDescribe(edtDescribe.getText().toString());
        project.setUpdateTime(DateUtil.date2Str(new Date()));
        project.setHoleCount("0");
        project.setRecordPerson(Common.getUserIDBySP(context));
        DBHelper dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Project, String> dao = dbHelper.getDao(Project.class);
            if (editMode) {
                dao.update(project);
            } else {
                dao.create(project);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK, new Intent()); //这理有2个参数(int resultCode, Intent intent)
        finish();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    //关联操作
    private void relevance(String strSerialNumber) {
        showProgressDialog(false);
        Map<String, String> params = new HashMap<>();
        params.put("project.serialNumber", strSerialNumber);
        OkHttpUtils.post().params(params).url(Urls.GET_PROJECT_INFO_BY_KEY_POST).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("relevance---->>onError:" + e.getMessage());
                ToastUtil.showToastS(context, "网络链接错误");
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("relevance---->>onResponse:" + response);
                dismissProgressDialog();
                if (JsonUtils.isGoodJson(response)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        String result2 = jsonResult.getResult();
                        Project mProject = gson.fromJson(result2.toString(), Project.class);
                        showProjectInfo(mProject);
                    } else {
                        showStacked(jsonResult.getMessage());
                    }
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }
            }


            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                L.e("relevance---->>parseNetworkResponse:" + response.toString());
                return super.parseNetworkResponse(response, id);
            }

        });
    }


    //普通的对话框提示
    private void showStacked(String s) {
        new MaterialDialog.Builder(context).title("提示").content(s).positiveText("确认").btnStackedGravity(GravityEnum.CENTER).forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    //成功获取到项目信息后的提示
    private void showProjectInfo(final Project project) {

        final String address = project.getProName() + project.getCityName() + "" + project.getDisName() + project.getAddress();
        String s = "项目名称:" + project.getFullName() + "\n" + "负 责 人:" + project.getRealName() + "\n" + "项目编号:" + project.getCode() + "\n" + "勘察单位:" + project.getCompanyName() + "\n" + "项目业主:" + project.getOwner() + "\n" + "项目地点:" + address + "\n";

        new MaterialDialog.Builder(context).title("提示").content(s).positiveText("确认").btnStackedGravity(GravityEnum.CENTER).forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        edtSerialNumber.setText(project.getSerialNumber());
                        edtFullName.setText(project.getFullName());
                        edtCode.setText(project.getCode());
                        edtLeader.setText(project.getRealName());

                        edtAddress.setText(address);
                        edtCompanyName.setText(project.getCompanyName());
                        edtOwner.setText(project.getOwner());
                        edtDescribe.setText(project.getDescribe());
                        if (!TextUtils.isEmpty(oldStrSerialNumber) && !oldStrSerialNumber.equals(project.getSerialNumber())) {
                            reRelevance();
                        }
                        oldStrSerialNumber = project.getSerialNumber();
                    }
                }).show();
    }

    //关联成功，提示对话框确定R后后修改项目下所有上传状态，所有关联的点，取消关联
    private ProjectDao projectDao;
    private HoleDao holeDao;
    private RecordDao recordDao;
    private MediaDao mediaDao;

    private void reRelevance() {
        L.e("---->>>reRelevance");
        showProgressDialog(true);
        projectDao = new ProjectDao(this);
        holeDao = new HoleDao(this);
        recordDao = new RecordDao(this);
        mediaDao = new MediaDao(this);
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            project.setState("1");
            projectDao.updateState(project);
            holeDao.updateState(project.getId());
            recordDao.updateState(project.getId());
            mediaDao.updateState(project.getId());
            handler.sendMessage(new Message());
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgressDialog();
        }
    };

    private void strSerialNumberTest() {
        strSerialNumber = edtSerialNumber.getText().toString();

        if ("".equals(strSerialNumber)) {
            ToastUtil.showToastS(context, "请输入序列号");
        } else {
            int APNType = Common.getAPNType(context);
            if (APNType > 0) {
                relevance(strSerialNumber);
            } else {
                //没有网络给予提示
                ToastUtil.showToastS(context, "没有网络,请检查网络");
            }
        }
    }

}
