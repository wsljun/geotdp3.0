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
 * 地层分类(黄土状粉土) 的片段.
 */
public class LayerDescHtzFtFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprMsd;       //密实度
    private MaterialBetterSpinner sprKlzc;      //颗粒组成
    private MaterialBetterSpinner sprKx;        //孔隙
    private MaterialBetterSpinner sprCzjl;      //垂直节理
    private MaterialBetterSpinner sprBhw;       //包含物

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprMsdList;
    private List<DropItemVo> sprKlzcList;
    private List<DropItemVo> sprKxList;
    private List<DropItemVo> sprCzjlList;
    private List<DropItemVo> sprBhwList;

    private String mContent;
    private static final String KEY_CONTENT = "TestFragment:st";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;

    private int sortNoYs = 0;
    private int sortNoKlzc = 0;
    private int sortNoKx = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_htzft, null);

        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprMsd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMsd);
        sprKlzc = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKlzc);
        sprKx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKx);
        sprCzjl = (MaterialBetterSpinner) convertView.findViewById(R.id.sprCzjl);
        sprBhw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprBhw);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("黄土状粉土_颜色"));
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("黄土状粉土_密实度"));
        sprKlzcList = dictionaryDao.getDropItemList(getSqlString("黄土状粉土_颗粒组成"));
        sprKxList = dictionaryDao.getDropItemList(getSqlString("黄土状粉土_孔隙"));
        sprCzjlList = dictionaryDao.getDropItemList(getSqlString("黄土状粉土_垂直节理"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("黄土状粉土_包含物"));


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
                                            dictionaryList.add(new Dictionary("1", "黄土状粉土_包含物", input.toString(), bhwList.size() + "", relateID, Record.TYPE_LAYER));
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

        sprYs.setAdapter(context, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprMsd.setAdapter(context, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        sprKlzc.setAdapter(context, sprKlzcList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKlzc.setOnItemClickListener(klzcListener);
        sprKx.setAdapter(context, sprKxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKx.setOnItemClickListener(kxListener);
        sprCzjl.setAdapter(context, sprCzjlList, MaterialBetterSpinner.MODE_CLEAR);

        initValue();
        return convertView;
    }

    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener klzcListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoKlzc = i == adapterView.getCount() - 1 ? i : 0;
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
        sprMsd.setText(record.getMsd());
        sprKlzc.setText(record.getKlzc());
        sprKx.setText(record.getKx());
        sprCzjl.setText(record.getCzjl());
        sprBhw.setText(record.getBhw());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setMsd(sprMsd.getText().toString());
        record.setKlzc(sprKlzc.getText().toString());
        record.setKx(sprKx.getText().toString());
        record.setCzjl(sprCzjl.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土状粉土_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (sortNoKlzc > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土状粉土_颗粒组成", sprKlzc.getText().toString(), "" + sortNoKlzc, relateID, Record.TYPE_LAYER));
        }
        if (sortNoKx > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土状粉土_孔隙", sprKx.getText().toString(), "" + sortNoKx, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}