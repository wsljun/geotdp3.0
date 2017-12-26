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
 * 地层分类(砂土) 的片段.
 */
public class LayerDescStFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprKwzc;                    //矿物组成
    private MaterialBetterSpinner sprYs;                    //颜色
    private MaterialBetterSpinner sprKljp;                //颗粒级配
    private MaterialBetterSpinner sprKlxz;                //颗粒形状
    private MaterialBetterSpinner sprSd;                    //湿度
    private MaterialBetterSpinner sprMsd;                //密实度

    private List<DropItemVo> sprKwzcList;                    //矿物组成
    private List<DropItemVo> sprYsList;                    //颜色
    private List<DropItemVo> sprKljpList;                //颗粒级配
    private List<DropItemVo> sprKlxzList;                //颗粒形状
    private List<DropItemVo> sprSdList;                    //湿度
    private List<DropItemVo> sprMsdList;                //密实度

    private String mContent;
    private static final String KEY_CONTENT = "TestFragment:st";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    //颗粒组成
    StringBuilder kwzcStr = new StringBuilder();
    MaterialDialog kwzcDialog;
    private List<String> kwzcList;
    //颜色
    StringBuilder ysStr = new StringBuilder();
    MaterialDialog ysDialog;
    private List<String> ysList;
    //颗粒形状
    StringBuilder klxzStr = new StringBuilder();
    MaterialDialog klxzDialog;
    private List<String> klxzList;
    //湿度
    StringBuilder sdStr = new StringBuilder();
    MaterialDialog sdDialog;
    private List<String> sdList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_st, null);
        sprKwzc = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKwzc);
        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprKljp = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKljp);
        sprKlxz = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKlxz);
        sprSd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprSd);
        sprMsd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMsd);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("砂土_颜色"));                    //颜色
        sprKljpList = dictionaryDao.getDropItemList(getSqlString("砂土_颗粒级配"));                //颗粒级配
        sprKlxzList = dictionaryDao.getDropItemList(getSqlString("砂土_颗粒形状"));                //颗粒形状
        sprSdList = dictionaryDao.getDropItemList(getSqlString("砂土_湿度"));                    //湿度
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("砂土_密实度"));                //密实度
        sprKwzcList = dictionaryDao.getDropItemList(getSqlString("砂土_矿物组成"));             //矿物组成
        kwzcList = new ArrayList<>();
        kwzcList = DropItemVo.getStrList(sprKwzcList);
//        kwzcList.add("石英");
//        kwzcList.add("云母");
//        kwzcList.add("长石");
        sprKwzc.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (kwzcDialog == null) {
                    kwzcDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(kwzcList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    kwzcStr.delete(0, kwzcStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) kwzcStr.append(',');
                                        kwzcStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprKwzc.setText(kwzcStr.toString());
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
                                            kwzcList.add(input.toString());
                                            dialog.dismiss();
                                            kwzcDialog.getBuilder().items(kwzcList);
                                            kwzcDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_矿物组成", input.toString(), kwzcList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprKwzc.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                kwzcDialog.show();
            }
        });

        ysList = new ArrayList<>();
        ysList = DropItemVo.getStrList(sprYsList);
        sprYs.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (ysDialog == null) {
                    ysDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(ysList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    ysStr.delete(0, ysStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) ysStr.append(',');
                                        ysStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprYs.setText(ysStr.toString());
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
                                            ysList.add(input.toString());
                                            dialog.dismiss();
                                            ysDialog.getBuilder().items(ysList);
                                            ysDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_颜色", input.toString(), ysList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprYs.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                ysDialog.show();
            }
        });

        klxzList = new ArrayList<>();
        klxzList = DropItemVo.getStrList(sprKlxzList);
        sprKlxz.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (klxzDialog == null) {
                    klxzDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(klxzList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    klxzStr.delete(0, klxzStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) klxzStr.append(',');
                                        klxzStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprKlxz.setText(klxzStr.toString());
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
                                            klxzList.add(input.toString());
                                            dialog.dismiss();
                                            klxzDialog.getBuilder().items(klxzList);
                                            klxzDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_颗粒形状", input.toString(), klxzList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprKlxz.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                klxzDialog.show();
            }
        });
        sdList = new ArrayList<>();
        sdList = DropItemVo.getStrList(sprSdList);
        sprSd.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (sdDialog == null) {
                    sdDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(sdList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    sdStr.delete(0, sdStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) sdStr.append(',');
                                        sdStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprSd.setText(sdStr.toString());
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
                                            sdList.add(input.toString());
                                            dialog.dismiss();
                                            sdDialog.getBuilder().items(sdList);
                                            sdDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_湿度", input.toString(), sdList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprSd.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                sdDialog.show();
            }
        });

        sprKljp.setAdapter(context, sprKljpList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(context, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        initValue();
        return convertView;
    }

    private void initValue() {
        sprKwzc.setText(record.getKwzc());
        sprYs.setText(record.getYs());
        sprKljp.setText(record.getKljp());
        sprKlxz.setText(record.getKlxz());
        sprSd.setText(record.getSd());
        sprMsd.setText(record.getMsd());
    }

    @Override
    public Record getRecord() {
        record.setKwzc(sprKwzc.getText().toString());
        record.setYs(sprYs.getText().toString());
        record.setKljp(sprKljp.getText().toString());
        record.setKlxz(sprKlxz.getText().toString());
        record.setSd(sprSd.getText().toString());
        record.setMsd(sprMsd.getText().toString());
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