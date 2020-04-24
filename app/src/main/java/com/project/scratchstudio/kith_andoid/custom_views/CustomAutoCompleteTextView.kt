package com.project.scratchstudio.kith_andoid.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import java.util.Locale

class CustomAutoCompleteTextView(internal val context: Context, attrs: AttributeSet) : AutoCompleteTextView(context, attrs) {

    private lateinit var currentItem: CustomAutoCompleteModel
    private var isSelect: Boolean = false
    private var editTextBehavior: EditTextBehavior = EditTextBehavior(context)
    private lateinit var dependentField: CustomAutoCompleteTextView

    private var items = ArrayList<CustomAutoCompleteModel>()

    fun initialize(errorMessage: String, dependentField: CustomAutoCompleteTextView?) {
        if (dependentField != null) {
            this.setDependentField(dependentField)
        }
        this.setOnFocusChangeListener(errorMessage)
        this.setOnClickListener()
    }

    fun getItems(): ArrayList<CustomAutoCompleteModel> {
        return items
    }

    fun getCurrentItem(): CustomAutoCompleteModel? {
        return if (::currentItem.isInitialized) {
            currentItem
        } else {
            null
        }
    }

    private fun setDependentField(field: CustomAutoCompleteTextView) {
        dependentField = field
    }

    private fun setSelect(select: Boolean) {
        isSelect = select
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
        return if (items.map { it.text.toLowerCase(Locale.ROOT) }.contains(text.toLowerCase(Locale.ROOT))) {
            if (::dependentField.isInitialized) {
                dependentField.show()
            }
            true
        } else {
            false
        }
    }

    fun isValidText(text: String): Boolean {
        val testText = if (::currentItem.isInitialized) {
            currentItem.text
        } else {
            "null"
        }
        Log.i("test", "$text ==$testText ?= " + (items.map { it.text.toLowerCase(Locale.ROOT) }.contains(text.toLowerCase(Locale.ROOT))))

        if (::dependentField.isInitialized) {
            dependentField.hide()
        }

        return if (text.isNotEmpty()
                && (::currentItem.isInitialized && !items.map { it.text.toLowerCase(Locale.ROOT) }.contains(text.toLowerCase(Locale.ROOT))
                        || !::currentItem.isInitialized)) {
            this.setSelect(false)
            if (!::currentItem.isInitialized) {
                currentItem = CustomAutoCompleteModel(-1, text)
            } else {
                currentItem.id = -1
                currentItem.text = text
            }
            true
        } else {
            if (::currentItem.isInitialized && !items.map { it.text.toLowerCase(Locale.ROOT) }.contains(text.toLowerCase(Locale.ROOT))) {
                setSelect(false)
                currentItem.text = ""
            } else if (::currentItem.isInitialized && items.map { it.text.toLowerCase(Locale.ROOT) }.contains(text.toLowerCase(Locale.ROOT))) {
                setSelect(true)
                for (i in 0 until items.size) {
                    if (items[i].text.toLowerCase(Locale.ROOT) == text.toLowerCase(Locale.ROOT)) {
                        if (!::currentItem.isInitialized) {
                            currentItem = CustomAutoCompleteModel(items[i].id, items[i].text)
                        } else {
                            currentItem.id = items[i].id
                            currentItem.text = items[i].text
                        }
                    }
                }
                if (::dependentField.isInitialized) {
                    dependentField.show()
                }
            }
            false
        }
    }

    private fun setOnFocusChangeListener(errorMessage: String) {
        this.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextBehavior.fieldClear(this)
            } else {
                if (this.getSelected()) {
                    editTextBehavior.fieldClear(this)
                } else {
                    if (items.map { it.text.toLowerCase(Locale.ROOT) }.contains(this.text.toString().toLowerCase(Locale.ROOT)) || this.text.isEmpty()) {
                        editTextBehavior.fieldClear(this)
                    } else {
                        editTextBehavior.fieldErrorWithText(this, errorMessage)
                        if (::dependentField.isInitialized) {
                            dependentField.hide()
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListener() {
        this.setOnItemClickListener { parent, view, position, id ->
            this.setSelect(true)
            currentItem = parent.getItemAtPosition(position) as CustomAutoCompleteModel
            if (::dependentField.isInitialized) {
                dependentField.show()
            }
        }
    }

    class CustomAutoCompleteModel(idd: Int, textt: String) {
        var id = 0
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