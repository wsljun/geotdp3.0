package com.geotdb.compile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Project;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 */
public class RelateHoleUserAdapter extends RecyclerView.Adapter<RelateHoleUserAdapter.MyViewHolder> {
    private List<LocalUser> list;
    private Context context;
    private LayoutInflater mInflater;

    public static final int HAVE_USER_NO = 1;//没有，获取列表
    public static final int HAVE_USER_ALL = 2;//有，获取数据时，需要点选
    private int have;//区分是否有checkbox


    public RelateHoleUserAdapter(Context context, List<LocalUser> list, int have) {
        this.context = context;
        this.list = list;
        this.have = have;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.list_item_hole_user_relate, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(rootView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        switch (have) {
            case HAVE_USER_ALL:
                holder.relate_user_check.setVisibility(View.VISIBLE);
                break;
            case HAVE_USER_NO:
                holder.relate_user_check.setVisibility(View.GONE);
                break;
        }
        holder.relate_user_check.setChecked(list.get(position).isSelect);
        holder.userName.setText(list.get(position).getRealName());
        holder.userCode.setText(list.get(position).getCode());
        if (list.get(position).getUploadTime() != null && !"".equals(list.get(position).getUploadTime())) {
            holder.userTime.setText(list.get(position).getUploadTime());
        } else {
            holder.userTime.setText(list.get(position).getRelateTime());
        }
        if (list.get(position).getDeptID() != null && !"".equals(list.get(position).getDeptID())) {
            if (list.get(position).getDeptID().equals(Common.getUserIDBySP(context))) {
                holder.userName.setTextColor(context.getResources().getColor(R.color.met_primaryColor));
            } else {
                holder.userName.setTextColor(context.getResources().getColor(R.color.met_floatingLabelTextColor));
            }
        }
        if (list.get(position).getMobilePhone() != null && !"".equals(list.get(position).getMobilePhone())) {
            holder.userPhone.setText("电话:" + list.get(position).getMobilePhone());
        } else {
            holder.userPhone.setText("电话:无");
        }
        //userList中的relativtLayout的点击事件
        if (have == HAVE_USER_ALL) {
            holder.rlate_user_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.relate_user_check.isChecked()) {
                        holder.relate_user_check.setChecked(false);
                    } else {
                        holder.relate_user_check.setChecked(true);
                    }
                    mOnItemUserListener.onItemClick(position);
                }
            });
        }
        //userList中的checkBox的点击事件
        holder.relate_user_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemUserListener.checkBoxClick(position);
            }
        });
    }

    public LocalUser getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private OnItemListener mOnItemUserListener;

    public void setOnItemListener(OnItemListener mOnItemUserListener) {
        this.mOnItemUserListener = mOnItemUserListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);

        void checkBoxClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userPhone;
        TextView userTime;
        CheckBox relate_user_check;
        RelativeLayout rlate_user_rl;
        TextView userCode;
        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.relate_user_name);
            userPhone = (TextView) itemView.findViewById(R.id.relate_user_phone);
            userTime = (TextView) itemView.findViewById(R.id.relate_user_time);
            relate_user_check = (CheckBox) itemView.findViewById(R.id.relate_user_check);
            rlate_user_rl = (RelativeLayout) itemView.findViewById(R.id.rlate_user_rl);
            userCode = (TextView) itemView.findViewById(R.id.relate_user_code);

        }
    }
}
