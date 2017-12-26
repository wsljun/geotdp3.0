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
 * 地层分类(冲填土) 的片段.
 */
public class LayerDescCttFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprWzcf;      //物质成份
    private MaterialBetterSpinner sprDjnd;      //堆积年代
    private MaterialBetterSpinner sprMsd;       //密实度
    private MaterialBetterSpinner sprJyx;       //均匀性

    private List<DropItemVo> sprWzcfList;
    private List<DropItemVo> sprDjndList;
    private List<DropItemVo> sprMsdList;
    private List<DropItemVo> sprJyxList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    StringBuilder str = new StringBuilder();
    MaterialDialog wzcfDialog;
    List<String> wzcfList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_ctt, null);
        sprWzcf = (MaterialBetterSpinner) convertView.findViewById(R.id.sprWzcf);
        sprDjnd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprDjnd);
        sprMsd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMsd);
        sprJyx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJyx);

        sprDjndList = dictionaryDao.getDropItemList(getSqlString("冲填土_堆积年代"));                //颗粒级配
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("冲填土_密实度"));                //颗粒形状
        sprJyxList = dictionaryDao.getDropItemList(getSqlString("冲填土_均匀性"));                    //湿度
        sprWzcfList = dictionaryDao.getDropItemList(getSqlString("冲填土_状态"));
        wzcfList = new ArrayList<>();
        wzcfList = DropItemVo.getStrList(sprWzcfList);
//        wzcfList.add("以泥沙为主");
        sprWzcf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (wzcfDialog == null) {
                    wzcfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(wzcfList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    str.delete(0, str.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) str.append(',');
                                        str.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprWzcf.setText(str.toString());
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
                                            wzcfList.add(input.toString());
                                            dialog.dismiss();
                                            wzcfDialog.getBuilder().items(wzcfList);
                                            wzcfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "冲填土_状态", input.toString(), wzcfList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprWzcf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                wzcfDialog.show();
            }
        });

        sprDjnd.setAdapter(context, sprDjndList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(context, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        sprJyx.setAdapter(context, sprJyxList, MaterialBetterSpinner.MODE_CLEAR);
        initValue();
        return convertView;
    }

    private void initValue() {
        sprWzcf.setText(record.getWzcf());
        sprDjnd.setText(record.getDjnd());
        sprMsd.setText(record.getMsd());
        sprJyx.setText(record.getJyx());
    }

    @Override
    public Record getRecord() {
        record.setWzcf(sprWzcf.getText().toString());
        record.setDjnd(sprDjnd.getText().toString());
        record.setMsd(sprMsd.getText().toString());
        record.setJyx(sprJyx.getText().toString());
//        record.setSd(sprMsd.getText().toString());
//        record.setMsd(sprMsd.getText().toString());
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