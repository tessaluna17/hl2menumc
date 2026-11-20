package com.hl2menu.ui;

/** Colours and metrics, sampled from the Source/VGUI dialogs in the reference screenshots. All ARGB. */
public final class Theme {
    private Theme() {}

    /**
     * Distance from the top of a text line to its baseline in Minecraft's text renderer. All text in
     * this mod is positioned by baseline, so nudge this by one if text looks a pixel too high or low.
     */
    public static final int BASELINE = 7;

    public static final int WHITE = 0xFFFFFFFF;
    public static final int TEXT = 0xFFE6E6E6;
    public static final int TEXT_DIM = 0xFFAEAEAE;
    public static final int TEXT_DISABLED = 0xFF7C7C7C;
    public static final int MENU_IDLE = 0xFFC9C9C9;
    public static final int ORANGE = 0xFFFF9D00;

    public static final int PANEL = 0xEC5B5B5B;
    public static final int PANEL_BORDER = 0xFFA9A9A9;
    public static final int SHADOW = 0x60000000;
    public static final int INSET = 0xFF454545;
    public static final int INSET_BORDER = 0xFF8C8C8C;

    public static final int BUTTON = 0xFF5F5F5F;
    public static final int BUTTON_HOVER = 0xFF7B7B7B;
    public static final int BUTTON_DISABLED = 0xFF4E4E4E;
    public static final int BUTTON_BORDER = 0xFFB7B7B7;

    public static final int TAB = 0xFF505050;
    public static final int TAB_SELECTED = 0xFF6E6E6E;

    public static final int TRACK = 0xFF2E2E2E;
    public static final int SLIDER_FILL = 0xFF8E8E8E;
    public static final int THUMB = 0xFFD2D2D2;
    public static final int THUMB_BORDER = 0xFF2B2B2B;

    public static final int ROW_SELECT = 0x40FF9D00;
    public static final int ROW_HOVER = 0x20FFFFFF;
}
