package com.project.scratchstudio.kith_andoid.custom_views

import android.content.Context
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import com.project.scratchstudio.kith_andoid.R

class EditTextBehavior(val context: Context) {

    fun notEmptyField(editText: EditText): Boolean {
        return if (editText.text.isEmpty()) {
            fieldError(editText)
            false
        } else {
            fieldClear(editText)
            true
        }
    }

    fun equalsFieldsWithText(editText1: EditText, editText2: EditText, errorText: String): Boolean {
        return if (editText1.text.toString() != editText2.text.toString()) {
//            fieldError(editText1)
            fieldErrorWithText(editText2, errorText)
            false
        } else {
//            fieldClear(editText1)
            fieldClear(editText2)
            true
        }
    }

    fun fieldError(editText: EditText) {
        val icon = AppCompatResources.getDrawable(context, R.drawable.ic_error_outline)
        icon!!.setBounds(0, 0, icon.intrinsicWidth - 13, icon.intrinsicHeight - 13)
        editText.setCompoundDrawables(null, null, icon, null)
    }

    fun fieldErrorWithText(editText: EditText, errorText: String) {
        val icon = AppCompatResources.getDrawable(context, R.drawable.ic_error_outline)
        icon!!.setBounds(0, 0, icon.intrinsicWidth - 13, icon.intrinsicHeight - 13)
        editText.setCompoundDrawables(null, null, icon, null)
        editText.setError(errorText, null)
    }

    fun fieldClear(editText: EditText) {
        editText.setCompoundDrawables(null, null, null, null)
    }

}