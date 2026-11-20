package com.hl2menu.ui;

import com.hl2menu.Hl2Fonts;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** One tab header in a row of tabs. The selected tab is lighter and merges into the page below it. */
public final class HlTab extends Element {
    private final String label;
    private final BooleanSupplier selected;
    private final Runnable action;

    public HlTab(String label, int x, int y, int w, int h, BooleanSupplier selected, Runnable action) {
        super(x, y, w, h);
        this.label = label;
        this.selected = selected;
        this.action = action;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        boolean sel = selected.getAsBoolean();
        boolean hover = hit(mx, my);
        int fill = sel ? Theme.TAB_SELECTED : hover ? Theme.BUTTON_HOVER : Theme.TAB;
        Draw.box(g, x, y, w, h, fill, Theme.BUTTON_BORDER);
        if (sel) {
            g.fill(x + 1, y + h - 1, x + w - 1, y + h + 1, fill);
        }
        Draw.textCentered(g, font, label, Hl2Fonts.MENU, x + w / 2, y + h / 2 + 4,
                sel ? Theme.WHITE : Theme.TEXT_DIM);
    }

    @Override
    public boolean click(int mx, int my, boolean doubleClick) {
        if (!hit(mx, my)) {
            return false;
        }
        action.run();
        return true;
    }
}
