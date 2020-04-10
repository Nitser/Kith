package com.project.scratchstudio.kith_andoid.custom_views.to_trash

import android.content.Context
import android.graphics.Typeface

import java.util.Locale

internal enum class CustomFont constructor(private val fileName: String) {
    JURA("fonts/JuraBook.ttf"),
    INTRO("fonts/Intro-Book.otf"),
    INTRO_REGULAR("fonts/intro_regular.ttf"),
    HATTEN("fonts/HATTEN.TTF"),
    ITCEDSCR("fonts/ITCEDSCR.TTF");

    fun asTypeface(context: Context): Typeface {
        return Typeface.createFromAsset(context.assets, fileName)
    }

    companion object {

        fun fromString(fontName: String): CustomFont {
            return valueOf(fontName.toUpperCase(Locale("ru", "RU")))
        }
    }
}
