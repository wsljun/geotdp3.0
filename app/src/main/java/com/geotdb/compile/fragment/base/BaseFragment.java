package com.geotdb.compile.fragment.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.vo.Dictionary;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context context;
    protected Activity activity;
    protected String relateID;//用来添加自定义字典的relateID字段
    /**
     * 存放自定义的字典的集合
     */
    protected List<Dictionary> dictionaryList = new ArrayList<>();
    public void startOnResume() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        L.d(TAG, "Fragment ->onCreate");
        context = this.getActivity();
        activity = this.getActivity();
        super.onCreate(savedInstanceState);
        relateID = Common.getUserIDBySP(context);
    }

    @Override
    public void onStart() {
        L.d(TAG, "Fragment ->onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        L.d(TAG, "Fragment ->onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        L.d(TAG, "Fragment ->onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        L.d(TAG, "Fragment ->onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        L.d("BaseFragment", "Fragment ->onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        L.d("BaseFragment", "Fragment ->onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        L.d("BaseFragment", "Fragment ->onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        L.d("BaseFragment", "Fragment ->onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        L.d("BaseFragment", "Fragment ->onDetach");
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        L.d("BaseFragment", "Fragment ->onSaveInstanceState");
    }

    /**
     * 根据sort查询字典库的sql
     * @param sqlName
     * @return
     */
    public String getSqlString(String sqlName) {
        return "select rowid as _id,name  from dictionary where sort='" + sqlName + "' order by cast(sortNo as int)";
    }




}
