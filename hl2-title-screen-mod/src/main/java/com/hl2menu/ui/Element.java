package com.hl2menu.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A rectangular piece of dialog UI. Elements are rebuilt whenever the screen is resized, so any
 * state that must survive (selected tab, selected save ...) lives in the owning dialog, not here.
 */
public abstract class Element {
    public int x;
    public int y;
    public int w;
    public int h;
    public boolean enabled = true;

    protected Element(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public final boolean hit(int mx, int my) {
        return Draw.inside(mx, my, x, y, w, h);
    }

    public abstract void render(GuiGraphicsExtractor g, Font font, int mx, int my);

    /** Returns true if the click was consumed. */
    public boolean click(int mx, int my, boolean doubleClick) {
        return false;
    }

    public void drag(int mx, int my) {}

    public void release() {}

    /** {@code amount} is positive when scrolling up. Returns true if consumed. */
    public boolean scroll(int mx, int my, double amount) {
        return false;
    }
}
