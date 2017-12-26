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
 * 地层分类(碎石土) 的片段.
 */
public class LayerDescSstFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;        //颜色
    private MaterialBetterSpinner sprMsd;        //密实度
    private MaterialBetterSpinner sprTcw;        //填充物
    private MaterialBetterSpinner sprKlxz;      //颗粒形状
    private MaterialBetterSpinner sprKlpl;      //颗粒排列
    private MaterialBetterSpinner sprYbljx;     //一般粒径小
    private MaterialBetterSpinner sprYbljd;     //一般粒径大
    private MaterialBetterSpinner sprJdljx;     //较大粒径小
    private MaterialBetterSpinner sprJdljd;     //较大粒径大
    private MaterialBetterSpinner sprZdlj;      //最大粒径
    private MaterialBetterSpinner sprMycf;      //岩母成份
    private MaterialBetterSpinner sprFhcd;      //风化程度
    private MaterialBetterSpinner sprKljp;      //颗粒级配
    private MaterialBetterSpinner sprSd;        //湿度
    private MaterialBetterSpinner sprJc;        //夹层

    private List<DropItemVo> sprYsList;        //颜色
    private List<DropItemVo> sprMsdList;        //密实度
    private List<DropItemVo> sprTcwList;        //填充物
    private List<DropItemVo> sprKlxzList;      //颗粒形状
    private List<DropItemVo> sprKlplList;      //颗粒排列
    private List<DropItemVo> sprYbljxList;     //一般粒径小
    private List<DropItemVo> sprYbljdList;     //一般粒径大
    private List<DropItemVo> sprJdljxList;     //较大粒径小
    private List<DropItemVo> sprJdljdList;     //较大粒径大
    private List<DropItemVo> sprZdljList;      //最大粒径
    private List<DropItemVo> sprMycfList;      //岩母成份
    private List<DropItemVo> sprFhcdList;      //风化程度
    private List<DropItemVo> sprKljpList;      //颗粒级配
    private List<DropItemVo> sprSdList;        //湿度
    private List<DropItemVo> sprJcList;        //夹层

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    StringBuilder mycfStr = new StringBuilder();
    MaterialDialog mycfDialog;
    private List<String> mycfList;

    StringBuilder jcStr = new StringBuilder();
    MaterialDialog jcDialog;
    private List<String> jcList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_cst, null);

        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);        //颜色
        sprMsd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMsd);        //密实度
        sprTcw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprTcw);        //充填物
        sprKlxz = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKlxz);      //颗粒形状
        sprKlpl = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKlpl);      //颗粒排列
        sprYbljx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYbljx);     //一般粒径小
        sprYbljd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYbljd);     //一般粒径大
        sprJdljx = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJdljx);     //较大粒径小
        sprJdljd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJdljd);     //较大粒径大
        sprZdlj = (MaterialBetterSpinner) convertView.findViewById(R.id.sprZdlj);      //最大粒径
        sprMycf = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMycf);      //岩母成份
        sprFhcd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprFhcd);      //风化程度
        sprKljp = (MaterialBetterSpinner) convertView.findViewById(R.id.sprKljp);      //颗粒级配
        sprSd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprSd);        //湿度
        sprJc = (MaterialBetterSpinner) convertView.findViewById(R.id.sprJc);        //夹层


        sprYsList = dictionaryDao.getDropItemList(getSqlString("碎石土_颜色"));        //颜色
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("碎石土_密实度"));        //密实度
        sprTcwList = dictionaryDao.getDropItemList(getSqlString("碎石土_充填物"));        //充填物
        sprKlxzList = dictionaryDao.getDropItemList(getSqlString("碎石土_颗粒形状"));      //颗粒形状
        sprKlplList = dictionaryDao.getDropItemList(getSqlString("碎石土_颗粒排列"));      //颗粒排列
        sprYbljxList = dictionaryDao.getDropItemList(getSqlString("碎石土_一般粒径小"));     //一般粒径小
        sprYbljdList = dictionaryDao.getDropItemList(getSqlString("碎石土_一般粒径大"));     //一般粒径大
        sprJdljxList = dictionaryDao.getDropItemList(getSqlString("碎石土_较大粒径小"));     //较大粒径小
        sprJdljdList = dictionaryDao.getDropItemList(getSqlString("碎石土_较大粒径大"));     //较大粒径大
        sprZdljList = dictionaryDao.getDropItemList(getSqlString("碎石土_最大粒径"));      //最大粒径
        sprMycfList = dictionaryDao.getDropItemList(getSqlString("碎石土_母岩成份"));      //岩母成份
        sprFhcdList = dictionaryDao.getDropItemList(getSqlString("碎石土_风化程度"));      //风化程度
        sprKljpList = dictionaryDao.getDropItemList(getSqlString("碎石土_颗粒级配"));      //颗粒级配
        sprSdList = dictionaryDao.getDropItemList(getSqlString("碎石土_湿度"));        //湿度
        sprJcList = dictionaryDao.getDropItemList(getSqlString("碎石土_夹层"));        //夹层

        sprYs.setAdapter(context, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprMsd.setAdapter(context, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        sprTcw.setAdapter(context, sprTcwList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprTcw.setOnItemClickListener(tcwListener);
        sprKlxz.setAdapter(context, sprKlxzList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKlxz.setOnItemClickListener(klxzListener);
        sprKlpl.setAdapter(context, sprKlplList, MaterialBetterSpinner.MODE_CLEAR);
        sprYbljx.setAdapter(context, sprYbljxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYbljx.setOnItemClickListener(bljxListener);
        sprYbljd.setAdapter(context, sprYbljdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYbljd.setOnItemClickListener(ybljdListener);
        sprJdljx.setAdapter(context, sprJdljxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprJdljx.setOnItemClickListener(jdljxListener);
        sprJdljd.setAdapter(context, sprJdljdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprJdljd.setOnItemClickListener(jdljdListener);
        sprZdlj.setAdapter(context, sprZdljList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprZdlj.setOnItemClickListener(zdljListener);
        sprFhcd.setAdapter(context, sprFhcdList, MaterialBetterSpinner.MODE_CLEAR);
        sprKljp.setAdapter(context, sprKljpList, MaterialBetterSpinner.MODE_CLEAR);
        sprSd.setAdapter(context, sprSdList, MaterialBetterSpinner.MODE_CLEAR);


        mycfList = new ArrayList<>();
        mycfList = DropItemVo.getStrList(sprMycfList);

        sprMycf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (mycfDialog == null) {
                    mycfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(mycfList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    mycfStr.delete(0, mycfStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) mycfStr.append(',');
                                        mycfStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprMycf.setText(mycfStr.toString());
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
                                            mycfList.add(input.toString());
                                            dialog.dismiss();
                                            mycfDialog.getBuilder().items(mycfList);
                                            mycfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "碎石土_母岩成份", input.toString(), mycfList.size() + "", relateID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprMycf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                mycfDialog.show();
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
                                            dictionaryList.add(new Dictionary("1", "碎石土_夹层", input.toString(), jcList.size() + "", relateID, Record.TYPE_LAYER));
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
        initValue();
        return convertView;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "淤泥_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (sortNoTcw > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_充填物", sprTcw.getText().toString(), "" + sortNoTcw, relateID, Record.TYPE_LAYER));
        }
        if (sortNoKlxz > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_颗粒形状", sprKlxz.getText().toString(), "" + sortNoKlxz, relateID, Record.TYPE_LAYER));
        }
        if (sortNoYbljx > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_一般粒径小", sprYbljx.getText().toString(), "" + sortNoYbljx, relateID, Record.TYPE_LAYER));
        }
        if (sortNoYbljd > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_一般粒径大", sprYbljd.getText().toString(), "" + sortNoYbljd, relateID, Record.TYPE_LAYER));
        }
        if (sortNoJdljx > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_较大粒径小", sprJdljx.getText().toString(), "" + sortNoJdljx, relateID, Record.TYPE_LAYER));
        }
        if (sortNoJdljd > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_较大粒径大", sprJdljd.getText().toString(), "" + sortNoJdljd, relateID, Record.TYPE_LAYER));
        }
        if (sortNoZdlj > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "碎石土_最大粒径", sprZdlj.getText().toString(), "" + sortNoZdlj, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }

    private int sortNoYs = 0;
    private int sortNoTcw = 0;
    private int sortNoKlxz = 0;
    private int sortNoYbljx = 0;
    private int sortNoYbljd = 0;
    private int sortNoJdljx = 0;
    private int sortNoJdljd = 0;
    private int sortNoZdlj = 0;
    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener tcwListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoTcw = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener klxzListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoKlxz = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener bljxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYbljx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener ybljdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYbljd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener jdljxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoJdljx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener jdljdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoJdljd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener zdljListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoZdlj = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    private void initValue() {
        sprYs.setText(record.getYs());
        sprMsd.setText(record.getMsd());
        sprTcw.setText(record.getTcw());
        sprKlxz.setText(record.getKlxz());
        sprKlpl.setText(record.getKlpl());
        sprYbljx.setText(record.getYbljx());
        sprYbljd.setText(record.getYbljd());
        sprJdljx.setText(record.getJdljx());
        sprJdljd.setText(record.getJdljd());
        sprZdlj.setText(record.getZdlj());
        sprMycf.setText(record.getMycf());
        sprFhcd.setText(record.getFhcd());
        sprKljp.setText(record.getKljp());
        sprSd.setText(record.getSd());
        sprJc.setText(record.getJc());

    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setMsd(sprMsd.getText().toString());
        record.setTcw(sprTcw.getText().toString());
        record.setKlxz(sprKlxz.getText().toString());
        record.setKlpl(sprKlpl.getText().toString());
        record.setYbljx(sprYbljx.getText().toString());
        record.setYbljd(sprYbljd.getText().toString());
        record.setJdljx(sprJdljx.getText().toString());
        record.setJdljd(sprJdljd.getText().toString());
        record.setZdlj(sprZdlj.getText().toString());
        record.setMycf(sprMycf.getText().toString());
        record.setFhcd(sprFhcd.getText().toString());
        record.setKljp(sprKljp.getText().toString());
        record.setSd(sprSd.getText().toString());
        record.setJc(sprJc.getText().toString());
        return record;
    }


}