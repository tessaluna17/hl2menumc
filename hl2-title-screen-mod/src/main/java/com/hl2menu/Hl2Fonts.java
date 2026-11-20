package com.hl2menu;

import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

/**
 * Text styles that point at the bundled TrueType fonts (assets/hl2menu/font/*.json) instead of
 * Minecraft's pixel font. Every piece of text in the HL2 menus is drawn with one of these.
 */
public final class Hl2Fonts {
    /** Regular 11px sans, used for dialog text. */
    public static final Style MENU = of("menu");
    /** Bold 11px sans, used for the main menu entries and dialog titles. */
    public static final Style MENU_BOLD = of("menu_bold");
    /** Thin wide title face in three sizes; the title screen picks the largest that fits. */
    public static final Style TITLE_L = of("title_l");
    public static final Style TITLE_M = of("title_m");
    public static final Style TITLE_S = of("title_s");

    private Hl2Fonts() {}

    private static Style of(String name) {
        // VERSION-SENSITIVE: custom fonts on a Style are set with a FontDescription since 1.21.9.
        // If this line stops compiling, check Style#withFont in the current Minecraft jar.
        return Style.EMPTY.withFont(
                new FontDescription.Resource(Identifier.fromNamespaceAndPath(Hl2Menu.MOD_ID, name)));
    }
}
