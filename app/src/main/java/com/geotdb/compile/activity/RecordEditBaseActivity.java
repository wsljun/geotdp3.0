/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geotdb.compile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.fragment.LocationFragment;
import com.geotdb.compile.fragment.MediaListFragment;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.Layer;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;

import java.util.List;

public class RecordEditBaseActivity extends AppCompatActivity {
    public Context context;

    public static final String EXTRA_RECORD = "record";
    public static final String EXTRA_HOLE = "hole";
    public static final String EXTRA_RECORD_TYPE = "recordType";

    public static final String EXTRA_HOLEID = "recordID";

    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    public Toolbar toolbar;
    public MediaListFragment mediaListFragment;
    public LocationFragment locationFragment;

    public Hole hole;
    public Record record;
    public String recordType = "";
    public boolean editMode = true; //编辑模式/新建模式

    HoleDao holeDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle.containsKey(EXTRA_HOLE)) {
            hole = (Hole) bundle.getSerializable(EXTRA_HOLE);
        }
        if (bundle.containsKey(EXTRA_HOLEID)) {
            String holeID = bundle.getString(EXTRA_HOLEID);
            holeDao = new HoleDao(this);
            hole = holeDao.queryForId(holeID);
        }
        if (bundle.containsKey(EXTRA_RECORD_TYPE)) {
            recordType = bundle.getString(EXTRA_RECORD_TYPE);
        }
        Bundle b = new Bundle();
        b.putSerializable(EXTRA_HOLE, hole);
        locationFragment = new LocationFragment();
        locationFragment.setArguments(b);
        FragmentManager fm = getSupportFragmentManager();
        //向detailFragment传入参数
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.locationFragment, locationFragment, "locationFragment");
        transaction.commit();
    }


    public void initView() {
        L.e("-->>RecordEditBaseActivity--initView");
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MediaListFragment.KEY_RECORD, record);

            mediaListFragment = new MediaListFragment();
            mediaListFragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.mediaListFragment, mediaListFragment, "mediaListFragment");
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateLocation() {
        locationFragment.location();

    }

    public String getLocationTime() {
        return locationFragment.edtLocationTime.getText().toString();

    }


    /**
     * 为了解决退出动画无效问题
     */
    public void setWindowAnimationStyle() {
//        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
//        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
//        activityStyle.recycle();
//        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
//        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
//        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
//        activityStyle.recycle();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_record, menu);
        return true;
    }

    //监听退去但是没有监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    public void cancel() {
        if ("0".equals(record.getState())) {
            record.delete(context);
            clearMediaList();
        }
    }

    @Override
    public void finish() {
        cancel();
        super.finish();
//        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.DEFAULT_KEYS_DIALER) {
            mediaListFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void clearMediaList() {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Media, String> mediaDao = dbHelper.getDao(Media.class);
            List<Media> mediaList = mediaDao.queryBuilder().where().eq("recordID", record.getId()).and().eq("state", "0").query();
            for (Media media : mediaList) {
                mediaDao.delete(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMediaList() {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Media, String> mediaDao = dbHelper.getDao(Media.class);
            List<Media> mediaList = mediaDao.queryBuilder().where().eq("recordID", record.getId()).and().eq("state", "0").query();
            for (Media media : mediaList) {
                media.setState("1");
                mediaDao.update(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateHoleState() {
        HoleDao holeDao = new HoleDao(context);
        hole.setState("1");
        holeDao.update(hole);
    }

//    public List<Media> getMediaList() {
//        List<Media> mediaList = mediaListFragment.mediaAdapter.getList();
//        if (mediaList.size() > 0) {
//            mediaList.remove(0); //被添加按钮占位的第一位
//        }
//
//        return mediaList;
//    }

    public AMapLocation getAMapLocation() {
        AMapLocation amapLocation = locationFragment.aMapLocation;
        return amapLocation;
    }


    public interface OnGetValueListener {
        Layer getValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.e("-->>RecordEditBaseActivity--onResume");
        mediaListFragment.onRefreshList();
    }

}
