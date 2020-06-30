package com.project.scratchstudio.kith_andoid.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import java.util.Locale

class CustomAutoCompleteTextView(internal val context: Context, attrs: AttributeSet) : AppCompatAutoCompleteTextView(context, attrs) {

    private var isSelect: Boolean = false
    private var editTextBehavior: EditTextBehavior = EditTextBehavior(context)
    private lateinit var dependentField: CustomAutoCompleteTextView
    private var items = HashMap<String, CustomAutoCompleteModel>()

    fun getItems(): HashMap<String, CustomAutoCompleteModel> {
        return items
    }

    fun initItems(map: HashMap<String, CustomAutoCompleteModel>) {
        items.clear()
        items.putAll(map)
    }

    fun initialize(errorMessage: String, dependentField: CustomAutoCompleteTextView?) {
        if (dependentField != null) {
            this.setDependentField(dependentField)
        }
        this.setOnFocusChangeListener(errorMessage)
        this.setOnClickListener()
    }

    private fun setDependentField(field: CustomAutoCompleteTextView) {
        dependentField = field
    }

    private fun hide() {
        if (::dependentField.isInitialized) {
            dependentField.text.clear()
            dependentField.visibility = View.GONE
        }
        this.text.clear()
        this.visibility = View.GONE
    }

    private fun show() {
        this.visibility = View.VISIBLE
    }

    fun getSelected(): Boolean = isSelect

    fun isItemsContainText(text: String): Boolean {
        return items.containsKey(text.toLowerCase(Locale.ROOT))
    }

    fun setSelectedIfItemsContainText(text: String) {
        if (isItemsContainText(text))
            selectedField()
    }

    private fun hideDependentField() {
        if (::dependentField.isInitialized) {
            (dependentField).hide()
        }
    }

    private fun showDependentField() {
        if (::dependentField.isInitialized) {
            (dependentField).show()
        }
    }

    private fun unselectedField() {
        hideDependentField()
        isSelect = false
    }

    private fun selectedField() {
        showDependentField()
        isSelect = true
    }

    fun isViewSelected(): Boolean {
        return isSelect
    }

    fun isValidText(text: String): Boolean {
        return if (text.isNotEmpty() && !items.containsKey(text)) {
            unselectedField()
            true
        } else {
            if (items.containsKey(text.toLowerCase(Locale.ROOT))) {
                selectedField()
            }
            false
        }
    }

    private fun setOnFocusChangeListener(errorMessage: String) {
        this.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextBehavior.fieldClear(this)
            } else {
                if (getSelected()) {
                    editTextBehavior.fieldClear(this)
                } else {
                    if (items.containsKey(this.text.toString().toLowerCase(Locale.ROOT)) || this.text.isEmpty()) {
                        editTextBehavior.fieldClear(this)
                    } else {
                        editTextBehavior.fieldErrorWithText(this, errorMessage)
                        hideDependentField()
                    }
                }
            }
        }
    }

    private fun setOnClickListener() {
        this.setOnItemClickListener { _, _, _, _ ->
            selectedField()
        }
    }

    class CustomAutoCompleteModel(idd: Int, textt: String) {
        var id = -1
        var text = ""

        init {
            this.id = idd
            this.text = textt
        }

        override fun toString(): String {
            return text
        }
    }

}