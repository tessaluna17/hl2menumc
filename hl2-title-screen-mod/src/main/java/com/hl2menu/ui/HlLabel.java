package com.hl2menu.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;

/** Static text positioned by its baseline. */
public final class HlLabel extends Element {
    private final String text;
    private final int color;
    private final Style style;

    public HlLabel(String text, int x, int baseline, int color, Style style) {
        super(x, baseline, 0, 0);
        this.text = text;
        this.color = color;
        this.style = style;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        Draw.text(g, font, text, style, x, y, color);
    }
}
