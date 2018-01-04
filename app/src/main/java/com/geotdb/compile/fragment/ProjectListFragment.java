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

package com.geotdb.compile.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.activity.HoleListActivity;
import com.geotdb.compile.activity.LoginActivity;
import com.geotdb.compile.activity.ReleteLocationActivity;
import com.geotdb.compile.db.HoleDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.activity.MainActivity;
import com.geotdb.compile.activity.ProjectEditActivity;
import com.geotdb.compile.dialog.ProjectInfoDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.service.UploadService;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.ProjectDao;
import com.geotdb.compile.slide.AbstractSlideExpandableListAdapter;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Project;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import net.qiujuer.genius.ui.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ProjectListFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<Project> list;
    ItemAdapter itemAdapter;
    LinearLayoutManager manager;
    private int page = 1;//页数
    private int count;//总记录数

    //    private RelativeLayout nullLayout;
//    private Button goLogin;
    private RelativeLayout zeroLayout;
    private TextView zeroText;
    private LocalUser localUser;

    private Context context;
    private ProjectDao projectDao;
    private HoleDao holeDao;
    private RecordDao recordDao;
    private MediaDao mediaDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frt_project_list, container, false);
        localUser = Common.getLocalUser(getActivity());
        context = getActivity();
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.project_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.project_swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.map_stroke));
//        nullLayout = (RelativeLayout) view.findViewById(R.id.project_null_rl);
//        goLogin = (Button) view.findViewById(R.id.project_null_btn);
        zeroLayout = (RelativeLayout) view.findViewById(R.id.project_zero_rl);
        zeroText = (TextView) view.findViewById(R.id.project_zero_tv);
        projectDao = new ProjectDao(context);
        holeDao = new HoleDao(context);
        recordDao = new RecordDao(context);
        mediaDao = new MediaDao(context);
    }

    private void initData() {
        manager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(manager);
        if (localUser != null) {
            new Thread(runnable).start();
        } else {
            list = new ArrayList<>();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            list = getProjectList(page);
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            L.e("---->>>" + msg.what);
            switch (msg.what) {
                case 1:
                    itemAdapter = new ItemAdapter(context, list);
                    recyclerView.setAdapter(itemAdapter);
                    break;
                case 2:
                    ToastUtil.showToastL(context, "删除项目成功");
                    dismissProgressDialog();
                    onRefreshList();
                    break;
                case 3:
                    ToastUtil.showToastL(context, "删除项目失败");
                    dismissProgressDialog();
                    break;
            }

        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        zeroText.setOnClickListener(zeroTextListener);
//        goLogin.setOnClickListener(nullLoginListener);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        recyclerView.setOnScrollListener(scrollListener);
    }

    //列表为0时，添加项目的文字
    View.OnClickListener zeroTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.setClass(context, ProjectEditActivity.class);
//            startActivityForResult(intent, ProjectEditActivity.REQUEST_CODE);
            ToastUtil.showToastS(getActivity(), "请点击右上角添加项目");
        }
    };

    //localUser为空的时候出现的btn
    View.OnClickListener nullLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            MainActivity mainActivity = (MainActivity) context;
