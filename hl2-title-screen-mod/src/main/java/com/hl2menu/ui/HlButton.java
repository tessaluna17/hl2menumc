package com.hl2menu.ui;

import com.hl2menu.Hl2Fonts;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Flat grey VGUI-style push button. */
public final class HlButton extends Element {
    private final String label;
    private final Runnable action;
    private BooleanSupplier activeWhen;

    public HlButton(String label, int x, int y, int w, int h, Runnable action) {
        super(x, y, w, h);
        this.label = label;
        this.action = action;
    }

    /** The button is only clickable while the supplier returns true. */
    public HlButton activeWhen(BooleanSupplier supplier) {
        this.activeWhen = supplier;
        return this;
    }

    private boolean active() {
        return enabled && (activeWhen == null || activeWhen.getAsBoolean());
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        boolean active = active();
        boolean hover = active && hit(mx, my);
        int fill = !active ? Theme.BUTTON_DISABLED : hover ? Theme.BUTTON_HOVER : Theme.BUTTON;
        Draw.box(g, x, y, w, h, fill, Theme.BUTTON_BORDER);
        int color = !active ? Theme.TEXT_DISABLED : hover ? Theme.WHITE : Theme.TEXT;
        Draw.textCentered(g, font, label, Hl2Fonts.MENU, x + w / 2, y + h / 2 + 4, color);
    }

    @Override
    public boolean click(int mx, int my, boolean doubleClick) {
        if (!active() || !hit(mx, my)) {
            return false;
        }
        action.run();
        return true;
    }
}
