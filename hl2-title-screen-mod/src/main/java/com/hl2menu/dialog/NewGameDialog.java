package com.hl2menu.dialog;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.VanillaBridge;
import com.hl2menu.screen.Hl2TitleScreen;
import com.hl2menu.ui.HlButton;
import com.hl2menu.ui.HlLabel;
import com.hl2menu.ui.Theme;

public final class NewGameDialog extends Dialog {
    public NewGameDialog(Hl2TitleScreen host) {
        super(host, "New Game", 360, 120);
    }

    @Override
    protected void build() {
        add(new HlLabel("Create a brand new world and start playing.", x + 14, y + 46, Theme.TEXT, Hl2Fonts.MENU));
        add(new HlLabel("Game mode, difficulty and seed come next.", x + 14, y + 64, Theme.TEXT_DIM, Hl2Fonts.MENU));

        int by = y + dh - 28;
        add(new HlButton("Start new game", x + dw - 190, by, 110, 18, this::start));
        add(new HlButton("Cancel", x + dw - 76, by, 66, 18, host::closeDialog));
    }

    private void start() {
        host.closeDialog();
        VanillaBridge.openCreateWorld(host);
    }
}
