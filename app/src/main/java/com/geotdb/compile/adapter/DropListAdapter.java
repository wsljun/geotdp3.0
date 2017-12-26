package com.geotdb.compile.adapter;

import java.util.List;

import com.geotdb.compile.R;
import com.geotdb.compile.vo.DropItemVo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DropListAdapter extends ArrayAdapter {
    private int mResourceId;
    List<DropItemVo> list;

    public DropListAdapter(Context context, int resource, List<DropItemVo> list) {
        super(context, resource, list);
        this.mResourceId = resource;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            DropItemVo item = list.get(position);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mResourceId, null);
            convertView.setTag(item);
            TextView titleTvw = (TextView) convertView.findViewById(R.id.title);
            titleTvw.setText(item.getName());
            return convertView;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public void insert(Object object, int index) {
        super.insert(object, index);
    }

    @Override
    public Object getItem(int location) {
        //由于列表里面添加了编号，以空格为分割点
        String s = list.get(location).getValue().toString();
        String[] arr = s.split(" ");
        return arr[0];
    }


}
