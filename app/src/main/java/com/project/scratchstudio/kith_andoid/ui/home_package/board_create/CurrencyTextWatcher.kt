package com.project.scratchstudio.kith_andoid.ui.home_package.board_create

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class CurrencyTextWatcher internal constructor(private val editText: EditText) : TextWatcher {
    private var current = ""

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString() != current) {
            var selection = editText.selectionEnd
            editText.removeTextChangedListener(this)
            Log.i("TextWatcher", "oldSel = $selection")
            if (s.toString().replace(" \u20BD".toRegex(), "").isEmpty()) {
                current = ""
                selection = 0
            } else if (s.isNotEmpty()) {
                current = s.toString().replace(" \u20BD".toRegex(), "") + " \u20BD"
            }
            editText.setText(current)
            Log.i("TextWatcher", "newSel = $selection")
            editText.setSelection(selection)
            editText.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable) {

    }
}
