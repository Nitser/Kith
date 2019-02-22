package com.project.scratchstudio.kith_andoid.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomFontEditText extends android.support.v7.widget.AppCompatEditText{
    private static final String sScheme = "http://schemas.android.com/apk/res-auto";
    private static final String sAttribute = "customFont";

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        } else {
            final String fontName = attrs.getAttributeValue(sScheme, sAttribute);

            if (fontName == null) {
                throw new IllegalArgumentException("You must provide \"" + sAttribute + "\" for your text view");
            } else {
                final Typeface customTypeface = CustomFont.fromString(fontName).asTypeface(context);
                setTypeface(customTypeface);
            }
        }
    }
}