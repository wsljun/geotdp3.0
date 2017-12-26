/*
 * Copyright (C) 2015 The Android Open Source Record
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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.activity.RecordListActivity;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.RecordEditActivity;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.dialog.RecordInfoDialog;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.slide.AbstractSlideExpandableListAdapter;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import net.qiujuer.genius.ui.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class RecordListFragment extends Fragment {
    public static final String KEY_HOLE_ID = "RecordListBaseFragment:holeID";
    RecyclerView recyclerView;
    String holeID;

    List<Record> list;
    ItemAdapter itemAdapter;
    LinearLayoutManager manager;
    private int page = 1;//页数
    private int count;//总记录数

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(KEY_HOLE_ID)) {
            holeID = getArguments().getString(KEY_HOLE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.frt_record_list, container, false);
        recyclerView = (RecyclerView) convertView.findViewById(R.id.record_recyclerview);
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        list = getRecordList(page);
        itemAdapter = new ItemAdapter(getActivity(), list);
        recyclerView.setAdapter(itemAdapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecordListActivity a = (RecordListActivity) getActivity();
                if (Math.abs(dy) > 4) {
                    if (dy > 0) {
                        a.hideFloat();
                    } else {
                        a.showFloat();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (list.size() != count) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                        page++;
                        swipeRefreshLayout.setRefreshing(true);
                        onLoadMore();
                        System.out.println("----加载---");
                    }
                } else {
                    //do smoething
                    System.out.println("----全部加载---");
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.record_swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.map_stroke));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                onRefreshList();
            }
        });
        return convertView;
    }

    public void onRefreshList() {
        page = 1;
        list.clear();
        list.addAll(getRecordList(page));
        itemAdapter.onCollapse();
        itemAdapter.notifyDataSetChanged();
    }

    //新增加载更多方法加载更多
    public void onLoadMore() {
        swipeRefreshLayout.setRefreshing(false);
        try {
            list.addAll(getRecordList(page));
            itemAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Record> getRecordList(int index) {
        List<Record> list = new ArrayList<Record>();
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        try {
            Dao<Record, String> dao = dbHelper.getDao(Record.class);
            String mediaCount = "select count(id) from media where state <> '0' and recordID=r.id";

            String recordUploadedCount = "select count(id) as uploadedCount from record where state='2'and id=r.id";
            String mediaUploadedCount = "select count(id) as uploadedCount from media where state='2'and recordID=r.id";

            String uploadedCount = "select sum(uploadedCount) from (" + recordUploadedCount + " union all " + mediaUploadedCount + ")";

            String recordNotUploadCount = "select count(id) as notUploadCount from record where state='1'and id=r.id";
            String mediaNotUploadCount = "select count(id) as notUploadCount from media where state='1'and recordID=r.id";

            String notUploadCount = "select sum(notUploadCount) from (" + recordNotUploadCount + " union all " + mediaNotUploadCount + ")";

            String sort = "";
            String sequence = "r.updateTime desc";

            RecordListActivity recordListActivity = (RecordListActivity) getActivity();
            String tagSort = recordListActivity.getSort();
            if (!"".equals(tagSort)) {
                sort = "and r.type='" + tagSort + "'";
            }

            String tagSequence = recordListActivity.getSequence();
            if ("1".equals(tagSequence)) {
                sequence = "r.updateTime desc";
            } else if ("2".equals(tagSequence)) {
                sequence = "r.updateTime asc";
            } else if ("3".equals(tagSequence)) {
                sequence = "r.beginDepth asc, r.endDepth asc";
            } else if ("4".equals(tagSequence)) {
                sequence = "r.beginDepth desc,r.endDepth desc";
            }

            //用于分页加载的sql 这里的index-1 第二页的时候是跳过一个size   ,r.operatePerson,r.testType,r.recordPerson
            String pageSql = String.format("limit (" + Urls.PAGESIZE + ") offset " + Urls.PAGESIZE * (index - 1));//size:每页显示条数，index页码
            String sql = "select r.id,r.code,r.type,r.state,(" + mediaCount + ")as mediaCount,r.updateTime,r.beginDepth,r.endDepth,r.title,(" + uploadedCount + ") as uploadedCount,(" + notUploadCount + ") as notUploadCount,r.projectID,r.holeID from record r where r.holeID='" + holeID + "' and state !='0' and updateID=''" + sort + "  order by " + sequence + " " + pageSql;
            //这里获取总记录条数
            Map<Integer, Integer> countMap = new RecordDao(getActivity()).getSortCountMap(holeID);
            count = countMap.get(1);

            System.out.println("----------------------------sql-" + sql);
            L.e("TAG", "count=" + count);
            GenericRawResults<Record> results = dao.queryRaw(sql, new RawRowMapper<Record>() {
                @Override
                public Record mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Record record = new Record();
                    record.setId(resultColumns[0]);
                    record.setCode(resultColumns[1]);
                    record.setType(resultColumns[2]);
                    record.setState(resultColumns[3]);
                    record.setMediaCount(resultColumns[4]);
                    record.setUpdateTime(resultColumns[5]);
                    record.setBeginDepth(resultColumns[6]);
                    record.setEndDepth(resultColumns[7]);
                    record.setTitle(resultColumns[8]);
                    record.setUploadedCount(Integer.valueOf(resultColumns[9]));
                    record.setNotUploadCount(Integer.valueOf(resultColumns[10]));
                    record.setProjectID(resultColumns[11]);
                    record.setHoleID(resultColumns[12]);
//                    record.setOperatePerson(resultColumns[12]);
//                    record.setTestType(resultColumns[13]);
//                    record.setRecordPerson(resultColumns[14]);
//                    record.jieMi();
                    return record;
                }
            });

            Iterator<Record> iterator = results.iterator();
            while (iterator.hasNext()) {
                Record record = iterator.next();
                //一下七个类型暂时不显示到列表中
                if (!record.getType().equals(Record.TYPE_SCENE_PRINCIPAL) && !record.getType().equals(Record.TYPE_SCENE_TECHNICIAN) && !record.getType().equals(Record.TYPE_SCENE_VIDEO) && !record.getType().equals(Record.TYPE_SCENE_OPERATEPERSON) && !record.getType().equals(Record.TYPE_SCENE_OPERATECODE) && !record.getType().equals(Record.TYPE_SCENE_RECORDPERSON) && !record.getType().equals(Record.TYPE_SCENE_SCENE)) {
                    list.add(record);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 等待对话框
     */
    MaterialDialog mDialog;

    //显示检验序列号的对话框
    public void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(getActivity()).title(R.string.dictionary_wait_dialog).content(R.string.please_wait).progress(true, 0).progressIndeterminateStyle(horizontal).build();
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
        private List<Record> mValues;
        private Context context;
        private FragmentActivity activity;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Record vo;

            public final View mView;
            public final TextView tvwBeginDepth;
            public final TextView tvwBeginEnd;
            public final TextView tvwEndDepth;
            public final TextView tvwType;
            public final TextView tvwName;
            public final TextView tvwUpdateTime;
            public final Button ivwInfo;
            public final MaterialProgressBar mpr;

            public final Button btnDetail;
            public final Button btnEdit;
            public final Button btnUpload;
            public final Button btnDelete;

            public final RelativeLayout ivwInfo_rl;
            public final View record_line;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mpr = (MaterialProgressBar) view.findViewById(R.id.mpr);
                tvwBeginDepth = (TextView) view.findViewById(R.id.tvwBeginDepth);
                tvwBeginEnd = (TextView) view.findViewById(R.id.tvwBeginEnd);
                tvwEndDepth = (TextView) view.findViewById(R.id.tvwEndDepth);
                tvwType = (TextView) view.findViewById(R.id.tvwType);
                tvwName = (TextView) view.findViewById(R.id.tvwName);
                tvwUpdateTime = (TextView) view.findViewById(R.id.tvwUpdateTime);
                ivwInfo = (Button) view.findViewById(R.id.ivwInfo);
                ivwInfo_rl = (RelativeLayout) view.findViewById(R.id.ivwInfo_rl);
                record_line = view.findViewById(R.id.record_line);

                btnDetail = (Button) view.findViewById(R.id.btnDetail);
                btnEdit = (Button) view.findViewById(R.id.btnEdit);
                btnUpload = (Button) view.findViewById(R.id.btnUpload);
                btnDelete = (Button) view.findViewById(R.id.btnDelete);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvwBeginDepth.getText();
            }
        }

        public ItemAdapter(Context context, List<Record> items) {
            this.context = context;
            this.activity = (FragmentActivity) context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            super.onCreateViewHolder(parent, viewType);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder rv, int position) {
            super.onBindViewHolder(rv, position);
            final ViewHolder holder = (ViewHolder) rv;
            holder.vo = mValues.get(position);
            //随机获取颜色，从颜色组里面取
            int[] androidColors = context.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor;
            String type = holder.vo.getType();
            if (type.equals(Record.TYPE_FREQUENCY)) {
                randomAndroidColor = androidColors[0];
            } else if (type.equals(Record.TYPE_LAYER)) {
                randomAndroidColor = androidColors[1];
            } else if (type.equals(Record.TYPE_GET_EARTH)) {
                randomAndroidColor = androidColors[5];
            } else if (type.equals(Record.TYPE_GET_WATER)) {
                randomAndroidColor = androidColors[6];
            } else if (type.equals(Record.TYPE_DPT)) {
                randomAndroidColor = androidColors[3];
            } else if (type.equals(Record.TYPE_SPT)) {
                randomAndroidColor = androidColors[4];
            } else if (type.equals(Record.TYPE_WATER)) {
                randomAndroidColor = androidColors[2];
            } else {
                randomAndroidColor = androidColors[0];
            }
            holder.btnDetail.setBackgroundColor(randomAndroidColor);
            holder.btnEdit.setBackgroundColor(randomAndroidColor);
            holder.btnUpload.setBackgroundColor(randomAndroidColor);
            holder.btnDelete.setBackgroundColor(randomAndroidColor);
            holder.ivwInfo_rl.setBackgroundColor(randomAndroidColor);
            holder.record_line.setBackgroundColor(randomAndroidColor);


            holder.mpr.setUseIntrinsicPadding(false);
//            holder.mpr.setShowTrack(false);
            int uploadedCount = holder.vo.getUploadedCount();          //已上传
            int notUploadCount = holder.vo.getNotUploadCount();         //未上传
            if (uploadedCount != 0) {
                int count = uploadedCount + notUploadCount;         // 总条数
                int progress = Integer.valueOf(uploadedCount * 100 / count);
                holder.mpr.setProgress(progress);
                holder.btnUpload.setText("上传(" + progress + "%)");
                holder.mpr.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));

            } else {
                holder.mpr.setProgress(0);
                holder.btnUpload.setText("上传");
                holder.mpr.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));
            }

            holder.btnUpload.setEnabled(notUploadCount != 0);
            holder.tvwBeginDepth.setText(holder.vo.getBeginDepth() + "m");
            holder.tvwEndDepth.setText(holder.vo.getEndDepth() + "m");
            //最笨的方法實現。。。。。。 机长、钻机、描述、场景，不再显示了
            if (holder.vo.getType().equals("取水")) {
                holder.tvwBeginDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setVisibility(View.GONE);
                holder.tvwEndDepth.setVisibility(View.GONE);
                holder.tvwBeginEnd.setText("~");
            } else if (holder.vo.getType().equals("水位")) {
                holder.tvwBeginDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setVisibility(View.VISIBLE);
                holder.tvwEndDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setText("/");
                holder.tvwEndDepth.setText(holder.vo.getEndDepth() + "m");
            } else if (holder.vo.getType().equals("机长")) {
                holder.tvwBeginDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginDepth.setText(TextUtils.isEmpty(holder.vo.getOperatePerson()) ? "---" : holder.vo.getOperatePerson());
                holder.tvwBeginEnd.setVisibility(View.GONE);
                holder.tvwEndDepth.setVisibility(View.GONE);
            } else if (holder.vo.getType().equals("钻机")) {
                holder.tvwBeginDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setVisibility(View.GONE);
                holder.tvwEndDepth.setVisibility(View.GONE);
                holder.tvwBeginDepth.setText(TextUtils.isEmpty(holder.vo.getTestType()) ? "---" : holder.vo.getTestType());
            } else if (holder.vo.getType().equals("描述员")) {
                holder.tvwBeginDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setVisibility(View.GONE);
                holder.tvwEndDepth.setVisibility(View.GONE);
                holder.tvwBeginDepth.setText(TextUtils.isEmpty(holder.vo.getRecordPerson()) ? "---" : holder.vo.getRecordPerson());
            } else if (holder.vo.getType().equals("场景")) {
                holder.tvwBeginDepth.setVisibility(View.GONE);
                holder.tvwBeginEnd.setVisibility(View.GONE);
                holder.tvwEndDepth.setVisibility(View.GONE);
            } else {
                holder.tvwBeginDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setVisibility(View.VISIBLE);
                holder.tvwEndDepth.setVisibility(View.VISIBLE);
                holder.tvwBeginEnd.setText("~");
            }

            holder.tvwType.setText(holder.vo.getType());
            holder.tvwName.setText(holder.vo.getTitle());
            holder.tvwUpdateTime.setText(holder.vo.getUpdateTime());

            holder.ivwInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goEdit(v, holder);
                }
            });
            holder.ivwInfo_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goEdit(v, holder);
                }
            });

            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    new RecordInfoDialog().show(activity, holder.vo.getId());
                }
            });

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goEdit(v, holder);
                }
            });
            //不允许单个记录上传
            holder.btnUpload.setEnabled(false);
