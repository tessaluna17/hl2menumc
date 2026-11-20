package com.hl2menu.ui;

import com.hl2menu.Hl2Fonts;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Scrollable two-line list with an orange selection frame, like the HL2 load/achievement lists. */
public final class HlList extends Element {
    public record Row(String primary, String secondary) {}

    private final List<Row> rows;
    private final int rowHeight;
    private final IntConsumer onSelect;
    private final IntConsumer onActivate;
    private int selected;
    private int scroll;

    public HlList(int x, int y, int w, int h, int rowHeight, List<Row> rows, int selected,
                  IntConsumer onSelect, IntConsumer onActivate) {
        super(x, y, w, h);
        this.rows = rows;
        this.rowHeight = rowHeight;
        this.selected = selected;
        this.onSelect = onSelect;
        this.onActivate = onActivate;
    }

    private int viewHeight() {
        return h - 2;
    }

    private int maxScroll() {
        return Math.max(0, rows.size() * rowHeight - viewHeight());
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        Draw.box(g, x, y, w, h, Theme.INSET, Theme.INSET_BORDER);
        boolean scrollable = maxScroll() > 0;
        int rowW = w - 2 - (scrollable ? 8 : 0);

        g.enableScissor(x + 1, y + 1, x + w - 1, y + h - 1);
        for (int i = 0; i < rows.size(); i++) {
            int ry = y + 1 + i * rowHeight - scroll;
            if (ry + rowHeight < y || ry > y + h) {
                continue;
            }
            Row row = rows.get(i);
            boolean sel = i == selected;
            boolean hover = Draw.inside(mx, my, x + 1, Math.max(ry, y + 1), rowW, rowHeight)
                    && Draw.inside(mx, my, x, y, w, h);
            if (sel) {
                g.fill(x + 1, ry, x + 1 + rowW, ry + rowHeight, Theme.ROW_SELECT);
                g.outline(x + 1, ry, rowW, rowHeight, Theme.ORANGE);
            } else if (hover) {
                g.fill(x + 1, ry, x + 1 + rowW, ry + rowHeight, Theme.ROW_HOVER);
            }
            Draw.text(g, font, row.primary(), Hl2Fonts.MENU, x + 8, ry + 11, Theme.WHITE);
            Draw.text(g, font, row.secondary(), Hl2Fonts.MENU, x + 8, ry + rowHeight - 5, Theme.TEXT_DIM);
        }
        g.disableScissor();

        if (scrollable) {
            int trackH = h - 4;
            int thumbH = Math.max(12, trackH * viewHeight() / (rows.size() * rowHeight));
            int thumbY = y + 2 + (trackH - thumbH) * scroll / maxScroll();
            g.fill(x + w - 6, y + 2, x + w - 2, y + 2 + trackH, Theme.TRACK);
            g.fill(x + w - 6, thumbY, x + w - 2, thumbY + thumbH, Theme.SLIDER_FILL);
        }
    }

    @Override
    public boolean click(int mx, int my, boolean doubleClick) {
        if (!hit(mx, my)) {
            return false;
        }
        int rel = my - y - 1 + scroll;
        if (rel >= 0) {
            int idx = rel / rowHeight;
            if (idx < rows.size()) {
                selected = idx;
                onSelect.accept(idx);
                if (doubleClick) {
                    onActivate.accept(idx);
                }
            }
        }
        return true;
    }

    @Override
    public boolean scroll(int mx, int my, double amount) {
        if (!hit(mx, my)) {
            return false;
        }
        scroll = Math.max(0, Math.min(maxScroll(), scroll - (int) Math.round(amount * rowHeight)));
        return true;
    }
}
