package com.hl2menu.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/** Small drawing helpers so the widgets don't repeat the same fill/outline/text boilerplate. */
public final class Draw {
    private Draw() {}

    /** Filled rectangle with a one pixel border. */
    public static void box(GuiGraphicsExtractor g, int x, int y, int w, int h, int fill, int border) {
        g.fill(x, y, x + w, y + h, fill);
        g.outline(x, y, w, h, border);
    }

    public static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    public static Component comp(String text, Style style) {
        return Component.literal(text).withStyle(style);
    }

    public static int width(Font font, String text, Style style) {
        return font.width(comp(text, style));
    }

    /** Draws text with its baseline at {@code baseline} and its left edge at {@code x}. */
    public static void text(GuiGraphicsExtractor g, Font font, String text, Style style, int x, int baseline, int color) {
        g.text(font, comp(text, style), x, baseline - Theme.BASELINE, color, false);
    }

    public static void textCentered(GuiGraphicsExtractor g, Font font, String text, Style style, int centerX, int baseline, int color) {
        text(g, font, text, style, centerX - width(font, text, style) / 2, baseline, color);
    }

    public static void textRight(GuiGraphicsExtractor g, Font font, String text, Style style, int rightX, int baseline, int color) {
        text(g, font, text, style, rightX - width(font, text, style), baseline, color);
    }
}
