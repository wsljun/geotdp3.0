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
import com.geotdb.compile.vo.LayerName;
import com.geotdb.compile.vo.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 取样(取土) 的片段.
 */
public class RecordEditGetEarthFragment extends RecordEditBaseFragment {

    private MaterialBetterSpinner sprEarthType;      //土样质量等级
    private MaterialBetterSpinner sprMode;        //取样工具和方法
    private MaterialBetterSpinner sprTestType;        //试验类型

    List<DropItemVo> earthTypeList;
    List<DropItemVo> earthModeList;
    List<DropItemVo> testTypeList;

    StringBuilder testStr = new StringBuilder();
    MaterialDialog testDialog;
    private List<String> testList;

    private int sortNoMode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_get_earth_edit, null);
        try {
            sprEarthType = (MaterialBetterSpinner) convertView.findViewById(R.id.sprEarthType);
            sprMode = (MaterialBetterSpinner) convertView.findViewById(R.id.sprMode);
            sprTestType = (MaterialBetterSpinner) convertView.findViewById(R.id.sprTestType);

            testList = new ArrayList<>();
            testList = DropItemVo.getStrList(getTestTypeList());
            sprTestType.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
                @Override
                public void onShow() {
                    if (testDialog == null) {
                        testDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(testList)

                                .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                        testStr.delete(0, testStr.length());
                                        for (int i = 0; i < which.length; i++) {
                                            if (i > 0) testStr.append(',');
                                            testStr.append(text[i]);
                                        }
                                        return true;
                                    }
                                }).callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        sprTestType.setText(testStr.toString());
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
                                                testList.add(input.toString());
                                                dialog.dismiss();
                                                testDialog.getBuilder().items(testList);
                                                testDialog.show();
                                                dictionaryList.add(new Dictionary("1", "试验类型", input.toString(), testList.size() + "", relateID,Record.TYPE_GET_EARTH));
                                            }
                                        }).show();
                                    }
                                }).dismissListener(new MaterialDialog.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        sprTestType.setIsPopup(false);
                                    }
                                }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                    }
                    testDialog.show();
                }
            });

            earthTypeList = getEarthTypeList();
            sprEarthType.setAdapter(context, earthTypeList, MaterialBetterSpinner.MODE_CLEAR);
            sprMode.setAdapter(context, getEarthModeList(), MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
            sprMode.setOnItemClickListener(modeListener);
            //设置工具方法为可选可编辑
            //sprMode.setIsEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initValue();
        return convertView;
    }


    MaterialBetterSpinner.OnItemClickListener modeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoMode = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private void initValue() {
        try {
            sprEarthType.setText(record.getEarthType());
            sprMode.setText(record.getGetMode());
            sprTestType.setText(record.getTestType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DropItemVo> getEarthTypeList() {
        earthTypeList = dictionaryDao.getDropItemList(getSqlString("质量等级"));
        addWord();
        return earthTypeList;
    }


    private List<DropItemVo> getEarthModeList() {
        return dictionaryDao.getDropItemList(getSqlString("取土工具和方法"));
    }

    private List<DropItemVo> getTestTypeList() {
        testTypeList = dictionaryDao.getDropItemList(getSqlString("试验类型"));
        return testTypeList;
    }

    //本地数据库添加字段
    private void addWord() {
        boolean haveHS = false;
        for (DropItemVo dropItemVo : earthTypeList) {
            if (dropItemVo.getName().equals("Ⅰ级样")) {
                dropItemVo.setName("Ⅰ级样(不扰动)");
            }
            if (dropItemVo.getName().equals("Ⅱ级样")) {
                dropItemVo.setName("Ⅱ级样(轻微扰动)");
            }
            if (dropItemVo.getName().equals("Ⅲ级样")) {
                dropItemVo.setName("Ⅲ级样(显著扰动)");
            }
            if (dropItemVo.getName().equals("Ⅳ级样")) {
                dropItemVo.setName("Ⅳ级样(完全扰动)");
            }
            //判断是否存在‘岩石字段’
            if (!dropItemVo.getName().equals("岩石")) {
                haveHS = false;
            } else {
                haveHS = true;
                break;
            }
        }
        if (!haveHS) {
            DropItemVo dropItemVo = new DropItemVo();
            dropItemVo.setId("420");
            dropItemVo.setName("岩石");
            dropItemVo.setValue("岩石");
            earthTypeList.add(dropItemVo);
            dictionaryDao.addDictionary(new Dictionary("0","质量等级","岩石","4","",Record.TYPE_GET_EARTH));
        }
    }

    @Override
    public Record getRecord() {
        record.setEarthType(sprEarthType.getText().toString());
        record.setGetMode(sprMode.getText().toString());
        record.setTestType(sprTestType.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprEarthType.getText().toString() + "--" + sprMode.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_GET_EARTH;
    }

    private void dcmsst_makedcms() {                        //生成说明
//        String  zfc,zfc1;
//        zfc="";zfc1="";
//
//        //将表中的 《颜色》《状态》《包含物》《夹层》数据转到数组中
//        zfc1=dcms_nt_ys.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"颜色:"+zfc1;
//        zfc1=dcms_nt_zt.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   状态:"+zfc1;
//        zfc1=dcms_nt_bhw.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   包含物:"+zfc1;
//        zfc1=dcms_nt_jc.getSelectedItem().toString().trim();	if (!zfc1.equals(""))	zfc=zfc+"   夹层:"+zfc1;
//        dcms_nt_dcms.setText(zfc);
    }


    @Override
    public boolean validator() {
        boolean validator = true;
//        if(TextUtils.isEmpty(sprEarthType.getText().toString())){
//            sprEarthType.setError("请选择质量等级");
//            validator = false;
//        }
//        if(TextUtils.isEmpty(sprMode.getText().toString())){
//            sprMode.setError("请选择取样工具和方法");
//            validator = false;
//        }
//        if(TextUtils.isEmpty(sprTestType.getText().toString())){
//            sprTestType.setError("请选择试验内容");
//            validator = false;
//        }

        if (sortNoMode > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1", "取土工具和方法", sprMode.getText().toString(), "" + sortNoMode, relateID,Record.TYPE_GET_EARTH));
        }

        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return validator;
    }
}