package com.hl2menu.screen;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.dialog.AchievementsDialog;
import com.hl2menu.dialog.Dialog;
import com.hl2menu.dialog.LoadGameDialog;
import com.hl2menu.dialog.NewGameDialog;
import com.hl2menu.dialog.OptionsDialog;
import com.hl2menu.dialog.QuitDialog;
import com.hl2menu.ui.Draw;
import com.hl2menu.ui.Theme;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * The Half-Life 2 main menu: wide-tracked title, small left-aligned text menu, and VGUI-style
 * dialogs floating over the regular Minecraft panorama. Everything is drawn with the bundled
 * TrueType fonts, never Minecraft's pixel font.
 *
 * <p>The layout follows the reference screenshot: title at about 9% of the screen width and 45% of
 * its height, menu starting just below it and slightly further left.
 */
public class Hl2TitleScreen extends Screen {
    private record Item(String label, Runnable action) {}

    private static final String[] TITLE_LETTERS = {"H", "λ", "L", "F", "-", "L", "I", "F", "E"};
    private static final Style[] TITLE_STYLES = {Hl2Fonts.TITLE_L, Hl2Fonts.TITLE_M, Hl2Fonts.TITLE_S};
    private static final int[] TITLE_SIZES = {36, 26, 18};

    private final List<Item> items;
    private Dialog dialog;

    private int titleX;
    private int titleBaseline;
    private int titleLevel;
    private int menuX;
    private int menuY;
    private int step;

    public Hl2TitleScreen() {
        super(Component.literal("Half-Life 2"));
        this.items = List.of(
                new Item("NEW GAME", () -> openDialog(new NewGameDialog(this))),
                new Item("LOAD GAME", () -> openDialog(new LoadGameDialog(this))),
                new Item("ACHIEVEMENTS", () -> openDialog(new AchievementsDialog(this))),
                new Item("OPTIONS", () -> openDialog(new OptionsDialog(this))),
                new Item("QUIT", () -> openDialog(new QuitDialog(this))));
    }

    // ---- dialog management (used by the dialogs) -------------------------------------------

    public void openDialog(Dialog next) {
        this.dialog = next;
        relayout();
    }

    public void closeDialog() {
        this.dialog = null;
    }

    public void relayout() {
        if (dialog != null) {
            dialog.layout(width, height);
        }
    }

    @Override
    protected void init() {
        relayout();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    // ---- layout ----------------------------------------------------------------------------

    private void computeLayout() {
        titleX = Math.max(12, (int) (width * 0.09));
        int available = (int) (width * 0.80) - titleX;
        titleLevel = TITLE_STYLES.length - 1;
        for (int i = 0; i < TITLE_STYLES.length; i++) {
            if (titleWidth(i) <= available) {
                titleLevel = i;
                break;
            }
        }
        int top = (int) (height * 0.38);
        titleBaseline = top + (int) (TITLE_SIZES[titleLevel] * 0.73);
        menuX = Math.max(8, titleX - (int) (width * 0.03));
        step = Math.max(14, (int) (height * 0.039));
        menuY = titleBaseline + Math.max(26, (int) (height * 0.07));
    }

    private int tracking(int level) {
        return (int) (TITLE_SIZES[level] * 0.42);
    }

    private int titleWidth(int level) {
        int total = 0;
        for (String letter : TITLE_LETTERS) {
            total += Draw.width(font, letter, TITLE_STYLES[level]) + tracking(level);
        }
        return total + Draw.width(font, "²", TITLE_STYLES[level]);
    }

    private int itemTop(int index) {
        return menuY + index * step - 4 - step / 2;
    }

    // ---- rendering -------------------------------------------------------------------------

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        // The parent draws the regular Minecraft panorama background.
        super.extractRenderState(g, mouseX, mouseY, delta);
        computeLayout();
        drawVignette(g);
        drawTitle(g);

        boolean blocked = dialog != null;
        for (int i = 0; i < items.size(); i++) {
            String label = items.get(i).label();
            int labelWidth = Draw.width(font, label, Hl2Fonts.MENU_BOLD);
            boolean hover = !blocked && Draw.inside(mouseX, mouseY, menuX - 4, itemTop(i), labelWidth + 8, step);
            Draw.text(g, font, label, Hl2Fonts.MENU_BOLD, menuX, menuY + i * step,
                    hover ? Theme.WHITE : Theme.MENU_IDLE);
        }

        if (dialog != null) {
            dialog.render(g, font, mouseX, mouseY);
        }
    }

    private void drawVignette(GuiGraphicsExtractor g) {
        g.fillGradient(0, 0, width, (int) (height * 0.25), 0x90000000, 0x00000000);
        g.fillGradient(0, (int) (height * 0.70), width, height, 0x00000000, 0xA0000000);
        // Darken the left side where the text sits, fading out towards the middle of the screen.
        int columns = (int) (width * 0.5);
        for (int i = 0; i < columns; i += 2) {
            int alpha = (int) (0x70 * (1.0 - i / (double) columns));
            g.fill(i, 0, i + 2, height, alpha << 24);
        }
    }

    private void drawTitle(GuiGraphicsExtractor g) {
        Style style = TITLE_STYLES[titleLevel];
        int tracking = tracking(titleLevel);
        int cursor = titleX;
        for (String letter : TITLE_LETTERS) {
            Draw.text(g, font, letter, style, cursor + 1, titleBaseline + 1, 0x90000000);
            Draw.text(g, font, letter, style, cursor, titleBaseline, Theme.WHITE);
            cursor += Draw.width(font, letter, style) + tracking;
        }
        int superscriptX = cursor - tracking / 2;
        Draw.text(g, font, "²", style, superscriptX + 1, titleBaseline + 1, 0x90000000);
        Draw.text(g, font, "²", style, superscriptX, titleBaseline, Theme.WHITE);
    }

    // ---- input -----------------------------------------------------------------------------
    // VERSION-SENSITIVE: the event-object signatures below (MouseButtonEvent / KeyEvent) are the
    // ones used since 1.21.9. They are all in this one block.

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            int mx = (int) event.x();
            int my = (int) event.y();
            if (dialog != null) {
                dialog.click(mx, my, doubleClick);
                return true;
            }
            computeLayout();
            for (int i = 0; i < items.size(); i++) {
                int labelWidth = Draw.width(font, items.get(i).label(), Hl2Fonts.MENU_BOLD);
                if (Draw.inside(mx, my, menuX - 4, itemTop(i), labelWidth + 8, step)) {
                    items.get(i).action().run();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dialog != null) {
            dialog.drag((int) event.x(), (int) event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dialog != null) {
            dialog.release();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (dialog != null) {
            dialog.scroll((int) mouseX, (int) mouseY, scrollY);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            if (dialog != null) {
                dialog.cancel();
            }
            return true;
        }
        return super.keyPressed(event);
    }
}
