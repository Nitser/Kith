package com.project.scratchstudio.kith_andoid.ui.neweditboard;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class CurrencyTextWatcher implements TextWatcher {
    private String current = "";
    private EditText editText;

    CurrencyTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (!s.toString().equals(current)) {
            int selection = editText.getSelectionEnd();
            editText.removeTextChangedListener(this);
            Log.i("TextWatcher", "oldSel = " + selection);
            if (s.toString().replaceAll(" \u20BD", "").length() == 0) {
                current = "";
                selection = 0;
            } else if (s.length() > 0) {
                current = s.toString().replaceAll(" \u20BD", "") + " \u20BD";
            }
            editText.setText(current);
            Log.i("TextWatcher", "newSel = " + selection);
            editText.setSelection(selection);
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(final Editable s) {

    }
}
