package com.project.scratchstudio.kith_andoid.ui.neweditboard

import android.content.Context
import android.util.AttributeSet

class CustomEditText : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        val text = text
        if (text != null) {
            if (text.toString().contains("\u20BD") && text.length > 2 && (selEnd > text.length - 2 || selStart > text.length - 2)) {
                setSelection(text.length - 2)
                return
            }
        }
        super.onSelectionChanged(selStart, selEnd)
    }
}
