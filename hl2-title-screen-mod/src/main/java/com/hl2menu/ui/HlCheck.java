package com.hl2menu.ui;

import com.hl2menu.Hl2Fonts;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Square check box with a label to its right. */
public final class HlCheck extends Element {
    private final String label;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    public HlCheck(String label, int x, int y, int w, int h, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(x, y, w, h);
        this.label = label;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        int cy = y + h / 2;
        boolean hover = hit(mx, my);
        Draw.box(g, x, cy - 5, 10, 10, Theme.INSET, hover ? Theme.WHITE : Theme.INSET_BORDER);
        if (getter.getAsBoolean()) {
            g.fill(x + 2, cy - 3, x + 8, cy + 3, Theme.WHITE);
        }
        Draw.text(g, font, label, Hl2Fonts.MENU, x + 18, cy + 4, hover ? Theme.WHITE : Theme.TEXT);
    }

    @Override
    public boolean click(int mx, int my, boolean doubleClick) {
        if (!enabled || !hit(mx, my)) {
            return false;
        }
        setter.accept(!getter.getAsBoolean());
        return true;
    }
}
