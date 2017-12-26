package com.geotdb.compile.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.geotdb.compile.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Administrator on 2017/3/9.
 */
public class MaterialEditTextNoEmoji extends MaterialEditText {
    private Context mContext;

    public MaterialEditTextNoEmoji(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public MaterialEditTextNoEmoji(Context context, AttributeSet arg1) {
        super(context, arg1);
        this.mContext = context;
        initEditText();
    }

    public MaterialEditTextNoEmoji(Context context, AttributeSet arg1, int arg2) {
        super(context, arg1, arg2);
        this.mContext = context;
        initEditText();
    }


    // 初始化edittext 控件
    private void initEditText() {
        setProhibitEmoji(this);
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }


    public void setProhibitEmoji(EditText et) {
        InputFilter[] filters = {getInputFilterProhibitEmoji()};
        et.setFilters(filters);
    }

    public InputFilter getInputFilterProhibitEmoji() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!isEmojiCharacter(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        ToastUtil.showToastS(mContext, "不支持输入Emoji表情符号");
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null,
                            sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };
        return filter;
    }

}