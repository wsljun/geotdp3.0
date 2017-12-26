package com.geotdb.compile.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.adapter.DropListAdapter;
import com.geotdb.compile.R;
import com.geotdb.compile.vo.DropItemVo;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import java.util.Calendar;
import java.util.List;


public class MaterialBetterSpinner extends MaterialAutoCompleteTextView implements AdapterView.OnItemClickListener {

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private boolean isPopup;
    private boolean isEnabled;
    private String text;
    private Context context;

    public MaterialBetterSpinner(Context context) {
        super(context);
        this.context = context;
        setOnItemClickListener(this);
    }

    public MaterialBetterSpinner(Context context, AttributeSet arg1) {
        super(context, arg1);
        this.context = context;
        setOnItemClickListener(this);
    }

    public MaterialBetterSpinner(Context context, AttributeSet arg1, int arg2) {
        super(context, arg1, arg2);
        this.context = context;
        setOnItemClickListener(this);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            if (!isEnabled) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
                setKeyListener(null);
            }
            if (getAdapter() != null) {
                performFiltering("", 0);
                dismissDropDown();
            }
        } else {
            isPopup = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            }
            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION) {
                    if (isPopup) {
                        dismissDropDown();
                        isPopup = false;
                    } else {
                        requestFocus();
                        showDropDown();
                        text = getText().toString();
                        if (onDialogListener != null) {
                            onDialogListener.onShow();
                        }
                        isPopup = true;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        isPopup = false;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(adapterView, view, i, l);
            clearFocus();
            requestFocus();
        }
        if (onCustomListener != null) {
            onCustomListener.onCustom(adapterView, view, i, l);
        }

    }


    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_more_black_18dp);
        if (dropdownIcon != null) {
            right = dropdownIcon;
            right.mutate().setAlpha(66);
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    public OnItemClickListener onItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> adapterView, View view, int i, long l);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    public OnCustomListener onCustomListener;


    public interface OnCustomListener {
        void onCustom(AdapterView<?> adapterView, View view, int i, long l);
    }

    public void setOnCustomListener(OnCustomListener onCustomListener) {
        this.onCustomListener = onCustomListener;
    }

    public OnDialogListener onDialogListener;

    public interface OnDialogListener {
        void onShow();
    }

    public void setOnDialogListener(OnDialogListener onDialogListener) {
        this.onDialogListener = onDialogListener;
    }

    public void setIsPopup(boolean isPopup) {
        this.isPopup = isPopup;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    //设置可清空
    public void setClear() {
        DropListAdapter adapter = (DropListAdapter) getAdapter();
        adapter.insert(new DropItemVo("", "清空...", ""), 0);
        adapter.notifyDataSetChanged();
    }

    //设置可自定义
    public MaterialBetterSpinner setCustom() {
        setCustom("编辑自定义内容");
        return this;
    }

    public void setCustom(String title) {
        setCustom(title, 16);
    }

    public void setCustom(final String title, final int maxLength) {
        DropListAdapter adapter = (DropListAdapter) getAdapter();
        adapter.insert(new DropItemVo("0", "自定义"), adapter.getCount());
        adapter.notifyDataSetChanged();

        setOnCustomListener(new MaterialBetterSpinner.OnCustomListener() {
            @Override
            public void onCustom(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == adapterView.getCount() - 1) {
                    new MaterialDialog.Builder(getContext()).title(title).inputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                            InputType.TYPE_TEXT_FLAG_CAP_WORDS).inputMaxLength(maxLength).input("请输入自定义内容", text, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            setText(input.toString());
                            InputMethodManager imm = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(dialog.getInputEditText().getWindowToken(), 0);
                            clearFocus();
                            requestFocus();
                        }
                    }).show();
                }
            }
        });
    }

    public static  int MODE_CLEAR = 1;
    public static  int MODE_CUSTOM = 2;
    public static  int MODE_CLEAR_CUSTOM = 3;

    public void setAdapter(Context context , List<DropItemVo> list) {
        setAdapter(context,list,0);
    }

    public void setAdapter(Context context , List<DropItemVo> list,int mode) {
        DropListAdapter adapter = new DropListAdapter(context, R.layout.drop_item, list);
        setAdapter(adapter);
        if(mode==1||mode==3){
            setClear();
        }
        if(mode==2||mode==3){
            setCustom();
        }
    }
}
