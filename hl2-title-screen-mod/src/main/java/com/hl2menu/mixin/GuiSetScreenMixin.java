package com.hl2menu.mixin;

import com.hl2menu.screen.Hl2TitleScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Swaps vanilla's title screen for the HL2 one at the single place every screen change goes through.
 * Since Minecraft 26.2 {@code setScreen} lives on {@link Gui} (it used to be on {@code Minecraft}).
 *
 * <p>Only the exact vanilla {@link TitleScreen} class is replaced, so other mods that subclass it
 * keep working.
 */
@Mixin(Gui.class)
public abstract class GuiSetScreenMixin {
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen hl2menu$replaceTitleScreen(Screen screen) {
        if (screen != null && screen.getClass() == TitleScreen.class) {
            return new Hl2TitleScreen();
        }
        return screen;
    }
}
