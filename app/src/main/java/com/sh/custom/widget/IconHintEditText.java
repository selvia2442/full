package com.sh.custom.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

public class IconHintEditText extends EditText{

	
	private boolean mHintIconShown = true;
	private Drawable mHintIcon;
	public IconHintEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mHintIcon = getCompoundDrawables()[0];
	}
	
	@Override
    public boolean onPreDraw() {
        boolean emptyText = TextUtils.isEmpty(getText());
        if (mHintIconShown != emptyText) {
        	mHintIconShown = emptyText;
            if (mHintIconShown) {
                setCompoundDrawables(mHintIcon, null, null, null);
            } else {
                setCompoundDrawables(null, null, null, null);
            }
            return false;
        }
        return super.onPreDraw();
    }
}
