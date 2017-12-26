package com.geotdb.compile.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;

import com.geotdb.compile.R;

import java.text.DecimalFormat;

/**
 */
public class MaterialEditTextInt extends MaterialEditTextMeter {

    DecimalFormat df = new DecimalFormat("##0");
    String str = "0";

    public MaterialEditTextInt(Context context) {
        super(context);
        init(context);
    }

    public MaterialEditTextInt(Context context, AttributeSet arg1) {
        super(context, arg1);
        init(context);
    }

    public MaterialEditTextInt(Context context, AttributeSet arg1, int arg2) {
        super(context, arg1, arg2);
        init(context);
    }

    @Override
    public void init(Context context) {
        this.context = context;
        this.activity = (Activity) context;
        setInputType(InputType.TYPE_NULL);
        setFloatingLabelAlwaysShown(true);
        setText("0");
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_jishu_bold_black_24dp);
        if (dropdownIcon != null && right == null) {
            right = dropdownIcon;
            right.mutate().setAlpha(66);
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    @Override
    public void setText(double d) {
        setCursorVisible(false);
        Editable editable = MaterialEditTextInt.this.getText();
        if (d != 0.00) {
            if (isInit) {
                isInit = false;
                setSelection(editable.length());
            }
        }
        try {
            SpannableString sp = new SpannableString(df.format(d));
//            sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0,1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            sp.setSpan(new BackgroundColorSpan(Color.BLACK), 0,1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //setText(sp);
            editable.clear();
            //editable.append(sp);
            editable.insert(0, sp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setText(String text) {
        double nn = 0;
        if (!"".equals(text)) {
            try {
                nn = Double.valueOf(text);
            } catch (Exception e) {
                nn = Double.valueOf(str);
                e.printStackTrace();
            }
        }
        setText(nn);
    }

    @Override
    public void initKeyboard() {
        keyboardView = (KeyboardView) activity.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(new Keyboard(context, R.xml.symbols_int));
        //如果在这报错,很可能在布局文件里没有定义KeyboardView
//        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setOnKeyboardActionListener(listener);
    }


    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        //一些特殊操作按键的codes是固定的比如完成、回退等
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            try {
                Editable editable = MaterialEditTextInt.this.getText();

                if (editable.length() == 0 && primaryCode != -2) {
                    editable.clear();
                    editable.insert(0, "0");
                    isMode = false;
                    setSelection(1);
                }

                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                    if ("0".equals(editable.toString())) {
                        reset();
                    } else {
                        str = editable.toString();
                        setHint(str);
                    }
                    hideKeyboard();
                } else if (primaryCode == -2) {// 隐藏键盘
                    hideKeyboard();
                } else if (primaryCode == 4896) {// 取消
                    hideKeyboard();
                } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                    if (editable.length() > 0) {
                        if (getSelectionStart() > 0) {
                            if (getSelectionStart() == 1) {
                                editable.replace(0, 1, "0");
                            } else {
                                editable.delete(getSelectionStart() - 1, getSelectionStart());
                            }
                        }
                    }
                } else if (primaryCode == 46) {// 小数点
                } else { //将要输入的数字现在编辑框中
                    double editableDouble = 0;
                    if (editable.length() > 0) {
                        editableDouble = Double.valueOf(editable.toString());
                    }

                    String primaryStr = Character.toString((char) primaryCode);
                    if (editableDouble == 0) {
                        setSelection(1);
                        editable.replace(getSelectionStart() - 1, getSelectionEnd(), primaryStr);
                    } else {
                        if (editable.length() < 4) {
//                            L.e("editable---------" + Integer.parseInt(editable.toString()));
//                            L.e("primaryStr---------" + primaryStr);
//                            if (Integer.parseInt(editable.toString()) >= 5 && Integer.parseInt(primaryStr) > 0) {
//                                setError("最大50");
//                            } else {
//                            }
                            editable.insert(getSelectionStart(), primaryStr);
                        } else {
                            setError("最大9999");
                        }
                        setSelection(editable.length());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                setError("非数字内容");
                reset();
            }
        }
    };

}
