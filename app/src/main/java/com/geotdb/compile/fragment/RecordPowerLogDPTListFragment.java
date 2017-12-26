/*
 * Copyright (C) 2015 The Android Open Source RecordPower
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

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.vo.RecordPower;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecordPowerLogDPTListFragment extends RecordListBaseFragment {


    List<RecordPower> list = new ArrayList<RecordPower>();
    @Override
    public void onRefreshList() {
        super.onRefreshList();
        recyclerView.setAdapter(new ItemAdapter(getActivity(), getRecordPowerList(holeID)));
        recyclerView.scrollToPosition(list.size() - 1);
    }


    @Override
    public void setupRecyclerView(RecyclerView recyclerView) {
        super.setupRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new ItemAdapter(getActivity(), getRecordPowerList(holeID)));
    }

    public List<RecordPower> getRecordPowerList(String recordPowerID) {
        list = new ArrayList<RecordPower>();
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        try {
            Dao<RecordPower, String> dao = dbHelper.getDao(RecordPower.class);
            GenericRawResults<RecordPower> results = dao.queryRaw("select id,code,type,updateTime,begin,end,blow from record_power where holeID='" + holeID + "' order by updateTime asc", new RawRowMapper<RecordPower>() {
                @Override
                public RecordPower mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    RecordPower recordPower = new RecordPower();
                    recordPower.setId(resultColumns[0]);
//                    recordPower.setCode(resultColumns[1]);
//                    recordPower.setType(resultColumns[2]);
//                    recordPower.setUpdateTime(resultColumns[3]);
//                    recordPower.setBegin(resultColumns[4]);
//                    recordPower.setEnd(resultColumns[5]);
//                    recordPower.setBlow(resultColumns[6]);
//                    recordPower.jieMi();
                    return recordPower;
                }
            });

            Iterator<RecordPower> iterator = results.iterator();
            while (iterator.hasNext()) {
                RecordPower recordPower = iterator.next();
                list.add(recordPower);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<RecordPower> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public RecordPower vo;

            public final View mView;
            public final TextView tvwCode;
            public final TextView tvwUpdateTime;
            public final TextView tvwBegin;
            public final TextView tvwEnd;
            public final TextView tvwBlow;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvwCode = (TextView) view.findViewById(R.id.tvwCode);
                tvwUpdateTime = (TextView) view.findViewById(R.id.tvwUpdateTime);
                tvwBegin = (TextView) view.findViewById(R.id.tvwBegin);
                tvwEnd = (TextView) view.findViewById(R.id.tvwEnd);
                tvwBlow = (TextView) view.findViewById(R.id.tvwBlow);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvwCode.getText();
            }
        }

        public ItemAdapter(Context context, List<RecordPower> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record_power_log_dpt, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder rv, int position) {
            final ViewHolder holder = (ViewHolder) rv;
            holder.vo = mValues.get(position);
//            holder.tvwCode.setText(holder.vo.getCode());
//            holder.tvwUpdateTime.setText(holder.vo.getUpdateTime());
//
//            holder.tvwBegin.setText(holder.vo.getBegin());
//            holder.tvwEnd.setText(holder.vo.getEnd());
//            holder.tvwBlow.setText(holder.vo.getBlow());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Activity activity = (Activity) v.getContext();
//                    final Intent intent = new Intent(context, RecordPowerMainActivity.class);
//                    intent.putExtra(RecordPowerMainActivity.EXTRA_NAME, holder.vo);
//                    // ActivityTransitionLauncher.with(activity).from(v.findViewById(R.id.ivwMap)).launch(intent);
//                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