//            new LoginDialog().show(mainActivity);
            startActivity(new Intent(context, LoginActivity.class));
        }
    };

    //列表滑动监听
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (count > Urls.PAGESIZE) {
                if (list.size() != count) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                        page++;
                        swipeRefreshLayout.setRefreshing(true);
                        onLoadMore();
                    }
                } else {
                    int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                        ToastUtil.showToastS(getActivity(), "全部加载");
                    }
                }
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            MainActivity a = (MainActivity) getActivity();
            if (Math.abs(dy) > 4) {
                if (dy > 0) {
                    a.hideFloat();
                } else {
                    a.showFloat();
                }
            }
        }
    };
    //下拉动画监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(false);
            onRefreshList();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getProjectList(page).size() == 0) {
            zeroLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            zeroLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
//            onRefreshList();
        }
    }

    //这是刷新的方法
    public void onRefreshList() {
        L.e("TAG", "project--onRefreshList");
        page = 1;
        try {
            if (getProjectList(page).size() == 0) {
                onResume();
            } else {
                list.clear();
                list.addAll(getProjectList(page));
                itemAdapter = new ItemAdapter(context, list);
                recyclerView.setAdapter(itemAdapter);
                itemAdapter.onCollapse();
                itemAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //新增加载更多方法加载更多
    public void onLoadMore() {
        try {
            list.addAll(getProjectList(page));
            itemAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }


    DBHelper dbHelper;

    /**
     * 包含上传进度信息的项目列表
     *
     * @return
     */
    private List<Project> getProjectList(int index) {
        List<Project> list = new ArrayList<Project>();
        dbHelper = DBHelper.getInstance(getActivity());
        try {
            Dao<Project, String> dao = dbHelper.getDao(Project.class);

            String holeCount = "select count(h.id) from hole h where state <> '0' and h.projectID=p.id";

//            String projectUploadedCount = "select count(p2.id) as uploadedCount from project p2 where state='2' and isDelete='0' and p2.id=p.id";
            String holeUploadedCount = "select count(h2.id) as uploadedCount from hole h2 where state='2'  and isDelete='0' and h2.projectID=p.id";
            String recordUploadedCount = "select count(r.id) as uploadedCount from record r where state='2' and isDelete='0' and r.projectID=p.id ";
            String mediaUploadedCount = "select count(m.id) as uploadedCount from media m where state='2' and isDelete='0' and m.projectID=p.id";

            String uploadedCount = "select sum(uploadedCount) from (" + holeUploadedCount + " union all " + recordUploadedCount + " union all " + mediaUploadedCount + ")";
//            String uploadedCount = "select sum(uploadedCount) from (" + projectUploadedCount + " union all " + holeUploadedCount + " union all " + recordUploadedCount + " union all " + mediaUploadedCount + ")";

            String projectNotUploadCount = "select count(p2.id) as notUploadCount from project p2 where state='1' and isDelete='0' and p2.id=p.id";
            String holeNotUploadCount = "select count(h2.id) as notUploadCount from hole h2 where state='1'  and isDelete='0' and h2.projectID=p.id";
            String recordNotUploadCount = "select count(r.id) as notUploadCount from record r where state='1' and isDelete='0' and r.projectID=p.id";
            String mediaNotUploadCount = "select count(m.id) as notUploadCount from media m where state='1' and isDelete='0' and m.projectID=p.id";

            String notUploadCount = "select sum(notUploadCount) from (" + projectNotUploadCount + " union all " + holeNotUploadCount + " union all " + recordNotUploadCount + " union all " + mediaNotUploadCount + ")";

            //用于分页加载的sql 这里的index-1 第二页的时候是跳过一个size
            String pageSql = String.format("limit (" + Urls.PAGESIZE + ") offset " + Urls.PAGESIZE * (index - 1));//size:每页显示条数，index页码
            //拼接sql语句，带分页 没有关联用户名，调试用
            String sql = "select p.id,p.code,p.fullName,p.leader,p.state,(" + holeCount + ")as holeCount,p.updateTime,p.mapPic,(" + uploadedCount + ") as uploadedCount,(" + notUploadCount + ") as notUploadCount,p.serialNumber from project p order by p.updateTime desc " + pageSql;
            //添加  recordPerson=''条件，查询老版本中的项目
//            String sql = "select p.id,p.code,p.fullName,p.leader,p.state,(" + holeCount + ")as holeCount,p.updateTime,p.mapPic,(" + uploadedCount + ") as uploadedCount,(" + notUploadCount + ") as notUploadCount,p.serialNumber from project p where recordPerson='' or recordPerson='" + localUser.getId() + "'order by p.updateTime desc " + pageSql;
            //这里获取总记录条数
            count = (int) dao.countOf();
            L.e("TAG", "sql---->>>" + sql);
            GenericRawResults<Project> results = dao.queryRaw(sql, new RawRowMapper<Project>() {
                @Override
                public Project mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Project project = new Project();
                    project.setId(resultColumns[0]);
                    project.setCode(resultColumns[1]);
                    project.setFullName(resultColumns[2]);
                    project.setLeader(resultColumns[3]);
                    project.setState(resultColumns[4]);
                    project.setHoleCount(resultColumns[5]);
                    project.setUpdateTime(resultColumns[6]);
                    project.setMapPic(resultColumns[7]);
                    project.setUploadedCount(Integer.valueOf(resultColumns[8]));
                    project.setNotUploadCount(Integer.valueOf(resultColumns[9]));
                    project.setSerialNumber(resultColumns[10]);
                    project.jieProject();
                    return project;
                }
            });

            Iterator<Project> iterator = results.iterator();
            while (iterator.hasNext()) {
                Project project = iterator.next();
                list.add(project);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        L.e("TAG", "count:" + count + "---page:" + page + "---size:" + list.size());
        return list;
    }

    /**
     * 等待对话框
     */
    MaterialDialog mDialog;

    //显示检验序列号的对话框
    public void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(context).title(R.string.dictionary_wait_dialog).content(R.string.please_wait).progress(true, 0).progressIndeterminateStyle(horizontal).build();
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

    public class ItemAdapter extends AbstractSlideExpandableListAdapter<ItemAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Project> mValues;
        private Context context;
        private FragmentActivity activity;

        //ho
        class ViewHolder extends RecyclerView.ViewHolder {
            public Project vo;
            public int position;

            public final View mView;
            public final TextView tvwFullName;
            public final TextView tvwCode;
            public final TextView tvwUpdateTime;
            public final RelativeLayout ivwInfo_rl;
            public final Button ivwInfo;
            public final TextView tvwHoleCount;
            public final TextView tvwState;
            public final RelativeLayout project_lr;
            public final View project_line;

            public final MaterialProgressBar mpr;

            public final Button btnDetail;
            public final Button btnHoleList;
            public final Button btnOutline;
            public final Button btnEdit;
            public final Button btnUpload;
            public final Button btnDelete;

            public final ImageView tvwHoleCount_iv;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvwFullName = (TextView) view.findViewById(R.id.tvwFullName);
                tvwCode = (TextView) view.findViewById(R.id.tvwCode);
                tvwUpdateTime = (TextView) view.findViewById(R.id.tvwUpdateTime);
                ivwInfo_rl = (RelativeLayout) view.findViewById(R.id.ivwInfo_rl);
                ivwInfo = (Button) view.findViewById(R.id.ivwInfo);
                tvwHoleCount = (TextView) view.findViewById(R.id.tvwHoleCount);
                tvwState = (TextView) view.findViewById(R.id.tvwState);
                project_lr = (RelativeLayout) view.findViewById(R.id.project_lr);
                project_line = view.findViewById(R.id.project_line);

                mpr = (MaterialProgressBar) view.findViewById(R.id.mpr);

                btnDetail = (Button) view.findViewById(R.id.btnDetail);
                btnHoleList = (Button) view.findViewById(R.id.btnHoleList);
                btnOutline = (Button) view.findViewById(R.id.btnOutline);
                btnEdit = (Button) view.findViewById(R.id.btnEdit);
                btnUpload = (Button) view.findViewById(R.id.btnUpload);
                btnDelete = (Button) view.findViewById(R.id.btnDelete);

                tvwHoleCount_iv = (ImageView) view.findViewById(R.id.tvwHoleCount_iv);
            }


            @Override
            public String toString() {
                return super.toString() + " '" + tvwFullName.getText();
            }
        }

        public ItemAdapter(Context context, List<Project> items) {
            this.context = context;
            this.activity = (FragmentActivity) context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            super.onCreateViewHolder(parent, viewType);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder rv, int position) {
            super.onBindViewHolder(rv, position);
            final ViewHolder holder = (ViewHolder) rv;
            //随机获取颜色，从颜色组里面取
            int[] androidColors = getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[position % 7];

            holder.ivwInfo_rl.setBackgroundColor(randomAndroidColor);
            holder.btnDetail.setBackgroundColor(randomAndroidColor);
            holder.btnHoleList.setBackgroundColor(randomAndroidColor);
            holder.btnOutline.setBackgroundColor(randomAndroidColor);
            holder.btnEdit.setBackgroundColor(randomAndroidColor);
            holder.btnUpload.setBackgroundColor(randomAndroidColor);
            holder.btnDelete.setBackgroundColor(randomAndroidColor);
            holder.project_lr.setBackgroundColor(randomAndroidColor);
            holder.project_line.setBackgroundColor(randomAndroidColor);

            holder.position = position;
            holder.vo = mValues.get(position);
            holder.mpr.setUseIntrinsicPadding(false);
//            holder.mpr.setShowTrack(false);
            int uploadedCount = holder.vo.getUploadedCount();          //已上传
            int notUploadCount = holder.vo.getNotUploadCount();         //未上传
            if (uploadedCount != 0) {
                int count = uploadedCount + notUploadCount;         // 总条数
                int progress = Integer.valueOf(uploadedCount * 100 / count);
                holder.mpr.setProgress(progress);
                holder.btnUpload.setText("上传数(" + uploadedCount + "/" + count + ")");
                holder.mpr.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));

            } else {
                holder.mpr.setProgress(0);
                holder.btnUpload.setText("上传数(" + uploadedCount + "/" + count + ")");
                holder.mpr.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));
            }
            //项目的状态 未关联、记录中、以提交
            if ("".equals(holder.vo.getSerialNumber())) {
                holder.tvwState.setText("未关联");
                holder.tvwState.setTextColor(getResources().getColor(R.color.md_material_gray_800));
            } else {
                holder.tvwState.setTextColor(getResources().getColor(R.color.list_normal1));
                List<Hole> holeList = new HoleDao(context).getHoleListBeSubmit(holder.vo.getId());
                if (null != holeList && holeList.size() > 0) {
                    if (holeList.size() == Integer.parseInt(holder.vo.getHoleCount())) {
                        holder.tvwState.setText("已提交");
                    } else {
                        holder.tvwState.setText("记录中");
                    }
                } else {
                    holder.tvwState.setText("记录中");
                }
            }

//            holder.btnUpload.setEnabled(notUploadCount != 0);
            holder.btnUpload.setEnabled(false);
            holder.tvwFullName.setText(holder.vo.getFullName());
            holder.tvwCode.setText(holder.vo.getCode());
            holder.tvwUpdateTime.setText(holder.vo.getUpdateTime());
            holder.tvwHoleCount.setText(holder.vo.getHoleCount());

            if (Integer.parseInt(holder.vo.getHoleCount()) > 0) {
                holder.tvwHoleCount.setTextColor(getResources().getColor(R.color.met_primaryColor2));
                holder.tvwHoleCount_iv.setBackgroundResource(R.drawable.ai_icon_recordcenter);
            } else {
                holder.tvwHoleCount.setTextColor(getResources().getColor(R.color.md_material_gray_800));
                holder.tvwHoleCount_iv.setBackgroundResource(R.drawable.ai_icon_recorddefault);
            }
            holder.ivwInfo_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goProjectMain(v, holder);
                }
            });
            holder.ivwInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goProjectMain(v, holder);
                }
            });

            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    new ProjectInfoDialog().show(activity, holder.vo.getId());
                }
            });

            holder.btnHoleList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goProjectMain(v, holder);
                }
            });
            holder.btnOutline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goReleteMap(holder);
                }
            });
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goProjectEdit(v, holder);
                }
            });
            holder.btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    if (!"".equals(holder.vo.getSerialNumber())) {
//                        startUploadService(holder);
                        ToastUtil.showToastL(context, "请从勘探点进行上传");
                    } else {
                        ToastUtil.showToastL(context, "该项目没有序列号");
                    }

                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDelDialog(holder.vo);
                }
            });


        }

        @Override
        public View getExpandToggleButton(View parent) {
            return parent.findViewById(R.id.lltItem);
        }

        @Override
        public View getExpandableView(View parent) {
            return parent.findViewById(R.id.expandable);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        public void goProjectMain(View v, ViewHolder holder) {
            try {
                Context context = v.getContext();
                Intent intent = new Intent(context, HoleListActivity.class);
                intent.putExtra(HoleListActivity.EXTRA_PROJECT, holder.vo);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //关联点地图
        public void goReleteMap(ViewHolder holder) {
            if (!"".equals(holder.vo.getSerialNumber())) {
                Intent intent = new Intent(context, ReleteLocationActivity.class);
                intent.putExtra("serialNumber", holder.vo.getSerialNumber());
                context.startActivity(intent);
            } else {
                ToastUtil.showToastL(context, "该项目没有序列号");
            }
        }

        public void goProjectEdit(View v, ViewHolder holder) {
            Context context = v.getContext();
            Activity activity = (Activity) v.getContext();
            Intent intent = new Intent();
            intent.setClass(context, ProjectEditActivity.class);
            intent.putExtra(ProjectEditActivity.EXTRA_PROJECT, holder.vo);
            activity.startActivityForResult(intent, ProjectEditActivity.REQUEST_CODE);
        }

        private void showDelDialog(final Project project) {
            int con = project.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete3;
            new MaterialDialog.Builder(context).content(con).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    showProgressDialog(false);
                    final Message msg = new Message();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (project.delete(context)) {
                                msg.what = 2;
                                handler.sendMessage(msg);
                            } else {
                                msg.what = 3;
                                handler.sendMessage(msg);
                            }
                        }
                    }).start();
                }
            }).show();
        }


        /**
         * okHttp上传
         */
        private void startUploadService(final ViewHolder holder) {
            final MaterialProgressBar materialProgressBar = holder.mpr;
            final Button btnUpload = holder.btnUpload;
//            materialProgressBar.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green_200));
            IntentFilter intentFilter = new IntentFilter("upload.success");
            //上传项目
            Project project = new ProjectDao(context).queryForId(holder.vo.getId());
            Intent intent = new Intent(context, UploadService.class);
//            Intent intent = new Intent(context, NewUploadService.class);
            intent.putExtra("project", project);
            context.startService(intent);
            context.registerReceiver(new BroadcastReceiver() {
                int uploadedCount = holder.vo.getUploadedCount();          //已上传
                int notUploadCount = holder.vo.getNotUploadCount();         //未上传
                int count = uploadedCount + notUploadCount;   // 总条数

                @Override
                public void onReceive(Context context, Intent intent) {
                    uploadedCount += 1;
                    int progress = (int) ((uploadedCount / (float) count) * 100);
                    L.e("TAG", "progress--->>>" + progress + "--uploadedCount=" + uploadedCount + "--count" + count + "--uploadedCount" + uploadedCount);
                    btnUpload.setText("上传(" + progress + "%)");
                    btnUpload.setEnabled(progress != 100);
                    materialProgressBar.setProgress(progress);
                    if (progress == 100) {
                        L.e("TAG", "unregisterReceiver--->>>注销广播");
                        context.unregisterReceiver(this);
                        onRefreshList();
                    }
                }
            }, intentFilter);
        }

    }
}
