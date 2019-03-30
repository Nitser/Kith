package com.project.scratchstudio.kith_andoid.CustomViews;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Locale;

enum CustomFont {
    JURA("fonts/JuraBook.ttf"),
    INTRO("fonts/Intro-Book.otf"),
    INTRO_REGULAR("fonts/intro_regular.ttf"),
    HATTEN("fonts/HATTEN.TTF"),
    ITCEDSCR("fonts/ITCEDSCR.TTF");

    private final String fileName;

    CustomFont(String fileName) {
        this.fileName = fileName;
    }

    static CustomFont fromString(String fontName) {
        return CustomFont.valueOf(fontName.toUpperCase(new Locale("ru", "RU")));
    }

    public Typeface asTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fileName);
    }
}
