package com.geotdb.compile.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.adapter.MyViewPagerAdapter;
import com.geotdb.compile.vo.JsonResult;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.fragment.PreviewCountFragment;
import com.geotdb.compile.fragment.PreviewShowFragment;
import com.geotdb.compile.service.UploadService;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Project;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.qiujuer.genius.ui.widget.Button;

import okhttp3.Call;
import okhttp3.Request;

/**
 *勘探点记录预览
 */
public class PreviewActivity extends AppCompatActivity {
    public static final String EXTRA_HOLE = "hole";
    private Hole hole;

    private PreviewCountFragment CountFragment;
    private PreviewShowFragment ShowFragment;

//    private Button button;
    boolean isUpload = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //接收传递的信息
        Bundle bundle = this.getIntent().getExtras();
        hole = (Hole) bundle.getSerializable(EXTRA_HOLE);
        //由于传来的hole不完整，所有再次查询完整的hole
        DBHelper dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            hole = dao.queryForId(hole.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.act_preview);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.preview_toolBar);
        mToolbar.setTitle("预览相关");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        button = (Button) findViewById(R.id.preview_button);
//        button.setOnClickListener(btnListener);
//        button.setVisibility(hole.getState().equals("3") ? View.GONE : View.VISIBLE);
        L.e("TAG", "hole.getState()-->>" + hole.getState());
        //向两个fragmnent都传递hole
        Bundle bh = new Bundle();
        bh.putSerializable(EXTRA_HOLE, hole);
        CountFragment = PreviewCountFragment.newInstance();
        CountFragment.setArguments(bh);
        ShowFragment = PreviewShowFragment.newInstance();
        ShowFragment.setArguments(bh);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.preview_viewpager);
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(CountFragment, "统计");//添加Fragment
        viewPagerAdapter.addFragment(ShowFragment, "展示");
        mViewPager.setAdapter(viewPagerAdapter);//设置适配器

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.preview_tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("TabOne"));//给TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText("TabTwo"));
        mTabLayout.setupWithViewPager(mViewPager);//给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestEnd();
        }
    };

    private void requestEnd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isUpload = new HoleDao(PreviewActivity.this).checkIsUpload(hole.getId());
                handler.sendMessage(new Message());
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isUpload) {
                showSubmitDialog();
            } else {
//                ToastUtil.showToastS(PreviewActivity.this, "还有内容未上传");
                //添加上传按钮
                Project project = new ProjectDao(PreviewActivity.this).queryForId(hole.getProjectID());
                if (!"".equals(project.getSerialNumber())) {
                    if (Integer.parseInt(hole.getLocationState()) == 0) {
                        finishDialog();
                    } else {
                        ToastUtil.showToastL(PreviewActivity.this, "勘察点没有定位");
                    }
                } else {
                    ToastUtil.showToastL(PreviewActivity.this, "该项目没有序列号");
                }
            }
        }
    };

    //提示是否上传对话框
    private void finishDialog() {
        new MaterialDialog.Builder(this).content("还有内容未上传,是否立即上传钻孔数据?").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
//                new UploadService(PreviewActivity.this).uploadHole(hole);
            }
        }).show();
    }

    private MaterialDialog mDialog;

    //显示检验序列号的对话框R.string.serialNumberTest_progress_dialog R.string.please_wait
    public void showProgressDialog(Context context, int title, int content, boolean progress, int max) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(context).title(title).content(content).progress(progress, max).progressIndeterminateStyle(false).build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    //销毁检验序列号的对话框
    public void dismissProgressDialog() {
        mDialog.dismiss();
    }

    public void doCommit() {
        L.e("TAG", "id--" + hole.getId());
        String serialNumber = new ProjectDao(this).queryForId(hole.getProjectID()).getSerialNumber();
//        Map<String, String> params = new HashMap<>();
//        params = hole.getNameValuePairMap(serialNumber);
        OkHttpUtils.post().url(Urls.GET_HOLE_SUBMIT + "/" + hole.getId()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("TAG", "onError:id=" + id + "--e:" + e.getMessage());
                ToastUtil.showToastS(PreviewActivity.this, "网络链接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "onResponse:response=" + response + "--id=" + id);
                Gson gson = new Gson();
                JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                if (jsonResult.getStatus()) {
                    hole.setState("3");
                    new HoleDao(PreviewActivity.this).update(hole);
//                    button.setVisibility(View.GONE);
                }
                ToastUtil.showToastS(PreviewActivity.this, jsonResult.getMessage());
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showProgressDialog(PreviewActivity.this, R.string.title_reminder, R.string.please_wait, true, 0);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissProgressDialog();
            }
        });
    }

    private void showSubmitDialog() {
        new MaterialDialog.Builder(this).content(R.string.preview_dig_con).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                doCommit();
            }
        }).show();
    }

}
