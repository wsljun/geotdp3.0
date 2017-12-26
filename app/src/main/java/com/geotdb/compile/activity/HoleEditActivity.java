package com.geotdb.compile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.adapter.RelateHoleAdapter;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.dialog.RelateHoleDialog;
import com.geotdb.compile.fragment.HoleLocationFragment;
import com.geotdb.compile.fragment.HoleSceneFragment;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.view.MaterialEditTextElevation;
import com.geotdb.compile.view.MaterialEditTextMeter;
import com.geotdb.compile.view.MaterialEditTextNoEmoji;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Project;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.qiujuer.genius.ui.widget.Button;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class HoleEditActivity extends BaseAppCompatActivity {
    public static final String EXTRA_HOLE = "hole";
    public static final String EXTRA_PROJECT = "project";
    public static final int REQUEST_CODE = 2000;

    private Hole hole;
    private Project project;
    private MaterialEditTextNoEmoji edtCode;
    private MaterialEditTextNoEmoji edtCode_test;
    private MaterialBetterSpinner sprType;
    private MaterialEditTextElevation edtElevation;
    private MaterialEditTextElevation edtDepth;
    private MaterialEditTextMeter edtRadius;
    private Button btnLocation;
    private LinearLayout lltLocation;
    private MaterialEditText edtMapLatitude;
    private MaterialEditText edtMapLongitude;
    private MaterialEditText edtMapTime;

    private FrameLayout fltHoleSceneFragment;

    private String strCode;
    private String strType;
    private String strElevation;
    private String strDepth;
    private String strRadius;
    private String strDescription;

    //勘察点编辑界面的定位片
    HoleLocationFragment holeLocationFragment;
    //添加机长描述等入口片
    HoleSceneFragment holeSceneFragment;

    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    private boolean editMode = true; //编辑模式/新建模式
    private MyCount mc;//倒计时类
    private AMapLocation aMapLocation;//定位信息实体类
    private List<DropItemVo> operateList;

    private String oldCode = "";//不管是修改还是添加，进来获取当前code，与改变之后的比较
    private boolean winCode = true;//当前勘察点是否重复

    private MaterialEditText relate_code;//关联的勘察点编号
    private Button doRelate;//做关联操作

    private HoleDao holeDao;
    private ProjectDao projectDao;
    private RelateHoleDialog relateHoleDialog;
    private List<Hole> relateList;//获取的勘察点的列表
    private Hole relateHole;//存放关联的勘察点
    private List<LocalUser> relateUserList;//存放关联获取勘察点有那些人关联过

    private CardView hole_description_cv;
    private RelativeLayout hole_description_rl;
    private ImageView hole_description_iv;
    private TextView hole_description_tv;
    private boolean showDescription = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_hole_edit);
        initView();
        loadData();
    }

    private void initView() {
        Common.setTimeZone(this);//默认时区为北京时区
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_addNewHole);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        sprType = (MaterialBetterSpinner) this.findViewById(R.id.sprType);
        sprType.setAdapter(context, getSprType(), MaterialBetterSpinner.MODE_CUSTOM);
        sprType.setOnItemClickListener(sprTypeListener);

        edtCode = (MaterialEditTextNoEmoji) this.findViewById(R.id.edtCode);
        edtCode_test = (MaterialEditTextNoEmoji) this.findViewById(R.id.edtCode_test);
        edtElevation = (MaterialEditTextElevation) this.findViewById(R.id.edtElevation);
        edtDepth = (MaterialEditTextElevation) this.findViewById(R.id.edtDepth);

        edtRadius = (MaterialEditTextMeter) this.findViewById(R.id.edtRadius);
        btnLocation = (Button) this.findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(locationListener);

        lltLocation = (LinearLayout) this.findViewById(R.id.lltLocation);
        fltHoleSceneFragment = (FrameLayout) this.findViewById(R.id.fltHoleSceneFragment);
        edtMapLatitude = (MaterialEditText) this.findViewById(R.id.edtMapLatitude);
        edtMapLongitude = (MaterialEditText) this.findViewById(R.id.edtMapLongitude);
        edtMapTime = (MaterialEditText) this.findViewById(R.id.edtMapTime);

        relate_code = (MaterialEditText) findViewById(R.id.relate_code);
