package com.geotdb.compile.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * 地层分类(填土) 的片段.
 */
public class LayerDescTtFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprZycf;      //主要成份
    private MaterialBetterSpinner sprCycf;      //次要成份
    private MaterialBetterSpinner sprDjnd;      //堆积年代
    private MaterialBetterSpinner sprMsd;       //密实度
    private MaterialBetterSpinner sprJyx;       //均匀性

    private List<DropItemVo> sprDjndList;
    private List<DropItemVo> sprMsdList;
    private List<DropItemVo> sprJyxList;
    private List<DropItemVo> sprZycyList;
    private List<DropItemVo> sprCycfList;

    private String mContent;
    private static final String KEY_CONTENT = "TestFragment:st";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    StringBuilder zycfStr = new StringBuilder();
    MaterialDialog zycfDialog;
    List<String> zycyList;

    StringBuilder cycfStr = new StringBuilder();
    MaterialDialog cycfDialog;
    List<String> cycfList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_tt, null);

        sprZycf = (MaterialBetterSpinner) convertView.findViewById(R.id.sprZycf);
        sprCycf = (MaterialBetterSpinner) convertView.findViewById(R.id.sprCycf);
        sprDjnd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprDjnd);
        sprMsd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMsd);
        sprJyx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJyx);

        sprDjndList = dictionaryDao.getDropItemList(getSqlString("填土_堆积年代"));
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("填土_密实度"));
        sprJyxList = dictionaryDao.getDropItemList(getSqlString("填土_均匀性"));
        sprZycyList = dictionaryDao.getDropItemList(getSqlString("填土_主要成分"));                    // 主要成分
        sprCycfList = dictionaryDao.getDropItemList(getSqlString("填土_次要成分"));                    //次要成分

        zycyList =DropItemVo.getStrList(sprZycyList);
        cycfList =DropItemVo.getStrList(sprCycfList);

//        zycyList = new ArrayList<>();//填土_主要成分
//        zycyList.add("卵石");
//        zycyList.add("角砾");
//        zycyList.add("黏性土");
//        zycyList.add("粉土");
//
//        cycfList = new ArrayList<>();//填土_次要成分
//        cycfList.add("建筑垃圾");
//        cycfList.add("生活垃圾");
//        cycfList.add("碎砖");
//        cycfList.add("块石");
        sprZycf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (zycfDialog == null) {
                    zycfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(zycyList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    zycfStr.delete(0, zycfStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) zycfStr.append(',');
                                        zycfStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprZycf.setText(zycfStr.toString());
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
                                            zycyList.add(input.toString());
                                            dialog.dismiss();
                                            zycfDialog.getBuilder().items(zycyList);
                                            zycfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "填土_主要成分", input.toString(), zycyList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprZycf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                zycfDialog.show();
            }
        });

        sprCycf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (cycfDialog == null) {
                    cycfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(cycfList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    cycfStr.delete(0, cycfStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) cycfStr.append(',');
                                        cycfStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprCycf.setText(cycfStr.toString());
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
                                            cycfList.add(input.toString());
                                            dialog.dismiss();
                                            cycfDialog.getBuilder().items(cycfList);
                                            cycfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "填土_次要成分", input.toString(), cycfList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();

                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprCycf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                cycfDialog.show();
            }
        });

        sprDjnd.setAdapter(context, sprDjndList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(context, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        sprJyx.setAdapter(context, sprJyxList, MaterialBetterSpinner.MODE_CLEAR);
        initValue();
        return convertView;
    }

    private void initValue() {
        sprZycf.setText(record.getZycf());
        sprCycf.setText(record.getCycf());
        sprDjnd.setText(record.getDjnd());
        sprMsd.setText(record.getMsd());
        sprJyx.setText(record.getJyx());
    }

    @Override
    public Record getRecord() {
        record.setZycf(sprZycf.getText().toString());
        record.setCycf(sprCycf.getText().toString());
        record.setDjnd(sprDjnd.getText().toString());
        record.setMsd(sprMsd.getText().toString());
        record.setJyx(sprJyx.getText().toString());
        return record;
    }
    @Override
    public boolean layerValidator() {
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }

}