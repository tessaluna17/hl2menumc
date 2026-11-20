package com.hl2menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;

/**
 * Every call into vanilla game flows (creating a world, opening a save, the stock options screen)
 * lives here, so that if a Minecraft update changes one of these signatures there is exactly one
 * file to touch. The HL2 screens themselves only talk to this class.
 */
public final class VanillaBridge {
    private VanillaBridge() {}

    /** Opens vanilla's world creation screen; backing out returns to {@code parent}. */
    public static void openCreateWorld(Screen parent) {
        Minecraft mc = Minecraft.getInstance();
        // VERSION-SENSITIVE: CreateWorldScreen.openFresh(Minecraft, Runnable) - the second argument
        // was a Screen in older versions.
        CreateWorldScreen.openFresh(mc, () -> mc.gui.setScreen(parent));
    }

    /** Loads the save in the given folder; if loading fails or is cancelled, returns to {@code parent}. */
    public static void openWorld(Screen parent, String levelFolder) {
        Minecraft mc = Minecraft.getInstance();
        // VERSION-SENSITIVE: WorldOpenFlows.openWorld(String, Runnable)
        mc.createWorldOpenFlows().openWorld(levelFolder, () -> mc.gui.setScreen(parent));
    }

    /** Opens vanilla's own options hub (controls, resource packs, language, accessibility ...). */
    public static void openVanillaOptions(Screen parent) {
        Minecraft mc = Minecraft.getInstance();
        // VERSION-SENSITIVE: OptionsScreen(Screen, Options)
        mc.gui.setScreen(new OptionsScreen(parent, mc.options));
    }

    public static void quit() {
        Minecraft.getInstance().stop();
    }
}
