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
import com.geotdb.compile.vo.Dictionary;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/17.
 */
public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.MyViewHolder> {

    private List<Dictionary> dictionaryList;
    private Context context;
    private LayoutInflater mInflater;

    public DictionaryAdapter(Context context, List<Dictionary> dictionaryList) {
        this.context = context;
        this.dictionaryList = dictionaryList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.list_item_setting_dictionary, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(rootView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(dictionaryList.get(position).getName());
        holder.sort.setText("(" + dictionaryList.get(position).getForm() + ")" + dictionaryList.get(position).getSort());
        holder.select.setChecked(dictionaryList.get(position).isSelect);
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemListener.checkBoxClick(position);
            }
        });

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.select.isChecked()) {
                    holder.select.setChecked(false);
                } else {
                    holder.select.setChecked(true);
                }
                mOnItemListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dictionaryList.size();
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void checkBoxClick(int position);

        void onItemClick(int position);
    }


    /**
     * ViewHolder
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView sort;
        CheckBox select;
        RelativeLayout rl;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_dictionary_name);
            sort = (TextView) itemView.findViewById(R.id.item_dictionary_sort);
            select = (CheckBox) itemView.findViewById(R.id.item_dictionary_select);
            rl = (RelativeLayout) itemView.findViewById(R.id.item_dictionary_rl);
        }
    }

    public void remove(Dictionary dictionary) {
        dictionaryList.remove(dictionary);
    }

    public Dictionary getItem(int pos) {
        return dictionaryList.get(pos);
    }

}