//            holder.btnUpload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Project project = new ProjectDao(context).queryForId(holder.vo.getProjectID());
//                    if (!"".equals(project.getSerialNumber())) {
//                        startUploadService(holder);
//                    } else {
//                        ToastUtil.showToastL(context, "该项目没有序列号");
//                    }
//                }
//            });

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

        public void goEdit(View v, ViewHolder holder) {
            Context context = v.getContext();
            Activity activity = (Activity) v.getContext();
            Intent intent = new Intent();
            intent.setClass(context, RecordEditActivity.class);
            intent.putExtra(RecordEditActivity.EXTRA_RECORD, holder.vo);
            intent.putExtra(RecordEditActivity.EXTRA_HOLEID, holder.vo.getHoleID());
//            L.e("TAG", "recordList---holeID=" + holder.vo.getHoleID());
            activity.startActivityForResult(intent, RecordEditActivity.REQUEST_CODE);
        }

        /**
         * okHttp上传
         */
//        private void startUploadService(final ViewHolder holder) {
//            final MaterialProgressBar materialProgressBar = holder.mpr;
//            final Button btnUpload = holder.btnUpload;
//            materialProgressBar.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green_200));
//            //上传项目
//            Record record = new RecordDao(context).queryForId(holder.vo.getId());
//            new UploadService(context).uploadreRecord(record);
//            IntentFilter intentFilter = new IntentFilter("upload.success");
//            context.registerReceiver(new BroadcastReceiver() {
//                int uploadedCount = holder.vo.getUploadedCount();          //已上传
//                int notUploadCount = holder.vo.getNotUploadCount();         //未上传
//                int count = uploadedCount + notUploadCount;   // 总条数
//
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    L.e("TAG", "uploadreceiver--->>>广播接收");
//                    uploadedCount += 1;
//                    int progress = (int) ((uploadedCount / (float) count) * 100);
//                    L.e("TAG", "progress--->>>" + progress + "--uploadedCount=" + uploadedCount + "--count" + count + "--uploadedCount" + uploadedCount);
//                    btnUpload.setText("上传(" + progress + "%)");
//                    btnUpload.setEnabled(progress != 100);
//                    materialProgressBar.setProgress(progress);
//                    if (progress == 100) {
//                        context.unregisterReceiver(this);
//                        L.e("TAG", "unregisterReceiver--->>>注销广播");
//                        onRefreshList();
//                    }
//                }
//            }, intentFilter);
//        }
        private void showDelDialog(final Record record) {
            final int con = record.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete4;
            new MaterialDialog.Builder(context).content(con).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    showProgressDialog(false);
                    final Message msg = new Message();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //删除记录，其实是添加这条记录的updateID为本记录id
                            record.setUpdateId(record.getId());
                            new RecordDao(context).update(record);
                            msg.what = 2;
                            handler.sendMessage(msg);
//                            if (record.delete(context)) {
//                                msg.what = 2;
//                                handler.sendMessage(msg);
//                            } else {
//                                msg.what = 3;
//                                handler.sendMessage(msg);
//                            }
                        }
                    }).start();

                }
            }).show();
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    ToastUtil.showToastL(getActivity(), "删除记录成功");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    RecordListFragment recordListFragment = (RecordListFragment) fragmentManager.findFragmentById(R.id.recordListFrameLayout);
                    recordListFragment.onRefreshList();
                    dismissProgressDialog();
                    onRefreshList();
                    break;
                case 3:
                    ToastUtil.showToastL(getActivity(), "删除记录失败");
                    dismissProgressDialog();
                    break;
            }

        }
    };
}
