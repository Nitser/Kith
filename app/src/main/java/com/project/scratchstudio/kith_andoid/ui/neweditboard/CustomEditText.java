package com.project.scratchstudio.kith_andoid.ui.neweditboard;

import android.content.Context;
import android.util.AttributeSet;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    public CustomEditText(final Context context) {
        super(context);
    }

    public CustomEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(final int selStart, final int selEnd) {
        CharSequence text = getText();
        if (text != null) {
            if (text.toString().contains("\u20BD") && text.length() > 2 && (selEnd > text.length() - 2 || selStart > text.length() - 2)) {
                setSelection(text.length() - 2);
                return;
            }
        }
        super.onSelectionChanged(selStart, selEnd);
    }
}
