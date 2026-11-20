package com.hl2menu.ui;

import com.hl2menu.Hl2Fonts;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Drop-down looking box that steps to the next value when clicked (e.g. Windowed / Full screen). */
public final class HlCycle extends Element {
    private final String label;
    private final int labelWidth;
    private final Supplier<String> value;
    private final Runnable next;

    public HlCycle(String label, int x, int y, int w, int h, int labelWidth, Supplier<String> value, Runnable next) {
        super(x, y, w, h);
        this.label = label;
        this.labelWidth = labelWidth;
        this.value = value;
        this.next = next;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        int cy = y + h / 2;
        Draw.text(g, font, label, Hl2Fonts.MENU, x, cy + 4, Theme.TEXT);
        int bx = x + labelWidth;
        int bw = w - labelWidth;
        boolean hover = Draw.inside(mx, my, bx, y + 1, bw, h - 2);
        Draw.box(g, bx, y + 1, bw, h - 2, hover ? Theme.BUTTON_HOVER : Theme.INSET, Theme.INSET_BORDER);
        Draw.text(g, font, value.get(), Hl2Fonts.MENU, bx + 6, cy + 4, Theme.WHITE);
        Draw.textRight(g, font, "▾", Hl2Fonts.MENU, bx + bw - 5, cy + 4, Theme.TEXT_DIM);
    }

    @Override
    public boolean click(int mx, int my, boolean doubleClick) {
        if (!enabled || !Draw.inside(mx, my, x + labelWidth, y + 1, w - labelWidth, h - 2)) {
            return false;
        }
        next.run();
        return true;
    }
}
