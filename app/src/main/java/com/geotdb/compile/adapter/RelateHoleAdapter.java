package com.geotdb.compile.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.LocalUser;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RelateHoleAdapter extends RecyclerView.Adapter<RelateHoleAdapter.MyViewHolder> {
    private List<Hole> list;
    private Context context;
    private LayoutInflater mInflater;

    private List<LocalUser> localUserList;//选择的user列表
    public static final int HAVE_NOALL = 1;//没有选择框 編輯界面
    public static final int HAVE_SOME = 2;//勘察点有 關聯勘察點
    public static final int HAVE_ALL = 3;//人物有 獲取數據
    private int have;

    public List<LocalUser> getLocalUserList() {
        return localUserList;
    }

    public void setLocalUserList(List<LocalUser> localUserList) {
        this.localUserList = localUserList;
    }

    public RelateHoleAdapter(Context context, List<Hole> list, int have) {
        this.context = context;
        this.list = list;
        this.have = have;
        mInflater = LayoutInflater.from(context);
        localUserList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.list_item_hole_relate, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(rootView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.relate_code_tv.setText(list.get(position).getCode());
        int h = RelateHoleUserAdapter.HAVE_USER_NO;//区分二级list的是否需要checkBox
        switch (have) {
            case HAVE_NOALL:
                holder.relate_hole_check.setVisibility(View.GONE);
                h = RelateHoleUserAdapter.HAVE_USER_NO;
                break;
            case HAVE_SOME:
                holder.relate_hole_check.setVisibility(View.VISIBLE);
                h = RelateHoleUserAdapter.HAVE_USER_NO;
                break;
            case HAVE_ALL:
                holder.relate_hole_check.setVisibility(View.GONE);
                h = RelateHoleUserAdapter.HAVE_USER_ALL;
                break;
        }
        final List<LocalUser> userList;
        final RelateHoleUserAdapter relateHoleUserAdapter;
        if (null != list.get(position).getUserList() && list.get(position).getUserList().size() > 0) {
            userList = list.get(position).getUserList();
            holder.relate_hole_open.setVisibility(View.VISIBLE);
        } else {
            userList = new ArrayList<>();
            holder.relate_hole_open.setVisibility(View.GONE);
        }
        holder.relate_size_tv.setText("已关联人数:" + userList.size());

        ViewGroup.LayoutParams layoutParams = holder.recyclerView.getLayoutParams();
        layoutParams.height = userList.size() * dip2px(50);
        holder.recyclerView.setLayoutParams(layoutParams);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        relateHoleUserAdapter = new RelateHoleUserAdapter(context, list.get(position).getUserList(), h);
        holder.recyclerView.setAdapter(relateHoleUserAdapter);
        if (h == RelateHoleUserAdapter.HAVE_USER_ALL) {
            //实现子list发布的点击事件
            relateHoleUserAdapter.setOnItemListener(new RelateHoleUserAdapter.OnItemListener() {
                @Override
                public void onItemClick(int position) {
                    addOrRemove(relateHoleUserAdapter, position);
                }

                @Override
                public void checkBoxClick(int position) {
                    addOrRemove(relateHoleUserAdapter, position);
                }
            });
        }
        //hole列表展開按鈕点击事件
        holder.relate_hole_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.e("hole列表展開按鈕点击事件");
                if (holder.relate_hole_open.isChecked()) {
                    holder.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    holder.recyclerView.setVisibility(View.GONE);
                }
            }
        });
        //hole列表relativeLayout点击事件
        holder.relate_hole_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.e("hole列表relativeLayout点击事件");
                if (have == HAVE_SOME || have == HAVE_NOALL) {
                    if (holder.relate_hole_check.isChecked()) {
                        holder.relate_hole_check.setChecked(false);
                    } else {
                        holder.relate_hole_check.setChecked(true);
                    }
                    mOnItemListener.onItemClick(position);
                }

            }
        });
        //hole列表checkBox点击事件
        holder.relate_hole_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.e("hole列表checkBox点击事件");
                mOnItemListener.checkBoxClick(position);
            }
        });

    }

    //添加或者删除userList中的项
    private void addOrRemove(RelateHoleUserAdapter relateHoleUserAdapter, int position) {
        if (localUserList.contains(relateHoleUserAdapter.getItem(position))) {
            localUserList.remove(relateHoleUserAdapter.getItem(position));
        } else {
            localUserList.add(relateHoleUserAdapter.getItem(position));
        }
    }


    boolean isSelectAll;

    //hole列表点击时，全选或者全部选其子项
    private void selectAll(RelateHoleUserAdapter relateHoleUserAdapter, List<LocalUser> uList) {
        if (!isSelectAll) {
            isSelectAll = true;
            for (int i = 0; i < uList.size(); i++) {
                if (uList.contains(relateHoleUserAdapter.getItem(i))) {
                    uList.remove(relateHoleUserAdapter.getItem(i));
                } else {
                    uList.add(relateHoleUserAdapter.getItem(i));
                }
                uList.get(i).isSelect = true;
            }
            relateHoleUserAdapter.notifyDataSetChanged();
        } else {
            isSelectAll = false;
            for (int i = 0; i < uList.size(); i++) {
                if (uList.contains(relateHoleUserAdapter.getItem(i))) {
                    uList.remove(relateHoleUserAdapter.getItem(i));
                } else {
                    uList.add(relateHoleUserAdapter.getItem(i));
                }
                uList.get(i).isSelect = false;
            }
            relateHoleUserAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 将dp转化为px
     */
    private int dip2px(float dip) {
        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return (int) (v + 0.5f);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);

        void checkBoxClick(int position);
    }

    public Hole getItem(int position) {
        return list.get(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView relate_code_tv;
        TextView relate_size_tv;
        RelativeLayout relate_hole_rl;
        RecyclerView recyclerView;
        CheckBox relate_hole_open;
        CheckBox relate_hole_check;

        public MyViewHolder(View itemView) {
            super(itemView);
            relate_code_tv = (TextView) itemView.findViewById(R.id.relate_code_tv);
            relate_size_tv = (TextView) itemView.findViewById(R.id.relate_size_tv);
            relate_hole_rl = (RelativeLayout) itemView.findViewById(R.id.relate_hole_rl);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.relate_hole_userlist);
            relate_hole_open = (CheckBox) itemView.findViewById(R.id.relate_hole_open);
            relate_hole_check = (CheckBox) itemView.findViewById(R.id.relate_hole_check);
        }

    }
}
