package com.geotdb.compile.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.vo.Hole;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21.
 */
public class ReleteLocationAdapter extends RecyclerView.Adapter<ReleteLocationAdapter.ViewHolder> {
    private List<Hole> data;

    public ReleteLocationAdapter(List<Hole> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定布局
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_relate_location, parent, false);
        //创建ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(data.get(position).getCode());
        holder.relate_location_item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CardView relate_location_item_ll;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.relate_location_item_name);
            relate_location_item_ll = (CardView) view.findViewById(R.id.relate_location_item_ll);
        }
    }
}
