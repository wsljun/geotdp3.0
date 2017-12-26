///*
// * Copyright (C) 2015 The Android Open Source Hole
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.geotdb.compile.fragment;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.geotdb.compile.activity.HoleEditActivity;
//import com.geotdb.compile.activity.PreviewActivity;
//import com.geotdb.compile.activity.base.MyApplication;
//import com.geotdb.compile.dialog.RelateHoleDialog;
//import com.geotdb.compile.utils.L;
//import com.geotdb.compile.dialog.HoleInfoDialog;
//import com.geotdb.compile.activity.RecordListActivity;
//import com.geotdb.compile.activity.HoleListActivity;
//import com.geotdb.compile.R;
//import com.geotdb.compile.service.UploadService;
//import com.geotdb.compile.fragment.base.BaseFragment;
//import com.geotdb.compile.utils.Urls;
//import com.geotdb.compile.db.DBHelper;
//import com.geotdb.compile.db.HoleDao;
//import com.geotdb.compile.db.ProjectDao;
//import com.geotdb.compile.slide.AbstractSlideExpandableListAdapter;
//import com.geotdb.compile.utils.ToastUtil;
//import com.geotdb.compile.vo.Dictionary;
//import com.geotdb.compile.vo.Hole;
//import com.geotdb.compile.vo.JsonResult;
//import com.geotdb.compile.vo.Project;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.dao.GenericRawResults;
//import com.j256.ormlite.dao.RawRowMapper;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import net.qiujuer.genius.ui.widget.Button;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
//import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
//import okhttp3.Call;
//
//public class HoleListFragment extends BaseFragment {
//
//    public static final String KEY_PROJECT_ID = "HoleListFragment:projectID";
//
//    private RecyclerView recyclerView;
//
//    String projectID;
//    List<Hole> list;
//    ItemAdapter itemAdapter;
//
//    private LinearLayoutManager manager;
//    private int page = 1;//页数
//    private int count;//总记录数
//
//    private SwipeRefreshLayout swipeRefreshLayout;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments().containsKey(KEY_PROJECT_ID)) {
//            projectID = getArguments().getString(KEY_PROJECT_ID);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.frt_hole_list, container, false);
//        recyclerView = (RecyclerView) view.findViewById(R.id.hole_recyclerview);
//        manager = new LinearLayoutManager(recyclerView.getContext());
//        recyclerView.setLayoutManager(manager);
//        list = getList(projectID, page);
//        itemAdapter = new HoleListFragment.ItemAdapter(getActivity(), list);
//        recyclerView.setAdapter(itemAdapter);
//
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                HoleListActivity a = (HoleListActivity) getActivity();
//                if (Math.abs(dy) > 4) {
//                    if (dy > 0) {
//                        a.hideFloat();
//                    } else {
//                        a.showFloat();
//                    }
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (list.size() != count) {
//                    int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
//                    int visibleItemCount = manager.getChildCount();
//                    int totalItemCount = manager.getItemCount();
//                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
//                        page++;
//                        swipeRefreshLayout.setRefreshing(true);
//                        onLoadMore();
//                    }
//                } else {
//                    //do smoething
//                }
//            }
//        });
//
//
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.hole_swiperefresh);
//        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.map_stroke));
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//                onRefreshList();
//
//            }
//        });
//        return view;
//    }
//
//    //这是刷新的方法
//    public void onRefreshList() {
//        page = 1;
//        list.clear();
//        list.addAll(getList(projectID, page));
//        itemAdapter.onCollapse();
//        itemAdapter.notifyDataSetChanged();
//        L.e("onRefreshList");
//    }
//
//
//    //新增加载更多方法加载更多
//    public void onLoadMore() {
//        swipeRefreshLayout.setRefreshing(false);
//        try {
//            list.addAll(getList(projectID, page));
//            itemAdapter.notifyDataSetChanged();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    DBHelper dbHelper;
//
//    private List<Hole> getList(String projectID, int index) {
//        List<Hole> list = new ArrayList<Hole>();
//
//        dbHelper = DBHelper.getInstance(getActivity());
//        try {
//            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
//            //钻孔的记录数量要 排除机长等基本信息和原始记录信息、机长等信息type不同，原始记录update不为空
//            String recordCount = "select count(id) from record r where state <> '0' and r.holeID=h.id and r.type<>'机长' and r.type<>'钻机'and r.type<>'描述员'and r.type<>'场景'and r.type<>'负责人'and r.type<>'工程师'and r.type<>'提钻录像' and r.updateID=''";
//
//            String currentDepth = "select max(r1.endDepth) from record r1 where state <> '0' and r1.holeID=h.id";
//
//            String holeUploadedCount = "select count(id) as uploadedCount from hole h2 where state='2' and h2.id=h.id";
//            String recordUploadedCount = "select count(id) as uploadedCount from record r2 where state='2'and r2.holeID=h.id";
//            String mediaUploadedCount = "select count(id) as uploadedCount from media m where state='2'and m.holeID=h.id";
//
//            String uploadedCount = "select sum(uploadedCount) from (" + holeUploadedCount + " union all " + recordUploadedCount + " union all " + mediaUploadedCount + ")";
//
//            String holeNotUploadCount = "select count(id) as notUploadCount from hole h2 where state='1' and h2.id=h.id";
//            String recordNotUploadCount = "select count(id) as notUploadCount from record r2 where state='1'and r2.holeID=h.id";
//            String mediaNotUploadCount = "select count(id) as notUploadCount from media m where state='1'and m.holeID=h.id";
//
//            String notUploadCount = "select sum(notUploadCount) from (" + holeNotUploadCount + " union all " + recordNotUploadCount + " union all " + mediaNotUploadCount + ")";
//            //用于分页加载的sql 这里的index-1 第二页的时候是跳过一个size  --
//            String pageSql = String.format("limit (" + Urls.PAGESIZE + ") offset " + Urls.PAGESIZE * (index - 1));//size:每页显示条数，index页码
//            String sql = "select h.id,h.code,h.type,h.state,(" + recordCount + ")as recordsCount,h.updateTime,h.mapLatitude,h.mapLongitude,h.mapPic,(" + uploadedCount + ") as uploadedCount,(" + notUploadCount + ") as notUploadCount,h.projectID ,(" + currentDepth + ")  as currentDepth,h.locationState from hole h where h.projectID='" + projectID + "' order by h.updateTime desc " + pageSql;
//            L.e("TAG", "sql---->>>" + sql);
//            count = (int) dao.countOf();
//            GenericRawResults<Hole> results = dao.queryRaw(sql, new RawRowMapper<Hole>() {
//                @Override
//                public Hole mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
//                    Hole hole = new Hole();
//                    hole.setId(resultColumns[0]);
//                    hole.setCode(resultColumns[1]);
//                    hole.setType(resultColumns[2]);
//                    hole.setState(resultColumns[3]);
//                    hole.setRecordsCount(resultColumns[4]);
//                    hole.setUpdateTime(resultColumns[5]);
//                    hole.setMapLatitude(resultColumns[6]);
//                    hole.setMapLongitude(resultColumns[7]);
//                    hole.setMapPic(resultColumns[8]);
//                    hole.setUploadedCount(Integer.valueOf(resultColumns[9]));
//                    hole.setNotUploadCount(Integer.valueOf(resultColumns[10]));
//                    hole.setProjectID(resultColumns[11]);
//                    hole.setCurrentDepth(resultColumns[12]);
//                    hole.setLocationState(resultColumns[13]);
//                    hole.jieMi();
//                    return hole;
//                }
//            });
//
//            Iterator<Hole> iterator = results.iterator();
//            while (iterator.hasNext()) {
//                Hole hole = iterator.next();
//                list.add(hole);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public class ItemAdapter extends AbstractSlideExpandableListAdapter<ItemAdapter.ViewHolder> {
//
//        private final TypedValue mTypedValue = new TypedValue();
//        private int mBackground;
//        private List<Hole> mValues;
//        private Context context;
//        private FragmentActivity activity;
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public Hole vo;
//
//            public final View mView;
//            public final TextView tvwCode;
//            public final TextView tvwType;
//            public final TextView tvwCurrentDepth;
//            public final TextView tvwUpdateTime;
//            public final TextView tvwRecordsCount;
//            public final Button ivwInfo;
//            public final MaterialProgressBar mpr;
//
//            public final Button btnDetail;
//
//            public final Button btnHoleList;
//            public final Button btnReport;
//            public final Button btnEdit;
//            public final Button btnUpload;
//            public final Button btnDelete;
//
//            public final TextView tvwState;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mpr = (MaterialProgressBar) view.findViewById(R.id.mpr);
//                tvwCode = (TextView) view.findViewById(R.id.tvwCode);
//                tvwType = (TextView) view.findViewById(R.id.tvwType);
//                tvwCurrentDepth = (TextView) view.findViewById(R.id.tvwCurrentDepth);
//                tvwUpdateTime = (TextView) view.findViewById(R.id.tvwUpdateTime);
//                tvwRecordsCount = (TextView) view.findViewById(R.id.tvwRecordsCount);
//                ivwInfo = (Button) view.findViewById(R.id.ivwInfo);
//
//                btnDetail = (Button) view.findViewById(R.id.btnDetail);
//                btnHoleList = (Button) view.findViewById(R.id.btnHoleList);
//                btnReport = (Button) view.findViewById(R.id.btnReport);
//                btnEdit = (Button) view.findViewById(R.id.btnEdit);
//                btnUpload = (Button) view.findViewById(R.id.btnUpload);
//                btnDelete = (Button) view.findViewById(R.id.btnDelete);
//
//                tvwState = (TextView) view.findViewById(R.id.tvwState);
//            }
//
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + tvwCode.getText();
//            }
//        }
//
//        public ItemAdapter(Context context, List<Hole> items) {
//            this.context = context;
//            this.activity = (FragmentActivity) context;
//            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//            mBackground = mTypedValue.resourceId;
//            mValues = items;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            //super.onCreateViewHolder(parent, viewType);
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_hole, parent, false);
//            view.setBackgroundResource(mBackground);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder rv, int position) {
//            super.onBindViewHolder(rv, position);
//            final ViewHolder holder = (ViewHolder) rv;
//            try {
//                holder.vo = mValues.get(position);
//                holder.mpr.setUseIntrinsicPadding(false);
////            holder.mpr.setShowTrack(false);
//                //状态
//                final int state = Integer.parseInt(holder.vo.getState());
//                final int locationState = Integer.parseInt(holder.vo.getLocationState());
//
//                int uploadedCount = holder.vo.getUploadedCount();          //已上传
//                int notUploadCount = holder.vo.getNotUploadCount();         //未上传
//                if (uploadedCount != 0) {
//                    int count = uploadedCount + notUploadCount;         // 总条数
//                    int progress = Integer.valueOf(uploadedCount * 100 / count);
//                    holder.mpr.setProgress(progress);
//                    if (progress == 100) {
//                        holder.btnUpload.setEnabled(false);
//                    } else {
//                        holder.btnUpload.setEnabled(true);
//                    }
//                    holder.btnUpload.setText("上传(" + progress + "%)");
//                } else {//上传数==0
//                    holder.mpr.setProgress(0);
//                    holder.btnUpload.setText("上传");
//                    holder.btnUpload.setEnabled(notUploadCount != 0);
//                }
//                holder.mpr.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));
//
//                if (locationState == 1) {
//                    holder.tvwState.setText("未开始");
//                } else if (locationState == 0 && state == 3) {
//                    holder.tvwState.setText("已提交");
//                } else {
//                    holder.tvwState.setText("记录中");
//                }
//
//
//                holder.tvwCode.setText(holder.vo.getCode());
//                holder.tvwType.setText(holder.vo.getType());
//                if (null != holder.vo.getCurrentDepth() && !"null".equals(holder.vo.getCurrentDepth())) {
//                    holder.tvwCurrentDepth.setText("深度:" + holder.vo.getCurrentDepth() + "m");
//                } else {
//                    holder.tvwCurrentDepth.setText("深度:0m");
//                }
//
//                holder.tvwUpdateTime.setText(holder.vo.getUpdateTime());
//                holder.tvwRecordsCount.setText(holder.vo.getRecordsCount());
//
//                //记录列表
//                holder.ivwInfo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        goMain(v, holder);
//                    }
//                });
//
//                //详情
//                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                        new HoleInfoDialog().show(activity, holder.vo.getId());
//                    }
//                });
//
//                //记录列表
//                holder.btnHoleList.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        goMain(v, holder);
//                    }
//                });
//                //记录浏览
//                holder.btnReport.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, PreviewActivity.class);
//                        intent.putExtra(PreviewActivity.EXTRA_HOLE, holder.vo);
//                        context.startActivity(intent);
//                    }
//                });
//                //编辑
//                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        goEdit(v, holder);
//                    }
//                });
//                //上传
//                holder.btnUpload.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Context context = v.getContext();
//                        Project project = new ProjectDao(context).queryForId(holder.vo.getProjectID());
//                        if (!"".equals(project.getSerialNumber())) {
//                            if (locationState == 0) {
//                                getHoleListForIntrnet(project.getSerialNumber());
////                                startUploadService(holder);
//                            } else {
//                                ToastUtil.showToastL(context, "勘察点没有定位");
//                            }
//                        } else {
//                            ToastUtil.showToastL(context, "该项目没有序列号");
//                        }
//
//                    }
//                });
//                // 删除
//                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDelDialog(holder.vo);
//                        L.e("----------------progress=" + holder.vo.getNotUploadCount());
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public View getExpandToggleButton(View parent) {
//            return parent.findViewById(R.id.lltItem);
//        }
//
//        @Override
//        public View getExpandableView(View parent) {
//            return parent.findViewById(R.id.expandable);
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//
//        public void goMain(View v, ViewHolder holder) {
//            try {
//                Hole hole = holder.vo;
//                if (Integer.parseInt(hole.getLocationState()) == 0) {
//                    Context context = v.getContext();
//                    final Intent intent = new Intent(context, RecordListActivity.class);
//                    intent.putExtra(RecordListActivity.EXTRA_HOLE, holder.vo);
//                    context.startActivity(intent);
//                } else {
//                    ToastUtil.showStacked(context, "勘探点未定位");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        public void goEdit(View v, ViewHolder holder) {
//            Context context = v.getContext();
//            Activity activity = (Activity) v.getContext();
//            Intent intent = new Intent();
//            intent.setClass(context, HoleEditActivity.class);
//            intent.putExtra(HoleEditActivity.EXTRA_HOLE, holder.vo);
//            activity.startActivityForResult(intent, HoleEditActivity.REQUEST_CODE);
//        }
//
//        private void showDelDialog(final Hole hole) {
//            int con = hole.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete2;
//            new MaterialDialog.Builder(context).content(con).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
//                @Override
//                public void onPositive(MaterialDialog dialog) {
//                    if (hole.delete(context)) {
//                        ToastUtil.showToastL(context, "删除勘探点成功");
//                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
//                        HoleListFragment holeListFragment = (HoleListFragment) fragmentManager.findFragmentById(R.id.holeListFragment);
//                        holeListFragment.onRefreshList();
//                    } else {
//                        ToastUtil.showToastL(context, "删除勘探点失败");
//                    }
//                }
//            }).show();
//        }
//
//
//        /**
//         * okHttp上传
//         */
//        private void startUploadService(final ViewHolder holder) {
//            final MaterialProgressBar materialProgressBar = holder.mpr;
//            final Button btnUpload = holder.btnUpload;
//            materialProgressBar.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green_200));
//            //上传项目 重新查询一下
//            Hole hole = new HoleDao(context).queryForId(holder.vo.getId());
//            new UploadService(context).uploadHole(hole);
//            IntentFilter intentFilter = new IntentFilter("upload.success");
//            context.registerReceiver(new BroadcastReceiver() {
//                int uploadedCount = holder.vo.getUploadedCount();          //已上传
//                int notUploadCount = holder.vo.getNotUploadCount();         //未上传
//                int count = uploadedCount + notUploadCount;   // 总条数
//
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    uploadedCount += 1;
//                    int progress = (int) ((uploadedCount / (float) count) * 100);
//                    btnUpload.setText("上传(" + progress + "%)");
//                    btnUpload.setEnabled(progress != 100);
//                    materialProgressBar.setProgress(progress);
//                    if (progress == 100) {
//                        btnUpload.setEnabled(false);
//                        context.unregisterReceiver(this);
//                        onRefreshList();
//                    }
//                }
//            }, intentFilter);
//        }
//
//        /**
//         * 获取勘察点列表
//         */
//        List<Hole> relateList = new ArrayList<>();
//        public void getHoleListForIntrnet(String serialNumber) {
//            Map<String, String> params = new HashMap<>();
//            params.put("serialNumber", serialNumber);
//            OkHttpUtils.post().url(Urls.GET_RELATE_HOLE).params(params).build().execute(new StringCallback() {
//                @Override
//                public void onError(Call call, Exception e, int id) {
//                    relateList = null;
//                    L.e("getHoleListForIntrnet--onError-->>" + e.getMessage());
//                }
//
//                @Override
//                public void onResponse(String response, int id) {
//                    L.e("TAG", "getHoleListForIntrnet--response-->>" + response);
//                    Gson gson = new Gson();
//                    relateList = gson.fromJson(response, new TypeToken<List<Hole>>() {
//                    }.getType());
//                    if (relateList != null && relateList.size() > 0) {
//                        L.e("TAG", "getHoleListForIntrnet--relateList-->>" + relateList.get(0).getId());
////                        new RelateHoleDialog().show();
//                    }else{
//                        ToastUtil.showToastS(context,"服务端未创建勘察点，无法关联");
//                    }
//                }
//            });
//
//
//        }
//
//
//    }
//}