//        relate_code.setOnClickListener(relateListenr);
        doRelate = (Button) findViewById(R.id.doRelate);
        doRelate.setOnClickListener(doRelateListener);

        relateList = new ArrayList<>();
        relateHoleDialog = new RelateHoleDialog();
        relateHoleDialog.OnListItemListener(relateListListener);
        relateHole = new Hole();
        holeDao = new HoleDao(this);
        projectDao = new ProjectDao(this);
        relateUserList = new ArrayList<>();

        //技术要求
        hole_description_cv = (CardView) findViewById(R.id.hole_description_cv);
        hole_description_rl = (RelativeLayout) findViewById(R.id.hole_description_rl);
        hole_description_rl.setOnClickListener(showDes);
        hole_description_iv = (ImageView) findViewById(R.id.hole_description_iv);
        hole_description_tv = (TextView) findViewById(R.id.hole_description_tv);

    }

    /**
     * 加载数据放在子线程里面完成、之后在更新UI
     */
    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getIntent().getExtras();
                //编辑点
                if (bundle.containsKey(EXTRA_HOLE)) {
                    editMode = true;
                    hole = (Hole) bundle.getSerializable(EXTRA_HOLE);
                    hole = holeDao.queryForId(hole.getId());
                    project = projectDao.queryForId(hole.getProjectID());
                } else {//新建点
                    project = (Project) bundle.getSerializable(EXTRA_PROJECT);
                    hole = new Hole(context, project.getId());
                    editMode = false;
                }
                handler.sendMessage(new Message());

            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initValue();
            //默认加载钻机
            setHoleSceneFragment(hole.getType());
            setHoleLocationFragment();
            mc = new MyCount(10000, 1000);
            mc.start();
        }
    };

    /**
     * 初始化数据
     */
    private void initValue() {
        if ("1".equals(hole.getLocationState())) {
            btnLocation.setVisibility(View.VISIBLE);
            lltLocation.setVisibility(View.GONE);
            fltHoleSceneFragment.setVisibility(View.GONE);
        } else {
            btnLocation.setVisibility(View.GONE);
            lltLocation.setVisibility(View.VISIBLE);
            fltHoleSceneFragment.setVisibility(View.VISIBLE);
        }
        if (hole.getRelateCode() != null && !"".equals(hole.getRelateCode())) {
            edtCode.setFloatingLabelText("关联的勘探点");
            edtCode.setText(hole.getRelateCode());
            edtCode.setEnabled(false);
            if (hole.getDescription() != null && !"".equals(hole.getDescription())) {
                hole_description_cv.setVisibility(View.VISIBLE);
                hole_description_tv.setText(hole.getDescription());
                L.e("---->>>de:" + hole.getDescription());
            } else {
                hole_description_cv.setVisibility(View.GONE);
            }
        } else {
            edtCode.setText(hole.getCode());
            hole_description_cv.setVisibility(View.GONE);
            edtCode.addTextChangedListener(edtCodeChangeListener);//可能是关联的勘探点，不需要重复的判断了
        }

        // 获取编辑框焦点,打开软键盘
        edtCode_test.requestFocus();
        oldCode = hole.getCode();
        sprType.setText(hole.getType());
        edtElevation.setText(hole.getElevation());
        edtDepth.setText(hole.getDepth());
        edtRadius.setText(hole.getRadius());

        edtMapLatitude.setText(hole.getMapLatitude());
        edtMapLongitude.setText(hole.getMapLongitude());
        edtMapTime.setText(hole.getMapTime());

        btnLocation.setEnabled(false);
//        relate_code.setText(hole.getRelateCode());
    }

    /**
     * 技术要求显示与隐藏
     */
    View.OnClickListener showDes = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (showDescription) {
                hole_description_tv.setVisibility(View.GONE);
                showDescription = false;
            } else {
                hole_description_tv.setVisibility(View.VISIBLE);
                showDescription = true;
            }
        }
    };

    /**
     * 点击获取关联勘察点列表
     */
    View.OnClickListener relateListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doRelate();
        }
    };
    /**
     * 与上边是同一操作
     */
    View.OnClickListener doRelateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doRelate();
        }
    };

    MaterialBetterSpinner.OnItemClickListener sprTypeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            setHoleSceneFragment(sprTypeList.get(i).getName());
        }
    };

    /**
     * 判断该勘察点的项目是否关联，是否有序列号
     */
    private void doRelate() {
        String serialNumber = projectDao.queryStrForId(hole.getProjectID());
        if ("".equals(serialNumber) || serialNumber == null) {
            ToastUtil.showToastS(HoleEditActivity.this, "所在项目未关联");
        } else {
            getHoleListForIntrnet(serialNumber);
        }
    }

    /**
     * 获取勘察点列表
     */
    public void getHoleListForIntrnet(String serialNumber) {
        showProgressDialog(false);
        Map<String, String> params = new HashMap<>();
        params.put("serialNumber", serialNumber);
        OkHttpUtils.post().url(Urls.GET_RELATE_HOLE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                relateList = null;
                L.e("getHoleListForIntrnet--onError-->>" + e.getMessage());
                ToastUtil.showToastS(context, "获取勘察点失败");
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("getHoleListForIntrnet--response-->>" + response);
                dismissProgressDialog();
                if (JsonUtils.isGoodJson(response)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        relateList = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Hole>>() {
                        }.getType());
                        if (relateList != null && relateList.size() > 0) {
                            relateHoleDialog.show(HoleEditActivity.this, relateList, RelateHoleAdapter.HAVE_NOALL);
                        } else {
                            ToastUtil.showToastS(context, "服务端未创建勘察点，无法关联");
                        }
                    } else {
                        ToastUtil.showToastS(context, jsonResult.getMessage());
                    }
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }

            }
        });
    }

    /**
     * 获取勘察点关联列表后的点击事件
     */
    RelateHoleDialog.OnListItemListener relateListListener = new RelateHoleDialog.OnListItemListener() {
        @Override
        public void onItemClick(int position) {
            //点击勘察点列表得到relateHole
            if(relateHoleDialog.holeList==null){
                relateHole = relateList.get(position);
            }else{
                relateHole = relateHoleDialog.holeList.get(position);
            }
            if (relateHole.getUserList() != null && relateHole.getUserList().size() > 0) {
                relateUserList = relateHole.getUserList();
            } else {
                relateUserList.clear();
            }
            relateHoleDialog.dismiss();
            //遍历数据库，查找是否关联
            if(holeDao.checkRelated(relateHole.getId(),hole.getProjectID())){
                ToastUtil.showToastS(context, "该发布点本地已经存在关联");
            }else{
                doRelateForIntrnet(relateHole.getId());
            }
        }
    };


    /**
     * doRelate操作
     */
    private void doRelateForIntrnet(String relateID) {
        showProgressDialog(false);
        Map<String, String> params = new HashMap<>();
        params.put("userID", Common.getUserIDBySP(this));
        params.put("relateID", relateID);
        params.put("holeID", hole.getId());
        L.e("------>>>params:" + params.toString());
        OkHttpUtils.post().url(Urls.DO_RELATE_HOLE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastS(context, "关联勘察点失败");
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("response----------" + response);
                dismissProgressDialog();
                //response拿到了hole的所有信息，从这里判断
                if (JsonUtils.isGoodJson(response)) {
                    Gson gson = new Gson();
                    Hole h = gson.fromJson(response.toString(), Hole.class);
                    //修改界面
                    edtCode.setText(h.getCode());
                    edtCode.setFloatingLabelText("关联的勘探点");
                    edtCode.setEnabled(false);
                    if (h.getDepth() != null) {
                        edtDepth.setText(h.getDepth());
                        hole.setDepth(h.getDepth());
                    }
                    if (h.getElevation() != null) {
                        edtElevation.setText(h.getElevation());
                        hole.setElevation(h.getElevation());
                    }
                    if (h.getDescription() != null) {
                        hole_description_cv.setVisibility(View.VISIBLE);
                        hole_description_tv.setText(h.getDescription());
                        hole.setDescription(h.getDescription());
                        L.e("---->>>de:" + h.getDescription());
                    } else {
                        hole_description_cv.setVisibility(View.GONE);
                    }
                    hole.setRelateID(h.getId());
                    hole.setRelateCode(h.getCode());
                    updataHoleNoMap(false);
                    ToastUtil.showToastS(context, "勘察点关联成功");
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }


            }
        });

    }

    /**
     * 是否获取关联勘察点的记录信息的dialog
     * 這裡不做獲取操作了
     */
    private void downloadHoleDialog() {
        String content;
        if (relateUserList != null && relateUserList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("已关联人员:" + "\n");
            for (LocalUser localUser : relateUserList) {
                sb.append("  -" + localUser.getRealName());
                if (localUser.getMobilePhone() != null && !"".equals(localUser.getMobilePhone())) {
                    sb.append("  phone:" + localUser.getMobilePhone());
                } else {
                    sb.append("  phone:无");
                }
                sb.append("\n");
            }
            content = sb.toString();
        } else {
            content = "当前未被关联";
        }

        new MaterialDialog.Builder(this).title(relateHole.getCode()).content(content).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                //获取关联勘察点记录
            }
        }).show();
    }

    /**
     * 定位按钮点击事件
     */
    View.OnClickListener locationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Common.gPSIsOPen(HoleEditActivity.this)) {
                location();
            } else {
                ToastUtil.showToastS(HoleEditActivity.this, "GPS未开启，请开启以提高精度");
            }
        }
    };

    /**
     * 当前code改变时验证是否重复
     */
    TextWatcher edtCodeChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(oldCode)) {
                //判断是否改变了code
                String newCode = edtCode.getText().toString();
                if (!oldCode.equals(newCode)) {
                    if (TextUtils.isEmpty(newCode)) {
                        winCode = false;
                        edtCode.setError("请输入勘察点编号");
                    } else {
                        //根据查询结果判断是否有一样的code
                        List<Hole> list = hole.getHoleByCode(HoleEditActivity.this, newCode, hole.getProjectID());
                        if (null == list || list.size() == 0) {
                            winCode = true;
                        } else {
                            winCode = false;
                            edtCode.setError("勘察点编号重复");
                        }
                    }
                }
            }

        }
    };


    /**
     * 地图fragment
     */
    private void setHoleLocationFragment() {
        try {
            holeLocationFragment = new HoleLocationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putSerializable(HoleLocationFragment.EXTRA_HOLE, hole);
//            bundle.putBoolean();
            holeLocationFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fltHoleLocationFragment, holeLocationFragment, "type");
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 場景等信息的fragment
     */
    private void setHoleSceneFragment(String holeType) {
        try {
            holeSceneFragment = new HoleSceneFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            //向detailFragment传入参数hole
            bundle.putSerializable(HoleLocationFragment.EXTRA_HOLE, hole);
            //传入参数区分是钻孔、探井
            //向detailFragment传入参数
            bundle.putString("holeType", holeType);
            holeSceneFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fltHoleSceneFragment, holeSceneFragment, "type");
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<DropItemVo> sprTypeList;

    public List<DropItemVo> getSprType() {
        sprTypeList = new ArrayList<>();
        sprTypeList.add(new DropItemVo("1", "钻孔"));
        sprTypeList.add(new DropItemVo("2", "探井"));
        sprTypeList.add(new DropItemVo("3", "先井后钻"));

        return sprTypeList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_hole, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            clearHole();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                clearHole();
                finish();
                return true;
            case R.id.act_save:
                add(true);
                return true;
            case R.id.act_help:
                ToastUtil.showToastS(this, "未添加");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存，hole基础信息
     */
    public void add(boolean doFinish) {
        //判断是否是自己的项目
        if (hole.getUserID() == null || hole.getUserID().equals("") || hole.getUserID().equals(Common.getUserIDBySP(this))) {
            strCode = edtCode.getText().toString();
            strType = sprType.getText().toString();
            strCode = edtCode.getText().toString();
            strElevation = edtElevation.getText().toString();
            strDepth = edtDepth.getText().toString();
            strRadius = edtRadius.getText().toString();
            strDescription = hole_description_tv.getText().toString();

            hole.setDescription(strDescription);
//            hole.setCode(strCode);
            hole.setType(strType);
            hole.setElevation(strElevation);
            hole.setDepth(strDepth);
            hole.setRadius(strRadius);
            hole.setUpdateTime(DateUtil.date2Str(new Date()));
            save(doFinish);
        } else {
            ToastUtil.showToastS(this, "不能编辑其他人项目");
        }

    }

    /**
     * 保存，获取map信息
     */
    public void save(boolean doFinish) {
        if (TextUtils.isEmpty(edtCode.getText().toString())) {
            ToastUtil.showToastS(this, "请输入勘探点编号");
        } else {
            if (winCode) {
                if (!hole.getLocationState().equals("0")) {
                    if (aMapLocation != null) {
                        hole.setMapLatitude(edtMapLatitude.getText().toString());
                        hole.setMapLongitude(edtMapLongitude.getText().toString());
                        hole.setMapTime(edtMapTime.getText().toString());
                        hole.setLocationState("0");
                    }
                }
                updataHoleNoMap(doFinish);
            } else {
                ToastUtil.showToastS(this, "勘探点编号编辑有误");
            }

        }
    }

    /**
     * 修改hole状态，返回参数给之前的activity
     */
    public void updataHoleNoMap(boolean doFinish) {
        try {
            hole.setState("1");
            if (editMode) {
                holeDao.update(hole);//修改记录时保存原有记录
            } else {
                holeDao.add(hole);
                project.setHoleCount2Int(project.getHoleCount2Int() + 1);
            }
            project.setUpdateTime(DateUtil.date2Str(new Date())+"");
            projectDao.update(project);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (doFinish) {
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

    /**
     * 新建点，未保存，删除其子记录
     * 在定位的时候保存了勘察点，
     */
    public void clearHole() {
        if ("0".equals(hole.getState())) {
            hole.delete(context);
        }
    }

    /**
     * 定位基准点操作，成功后显示场景等布局
     */
    public void location() {
        try {
            aMapLocation = holeLocationFragment.location();
            if (aMapLocation != null) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                edtMapLatitude.setText(String.valueOf(aMapLocation.getLatitude()));
                edtMapLongitude.setText(String.valueOf(aMapLocation.getLongitude()));
                edtMapTime.setText(String.valueOf(df.format(new Date(aMapLocation.getTime()))));
                btnLocation.setVisibility(View.GONE);
                lltLocation.setVisibility(View.VISIBLE);
                fltHoleSceneFragment.setVisibility(View.VISIBLE);
                //定位成功才可以进行 另四项的编写,从hole里面获取坐标来判断是否定位
                hole.setMapLatitude(String.valueOf(aMapLocation.getLatitude()));
                hole.setMapLongitude(String.valueOf(aMapLocation.getLongitude()));
                //定位成功后停止定位
                holeLocationFragment.stop();
                add(false);
            } else {
                ToastUtil.showToastS(this, "定位失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnLocation.setEnabled(true);
            btnLocation.setText("定位钻孔");

        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnLocation.setText("定位钻孔(" + millisUntilFinished / 1000 + ")...");
        }
    }

}
