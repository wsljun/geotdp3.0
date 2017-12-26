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
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.view.MaterialBetterSpinner;
import com.geotdb.compile.vo.Dictionary;
import com.geotdb.compile.vo.DropItemVo;
import com.geotdb.compile.vo.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 地层分类(粉黏互层) 的片段.
 */
public class LayerDescFnhcFragment extends LayerDescBaseFragment {

    private MaterialBetterSpinner sprYs;            //颜色
    private MaterialBetterSpinner sprBhw;           //包含物
    private MaterialBetterSpinner sprZt;            //状态
    private MaterialBetterSpinner sprFtfchd;        //粉土分层厚度
    private MaterialBetterSpinner sprFzntfchd;      //粉质黏土分层厚度

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprZtList;
    private List<DropItemVo> sprFtfchdList;
    private List<DropItemVo> sprFzntfchdList;

    private String mContent;
    private static final String KEY_CONTENT = "TestFragment:st";

    private int sortNoYs = 0;
    private int sortNoFtfchd = 0;
    private int sortNoFzntfchd = 0;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_dcms_fnhc, null);
        sprYs = (MaterialBetterSpinner) convertView.findViewById(R.id.sprYs);
        sprBhw = (MaterialBetterSpinner) convertView.findViewById(R.id.sprBhw);
        sprZt = (MaterialBetterSpinner) convertView.findViewById(R.id.sprZt);
        sprFtfchd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprFtfchd);
        sprFzntfchd = (MaterialBetterSpinner) convertView.findViewById(R.id.sprFzntfchd);

        sprYsList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_颜色"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_包含物"));
        sprZtList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_状态"));
        sprFtfchdList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_粉土层厚"));
        sprFzntfchdList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_粉黏层厚"));

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
                                            dictionaryList.add(new Dictionary("1", "粉黏互层_包含物", input.toString(), bhwList.size() + "", relateID, Record.TYPE_LAYER));
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
        sprZt.setAdapter(context, sprZtList, MaterialBetterSpinner.MODE_CLEAR);
        sprFtfchd.setAdapter(context, sprFtfchdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprFtfchd.setOnItemClickListener(FtfchdListener);
        sprFzntfchd.setAdapter(context, sprFzntfchdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprFzntfchd.setOnItemClickListener(FzntfchdListener);
        initValue();
        return convertView;
    }

    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener FtfchdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoFtfchd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener FzntfchdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoFzntfchd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    private void initValue() {
        sprYs.setText(record.getYs());
        sprBhw.setText(record.getBhw());
        sprZt.setText(record.getZt());
        sprFtfchd.setText(record.getFtfchd());
        sprFzntfchd.setText(record.getFzntfchd());
    }

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        record.setZt(sprZt.getText().toString());
        record.setFtfchd(sprFtfchd.getText().toString());
        record.setFzntfchd(sprFzntfchd.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉黏互层_颜色", sprYs.getText().toString(), "" + sortNoYs, relateID, Record.TYPE_LAYER));
        }
        if (sortNoFtfchd > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉黏互层_粉土层厚", sprFtfchd.getText().toString(), "" + sortNoFtfchd, relateID, Record.TYPE_LAYER));
        }
        if (sortNoFzntfchd > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉黏互层_粉黏层厚", sprFzntfchd.getText().toString(), "" + sortNoFzntfchd, relateID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}