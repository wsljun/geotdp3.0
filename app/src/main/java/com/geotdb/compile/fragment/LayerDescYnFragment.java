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
 * 地层分类(淤泥) 的片段.
 */
public class LayerDescYnFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprBhw;       //包含物
    private MaterialBetterSpinner sprHsl;       //含水量
    private MaterialBetterSpinner sprZt;        //状态

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprHslList;
    private List<DropItemVo> sprZtList;

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

    StringBuilder str = new StringBuilder();
    MaterialDialog ztDialog;
    private List<String> ztList;

    private int sortNoYs = 0;
    private int sortNoHsl = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_yn, null);
        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprBhw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprBhw);
        sprHsl = (MaterialBetterSpinner) convertView.findViewById(R.id.sprHsl);
        sprZt = (MaterialBetterSpinner) convertView.findViewById(R.id.sprZt);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("淤泥_颜色"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("淤泥_包含物"));
        sprHslList = dictionaryDao.getDropItemList(getSqlString("淤泥_含水量"));
        sprZtList = dictionaryDao.getDropItemList(getSqlString("淤泥_状态"));

        sprYs.setAdapter(context, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprHsl.setAdapter(context, sprHslList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprHsl.setOnItemClickListener(hslListener);

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
                                            dictionaryList.add(new Dictionary("1", "淤泥_包含物", input.toString(), bhwList.size() + "", relateID, Record.TYPE_LAYER));
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

        ztList = new ArrayList<>();
        ztList = DropItemVo.getStrList(sprZtList);
//        ztList.add("流塑");
        sprZt.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (ztDialog == null) {
                    ztDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(ztList)

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
                                    sprZt.setText(str.toString());
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
                                            ztList.add(input.toString());
                                            dialog.dismiss();
                                            ztDialog.getBuilder().items(ztList);
                                            ztDialog.show();
                                            dictionaryList.add(new Dictionary("1", "淤泥_状态", input.toString(), ztList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprZt.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                ztDialog.show();
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
    MaterialBetterSpinner.OnItemClickListener hslListener= new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoHsl = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void initValue() {
        sprYs.setText(record.getYs());
        sprBhw.setText(record.getBhw());
        sprHsl.setText(record.getHsl());
        sprZt.setText(record.getZt());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        record.setHsl(sprHsl.getText().toString());
        record.setZt(sprZt.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "淤泥_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (sortNoHsl > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "淤泥_含水量", sprHsl.getText().toString(), "" + sortNoHsl, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}

