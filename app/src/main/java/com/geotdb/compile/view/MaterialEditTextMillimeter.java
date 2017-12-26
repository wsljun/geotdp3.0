package com.geotdb.compile.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.geotdb.compile.R;


public class MaterialEditTextMillimeter extends MaterialEditTextInt {

    public MaterialEditTextMillimeter(Context context) {
        super(context);
    }

    public MaterialEditTextMillimeter(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MaterialEditTextMillimeter(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_millimeter_bold_black_24dp);
        if (dropdownIcon != null) {
            right = dropdownIcon;
            right.mutate().setAlpha(66);
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

}
