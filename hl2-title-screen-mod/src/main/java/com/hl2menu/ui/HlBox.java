package com.hl2menu.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Recessed content area that sits inside a dialog (tab pages, empty list placeholders). */
public final class HlBox extends Element {
    public HlBox(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        Draw.box(g, x, y, w, h, Theme.INSET, Theme.INSET_BORDER);
    }
}
