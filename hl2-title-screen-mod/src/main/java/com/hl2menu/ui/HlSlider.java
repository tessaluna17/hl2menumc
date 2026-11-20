package com.hl2menu.ui;

import com.hl2menu.Hl2Fonts;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Labelled horizontal slider: [label] [track] [value]. The getter and setter work on a normalised
 * 0..1 value, so the owner decides how that maps onto a game option.
 */
public final class HlSlider extends Element {
    private final String label;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;
    private final Supplier<String> valueText;
    private final int labelWidth;
    private final int valueWidth;
    private boolean dragging;

    public HlSlider(String label, int x, int y, int w, int h, int labelWidth, int valueWidth,
                    DoubleSupplier getter, DoubleConsumer setter, Supplier<String> valueText) {
        super(x, y, w, h);
        this.label = label;
        this.labelWidth = labelWidth;
        this.valueWidth = valueWidth;
        this.getter = getter;
        this.setter = setter;
        this.valueText = valueText;
    }

    private int trackX() {
        return x + labelWidth;
    }

    private int trackW() {
        return Math.max(10, w - labelWidth - valueWidth - 6);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        int cy = y + h / 2;
        Draw.text(g, font, label, Hl2Fonts.MENU, x, cy + 4, Theme.TEXT);

        int tx = trackX();
        int tw = trackW();
        g.fill(tx, cy - 2, tx + tw, cy + 2, Theme.TRACK);
        g.outline(tx, cy - 2, tw, 4, Theme.INSET_BORDER);

        double v = clamp01(getter.getAsDouble());
        int thumbX = tx + (int) Math.round((tw - 5) * v);
        if (thumbX > tx + 1) {
            g.fill(tx + 1, cy - 1, thumbX, cy + 1, Theme.SLIDER_FILL);
        }
        boolean hot = dragging || Draw.inside(mx, my, thumbX, cy - 6, 5, 12);
        Draw.box(g, thumbX, cy - 6, 5, 12, hot ? Theme.WHITE : Theme.THUMB, Theme.THUMB_BORDER);

        Draw.textRight(g, font, valueText.get(), Hl2Fonts.MENU, x + w, cy + 4, Theme.TEXT);
    }

    private void apply(int mx) {
        int tw = trackW();
        double v = (mx - trackX() - 2.5) / (tw - 5);
        setter.accept(clamp01(v));
    }

    @Override
    public boolean click(int mx, int my, boolean doubleClick) {
        if (!enabled || !Draw.inside(mx, my, trackX() - 3, y, trackW() + 6, h)) {
            return false;
        }
        dragging = true;
        apply(mx);
        return true;
    }

    @Override
    public void drag(int mx, int my) {
        if (dragging) {
            apply(mx);
        }
    }

    @Override
    public void release() {
        dragging = false;
    }
}
