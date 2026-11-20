package com.hl2menu.dialog;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.screen.Hl2TitleScreen;
import com.hl2menu.ui.Draw;
import com.hl2menu.ui.Element;
import com.hl2menu.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A floating grey VGUI dialog (title bar, close box, content). Subclasses add their elements in
 * {@link #build()}, which runs again after every resize, so keep state in fields, not in elements.
 */
public abstract class Dialog {
    protected final Hl2TitleScreen host;
    protected final String title;
    private final int wantedWidth;
    private final int wantedHeight;
    protected int x;
    protected int y;
    protected int dw;
    protected int dh;
    private final List<Element> elements = new ArrayList<>();

    protected Dialog(Hl2TitleScreen host, String title, int wantedWidth, int wantedHeight) {
        this.host = host;
        this.title = title;
        this.wantedWidth = wantedWidth;
        this.wantedHeight = wantedHeight;
    }

    /** Called by the screen on open and on every resize. */
    public final void layout(int screenW, int screenH) {
        dw = Math.min(wantedWidth, screenW - 8);
        dh = Math.min(wantedHeight, screenH - 8);
        x = (screenW - dw) / 2;
        y = (screenH - dh) / 2;
        elements.clear();
        build();
    }

    /** Create the dialog's elements using the absolute position fields x, y, dw, dh. */
    protected abstract void build();

    protected final void add(Element element) {
        elements.add(element);
    }

    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        g.fill(x + 3, y + 3, x + dw + 3, y + dh + 3, Theme.SHADOW);
        Draw.box(g, x, y, dw, dh, Theme.PANEL, Theme.PANEL_BORDER);
        Draw.text(g, font, title.toUpperCase(Locale.ROOT), Hl2Fonts.MENU_BOLD, x + 10, y + 16, Theme.WHITE);
        boolean overClose = Draw.inside(mx, my, x + dw - 18, y + 4, 14, 14);
        Draw.textCentered(g, font, "×", Hl2Fonts.MENU_BOLD, x + dw - 11, y + 17,
                overClose ? Theme.WHITE : Theme.TEXT_DIM);
        for (Element element : elements) {
            element.render(g, font, mx, my);
        }
    }

    public void click(int mx, int my, boolean doubleClick) {
        if (Draw.inside(mx, my, x + dw - 18, y + 4, 14, 14)) {
            cancel();
            return;
        }
        // Iterate a copy: a click may close or rebuild the dialog.
        for (Element element : new ArrayList<>(elements)) {
            if (element.click(mx, my, doubleClick)) {
                return;
            }
        }
    }

    public void drag(int mx, int my) {
        for (Element element : elements) {
            element.drag(mx, my);
        }
    }

    public void release() {
        for (Element element : elements) {
            element.release();
        }
    }

    public void scroll(int mx, int my, double amount) {
        for (Element element : elements) {
            if (element.scroll(mx, my, amount)) {
                return;
            }
        }
    }

    /** Escape key and the close box. Dialogs with unsaved changes override this to undo them. */
    public void cancel() {
        host.closeDialog();
    }
}
