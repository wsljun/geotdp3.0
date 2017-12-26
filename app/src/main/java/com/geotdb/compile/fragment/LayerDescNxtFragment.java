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
 * 地层分类(黏性土) 的片段.
 */
public class LayerDescNxtFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprZt;      //状态
    private MaterialBetterSpinner sprBhw;      //包含物
    private MaterialBetterSpinner sprJc;        //夹层

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprZtList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprJcList;

    private int sortNoYs = 0;

    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;


    StringBuilder jcStr = new StringBuilder();
    MaterialDialog jcDialog;
    private List<String> jcList;


    private static final String KEY_CONTENT = "TestFragment:st";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_nxt, null);

        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprZt = (MaterialBetterSpinner) convertView.findViewById(R.id.sprZt);
        sprBhw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprBhw);
        sprJc = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJc);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("黏性土_颜色"));
        sprZtList = dictionaryDao.getDropItemList(getSqlString("黏性土_状态"));
//        addDataBase();
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("黏性土_包含物"));
        sprJcList = dictionaryDao.getDropItemList(getSqlString("黏性土_夹层"));


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
                                            dictionaryList.add(new Dictionary("1", "黏性土_包含物", input.toString(), bhwList.size() + "", relateID, Record.TYPE_LAYER));
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
                                            dictionaryList.add(new Dictionary("1", "黏性土_夹层", input.toString(), jcList.size() + "", relateID, Record.TYPE_LAYER));
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
        sprZt.setAdapter(context, sprZtList, MaterialBetterSpinner.MODE_CLEAR);

        initValue();
        return convertView;
    }
    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    //本地数据库添加字段
//    private void addDataBase() {
//        boolean haveYKS= false;
//        for(DropItemVo dropItemVo:sprZtList){
//            if(!dropItemVo.getName().equals("硬可塑")){
//                haveYKS = false;
//            }else{
//                haveYKS = true;
//                break;
//            }
//        }
//        if(!haveYKS){
//            DropItemVo dropItemVo = new DropItemVo();
//            dropItemVo.setId("217");
//            dropItemVo.setName("硬可塑");
//            dropItemVo.setValue("硬可塑");
//            sprZtList.add(dropItemVo);
//            dictionaryDao.addDictionary(new Dictionary("0","黏性土_状态","硬可塑","8",""));
//        }
//        boolean haveRKS= false;
//        for(DropItemVo dropItemVo:sprZtList){
//            if(!dropItemVo.getName().equals("软可塑")){
//                haveRKS = false;
//            }else{
//                haveRKS = true;
//                break;
//            }
//        }
//        if(!haveRKS){
//            DropItemVo dropItemVo = new DropItemVo();
//            dropItemVo.setId("217");
//            dropItemVo.setName("软可塑");
//            dropItemVo.setValue("软可塑");
//            sprZtList.add(dropItemVo);
//            dictionaryDao.addDictionary(new Dictionary("0","黏性土_状态","软可塑","7",""));
//        }
//    }

    private void initValue() {
        sprYs.setText(record.getYs());
        sprZt.setText(record.getZt());
        sprBhw.setText(record.getBhw());
        sprJc.setText(record.getJc());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setZt(sprZt.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        record.setJc(sprJc.getText().toString());
        return record;
    }


    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黏性土_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}