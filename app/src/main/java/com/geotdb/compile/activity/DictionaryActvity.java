package com.geotdb.compile.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.adapter.DictionaryAdapter;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.utils.JsonUtils;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DictionaryDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.JsonResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 字典管理
 */
public class DictionaryActvity extends BaseAppCompatActivity {
    private Toolbar toolbar;
    private List<Dictionary> dictionaryList;
    private DictionaryDao dictionaryDao;
    private RecyclerView dictionary_list;
    private DictionaryAdapter adapter;
    private List<Dictionary> list;

    private boolean isSelectAll;

    private Map<String, String> map;
    private DictionaryDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting_dictionary);
        initView();
        initData();
//        IntentFilter downloadFilter = new IntentFilter(Urls.DICTIONARY_DOWNLOAD_COMPLETE);
//        registerReceiver(downloadReceiver, downloadFilter);
//        IntentFilter uploadFilter = new IntentFilter(Urls.DICTIONARY_UPLOAD_COMPLETE);
//        registerReceiver(uploadReceiver, uploadFilter);

        map = new HashMap<>();
        dao = new DictionaryDao(context);
    }

//    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            initList();
//            dismissProgressDialog();
//        }
//    };
//    BroadcastReceiver uploadReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            dismissProgressDialog();
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(downloadReceiver);
//        unregisterReceiver(uploadReceiver);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.dictionary_toolbar);
        toolbar.setTitle("字典库管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dictionary_list = (RecyclerView) findViewById(R.id.dictionary_list);
        dictionary_list.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        dictionaryList = new ArrayList<>();
        dictionaryDao = new DictionaryDao(this);
        list = new ArrayList<>();
        initList();
    }

    private void initList() {
        isSelectAll = false;
        dictionaryList.clear();
        list.clear();
        dictionaryList = dictionaryDao.getDictionary();
        if (dictionaryList != null) {
            adapter = new DictionaryAdapter(this, dictionaryList);
            dictionary_list.setAdapter(adapter);
        }
        setListener();
    }

    private void setListener() {
        adapter.setOnItemListener(new DictionaryAdapter.OnItemListener() {
            @Override
            public void checkBoxClick(int position) {
                addOrRemove(position);
            }

            @Override
            public void onItemClick(int position) {
                addOrRemove(position);
            }
        });
    }

    private void addOrRemove(int position) {
        if (list.contains(adapter.getItem(position))) {
            list.remove(adapter.getItem(position));
        } else {
            list.add(adapter.getItem(position));
        }
    }

    private void delete() {
        if (list != null && list.size() > 0) {
            deleteDialog();
        } else {
            ToastUtil.showToastS(this, "当前没有自定义词库");
        }
    }

    private void selectAll() {
        if (dictionaryList != null && dictionaryList.size() > 0) {
            if (!isSelectAll) {
                isSelectAll = true;
                for (int i = 0; i < dictionaryList.size(); i++) {
                    addOrRemove(i);
                    dictionaryList.get(i).isSelect = true;
                }
                adapter.notifyDataSetChanged();
            } else {
                isSelectAll = false;
                for (int i = 0; i < dictionaryList.size(); i++) {
                    addOrRemove(i);
                    dictionaryList.get(i).isSelect = false;
                }
                adapter.notifyDataSetChanged();
            }
        } else {
            ToastUtil.showToastS(this, "当前没有自定义词库");
        }
    }

    public void upload() {
        if (dictionaryList != null && dictionaryList.size() > 0) {
            uploadDialog();
        } else {
            ToastUtil.showToastS(this, "当前没有自定义词库");
        }
    }

    public void download() {
        downloadDialog();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dictionary_delete:
                delete();
                break;
            case R.id.dictionary_all:
                selectAll();
                break;
            case R.id.dictionary_download:
                download();
                break;
            case R.id.dictionary_upload:
                upload();
                break;
        }
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

    /**
     * 删除确认
     */
    private void deleteDialog() {
        new MaterialDialog.Builder(this).content("确认删除选中词库").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                dictionaryDao.deleteByDicList(list);
                initList();
            }
        }).show();
    }

    /**
     * 下载词库确认
     */

    private void downloadDialog() {
        final String relateID = Common.getUserIDBySP(DictionaryActvity.this);
        if (!TextUtils.isEmpty(relateID)) {
            new MaterialDialog.Builder(this).content("下载关联词库，将删除本地词库，是否下载？").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    downloadDictionary(relateID);
                }
            }).show();
        } else {
            ToastUtil.showToastS(this, "请先登陆");
        }
    }

    /**
     * 上传词库确认
     */
    private void uploadDialog() {
        new MaterialDialog.Builder(this).content("上传本地词库，将覆盖云端备份，是否上传？").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                uploadDictionary(dictionaryList);
            }
        }).show();
    }


    public void downloadDictionary(final String relateID) {
        showProgressDialog(false);
        map.put("relateID", relateID);
        OkHttpUtils.post().url(Urls.DICTIONARY_DOWNLOAD).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastS(context, "下载失败");
                L.e("downloadDictionary--onError-->>" + e.getMessage());
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                L.e("downloadDictionary--onResponse-->>" + response);
                if (JsonUtils.isGoodJson(response)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        try {
                            L.e("-------result-----" + jsonResult.getResult());
                            List<Dictionary> list = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Dictionary>>() {
                            }.getType());
                            dao.deleteAll();
                            dao.addDictionaryList(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        L.e("TAG", "downloadDictionary--onResponse-->>数据为空");
                        ToastUtil.showToastS(context, "数据为空");
                    }
                    initList();
                } else {
                    ToastUtil.showToastS(context, "服务器异常，请联系客服");
                }

            }
        });
    }

    public void uploadDictionary(List<Dictionary> list) {
        L.e("list.size=" + list.size());
        map = getMap(list);
        L.e("map.size=" + map.size());
        showProgressDialog(false);
        OkHttpUtils.post().url(Urls.DICTIONARY_UPLOAD).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastS(context, "上传失败");
                L.e("uploadDictionary--onError-->>" + e.getMessage());
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                ToastUtil.showToastS(context, "上传成功");
                L.e("uploadDictionary--onResponse-->>" + response);
                dismissProgressDialog();
                initList();
            }
        });
    }


    private Map<String, String> getMap(List<Dictionary> list) {
        for (int i = 0; i < list.size(); i++) {
            map.put("dictionary[" + i + "].name", list.get(i).getName());
            map.put("dictionary[" + i + "].relateID", list.get(i).getRelateID());
            map.put("dictionary[" + i + "].type", list.get(i).getType());
            map.put("dictionary[" + i + "].sort", list.get(i).getSort());
            map.put("dictionary[" + i + "].sortNo", list.get(i).getSortNo());
            map.put("dictionary[" + i + "].form", list.get(i).getForm());
        }
        return map;
    }
}
