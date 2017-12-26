package com.geotdb.compile.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 地层分类(黄土状黏性土) 的片段.
 */
public class LayerDescHtznxtFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprZt;        //状态
    private MaterialBetterSpinner sprKx;        //孔隙
    private MaterialBetterSpinner sprCzjl;      //垂直节理
    private MaterialBetterSpinner sprBhw;       //包含物

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprZtList;
    private List<DropItemVo> sprKxList;
    private List<DropItemVo> sprCzjlList;
    private List<DropItemVo> sprBhwList;


    private int sortNoYs = 0;
    private int sortNoKx = 0;
    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_htznxt, null);

        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprZt = (MaterialBetterSpinner) convertView.findViewById(R.id.sprZt);
        sprKx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKx);
        sprCzjl = (MaterialBetterSpinner) convertView.findViewById(R.id.sprCzjl);
        sprBhw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprBhw);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("黄土_颜色"));
        sprZtList = dictionaryDao.getDropItemList(getSqlString("黄土_状态"));
        sprKxList = dictionaryDao.getDropItemList(getSqlString("黄土_孔隙"));
        sprCzjlList = dictionaryDao.getDropItemList(getSqlString("黄土_垂直节理"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("黄土_包含物"));

        sprYs.setAdapter(context, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprZt.setAdapter(context, sprZtList, MaterialBetterSpinner.MODE_CLEAR);
        sprKx.setAdapter(context, sprKxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKx.setOnItemClickListener(kxListener);
        sprCzjl.setAdapter(context, sprCzjlList, MaterialBetterSpinner.MODE_CLEAR);

        bhwList = new ArrayList<>();
        bhwList = DropItemVo.getStrList(sprBhwList);

        sprBhw.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (bhwDialog == null) {
                    bhwDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(bhwList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    bhwStr.delete(0, bhwStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) bhwStr.append(',');
                                        bhwStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprBhw.setText(bhwStr.toString());
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                    new MaterialDialog.Builder(getContext()).title(R.string.custom).inputType(InputType.TYPE_CLASS_TEXT |
                                            InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                            InputType.TYPE_TEXT_FLAG_CAP_WORDS).inputMaxLength(10).input("请输入自定义内容", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            bhwList.add(input.toString());
                                            dialog.dismiss();
                                            bhwDialog.getBuilder().items(bhwList);
                                            bhwDialog.show();
                                            dictionaryList.add(new Dictionary("1", "黄土_包含物", input.toString(), bhwList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprBhw.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                bhwDialog.show();
            }
        });
        initValue();
        return convertView;
    }

    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener kxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoKx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void initValue() {
        sprYs.setText(record.getYs());
        sprZt.setText(record.getZt());
        sprKx.setText(record.getKx());
        sprCzjl.setText(record.getCzjl());
        sprBhw.setText(record.getBhw());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setZt(sprZt.getText().toString());
        record.setKx(sprKx.getText().toString());
        record.setCzjl(sprCzjl.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (sortNoKx > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土_孔隙", sprKx.getText().toString(), "" + sortNoKx, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}