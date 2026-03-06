package com.notepad.app;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class GradientDrawableHelper {
    public static void setBorderDrawable(View view, int fillColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setStroke(3, 0xFF888888);
        gd.setCornerRadius(10f);
        view.setBackground(gd);
    }
}
