package com.geotdb.compile.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.adapter.RelateHoleAdapter;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.view.MaterialEditTextNoEmoji;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.LocalUser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 获取勘察点列表
 */
public class RelateHoleDialog extends DialogFragment {
    private RecyclerView recyclerView;
    private Context mContext;
    private List<Hole> list;
    private RelateHoleAdapter relateHoleAdapter;

    private MaterialEditTextNoEmoji search;
    private TextView prompt;
    private List<Hole> checkList;//存放选择的hole
    private List<LocalUser> localUserList;//存放選擇的user
    private int have;
    public List<Hole> holeList;
    public void show(AppCompatActivity context, List<Hole> list, int have) {
        this.mContext = context;
        for (Hole hole : list) {
            if (hole.getUserList() == null || hole.getUserList().size() == 0) {
                hole.setUserCount(0);
            } else {
                hole.setUserCount(hole.getUserList().size());
            }
        }
        Collections.sort(list, new Comparator<Hole>() {
            @Override
            public int compare(Hole o1, Hole o2) {
                int i = o1.getUserCount() - o2.getUserCount();
                if (i == 0) {
                    if (o1.getCode() != null && o2.getCode() != null) {
                        return o1.getCode().compareTo(o2.getCode());
                    }
                }
                return i;
            }
        });
        this.list = list;
        this.have = have;
        show(context.getFragmentManager(), "RelateHoleDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);

        builder.customView(R.layout.dlg_relate_hole, true);
        builder.negativeText(R.string.disagree);
        builder.btnSelector(R.drawable.md_btn_cancel_selector_custom, DialogAction.POSITIVE);
        builder.btnStackedGravity(GravityEnum.CENTER);
        if (have == RelateHoleAdapter.HAVE_ALL) {
            builder.title(R.string.relate_hole_get);
        } else {
            builder.title(R.string.relate_hole_relate);
        }
        if (have == RelateHoleAdapter.HAVE_ALL || have == RelateHoleAdapter.HAVE_SOME) {
            builder.positiveText(R.string.agree);
            builder.callback(positive);
        }
        MaterialDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失
        recyclerView = (RecyclerView) dialog.findViewById(R.id.relate_hole_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        relateHoleAdapter = new RelateHoleAdapter(mContext, list, have);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(relateHoleAdapter);
        relateHoleAdapter.setOnItemListener(onItemListener);
        checkList = new ArrayList<>();
        localUserList = new ArrayList<>();

        search = (MaterialEditTextNoEmoji) dialog.findViewById(R.id.search);
        search.addTextChangedListener(textWatcher);
        prompt = (TextView) dialog.findViewById(R.id.prompt);
        return dialog;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = search.getText().toString();
            holeList = new ArrayList<>();
            if (!TextUtils.isEmpty(str)) {
                holeList.addAll(search(list, str));
                if (holeList.size() > 0) {
                    relateHoleAdapter = new RelateHoleAdapter(mContext, holeList, have);
                    recyclerView.setVisibility(View.VISIBLE);
                    prompt.setVisibility(View.GONE);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(relateHoleAdapter);
                    relateHoleAdapter.setOnItemListener(onItemListener);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    prompt.setVisibility(View.VISIBLE);
                }
            } else {
                holeList.addAll(list);
                recyclerView.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.GONE);
                relateHoleAdapter = new RelateHoleAdapter(mContext, holeList, have);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(relateHoleAdapter);
                relateHoleAdapter.setOnItemListener(onItemListener);
            }
        }
    };

    /**
     * 筛选list
     *
     * @param list 要筛选的list
     * @param key  筛选的key
     * @param <T>
     * @return
     */
    private <T> List<T> search(List<T> list, String key) {
        //如果查询的值不是空的就走进来然后返回搜索后的值，否则返回原本的值
        if (list != null && list.size() > 0) {
            //new一个新的容器
            List<T> area = new ArrayList<>();
            boolean isok;
            //循环olist集合
            for (T t : list) {
                //判断a里面如果包含了搜索的值，有就添加，没有否则就不添加(会查出属性名)
                //if (t.toString().toUpperCase().indexOf(str) != -1)
                //area.add(t);
                isok = false;
                //遍历实体类，获取属性名和属性值
                for (Field field : t.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    //下面是指定要查的属性
                    //Item_FirstLetter Item_Name  Item_Col1
                    switch (field.getName()) {
                        case "code":
                            try {
                                isok = field.get(t).toString().toLowerCase().contains(key.toLowerCase());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    if (isok)
                        break;
                }
                if (isok)
                    area.add(t);
            }
            return area;
        } else {
            return list;
        }
    }

    /**
     * dialog 确定监听，做关联、获取操作
     */
    MaterialDialog.ButtonCallback positive = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            if (checkList.size() > 0) {
                onRelateOrGet.onRelate(checkList);
            }
            if (relateHoleAdapter.getLocalUserList().size() > 0) {
                onRelateOrGet.onGet(relateHoleAdapter.getLocalUserList());
            }
        }
    };


    RelateHoleAdapter.OnItemListener onItemListener = new RelateHoleAdapter.OnItemListener() {
        @Override
        public void onItemClick(int position) {
            //hole编辑界面才需要这个动作
            if (have == RelateHoleAdapter.HAVE_NOALL) {
                onListItemListener.onItemClick(position);
            } else {
                addOrRemove(position);
            }
        }

        @Override
        public void checkBoxClick(int position) {
            addOrRemove(position);
        }
    };

    private void addOrRemove(int position) {
        if (checkList.contains(relateHoleAdapter.getItem(position))) {
            checkList.remove(relateHoleAdapter.getItem(position));
        } else {
            checkList.add(relateHoleAdapter.getItem(position));
        }
    }

    private OnRelateOrGet onRelateOrGet;

    public interface OnRelateOrGet {
        void onRelate(List<Hole> checkList);

        void onGet(List<LocalUser> localUserList);
    }

    //在projectMainActivity中实现
    public void OnRelateOrGet(OnRelateOrGet onRelateOrGet) {
        this.onRelateOrGet = onRelateOrGet;
    }

    private OnListItemListener onListItemListener;

    public void OnListItemListener(OnListItemListener onListItemListener) {
        this.onListItemListener = onListItemListener;
    }

    //在HoleEditActivity中实现
    public interface OnListItemListener {
        void onItemClick(int position);
    }

}
