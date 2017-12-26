package com.geotdb.compile.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.geotdb.compile.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * 还有两个实现点.1.点击完成后的焦点转移.2.深度的连贯性.就是终止要比起始大.的验证.
 */
public class MaterialEditTextMeter extends MaterialEditText {

    DecimalFormat df = new DecimalFormat("##0.00");

    KeyboardView keyboardView;
    Context context;
    Activity activity;

    boolean isMode = false; //是否为小数模式
    boolean isInit = true;

    String str = "0.00";


    public MaterialEditTextMeter(Context context) {
        super(context);
        init(context);
    }

    public MaterialEditTextMeter(Context context, AttributeSet arg1) {
        super(context, arg1);
        init(context);
    }

    public MaterialEditTextMeter(Context context, AttributeSet arg1, int arg2) {
        super(context, arg1, arg2);
        init(context);
    }


    public void init(Context context) {
        this.context = context;
        this.activity = (Activity) context;
        setInputType(InputType.TYPE_NULL);
        setFloatingLabelAlwaysShown(true);
        setText(0.00);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_meter_bold_black_24dp);
        if (dropdownIcon != null&&right==null) {
            right = dropdownIcon;
            right.mutate().setAlpha(66);
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    public void setText(double d) {
        setCursorVisible(false);
        Editable editable = MaterialEditTextMeter.this.getText();
        if (d != 0.00) {
            if (isInit) {
                isInit = false;
                double mode = d - (int) d;
                if (mode > 0) {
                    isMode = true;
                } else {
                    setSelection(editable.length() - 3);
                }
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

    public void setText(String text) {
        double nn = 0.0;
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (isEnabled()) {
                    if (keyboardView != null) {
                        if (keyboardView.getVisibility() == View.VISIBLE) {
                            keyboardView.setVisibility(View.GONE);
                        } else {
                            keyboardView.setVisibility(View.VISIBLE);
                        }
                    }
                    requestFocus();
                    //showKeyboard();
                }
                //return false;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            showKeyboard();
        } else {
            if ("".equals(getText().toString())) {
                reset();
            }
            keyboardView.setVisibility(View.GONE);
            setShowClearButton(false);
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    public void initKeyboard() {
        keyboardView = (KeyboardView) activity.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(new Keyboard(context, R.xml.symbols_save));
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
                Editable editable = MaterialEditTextMeter.this.getText();

                if (editable.length() == 0 && primaryCode != -2) {
                    editable.clear();
                    editable.insert(0, "0.00");
                    isMode = false;
                    setSelection(1);
                }

                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                    if ("0.00".equals(editable.toString())) {
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
                            } else if (getSelectionStart() == editable.length()) {
                                editable.replace(getSelectionStart() - 1, getSelectionStart(), "0");
                                setSelection(getSelectionStart() - 1);
                            } else {
                                if (isMode) {
                                    if (getSelectionStart() == editable.length() - 2) {
                                        isMode = false;
                                        setSelection(getSelectionStart() - 1);

                                        if (editable.length() > 4) {
                                            editable.delete(getSelectionStart() - 1, getSelectionStart());
                                        } else {
                                            editable.replace(0, 1, "0");
                                        }
                                    } else {
                                        editable.replace(getSelectionStart() - 1, getSelectionStart(), "0");
                                        setSelection(getSelectionStart() - 1);
                                    }
                                } else {
                                    editable.delete(getSelectionStart() - 1, getSelectionStart());
                                }
                            }
                        }
                    }
                } else if (primaryCode == 46) {// 小数点
                    isMode = true;
                    setSelection(editable.length() - 2);
                } else { //将要输入的数字现在编辑框中
                    double editableDouble = 0.00;
                    if (editable.length() > 0) {
                        editableDouble = Double.valueOf(editable.toString());
                    }

                    String primaryStr = Character.toString((char) primaryCode);
                    if (editableDouble == 0.00) {
                        if (isMode) {
                            editable.replace(getSelectionStart(), getSelectionEnd() + 1, primaryStr);
                            setSelection(getSelectionStart() + 1);

                        } else {
                            setSelection(1);
                            editable.replace(getSelectionStart() - 1, getSelectionEnd(), primaryStr);
                        }
                    } else {
                        if (isMode) {
                            if (getSelectionStart() != editable.length()) {
                                editable.replace(getSelectionStart(), getSelectionEnd() + 1, primaryStr);
                                setSelection(getSelectionStart() + 1);
                            } else {
                                setError("只能记录到厘米");
                            }
                        } else {
                            if (editable.length() < 6) {
                                editable.insert(getSelectionStart(), primaryStr);
                            } else {
                                setError("不能记录超过999m");
                            }
                            setSelection(editable.length() - 3);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                setError("非数字内容");
                reset();
            }
        }
    };

    public void reset() {
        getText().clear();
        getText().insert(0, str);
    }

    public void showKeyboard() {
        Editable editable = MaterialEditTextMeter.this.getText();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
        hideSoftInputMethod();
        setKeyListener(null);
        initKeyboard();
        //setKeyListener(numericOnlyListener);
        str = editable.toString();
        setHint(str);
//        editable.replace(0, getText().length(), "");
        //editable.clear();
        // 当获取焦点的时候.单位隐藏
        setShowClearButton(true);
        super.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        try {
            int visibility = keyboardView.getVisibility();
            if (visibility == View.VISIBLE) {
                keyboardView.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void hideSoftInputMethod() {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;

        if (currentVersion >= 16) {  // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {    // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);
            } catch (NoSuchMethodException e) {
                setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
