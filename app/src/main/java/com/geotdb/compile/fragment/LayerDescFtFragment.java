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
import com.geotdb.compile.adapter.DropListAdapter;
import com.geotdb.compile.R;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 地层分类(粉土) 的片段.
 */
public class LayerDescFtFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprBhw;       //包含物
    private MaterialBetterSpinner sprJc;        //夹层
    private MaterialBetterSpinner sprSd;        //湿度
    private MaterialBetterSpinner sprMsd;       //密实度

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprJcList;
    private List<DropItemVo> sprSdList;
    private List<DropItemVo> sprMsdList;

    private String mContent;
    private static final String KEY_CONTENT = "TestFragment:st";

    private int sortNoYs = 0;

    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;


    StringBuilder jcStr = new StringBuilder();
    MaterialDialog jcDialog;
    private List<String> jcList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_ft, null);

        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprBhw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprBhw);
        sprJc = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJc);
        sprSd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprSd);
        sprMsd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMsd);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("粉土_颜色"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("粉土_包含物"));
        sprJcList = dictionaryDao.getDropItemList(getSqlString("粉土_夹层"));
        sprSdList = dictionaryDao.getDropItemList(getSqlString("粉土_湿度"));
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("粉土_密实度"));

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
                                            dictionaryList.add(new Dictionary("1", "粉土_包含物", input.toString(), bhwList.size() + "", relateID, Record.TYPE_LAYER));
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

        jcList = new ArrayList<>();
        jcList = DropItemVo.getStrList(sprJcList);

        sprJc.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (jcDialog == null) {
                    jcDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(jcList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    jcStr.delete(0, jcStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) jcStr.append(',');
                                        jcStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprJc.setText(jcStr.toString());
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
                                            jcList.add(input.toString());
                                            dialog.dismiss();
                                            jcDialog.getBuilder().items(jcList);
                                            jcDialog.show();
                                            dictionaryList.add(new Dictionary("1", "粉土_夹层", input.toString(), jcList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprJc.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                jcDialog.show();
            }
        });

        sprYs.setAdapter(context, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprSd.setAdapter(context, sprSdList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(context, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        initValue();
        return convertView;
    }
    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void setAdapter(MaterialBetterSpinner spr, List<DropItemVo> list, DropListAdapter adapter) {
        adapter = new DropListAdapter(context, R.layout.drop_item, list);
        spr.setAdapter(adapter);
    }

    private void dcmsst_makedcms() {                        //生成说明
//        String  zfc,zfc1;
//        zfc="";zfc1="";
//
//        //将表中的 《颜色》《包含物》《湿度》《密实度》《夹层》数据转到数组中
//        zfc1=dcms_ft_ys.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"颜色:"+zfc1;
//        zfc1=dcms_ft_bhw.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   包含物:"+zfc1;
//        zfc1=dcms_ft_sd.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   湿度:"+zfc1;
//        zfc1=dcms_ft_msd.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   密实度:"+zfc1;
//        zfc1=dcms_ft_jc.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   夹层:"+zfc1;
//
//        dcms_ft_dcms.setText(zfc);
    }


    private void initValue() {
        sprYs.setText(record.getYs());
        sprBhw.setText(record.getBhw());
        sprJc.setText(record.getJc());
        sprSd.setText(record.getSd());
        sprMsd.setText(record.getMsd());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        record.setJc(sprJc.getText().toString());
        record.setSd(sprSd.getText().toString());
        record.setMsd(sprMsd.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉土_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}