package com.notepad.app;

import android.content.Context;
import android.graphics.Color;

public class ThemeManager {
    private static final String PREFS = "theme_prefs";
    private static final String KEY_DARK = "is_dark";

    public static boolean isDarkTheme(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                  .getBoolean(KEY_DARK, false);
    }

    public static void setDarkTheme(Context ctx, boolean dark) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
           .edit().putBoolean(KEY_DARK, dark).apply();
    }

    public static int getBgColor(boolean dark)         { return dark ? 0xFF121212 : 0xFFFFFFFF; }
    public static int getTextColor(boolean dark)       { return dark ? 0xFFEEEEEE : 0xFF222222; }
    public static int getHintColor(boolean dark)       { return dark ? 0xFF666666 : 0xFFAAAAAA; }
    public static int getToolbarColor(boolean dark)    { return dark ? 0xFF1E1E1E : 0xFF1A1A2E; }
    public static int getListBgColor(boolean dark)     { return dark ? 0xFF1A1A1A : 0xFFF0F0F5; }
    public static int getSubToolbarColor(boolean dark) { return dark ? 0xFF2C2C2C : 0xFFF5F5F5; }
    public static int getDrawSubToolbar(boolean dark)  { return dark ? 0xFF252535 : 0xFFE8EAF6; }
    public static int getDefaultPenColor(boolean dark) { return dark ? Color.WHITE : Color.BLACK; }

    // Card colors sorted warm→cool→neutral for a pleasant gradient sweep
    public static final int[] CARD_COLORS_LIGHT = {
        0xFFFFF9C4, 0xFFFFECB3, 0xFFFFCCBC, 0xFFFFAB91,
        0xFFFFCC80, 0xFFF8BBD0, 0xFFE1BEE7, 0xFFCE93D8,
        0xFF90CAF9, 0xFF81D4FA, 0xFFB2EBF2, 0xFF80DEEA,
        0xFFC8E6C9, 0xFFA5D6A7, 0xFFDCEDC8, 0xFFE0E0E0
    };

    public static final int[] CARD_COLORS_DARK = {
        0xFF4A3320, 0xFF4A2820, 0xFF4A2030, 0xFF3A2048,
        0xFF20304A, 0xFF18284A, 0xFF18402A, 0xFF304018,
        0xFF282828, 0xFF303030, 0xFF383020, 0xFF203038,
        0xFF381818, 0xFF183838, 0xFF241838, 0xFF182438
    };
}
