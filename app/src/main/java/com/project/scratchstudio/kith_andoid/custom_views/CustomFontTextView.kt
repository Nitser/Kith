package com.project.scratchstudio.kith_andoid.custom_views

import android.content.Context
import android.util.AttributeSet

class CustomFontTextView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {

        if (!isInEditMode) {
            val fontName = attrs.getAttributeValue(sScheme, sAttribute)

            requireNotNull(fontName) { "You must provide \"$sAttribute\" for your text view" }
            val customTypeface = CustomFont.fromString(fontName).asTypeface(context)
            typeface = customTypeface
        }
    }

    companion object {

        private const val sScheme = "http://schemas.android.com/apk/res-auto"
        private const val sAttribute = "customFont"
    }
}